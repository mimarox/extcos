package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;
import static net.sf.extcos.util.StringUtils.append;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.extcos.spi.AnnotationMetadata;
import net.sf.extcos.spi.ClassLoaderHolder;
import net.sf.extcos.spi.ResourceAccessor;
import net.sf.extcos.util.Assert;
import net.sf.extcos.util.ClassUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.EmptyVisitor;

public class JavaResourceAccessor implements ResourceAccessor {
	private class BooleanHolder {
		boolean value;
	}
	
	private class NameHolder {
		String name;
	}
	
	private class GeneralVisitor extends EmptyVisitor {
		private final NameHolder nameHolder;
		private final BooleanHolder isClassHolder;

		private GeneralVisitor(NameHolder nameHolder,
				BooleanHolder isClassHolder) {
			this.nameHolder = nameHolder;
			this.isClassHolder = isClassHolder;
		}

		@Override
		public void visit(int version, int access, String name,
				String signature, String superName, String[] interfaces) {
			if (!(Modifier.isAbstract(access) ||
					Modifier.isInterface(access) ||
					isEnum(version, superName))) {
				isClassHolder.value = true;
				nameHolder.name = name;
				
				readInterfaces(superName, interfaces);
				readSuperClasses(superName);
			}
		}

		private boolean isEnum(int version, String superName) {
			if (version >= Opcodes.V1_5 && "java/lang/Enum".equals(superName)) {
				return true;
			} else {
				return false;
			}
		}

		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			annotations = new HashMap<String, AnnotationMetadata>();
			
			if (isClassHolder.value && visible) {
				final AnnotationMetadataImpl metadata = new AnnotationMetadataImpl();
				final String className = Type.getType(desc).getClassName();

				return new EmptyVisitor() {
					public void visit(String name, Object value) {
						metadata.register(name, value);
					}

					public void visitEnum(String name, String desc, String value) {
						try {
							String enumName = Type.getType(desc).getClassName();
							Class<?> enumClass = ClassLoaderHolder.getClassLoader().loadClass(enumName);
							Method valueOf = enumClass.getDeclaredMethod("valueOf",	String.class);
							Object object = valueOf.invoke(null, value);
							metadata.register(name, object);
						} catch (Exception ex) {
						}
					}
					
					public void visitEnd() {
						try {
							Class<?> annotationClass =
								ClassLoaderHolder.getClassLoader().loadClass(className);
							// Check declared default values of attributes in the annotation type.
							Method[] annotationAttributes = annotationClass.getMethods();
							for (int i = 0; i < annotationAttributes.length; i++) {
								Method annotationAttribute = annotationAttributes[i];
								String attributeName = annotationAttribute.getName();
								Object defaultValue = annotationAttribute.getDefaultValue();
								if (defaultValue != null && !metadata.hasKey(attributeName)) {
									metadata.register(attributeName, defaultValue);
								}
							}
							annotations.put(className, metadata);
						}
						catch (ClassNotFoundException ex) {
							// Class not found - can't determine meta-annotations.
						}
					}
				};
			} else {
				return null;
			}
		}

		@Override
		public void visitInnerClass(String name, String outerName,
				String innerName, int access) {
			if (isClassHolder.value && nameHolder.name != null &&
					nameHolder.name.equals(name))
				isClassHolder.value = false;
		}

