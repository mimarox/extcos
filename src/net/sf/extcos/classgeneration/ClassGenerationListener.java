package net.sf.extcos.classgeneration;

public interface ClassGenerationListener {
	<T> void classGenerated(Class<? extends T> clazz);
}