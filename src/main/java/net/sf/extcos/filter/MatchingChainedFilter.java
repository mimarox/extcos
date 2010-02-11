package net.sf.extcos.filter;

public interface MatchingChainedFilter extends ChainedFilter {
	void setResourceMatcher(ResourceMatcher resourceMatcher);
}