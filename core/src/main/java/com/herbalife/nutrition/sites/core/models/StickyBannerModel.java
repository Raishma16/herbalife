package com.herbalife.nutrition.sites.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;


@Model(
        adaptables = {SlingHttpServletRequest.class},
        resourceType = {StickyBannerModel.RESOURCE_TYPE},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)

public class StickyBannerModel {
		
	    protected static final String RESOURCE_TYPE = "herbalifenutrition/components/content/stickybanner";
         
        @ValueMapValue
        private String link;
        
        @ValueMapValue
        private String shortMessage;
        
        @ValueMapValue
        private String buttonLabel;
            
        public String getMessage() {
            return shortMessage;
        }
        
        public String getLink() {
            return link;
        }
        
        public String getLabel() {
            return buttonLabel;
        }

}