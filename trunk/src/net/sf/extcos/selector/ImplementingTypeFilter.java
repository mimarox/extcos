package net.sf.extcos.selector;

import java.util.Set;

public interface ImplementingTypeFilter extends MultipleTypeFilter {
    public Set<Class<?>> getInterfaces();
}