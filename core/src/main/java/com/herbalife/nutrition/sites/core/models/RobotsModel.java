package com.herbalife.nutrition.sites.core.models;

import com.herbalife.nutrition.sites.core.constants.SEOConstants;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

@Model(
        adaptables = {Resource.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class RobotsModel {

    @Self
    protected Resource resource;

    @Inject
    @Default(values = SEOConstants.ROBOTS_INDEX_TAG)
    @Named(SEOConstants.ROBOTS_NOINDEX_PROPERTY)
    private String indexPolicy;

    @Inject
    @Default(values = SEOConstants.ROBOTS_FOLLOW_TAG)
    @Named(SEOConstants.ROBOTS_NOFOLLOW_PROPERTY)
    private String followPolicy;

    public String getIndexPolicy() {
        return indexPolicy;
    }

    public String getFollowPolicy() {
        return followPolicy;
    }
}
