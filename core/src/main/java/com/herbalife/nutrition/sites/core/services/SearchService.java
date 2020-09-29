package com.herbalife.nutrition.sites.core.services;

import java.util.Map;

import org.apache.sling.api.resource.ResourceResolver;

import com.day.cq.search.result.SearchResult;

public interface SearchService {
	public SearchResult search(Map<String, Object> predicates, ResourceResolver resolver);
}
