package com.herbalife.nutrition.sites.core.servlets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;
import javax.jcr.Session;
import javax.servlet.Servlet;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.dam.cfm.converter.ContentTypeConverter;
import com.adobe.cq.wcm.core.components.models.contentfragment.ContentFragmentList;
import com.adobe.cq.wcm.core.components.models.contentfragment.DAMContentFragment;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.TagConstants;
import com.herbalife.nutrition.sites.core.constants.ServletConstants;
import com.herbalife.nutrition.sites.core.dtos.DAMContentFragmentImpl;

@SuppressWarnings("serial")
@Component(service = Servlet.class, property = {
		ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
		ServletResolverConstants.SLING_SERVLET_PATHS + "=" + ContentFragmentListServlet.PATH })
public class ContentFragmentListServlet extends SlingSafeMethodsServlet {
	public static final String PATH = "/bin/ContentFragmentListServlet";

	private static final Logger LOG = LoggerFactory.getLogger(ContentFragmentListServlet.class);

	public static final String DEFAULT_DAM_PARENT_PATH = "/content/dam";

	@Reference
	private QueryBuilder queryBuilder;
	@Inject
	private ContentTypeConverter contentTypeConverter;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
		String parentPath = StringUtils.defaultIfBlank(request.getParameter("./" + ContentFragmentList.PN_PARENT_PATH),
				DEFAULT_DAM_PARENT_PATH);
		String modelPath = request.getParameter("./" + ContentFragmentList.PN_MODEL_PATH);
		String[] tagNames = request.getParameterValues("./" + ContentFragmentList.PN_TAG_NAMES);
		String maxItems = StringUtils.defaultIfBlank(request.getParameter("./" + ContentFragmentList.PN_MAX_ITEMS),
				"-1");
		String[] elementNames = null;
		JSONObject cfResponse = new JSONObject();
		ArrayList<String> cfTitles = new ArrayList<String>();
		ResourceResolver resolver = request.getResourceResolver();
		Map<String, String> predicates = new HashMap<>();
		predicates.put("path", parentPath);
		predicates.put("type", "dam:Asset");
		predicates.put("p.limit", maxItems);
		predicates.put("orderby", "@" + JcrConstants.JCR_CREATED);
		predicates.put("orderby.sort", "desc");
		predicates.put("1_property", JcrConstants.JCR_CONTENT + "/data/cq:model");
		if (StringUtils.isNotEmpty(modelPath)) {
			predicates.put("1_property.value", modelPath);
		}
		if (ArrayUtils.isNotEmpty(tagNames)) {
			predicates.put("2_property", JcrConstants.JCR_CONTENT + "/metadata/" + JcrConstants.JCR_MIXINTYPES);
			predicates.put("2_property.value", TagConstants.NT_TAGGABLE);
			predicates.put("tagid.property", JcrConstants.JCR_CONTENT + "/metadata/cq:tags");
			for (int i = 0; i < tagNames.length; i++) {
				predicates.put("tagid." + (i + 1) + "_value", tagNames[i]);
			}
		}
		final Query query = queryBuilder.createQuery(PredicateGroup.create(predicates),
				resolver.adaptTo(Session.class));
		SearchResult result = query.getResult();
		try {
			Iterator<Resource> resourceIterator = result.getResources();
			while (resourceIterator.hasNext()) {
				Resource resource = resourceIterator.next();
				DAMContentFragment contentFragmentModel = new DAMContentFragmentImpl(resource, contentTypeConverter,
						elementNames);
				LOG.debug(contentFragmentModel.getTitle());
				cfTitles.add(contentFragmentModel.getTitle());
			}
			cfResponse.put("cfTitles", cfTitles);
			LOG.debug("CF Titles: " + cfTitles);
			response.setContentType(ServletConstants.JSON_CONTENT_TYPE);
			response.setCharacterEncoding(ServletConstants.UTF8_CHARACTER_ENCODING);
			response.getWriter().write(cfResponse.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
