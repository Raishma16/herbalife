package com.herbalife.nutrition.sites.core.services.impl;

import java.util.Map;

import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.herbalife.nutrition.sites.core.services.SearchService;

@Component
public class SearchServiceImpl implements SearchService {
	@Reference
	private QueryBuilder queryBuilder;

	@Override
	public SearchResult search(Map<String, Object> predicates, ResourceResolver resolver) {
		final Query query = queryBuilder.createQuery(PredicateGroup.create(predicates),
				resolver.adaptTo(Session.class));
		return query.getResult();
	}
}
