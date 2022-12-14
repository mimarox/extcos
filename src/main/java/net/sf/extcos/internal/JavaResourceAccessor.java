package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.extcos.spi.AnnotationMetadata;
import net.sf.extcos.spi.QueryContext;
import net.sf.extcos.spi.ResourceAccessor;
import net.sf.extcos.util.Assert;
import net.sf.extcos.util.ClassUtils;

public class JavaResourceAccessor implements ResourceAccessor {
	private static class DynamicClassLoader extends ClassLoader {
		private Map<String, Class<?>> classes = new HashMap<>();
		
		public Class<?> defineClass(final String name, final byte[] b) {
			if (classes.containsKey(name)) {
				return classes.get(name);
			} else {
				Class<?> c = defineClass(name, b, 0, b.length);
				classes.put(name, c);
				return c;
			}
		}
	}
	
	private class BooleanHolder {
		boolean value;
	}

	private class NameHolder {
		String name;
	}

	private abstract class AnnotatedClassVisitor extends ClassVisitor {
		private AnnotatedClassVisitor() {
			super(Opcodes.ASM7);
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
				final String[] interfaces) {
			if (!(Modifier.isAbstract(access) ||
					Modifier.isInterface(access) ||
					excludeBecauseIsEnum(version, superName))) {
				isClassHolder.value = true;
				nameHolder.name = name;

				readInterfaces(superName, interfaces);
				readSuperClasses(superName);
			}
		}

		private boolean excludeBecauseIsEnum(final int version, final String superName) {
			boolean isEnum = false;
			
			if (version >= Opcodes.V1_5 && "java/lang/Enum".equals(superName)) {
				isEnum = true;
			}
			
			return isEnum && !QueryContext.getInstance().isIncludeEnums();
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
		private final String className;

		private AnnotationVisitorImpl(final String desc) {
			super(Opcodes.ASM7);
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
				Class<?> enumClass = QueryContext.getInstance().getClassLoader().loadClass(enumName);
				Method valueOf = enumClass.getDeclaredMethod("valueOf",	String.class);
				Object object = valueOf.invoke(null, value);
				metadata.putParameter(name, object);
			} catch (Exception ex) {
				logger.warn("An exception occurred", ex);
			}
		}

		@Override
		public void visitEnd() {
			try {
				Class<?> annotationClass =
						QueryContext.getInstance().getClassLoader().loadClass(className);
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
				logger.error("Class not found - can't determine meta-annotations", ex);
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

	private static final int ASM_FLAGS =
			ClassReader.SKIP_DEBUG +
			ClassReader.SKIP_CODE  +
			ClassReader.SKIP_FRAMES;

	private static final DynamicClassLoader LOADER = new DynamicClassLoader();
	
	private byte[] resourceBytes;
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
		
		return LOADER.defineClass(className, resourceBytes);
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
			readClassData();
		} catch (IOException e) {
			isClass = false;
			logger.error("Error reading resource", e);
		}
	}

	private byte[] readBytes(final URL resourceUrl) throws IOException {
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
						QueryContext.getInstance().getClassLoader().getResourceAsStream(superName + ".class"));

				reader.accept(new AnnotatedClassVisitor() {
					@Override
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

	private void readInterfaces(final String superName, final String[] interfaces) {
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
					QueryContext.getInstance().getClassLoader().getResourceAsStream(type + ".class"));

			reader.accept(new ClassVisitor(Opcodes.ASM7) {
				@Override
				public void visit(final int version, final int access, final String name,
						final String signature, final String superName, final String[] interfaces) {
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
