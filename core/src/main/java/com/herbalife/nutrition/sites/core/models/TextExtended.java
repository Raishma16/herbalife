package com.herbalife.nutrition.sites.core.models;


public interface TextExtended {
	
	 /**
     * Name of the resource property that defines whether or not the teaser has Call-to-Action elements
     *
     * @since com.adobe.cq.wcm.core.components.models 12.4.0
     */
    String PN_READMORE_ENABLED = "readMoreEnabled";
    

    /**
     * Retrieves enable read more value.
     *
     * @return flag for read more enabled, set false as default.
     */
    default boolean isReadMoreEnabled() {
        throw new UnsupportedOperationException();
    }
    
}
