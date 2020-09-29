package com.herbalife.nutrition.sites.core.services.impl;

import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import com.herbalife.nutrition.sites.core.annotations.SocialMediaFeedConfig;
import com.herbalife.nutrition.sites.core.services.SocialMediaFeedService;

@Component(service = SocialMediaFeedService.class, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = SocialMediaFeedConfig.class)
public class SocialMediaFeedServiceImpl implements SocialMediaFeedService {

	private SocialMediaFeedConfig config;

	@Reference
	private SlingSettingsService settings;

	@Activate
	public void activate(SocialMediaFeedConfig config) {
		this.config = config;
	}

	public String getFBFeedPluginURL() {
		return config.fbFeedPluginURL();
	}
}
