package net.sf.extcos.internal.factory;

import java.util.LinkedHashSet;
import java.util.Set;

import net.sf.extcos.filter.FilterInterceptor;
import net.sf.extcos.internal.ThreadingFilterInterceptor;

public class FilterInterceptorsFactory {
	public static Set<Class<? extends FilterInterceptor>> buildFilterInterceptors() {
		Set<Class<? extends FilterInterceptor>> interceptors =
				new LinkedHashSet<Class<? extends FilterInterceptor>>();

		interceptors.add(ThreadingFilterInterceptor.class);

		return interceptors;
	}
}
