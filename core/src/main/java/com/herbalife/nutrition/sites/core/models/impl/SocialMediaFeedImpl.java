package com.herbalife.nutrition.sites.core.models.impl;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.herbalife.nutrition.sites.core.models.SocialMediaFeed;
import com.herbalife.nutrition.sites.core.services.SocialMediaFeedService;

@Model(adaptables = { SlingHttpServletRequest.class }, adapters = { SocialMediaFeed.class }, resourceType = {
		SocialMediaFeedImpl.RESOURCE_TYPE }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SocialMediaFeedImpl implements SocialMediaFeed {
	protected static final String RESOURCE_TYPE = "herbalifenutrition/components/content/socialmediafeed";
	private static final Logger LOG = LoggerFactory.getLogger(SocialMediaFeedImpl.class);
	private static final String TABS = "&tabs=timeline";
	private static final String SMALL_HEADER = "&small_header=";
	private static final String HIDE_COVER = "&hide_cover=";
	private String fbFeedPluginURL;

	@ValueMapValue
	private String fbPageName;

	@ValueMapValue
	@Default(values = "false")
	private String showSmallHeader;

	@ValueMapValue
	@Default(values = "false")
	private String hideCover;

	@Inject
	SocialMediaFeedService socialMediaFeedService;

	@PostConstruct
	protected void init() {
		fbFeedPluginURL = socialMediaFeedService.getFBFeedPluginURL() + fbPageName + TABS + SMALL_HEADER
				+ showSmallHeader + HIDE_COVER + hideCover;
	}

	public String getFBFeedPluginURL() {
		return fbFeedPluginURL;
	}

	@Override
	public boolean isEmpty() {
		if (StringUtils.isBlank(fbPageName)) {
			LOG.warn("Facebook page name should be configured to display the Facebook feed");
			return true;
		} else
			return false;
	}
}
