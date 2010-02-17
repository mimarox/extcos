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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
	private class SuperInterfaceVisitor extends EmptyVisitor {
		private final BooleanHolder result;
		private final String superInterface;

		private SuperInterfaceVisitor(BooleanHolder result, String superInterface) {
			this.result = result;
			this.superInterface = superInterface;
		}

		@Override
		public void visit(int version, int access, String name,
				String signature, String superName, String[] interfaces) {
			if (Arrays.asList(interfaces).contains(superInterface) ||
					isSuperInterface(superInterface, interfaces)) {
				result.value = true;
			}
		}
	}

	private class SuperClassVisitor extends EmptyVisitor {
		private final BooleanHolder result;
		private final String topName;

		private SuperClassVisitor(BooleanHolder result, String topName) {
			this.result = result;
			this.topName = topName;
		}

		@Override
		public void visit(int version, int access, String name,
				String signature, String superName, String[] interfaces) {
			if (topName.equals(superName) ||
					isSubclassOf(superName, topName)) {
				result.value = true;
			}
		}
	}

	private class InterfaceVisitor extends EmptyVisitor {
		private final BooleanHolder result;
		private final String interfaceName;

		private InterfaceVisitor(BooleanHolder result, String interfaceName) {
			this.result = result;
			this.interfaceName = interfaceName;
		}

		public void visit(int version, int access, String name,
				String signature, String superName, String[] interfaces) {
			if (Arrays.asList(interfaces).contains(interfaceName) ||
					inheritedInterface(interfaceName, superName)  ||
					isSuperInterface(interfaceName, interfaces)) {
				result.value = true;
			}
		}
	}

	private class BooleanHolder {
		boolean value;
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
    
    private static final byte[] INVALID_RESOURCE = new byte[0];
	private byte[] resourceBytes;
	private URL resourceUrl;
	private String className;
	
	@Override
	public AnnotationMetadata getAnnotationMetadata(
			Class<? extends Annotation> annotation) {
		if (resourceBytes == INVALID_RESOURCE) {
			return null;
		}
		
		class MetadataHolder {
			AnnotationMetadataImpl metadata;
		}
		
		final MetadataHolder metadataHolder = new MetadataHolder();
		final String annotationType = Type.getDescriptor(annotation);
		
		ClassReader reader = new ClassReader(resourceBytes);
		reader.accept(new EmptyVisitor() {
			public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
				if (annotationType.equals(desc) && visible) {
					metadataHolder.metadata = new AnnotationMetadataImpl();
					final String className = Type.getType(desc).getClassName();

					return new EmptyVisitor() {
						public void visit(String name, Object value) {
							metadataHolder.metadata.register(name, value);
						}

						public void visitEnum(String name, String desc, String value) {
							try {
								String enumName = Type.getType(desc).getClassName();
								Class<?> enumClass = ClassLoaderHolder.getClassLoader().loadClass(enumName);
								Method valueOf = enumClass.getDeclaredMethod("valueOf",	String.class);
								Object object = valueOf.invoke(null, value);
								metadataHolder.metadata.register(name, object);
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
									if (defaultValue != null && !metadataHolder.metadata.hasKey(attributeName)) {
										metadataHolder.metadata.register(attributeName, defaultValue);
									}
								}
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
		}, ASM_FLAGS);
		
		return metadataHolder.metadata;
	}

	@Override
	public boolean hasInterface(Class<?> interfaze) {
		if (resourceBytes == INVALID_RESOURCE) {
			return false;
		}
		
		BooleanHolder result = new BooleanHolder();
		
		String interfaceName =
			ClassUtils.convertClassNameToResourcePath(
					interfaze.getCanonicalName());
		
		ClassReader reader = new ClassReader(resourceBytes);
		reader.accept(new InterfaceVisitor(result, interfaceName),
				ASM_FLAGS);
		
		return result.value;
	}

	private boolean inheritedInterface(String interfaceName,
			String superName) {
		if ("java/lang/Object".equals(superName)) {
			return false;
		}
		
		try {
			ClassReader reader = new ClassReader(
					ClassUtils.convertResourcePathToClassName(superName));
			
			BooleanHolder result = new BooleanHolder();
			
			reader.accept(new InterfaceVisitor(result, interfaceName),
					ASM_FLAGS);
			
			return result.value;
		} catch (Exception e) {
			return false;
		}
	}
	
	private boolean isSuperInterface(String superInterface, String[] interfaces) {
		for (String interfaze : interfaces) {
			try {
				ClassReader reader = new ClassReader(
						ClassUtils.convertResourcePathToClassName(interfaze));
				
				BooleanHolder result = new BooleanHolder();
				
				reader.accept(new SuperInterfaceVisitor(result, superInterface),
						ASM_FLAGS);
				
				if (result.value) return true;
			} catch (Exception e) {}
		}
		
		return false;
	}
	
	@Override
	public boolean isClass() {
		if (resourceBytes == INVALID_RESOURCE) {
			return false;
		}
		
		class NameHolder {
			String name;
		}
		
		final BooleanHolder result = new BooleanHolder();
		final NameHolder nameHolder = new NameHolder();		
		
		ClassReader reader = new ClassReader(resourceBytes);
		reader.accept(new EmptyVisitor() {
			
			@Override
			public void visit(int version, int access, String name,
					String signature, String superName, String[] interfaces) {
				if (!(Modifier.isAbstract(access) ||
						Modifier.isInterface(access) ||
						isEnum(version, superName))) {
					result.value = true;
					nameHolder.name = name;
				}
			}
			
			@Override
			public void visitInnerClass(String name, String outerName,
					String innerName, int access) {
				if (result.value && nameHolder.name != null &&
						nameHolder.name.equals(name))
					result.value = false;
			}
			
			@Override
			public void visitOuterClass(String owner, String name, String desc) {
				result.value = false;
			}
		}, ASM_FLAGS);
		
		if (result.value) {
			className =
				ClassUtils.convertResourcePathToClassName(nameHolder.name);
		}
		
		return result.value;
	}

	private boolean isEnum(int version, String superName) {
		if (version >= Opcodes.V1_5 && "java/lang/Enum".equals(superName)) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean isSubclassOf(Class<?> clazz) {
		if (resourceBytes == INVALID_RESOURCE) {
			return false;
		}
		
		if (clazz == Object.class) {
			return true;
		}
		
		BooleanHolder result = new BooleanHolder();
		String topName = ClassUtils.convertClassNameToResourcePath(
					clazz.getCanonicalName());
			
		ClassReader reader = new ClassReader(resourceBytes);
		reader.accept(new SuperClassVisitor(result, topName), ASM_FLAGS);
		
		return result.value;
	}

	private boolean isSubclassOf(String superName, String topName) {
		if ("java/lang/Object".equals(topName)) {
			return true;
		}
		if ("java/lang/Object".equals(superName)) {
			return false;
		}
		
		try {
			ClassReader reader = new ClassReader(
					ClassUtils.convertResourcePathToClassName(superName));
			
			BooleanHolder result = new BooleanHolder();
			
			reader.accept(new SuperClassVisitor(result, topName),
					ASM_FLAGS);
			
			return result.value;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void setResourceUrl(URL resourceUrl) {
        Assert.notNull(resourceUrl, iae());
        
        try {
			this.resourceBytes = readBytes(resourceUrl);
			this.resourceUrl = resourceUrl;
		} catch (IOException e) {
			this.resourceBytes = INVALID_RESOURCE;
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
	
	public Class<?> generateClass() {
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
}
