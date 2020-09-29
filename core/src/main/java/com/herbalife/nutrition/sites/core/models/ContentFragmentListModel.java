package com.herbalife.nutrition.sites.core.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Session;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;
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
import com.herbalife.nutrition.sites.core.dtos.DAMContentFragmentImpl;

@Model(adaptables = SlingHttpServletRequest.class)
public class ContentFragmentListModel implements ContentFragmentList {

	private static final Logger LOG = LoggerFactory.getLogger(ContentFragmentListModel.class);

	public static final String DEFAULT_DAM_PARENT_PATH = "/content/dam";

	public static final int DEFAULT_MAX_ITEMS = -1;

	@Self(injectionStrategy = InjectionStrategy.REQUIRED)
	private SlingHttpServletRequest slingHttpServletRequest;

	@Inject
	private ContentTypeConverter contentTypeConverter;

	@SlingObject
	private ResourceResolver resourceResolver;

	@ValueMapValue(name = ContentFragmentList.PN_MODEL_PATH, injectionStrategy = InjectionStrategy.OPTIONAL)
	private String modelPath;

	@ValueMapValue(name = ContentFragmentList.PN_ELEMENT_NAMES, injectionStrategy = InjectionStrategy.OPTIONAL)
	private String[] elementNames;

	@ValueMapValue(name = ContentFragmentList.PN_TAG_NAMES, injectionStrategy = InjectionStrategy.OPTIONAL)
	private String[] tagNames;

	@ValueMapValue(name = ContentFragmentList.PN_PARENT_PATH, injectionStrategy = InjectionStrategy.OPTIONAL)
	private String parentPath;

	@ValueMapValue(name = ContentFragmentList.PN_MAX_ITEMS, injectionStrategy = InjectionStrategy.OPTIONAL)
	@Default(intValues = DEFAULT_MAX_ITEMS)
	private int maxItems;

	@ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
	private String[] cfTitles;

	private List<DAMContentFragment> items = new ArrayList<DAMContentFragment>();
	private String[] newCFTitles;

	@PostConstruct
	private void initModel() {
		// Default path limits search to DAM
		if (StringUtils.isEmpty(parentPath)) {
			if (StringUtils.isBlank(modelPath)) {
				LOG.warn("At least a path or a model needs to be configured to load results.");
				return;
			}
			parentPath = DEFAULT_DAM_PARENT_PATH;
		}

		Session session = resourceResolver.adaptTo(Session.class);
		if (session == null) {
			LOG.warn("Session was null therefore no query was executed");
			return;
		}
		QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
		if (queryBuilder == null) {
			LOG.warn("Query builder was null therefore no query was executed");
			return;
		}

		Map<String, String> queryParameterMap = new HashMap<>();
		queryParameterMap.put("path", parentPath);
		queryParameterMap.put("type", "dam:Asset");
		queryParameterMap.put("p.limit", Integer.toString(maxItems));
		queryParameterMap.put("orderby", "@" + JcrConstants.JCR_CREATED);
		queryParameterMap.put("orderby.sort", "desc");
		queryParameterMap.put("1_property", JcrConstants.JCR_CONTENT + "/data/cq:model");
		if (!StringUtils.isEmpty(modelPath)) {
			queryParameterMap.put("1_property.value", modelPath);
		}

		ArrayList<String> allTags = new ArrayList<>();
		if (tagNames != null && tagNames.length > 0) {
			allTags.addAll(Arrays.asList(tagNames));
		}
		if (!allTags.isEmpty()) {
			// Check for the taggable mixin
			queryParameterMap.put("2_property", JcrConstants.JCR_CONTENT + "/metadata/" + JcrConstants.JCR_MIXINTYPES);
			queryParameterMap.put("2_property.value", TagConstants.NT_TAGGABLE);
			// Check for the actual tags (by default, tag are or'ed)
			queryParameterMap.put("tagid.property", JcrConstants.JCR_CONTENT + "/metadata/cq:tags");
			for (int i = 0; i < allTags.size(); i++) {
				queryParameterMap.put(String.format("tagid.%d_value", i + 1), allTags.get(i));
			}
		}
		PredicateGroup predicateGroup = PredicateGroup.create(queryParameterMap);
		Query query = queryBuilder.createQuery(predicateGroup, session);

		SearchResult searchResult = query.getResult();

		LOG.debug("Query statement: '{}'", searchResult.getQueryStatement());

		// Query builder has a leaking resource resolver, so the following work around
		// is required.
		ResourceResolver leakingResourceResolver = null;

		try {
			// Iterate over the hits if you need special information
			Iterator<Resource> resourceIterator = searchResult.getResources();
			HashMap<String, DAMContentFragment> contentFragmentMap = new HashMap<String, DAMContentFragment>();
			while (resourceIterator.hasNext()) {
				Resource resource = resourceIterator.next();
				if (leakingResourceResolver == null) {
					// Get a reference to QB's leaking resource resolver
					leakingResourceResolver = resource.getResourceResolver();
				}
				DAMContentFragment contentFragmentModel = new DAMContentFragmentImpl(resource, contentTypeConverter,
						elementNames);
				if (ArrayUtils.isNotEmpty(cfTitles) && cfTitles.length > 1) {
					contentFragmentMap.put(contentFragmentModel.getTitle(), contentFragmentModel);
					if (!ArrayUtils.contains(cfTitles, contentFragmentModel.getTitle()))
						newCFTitles = ArrayUtils.add(newCFTitles, contentFragmentModel.getTitle());
				} else
					items.add(contentFragmentModel);
			}
			if (ArrayUtils.isNotEmpty(cfTitles) && cfTitles.length > 1) {
				for (String cfTitle : cfTitles) {
					if (contentFragmentMap.get(cfTitle) != null)
						items.add(contentFragmentMap.get(cfTitle));
					else
						cfTitles = ArrayUtils.removeElement(cfTitles, cfTitle);
				}
				if (ArrayUtils.isNotEmpty(newCFTitles)) {
					ArrayUtils.reverse(newCFTitles);
					for (String newCFTitle : newCFTitles) {
						items.add(contentFragmentMap.get(newCFTitle));
						cfTitles = ArrayUtils.add(cfTitles, newCFTitle);
					}
				}
				ModifiableValueMap cflValueMap = slingHttpServletRequest.getResource()
						.adaptTo(ModifiableValueMap.class);
				cflValueMap.put("cfTitles", cfTitles);
				resourceResolver.commit();
			}
		} catch (PersistenceException e) {
			e.printStackTrace();
		} finally {
			if (leakingResourceResolver != null) {
				// Always close the leaking query builder resource resolver
				leakingResourceResolver.close();
			}
		}
	}

	@Override
	public Collection<DAMContentFragment> getListItems() {
		return items;
	}

	@Override
	public String getExportedType() {
		return slingHttpServletRequest.getResource().getResourceType();
	}
}
