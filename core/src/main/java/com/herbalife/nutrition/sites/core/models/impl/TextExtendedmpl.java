package com.herbalife.nutrition.sites.core.models.impl;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.herbalife.nutrition.sites.core.models.TextExtended;


@Model(
        adaptables = {SlingHttpServletRequest.class},
        adapters = {TextExtended.class},
        resourceType = {TextExtendedmpl.RESOURCE_TYPE},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)

public class TextExtendedmpl implements TextExtended {
		
		private static final Logger LOGGER = LoggerFactory.getLogger(TextExtendedmpl.class);
	    protected static final String RESOURCE_TYPE = "herbalifenutrition/components/content/text";
	    
	    private boolean readMoreEnabled = false;
	    
	    @Inject
	    private Resource resource;
         
        @ValueMapValue
        private String readMoreText;
       
        
        @PostConstruct
        private void initModel() {
        	 ValueMap properties = resource.getValueMap();
        	 readMoreEnabled = properties.get(TextExtended.PN_READMORE_ENABLED, readMoreEnabled);
        	 LOGGER.debug("VALUE FOR READ MORE: "+ readMoreEnabled);
        }
        
        @Override
        public boolean isReadMoreEnabled() {
            return readMoreEnabled;
        }

}
