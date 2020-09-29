package com.herbalife.nutrition.sites.core.constants;

import com.day.cq.commons.jcr.JcrConstants;

public class SearchConstants {
	// search component path
	public static final String SEARCH_COMPONENT_PATH = "/jcr:content/root/responsivegrid/search";
	// dialog properties
	public static final String CONTENT_FRAGMENT_SEARCH_PATH = "contentFragmentSearchPath";
	public static final String CATEGORY_TAG_NAMES = "categoryTagNames";
	public static final String SUGGESTIONS_LIMIT = "suggestionsLimit";
	public static final String ENABLE_DOCUMENT_SEARCH = "enableDocumentSearch";
	public static final String DOCUMENT_SEARCH_PATH = "documentSearchPath";
	public static final String SHOW_CATEGORY_FILTER = "showCategoryFilter";
	public static final String SHOW_PAGINATION = "showPagination";
	public static final String PAGINATION_LIMIT = "paginationLimit";

	// predicate defaults
	public static final String DEFAULT_CONTENT_FRAGMENT_SEARCH_PATH = "/content/dam/regional/apac";
	public static final String DEFAULT_DOCUMENT_SEARCH_PATH = "/content/dam/regional";
	public static final String DEFAULT_CONTENT_TYPE = "pages";
	public static final String DEFAULT_RESULTS_LIMIT = "10";
	public static final String DEFAULT_ALL_RESULTS_LIMIT = "-1";
	public static final String DEFAULT_SUGGESTIONS_LIMIT = "5";
	public static final String METADATA_NODE_NAME = JcrConstants.JCR_CONTENT + "/metadata/";
	public static final String CF_TITLE_PROPERTY_NAME = JcrConstants.JCR_CONTENT + "/data/master/@title-text";
	public static final String[] DOCUMENT_FORMAT_PROPERTY_VALUES = { "application/pdf",
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document" };

	// query parameters
	public static final String SEARCH_TERM = "searchTerm";
	public static final String CONTENT_TYPE = "contentType";
	public static final String CATEGORY = "category";
	public static final String OFFSET = "offset";
	public static final String[] CONTENT_TYPES = { "pages", "documents" };

	// document metadata properties
	public static final String DOCUMENT_TITLE = "dc:title";
	public static final String DOCUMENT_DESCRIPTION = "dc:description";
}
