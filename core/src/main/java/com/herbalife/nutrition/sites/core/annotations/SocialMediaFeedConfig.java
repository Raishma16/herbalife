package com.herbalife.nutrition.sites.core.annotations;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Social Media Feed Configuration", description = "Service Configuration")
public @interface SocialMediaFeedConfig {
	@AttributeDefinition(name = "Facebook Feed Plugin URL", description = "Configuration value", defaultValue = "https://www.facebook.com/plugins/page.php?href=https://www.facebook.com/")
	String fbFeedPluginURL();
}
