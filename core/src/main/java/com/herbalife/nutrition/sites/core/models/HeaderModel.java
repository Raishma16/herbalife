package com.herbalife.nutrition.sites.core.models;

import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.day.cq.commons.LanguageUtil;
import com.herbalife.nutrition.sites.core.constants.HeaderFooterConstants;
import com.herbalife.nutrition.sites.core.constants.SearchConstants;
import com.herbalife.nutrition.sites.core.helpers.ContentFragmentHelper;
import com.herbalife.nutrition.sites.core.helpers.SiteStructureHelper;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeaderModel {

	private static final Logger LOG = LoggerFactory.getLogger(HeaderModel.class);

	@Self
	private SlingHttpServletRequest request;

	@Inject
	private Resource resource;

	@SlingObject
	private ResourceResolver resolver;

	@Inject
	@Named(HeaderFooterConstants.HEADER_LINKS_CF_PATH)
	@Via("resource")
	private String headerLinks;

	@Inject
	@Named(HeaderFooterConstants.HEADER_ENABLE_SEARCH)
	@Via("resource")
	private String enableSearch;

	private String memberLogin;
	private String findMember;
	private String regionLinks;
	private String countryName;
	private String searchPagePath;
	private String searchFieldPlaceholder;
	private String searchButtonLabel;
	private String contentFragmentSearchPath;
	private String[] categoryTagNames;
	private String suggestionsLimit;

	@PostConstruct
	private void init() {
		// If not Home, get Home settings
		if (headerLinks == null) {
			Resource footerResource = SiteStructureHelper.getHeaderResource(request);
			if (footerResource != null) {
				headerLinks = (String) footerResource.getValueMap().get(HeaderFooterConstants.HEADER_LINKS_CF_PATH);
			}
		}

		if (headerLinks != null) {
			// Get ContentFragments
			Resource headerLinksResource = request.getResourceResolver().getResource(headerLinks);
			if (headerLinksResource != null) {
				ContentFragment headerLinksFragment = headerLinksResource.adaptTo(ContentFragment.class);

				// If variation site, get variation values
				String languageCountryCode = SiteStructureHelper.getLanguageCountryFromPath(request);

				if (StringUtils.isNotBlank(languageCountryCode)) {
					memberLogin = (String) ContentFragmentHelper.getValueByVariation(headerLinksFragment,
							HeaderFooterConstants.HEADER_CF_MEMBER_LOGIN, languageCountryCode);
					findMember = (String) ContentFragmentHelper.getValueByVariation(headerLinksFragment,
							HeaderFooterConstants.HEADER_CF_FIND_MEMBER, languageCountryCode);
					regionLinks = (String) ContentFragmentHelper.getValueByVariation(headerLinksFragment,
							HeaderFooterConstants.HEADER_CF_REGION_LINKS, languageCountryCode);
				}
			}
		}

		if (enableSearch == null) {
			Resource headerResource = SiteStructureHelper.getHeaderResource(request);
			if (headerResource != null) {
				enableSearch = (String) headerResource.getValueMap().get(HeaderFooterConstants.HEADER_ENABLE_SEARCH);
			}
		}

		Locale extendedLocale = SiteStructureHelper.getExtendedLocale(request);
		if (extendedLocale != null) {
			countryName = extendedLocale.getDisplayCountry(extendedLocale);
		} else {
			countryName = request.getLocale().getDisplayCountry(request.getLocale());
		}
		if (enableSearch != null) {
			String headerResourcePath = SiteStructureHelper.getHeaderResource(request).getPath();
			searchPagePath = LanguageUtil.getLanguageRoot(headerResourcePath) + "/"
					+ HeaderFooterConstants.HEADER_SEARCH_PATH;
			Resource searchComponentResource = resolver
					.getResource(searchPagePath + SearchConstants.SEARCH_COMPONENT_PATH);
			if (searchComponentResource != null) {
				ValueMap valueMap = searchComponentResource.getValueMap();
				searchFieldPlaceholder = valueMap.get("searchFieldPlaceholder", "");
				searchButtonLabel = valueMap.get("searchButtonLabel", "");
				contentFragmentSearchPath = valueMap.get(SearchConstants.CONTENT_FRAGMENT_SEARCH_PATH, "");
				categoryTagNames = valueMap.get(SearchConstants.CATEGORY_TAG_NAMES, ArrayUtils.EMPTY_STRING_ARRAY);
				suggestionsLimit = valueMap.get(SearchConstants.SUGGESTIONS_LIMIT,
						SearchConstants.DEFAULT_SUGGESTIONS_LIMIT);
			} else
				LOG.warn("Search component should be configured in Search Results page for search to work");
		}
	}

	public String getMemberLogin() {
		return memberLogin;
	}

	public String getFindMember() {
		return findMember;
	}

	public String getRegionLinks() {
		return regionLinks;
	}

	public String getCountryName() {
		return countryName;
	}

	public String getEnableSearch() {
		return enableSearch;
	}

	public String getSearchPagePath() {
		return searchPagePath;
	}

	public String getSearchFieldPlaceholder() {
		return searchFieldPlaceholder;
	}

	public String getSearchButtonLabel() {
		return searchButtonLabel;
	}

	public String getContentFragmentSearchPath() {
		return contentFragmentSearchPath;
	}

	public String[] getCategoryTagNames() {
		return categoryTagNames;
	}

	public String getSuggestionsLimit() {
		return suggestionsLimit;
	}
}