		@Override
		public void visitOuterClass(String owner, String name, String desc) {
			isClassHolder.value = false;
		}
	}

	private class AnnotationMetadataImpl implements AnnotationMetadata {
		private Map<String, Object> parameters =
			new HashMap<String, Object>();
		
		public Object getValue(String key) {
			return parameters.get(key);
		}

		public boolean hasKey(String key) {
			return parameters.containsKey(key);
		}
		
		protected void register(String key, Object value) {
			parameters.put(key, value);
		}
	}
	
	private static Log logger = LogFactory.getLog(JavaResourceAccessor.class);
	
	private static Method defineClass;
    private static Method resolveClass;
    
    static {
        try {
            AccessController.doPrivileged(
            		new PrivilegedExceptionAction<Void>(){
                public Void run() throws Exception{
                    Class<?> cl = Class.forName("java.lang.ClassLoader");
                    
                    defineClass = cl.getDeclaredMethod("defineClass",
                            new Class[] { String.class, byte[].class,
                                         int.class, int.class });
                    
                    resolveClass = cl.getDeclaredMethod("resolveClass",
                    		Class.class);
                    
                    return null;
                }
            });
        }
        catch (PrivilegedActionException pae) {
            throw new RuntimeException("cannot initialize Java Resource Accessor", pae.getException());
        }
    }
    
    private static final int ASM_FLAGS =
    	ClassReader.SKIP_DEBUG +
    	ClassReader.SKIP_CODE  +
    	ClassReader.SKIP_FRAMES;
    
	private byte[] resourceBytes;
	private URL resourceUrl;
	private String className;
	
	private Map<String, AnnotationMetadata> annotations;
	private Set<String> interfaces;
	private Set<String> superClasses;
	private boolean isClass;
	
	public Class<?> generateClass() {
		if (!isClass) {
			return null;
		}
		
		Class<?> clazz = null;
		ClassLoader loader = ClassLoaderHolder.getClassLoader();
		
		try {
			defineClass.setAccessible(true);
			resolveClass.setAccessible(true);
			
			clazz = (Class<?>)defineClass.invoke(loader, 
					className, resourceBytes, 0, resourceBytes.length);
			resolveClass.invoke(loader, clazz);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof LinkageError) {
				try {
					clazz = Class.forName(className, true, loader);
				} catch (ClassNotFoundException e1) {
					logger.error(append("Error creating class from URL [",
							resourceUrl.toString(), "]"), e1);
				}
			} else {
				logger.error(append("Error creating class from URL [",
						resourceUrl.toString(), "]"), e.getCause());
			}
		} catch (Exception e) {
			logger.error(append("Error creating class from URL [",
					resourceUrl.toString(), "]"), e);
		} finally {
			defineClass.setAccessible(false);
			resolveClass.setAccessible(false);
		}
		
		return clazz;
	}

	public AnnotationMetadata getAnnotationMetadata(
			Class<? extends Annotation> annotation) {
		if (isClass && annotations != null &&
				annotations.containsKey(annotation.getCanonicalName())) {
			return annotations.get(annotation.getCanonicalName());
		} else {
			return null;
		}
	}

	public boolean hasInterface(Class<?> interfaze) {
		if (isClass && interfaces != null) {
			return interfaces.contains(interfaze.getCanonicalName());
		} else {
			return false;
		}
	}

	public boolean isClass() {
		return isClass;
	}

	public boolean isSubclassOf(Class<?> clazz) {
		if (clazz == Object.class)
			return true;
		
		if (isClass && superClasses != null) {
			return superClasses.contains(clazz.getCanonicalName());
		} else {
			return false;
		}
	}

	public void setResourceUrl(URL resourceUrl) {
        Assert.notNull(resourceUrl, iae());
        
        try {
			this.resourceBytes = readBytes(resourceUrl);
			this.resourceUrl = resourceUrl;
			readClassData();
		} catch (IOException e) {
			isClass = false;
			logger.error("Error reading resource", e);
		}
	}

	private byte[] readBytes(URL resourceUrl) throws IOException {
		InputStream classStream = new BufferedInputStream(resourceUrl.openStream());
		List<Byte> buffer = new ArrayList<Byte>();
		int readByte;
		
		while((readByte = classStream.read()) != -1) {
			buffer.add((byte)readByte);
		}
		
		byte[] bytes = new byte[buffer.size()];
		
		for (int i = 0; i < buffer.size(); i++) {
			bytes[i] = buffer.get(i);
		}
		
		return bytes;
	}
	
	private void readClassData() {
		BooleanHolder isClassHolder = new BooleanHolder();
		NameHolder nameHolder = new NameHolder();		
		
		ClassReader reader = new ClassReader(resourceBytes);
		reader.accept(new GeneralVisitor(nameHolder, isClassHolder),
				ASM_FLAGS);
		
		isClass = isClassHolder.value;
		
		if (isClass) {
			className =
				ClassUtils.convertResourcePathToClassName(nameHolder.name);
		} else {
			// if the resource isn't a valid class, clean memory
			annotations   = null;
			interfaces    = null;
			superClasses  = null;
			resourceBytes = null;
			resourceUrl   = null;
		}
	}
	
	private void readSuperClasses(String superName) {
		if (!"java/lang/Object".equals(superName)) {
			if (superClasses == null) {
				superClasses = new ArraySet<String>();
			}
			
			String superClass = ClassUtils.convertResourcePathToClassName(
					superName);
			superClasses.add(superClass);
			
			try {
				ClassReader reader = new ClassReader(superClass);
				reader.accept(new EmptyVisitor() {
					@Override
					public void visit(int version, int access, String name,
							String signature, String superName, String[] interfaces) {
						readSuperClasses(superName);
					}
				}, ASM_FLAGS);
			} catch (Exception e) {}
		}
	}

	private void readInterfaces(String superName, String[] interfaces) {
		if (this.interfaces == null && interfaces.length > 0) {
			this.interfaces = new ArraySet<String>();
		}
		
		for (String interfaze : interfaces) {
			this.interfaces.add(
					ClassUtils.convertResourcePathToClassName(interfaze));
			readSuperInterfaces(interfaze);
		}
		
		readInheritedInterfaces(superName);
	}

	private void readInheritedInterfaces(String superName) {
		if ("java/lang/Object".equals(superName)) {
			return;
		}
		
		readSuperInterfaces(superName);
	}

	private void readSuperInterfaces(String type) {
		try {
			ClassReader reader = new ClassReader(
					ClassUtils.convertResourcePathToClassName(type));
			
			reader.accept(new EmptyVisitor() {
				@Override
				public void visit(int version, int access, String name,
						String signature, String superName, String[] interfaces) {
					readInterfaces(superName, interfaces);
				}
			}, ASM_FLAGS);
		} catch (Exception e) {}
	}
}