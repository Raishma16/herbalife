package com.herbalife.nutrition.sites.core.models.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.inherit.ComponentInheritanceValueMap;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.ResultPage;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagConstants;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.herbalife.nutrition.sites.core.constants.SearchConstants;
import com.herbalife.nutrition.sites.core.dtos.FilterOptionTO;
import com.herbalife.nutrition.sites.core.dtos.ResultPageTO;
import com.herbalife.nutrition.sites.core.dtos.ResultTO;
import com.herbalife.nutrition.sites.core.helpers.SiteStructureHelper;
import com.herbalife.nutrition.sites.core.models.Search;
import com.herbalife.nutrition.sites.core.services.SearchService;

@Model(adaptables = SlingHttpServletRequest.class, adapters = Search.class, resourceType = SearchImpl.RESOURCE_TYPE)
public class SearchImpl implements Search {
	protected static final String RESOURCE_TYPE = "herbalifenutrition/components/content/search";
	private static final Logger LOG = LoggerFactory.getLogger(SearchImpl.class);
	@Self
	private SlingHttpServletRequest request;
	@SlingObject
	private ResourceResolver resolver;
	@Inject
	private SearchService searchService;
	private ComponentInheritanceValueMap valueMap;
	private List<ResultTO> results;
	private SearchResult result;
	private List<ResultPageTO> pagination;
	private List<FilterOptionTO> contentTypes = Collections.emptyList();
	private List<FilterOptionTO> categories = Collections.emptyList();
	private String searchTerm = "";
	private String contentType;
	private String[] category;
	private long resultStart;
	private long resultEnd;
	private long totalResults;
	private String subText;
	private String[] categoryTagNames;

	@PostConstruct
	private void init() {
		valueMap = new ComponentInheritanceValueMap(request.getResource());
		searchTerm = request.getParameter(SearchConstants.SEARCH_TERM);
		contentType = StringUtils.defaultIfBlank(request.getParameter(SearchConstants.CONTENT_TYPE),
				SearchConstants.DEFAULT_CONTENT_TYPE);
		category = request.getParameterValues(SearchConstants.CATEGORY);
		categoryTagNames = valueMap.get(SearchConstants.CATEGORY_TAG_NAMES, ArrayUtils.EMPTY_STRING_ARRAY);
		subText = valueMap.get("noResultsText", "");
		if (valueMap.get(SearchConstants.ENABLE_DOCUMENT_SEARCH, "false").equals("true")) {
			contentTypes = new ArrayList<>();
			for (String cType : SearchConstants.CONTENT_TYPES) {
				FilterOptionTO filterOptionTO = new FilterOptionTO(cType,
						valueMap.get(cType + "Label", StringUtils.capitalize(cType)), contentType.equals(cType));
				contentTypes.add(filterOptionTO);
			}
		}
		if (valueMap.get(SearchConstants.SHOW_CATEGORY_FILTER, "false").equals("true")) {
			categories = new ArrayList<>();
			TagManager tagManager = resolver.adaptTo(TagManager.class);
			Locale locale = SiteStructureHelper.getExtendedLocale(request);
			for (String categoryTagName : categoryTagNames) {
				Tag categoryTag = tagManager.resolve(categoryTagName);
				FilterOptionTO filterOptionTO = new FilterOptionTO(categoryTag.getTagID(),
						StringUtils.defaultIfBlank(categoryTag.getTitle(locale), categoryTag.getTitle()),
						ArrayUtils.contains(category, categoryTag.getTagID()));
				categories.add(filterOptionTO);
			}
		}
		if (StringUtils.isNotBlank(searchTerm)) {
			results = new ArrayList<>();
			result = searchService.search(getPredicates(), resolver);
			totalResults = result.getTotalMatches();
			if (totalResults > 0) {
				if (contentType.equals(SearchConstants.DEFAULT_CONTENT_TYPE)) {
					for (Hit hit : result.getHits()) {
						try {
							ValueMap valueMap = hit.getResource().getChild("jcr:content/data/master").getValueMap();
							Page page = resolver.adaptTo(PageManager.class)
									.getPage(valueMap.get("title-text-link", String.class));
							if (page != null) {
								ResultTO resultTO = new ResultTO(page.getPageTitle(), page.getDescription(),
										page.getPath() + ".html");
								results.add(resultTO);
							}
						} catch (RepositoryException e1) {
							e1.printStackTrace();
						}
					}
				} else {
					for (Hit hit : result.getHits()) {
						Asset asset = null;
						try {
							asset = hit.getResource().adaptTo(Asset.class);
						} catch (RepositoryException e) {
							e.printStackTrace();
						}
						ResultTO resultTO = new ResultTO(asset.getMetadataValue(SearchConstants.DOCUMENT_TITLE),
								asset.getMetadataValue(SearchConstants.DOCUMENT_DESCRIPTION), asset.getPath());
						results.add(resultTO);
					}
				}
				if (valueMap.get(SearchConstants.SHOW_PAGINATION, "false").equals("true")) {
					pagination = new ArrayList<ResultPageTO>();
					long hitNum = result.getHitsPerPage();
					if (result.getResultPages().size() > 1) {
						for (ResultPage page : result.getResultPages()) {
							ResultPageTO paginationTO = new ResultPageTO(page.getStart(),
									String.valueOf((page.getStart() / hitNum) + 1), page.isCurrentPage());
							pagination.add(paginationTO);
							if (page.isCurrentPage()) {
								resultStart = page.getStart() + 1;
								resultEnd = page.getStart() + hitNum;
								if (resultEnd > totalResults)
									resultEnd = totalResults;
							}
						}
					} else {
						resultStart = 1;
						resultEnd = totalResults;
					}
					subText = valueMap.get("resultsRangeText", "").replace("<range>", resultStart + "-" + resultEnd)
							.replace("<total>", String.valueOf(totalResults));
				} else
					subText = valueMap.get("totalResultsText", "").replace("<total>", String.valueOf(totalResults));
			}
		}
	}

