package com.herbalife.nutrition.sites.core.helpers;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.ContentVariation;
import com.adobe.cq.dam.cfm.FragmentData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentFragmentHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentFragmentHelper.class);

    /**
     * Returns the variation value if exist otherwise the master one.
     */
    public static Object getValueByVariation(ContentFragment contentFragment, String elementName, String variation) {
        FragmentData value;
        ContentElement contentElement = contentFragment.getElement(elementName);
        ContentVariation contentVariation = contentElement.getVariation(variation);
        if (contentVariation == null) {
            value = contentElement.getValue();
        } else {
            value = contentVariation.getValue();
        }
        return value == null ? null : value.getValue();
    }
}
