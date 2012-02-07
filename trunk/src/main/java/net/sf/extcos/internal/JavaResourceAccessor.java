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

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaResourceAccessor implements ResourceAccessor {
	private class BooleanHolder {
		boolean value;
	}

	private class NameHolder {
		String name;
	}

	private abstract class AnnotatedClassVisitor extends ClassVisitor {
		private AnnotatedClassVisitor() {
			super(Opcodes.ASM4);
		}

		@Override
		public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
			if (shouldVisitAnnotation(visible)) {
				if (annotations == null) {
					annotations = new HashMap<String, AnnotationMetadata>();
				}
				return new AnnotationVisitorImpl(desc);
			}

			return null;
		}

		protected abstract boolean shouldVisitAnnotation(boolean visible);
	}

	private class GeneralVisitor extends AnnotatedClassVisitor {
		private final NameHolder nameHolder;
		private final BooleanHolder isClassHolder;

		private GeneralVisitor(final NameHolder nameHolder,
				final BooleanHolder isClassHolder) {
			this.nameHolder = nameHolder;
			this.isClassHolder = isClassHolder;
		}

		@Override
		public void visit(final int version, final int access, final String name,
				final String signature, final String superName,
				@SuppressWarnings("hiding") final String[] interfaces) {
			if (!(Modifier.isAbstract(access) ||
					Modifier.isInterface(access) ||
					isEnum(version, superName))) {
				isClassHolder.value = true;
				nameHolder.name = name;

				readInterfaces(superName, interfaces);
				readSuperClasses(superName);
			}
		}

		private boolean isEnum(final int version, final String superName) {
			if (version >= Opcodes.V1_5 && "java/lang/Enum".equals(superName)) {
				return true;
			}

			return false;
		}

		@Override
		public void visitInnerClass(final String name, final String outerName,
				final String innerName, final int access) {
			if (isClassHolder.value && nameHolder.name != null &&
					nameHolder.name.equals(name)) {
				isClassHolder.value = false;
			}
		}

		@Override
		public void visitOuterClass(final String owner, final String name, final String desc) {
			isClassHolder.value = false;
		}

		@Override
		protected boolean shouldVisitAnnotation(final boolean visible) {
			return isClassHolder.value && visible;
		}
	}

	private class AnnotationVisitorImpl extends AnnotationVisitor {
		private final AnnotationMetadataImpl metadata;
		@SuppressWarnings("hiding")
		private final String className;

		private AnnotationVisitorImpl(final String desc) {
			super(4);
			metadata = new AnnotationMetadataImpl();
			className = Type.getType(desc).getClassName();
		}

		@Override
		public void visit(final String name, final Object value) {
			metadata.putParameter(name, value);
		}

		@Override
		public void visitEnum(final String name, final String desc, final String value) {
			try {
				String enumName = Type.getType(desc).getClassName();
				Class<?> enumClass = ClassLoaderHolder.getClassLoader().loadClass(enumName);
				Method valueOf = enumClass.getDeclaredMethod("valueOf",	String.class);
				Object object = valueOf.invoke(null, value);
				metadata.putParameter(name, object);
			} catch (Exception ex) { /* ignored */ }
		}

		@Override
		public void visitEnd() {
			try {
				Class<?> annotationClass =
						ClassLoaderHolder.getClassLoader().loadClass(className);
				// Check declared default values of attributes in the annotation type.
				Method[] annotationAttributes = annotationClass.getMethods();
				for (Method annotationAttribute : annotationAttributes) {
					String attributeName = annotationAttribute.getName();
					Object defaultValue = annotationAttribute.getDefaultValue();
					if (defaultValue != null && !metadata.hasKey(attributeName)) {
						metadata.putParameter(attributeName, defaultValue);
					}
				}
				annotations.put(className, metadata);
			}
			catch (ClassNotFoundException ex) {
				// Class not found - can't determine meta-annotations.
			}
		}
	}

	private class AnnotationMetadataImpl implements AnnotationMetadata {
		private final Map<String, Object> parameters =
				new HashMap<String, Object>();

		@Override
		public Object getValue(final String key) {
			return parameters.get(key);
		}

		@Override
		public boolean hasKey(final String key) {
			return parameters.containsKey(key);
		}

		protected void putParameter(final String key, final Object value) {
			parameters.put(key, value);
		}
	}

	private static Logger logger = LoggerFactory.getLogger(JavaResourceAccessor.class);

	private static Method defineClass;
	private static Method resolveClass;

	static {
		try {
			AccessController.doPrivileged(
					new PrivilegedExceptionAction<Void>(){
						@Override
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

	@Override
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

	@Override
	public AnnotationMetadata getAnnotationMetadata(
			final Class<? extends Annotation> annotation) {
		if (isClass && annotations != null &&
				annotations.containsKey(annotation.getCanonicalName())) {
			return annotations.get(annotation.getCanonicalName());
		}

		return null;
	}

	@Override
	public boolean hasInterface(final Class<?> interfaze) {
		if (isClass && interfaces != null) {
			return interfaces.contains(interfaze.getCanonicalName());
		}

		return false;
	}

	@Override
	public boolean isClass() {
		return isClass;
	}

	@Override
	public boolean isSubclassOf(final Class<?> clazz) {
		if (clazz == Object.class) {
			return true;
		}

		if (isClass && superClasses != null) {
			return superClasses.contains(clazz.getCanonicalName());
		}

		return false;
	}

	@Override
	public void setResourceUrl(final URL resourceUrl) {
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

	private byte[] readBytes(@SuppressWarnings("hiding") final URL resourceUrl) throws IOException {
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

	private void readSuperClasses(final String superName) {
		if (!"java/lang/Object".equals(superName)) {
			if (superClasses == null) {
				superClasses = new ArraySet<String>();
			}

			String superClass = ClassUtils.convertResourcePathToClassName(
					superName);
			superClasses.add(superClass);

			try {
				ClassReader reader = new ClassReader(
						ClassLoaderHolder.getClassLoader().getResourceAsStream(superName + ".class"));

				reader.accept(new AnnotatedClassVisitor() {
					@Override
					@SuppressWarnings("hiding")
					public void visit(final int version, final int access, final String name,
							final String signature, final String superName, final String[] interfaces) {
						readSuperClasses(superName);
					}

					@Override
					protected boolean shouldVisitAnnotation(final boolean visible) {
						return visible;
					}
				}, ASM_FLAGS);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("Unable to read super class [" + superName + "]", e);
				}
			}
		}
	}

	private void readInterfaces(final String superName, @SuppressWarnings("hiding") final String[] interfaces) {
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

	private void readInheritedInterfaces(final String superName) {
		if ("java/lang/Object".equals(superName)) {
			return;
		}

		readSuperInterfaces(superName);
	}

	private void readSuperInterfaces(final String type) {
		String interfaze = ClassUtils.convertResourcePathToClassName(type);

		try {
			ClassReader reader = new ClassReader(
					ClassLoaderHolder.getClassLoader().getResourceAsStream(type + ".class"));

			reader.accept(new ClassVisitor(Opcodes.ASM4) {
				@Override
				public void visit(final int version, final int access, final String name,
						final String signature, final String superName, @SuppressWarnings("hiding") final String[] interfaces) {
					readInterfaces(superName, interfaces);
				}
			}, ASM_FLAGS);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("Unable to read interface [" + interfaze + "]", e);
			}
		}
	}
}
