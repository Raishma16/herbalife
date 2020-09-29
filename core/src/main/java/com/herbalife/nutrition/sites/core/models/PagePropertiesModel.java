package com.herbalife.nutrition.sites.core.models;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.herbalife.nutrition.sites.core.constants.PagePropertiesConstants;


@Model(
        adaptables = {Resource.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)

public class PagePropertiesModel {
	
	private static final Logger logger = LoggerFactory.getLogger(PagePropertiesModel.class);
     
	@ValueMapValue
	@Named(PagePropertiesConstants.PAGE_META_TITLE)
	private String metaTitle;
	
	public String getMetaTitle() {
        return metaTitle;
    }

}