	public HashMap<String, Object> getPredicates() {
		HashMap<String, Object> predicates = new HashMap<>();
		predicates.put("fulltext", searchTerm);
		predicates.put("type", DamConstants.NT_DAM_ASSET);
		predicates.put("tagid.property", SearchConstants.METADATA_NODE_NAME + TagConstants.PN_TAGS);
		predicates.put("property", SearchConstants.METADATA_NODE_NAME + JcrConstants.JCR_MIXINTYPES);
		predicates.put("property.value", TagConstants.NT_TAGGABLE);
		if (contentType.equals(SearchConstants.DEFAULT_CONTENT_TYPE)) {
			predicates.put("path", valueMap.get(SearchConstants.CONTENT_FRAGMENT_SEARCH_PATH,
					SearchConstants.DEFAULT_CONTENT_FRAGMENT_SEARCH_PATH));
			predicates.put("1_property", JcrConstants.JCR_CONTENT + "/data/cq:model");
			predicates.put("1_property.operation", "exists");
		} else {
			predicates.put("path",
					valueMap.get(SearchConstants.DOCUMENT_SEARCH_PATH, SearchConstants.DEFAULT_DOCUMENT_SEARCH_PATH));
			predicates.put("1_property", SearchConstants.METADATA_NODE_NAME + DamConstants.DC_FORMAT);
			for (int i = 0; i < SearchConstants.DOCUMENT_FORMAT_PROPERTY_VALUES.length; i++)
				predicates.put("1_property." + (i + 1) + "_value", SearchConstants.DOCUMENT_FORMAT_PROPERTY_VALUES[i]);
		}
		if (ArrayUtils.isEmpty(category)) {
			for (int i = 0; i < categoryTagNames.length; i++)
				predicates.put("tagid." + (i + 1) + "_value", categoryTagNames[i]);
		} else {
			for (int i = 0; i < category.length; i++)
				predicates.put("tagid." + (i + 1) + "_value", category[i]);
		}
		if (valueMap.get(SearchConstants.SHOW_PAGINATION, "false").equals("true")) {
			predicates.put("p.offset", request.getParameter(SearchConstants.OFFSET));
			predicates.put("p.limit",
					valueMap.get(SearchConstants.PAGINATION_LIMIT, SearchConstants.DEFAULT_RESULTS_LIMIT));
		} else
			predicates.put("p.limit", SearchConstants.DEFAULT_ALL_RESULTS_LIMIT);
		return predicates;
	}

	@Override
	public boolean isEmpty() {
		if (StringUtils.isBlank(valueMap.get(SearchConstants.CONTENT_FRAGMENT_SEARCH_PATH, ""))) {
			LOG.warn("Content Fragment Search Path should always be configured");
			return true;
		} else if (ArrayUtils.isEmpty(categoryTagNames)) {
			LOG.warn("Category Tags should always be configured");
			return true;
		} else if (valueMap.get(SearchConstants.ENABLE_DOCUMENT_SEARCH, "false").equals("true")
				&& StringUtils.isBlank(valueMap.get(SearchConstants.DOCUMENT_SEARCH_PATH, ""))) {
			LOG.warn("Document Search Path cannot be empty when Enable Document Search is checked");
			return true;
		} else
			return false;
	}

	@Override
	public String getSearchTerm() {
		return searchTerm;
	}

	@Override
	public String getTitleText() {
		return StringUtils.replace(valueMap.get("searchResultsText", ""), "<search-term>", searchTerm);
	}

	@Override
	public String getSubText() {
		return subText;
	}

	@Override
	public List<ResultTO> getResults() {
		return results;
	}

	@Override
	public List<ResultPageTO> getPagination() {
		return pagination;
	}

	@Override
	public List<FilterOptionTO> getContentTypes() {
		return contentTypes;
	}

	@Override
	public List<FilterOptionTO> getCategories() {
		return categories;
	}

}
