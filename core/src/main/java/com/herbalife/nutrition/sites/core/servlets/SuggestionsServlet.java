package com.herbalife.nutrition.sites.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.TagConstants;
import com.herbalife.nutrition.sites.core.constants.SearchConstants;
import com.herbalife.nutrition.sites.core.constants.ServletConstants;
import com.herbalife.nutrition.sites.core.dtos.ResultTO;

@Component(service = Servlet.class, property = {
		ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
		ServletResolverConstants.SLING_SERVLET_PATHS + "=" + SuggestionsServlet.PATH })
public class SuggestionsServlet extends SlingSafeMethodsServlet {
	public static final String PATH = "/bin/SuggestionsServlet";

	@Reference
	private QueryBuilder queryBuilder;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		ResourceResolver resolver = request.getResourceResolver();
		HashMap<String, Object> predicates = new HashMap<>();
		List<ResultTO> suggestions = new ArrayList<>();
		JSONObject suggestionsResponse = new JSONObject();
		predicates.put("path", request.getParameter(SearchConstants.CONTENT_FRAGMENT_SEARCH_PATH));
		predicates.put("type", DamConstants.NT_DAM_ASSET);
		predicates.put("fulltext.relPath", SearchConstants.CF_TITLE_PROPERTY_NAME);
		predicates.put("fulltext", request.getParameter(SearchConstants.SEARCH_TERM) + "*");
		predicates.put("tagid.property", SearchConstants.METADATA_NODE_NAME + TagConstants.PN_TAGS);
		predicates.put("property", SearchConstants.METADATA_NODE_NAME + JcrConstants.JCR_MIXINTYPES);
		predicates.put("property.value", TagConstants.NT_TAGGABLE);
		String[] categoryTagNames = request.getParameterValues(SearchConstants.CATEGORY_TAG_NAMES);
		for (int i = 0; i < categoryTagNames.length; i++)
			predicates.put("tagid." + (i + 1) + "_value", categoryTagNames[i]);
		predicates.put("p.limit", request.getParameter(SearchConstants.SUGGESTIONS_LIMIT));
		final Query query = queryBuilder.createQuery(PredicateGroup.create(predicates),
				resolver.adaptTo(Session.class));
		SearchResult result = query.getResult();
		Iterator<Resource> resourceIterator = result.getResources();
		while (resourceIterator.hasNext()) {
			Resource resource = resourceIterator.next();
			ValueMap valueMap = resource.getChild("jcr:content/data/master").getValueMap();
			ResultTO resultTO = new ResultTO(valueMap.get("title-text", String.class),
					valueMap.get("title-text-link", String.class) + ".html");
			suggestions.add(resultTO);
		}
		try {
			suggestionsResponse.put("suggestions", suggestions);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		response.setContentType(ServletConstants.JSON_CONTENT_TYPE);
		response.setCharacterEncoding(ServletConstants.UTF8_CHARACTER_ENCODING);
		response.getWriter().write(suggestionsResponse.toString());
	}
}
