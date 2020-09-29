package com.herbalife.nutrition.sites.core.helpers;

import com.day.cq.dam.api.Asset;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssetHelper { 

    private static final Logger LOGGER = LoggerFactory.getLogger(AssetHelper.class);

    private AssetHelper() { }

    public static String getUuidForImage(ResourceResolver resourceResolver, String assetPath) {
        String uuid="";
        if (StringUtils.isNotEmpty(assetPath)) {
            final Resource assetResource = resourceResolver.getResource(assetPath);
            if (assetResource != null) {
                Asset asset = assetResource.adaptTo(Asset.class);
                if (asset != null) {
                    uuid = asset.getID();
                }
                else {
                    LOGGER.error("Unable to adapt resource '{}' to an asset.", assetPath);
                }
            }
            else {
                LOGGER.error("Unable to find resource '{}'.", assetPath);
            }
        }
        return uuid;
    }
    
    public static String getAltForImage(ResourceResolver resourceResolver, String assetPath) {
        String alt = "";
        if (StringUtils.isNotEmpty(assetPath)) {
            // the image is coming from DAM
            final Resource assetResource = resourceResolver.getResource(assetPath);
            if (assetResource != null) {
                Asset asset = assetResource.adaptTo(Asset.class);
                if (asset != null) {
                    alt = asset.getMetadataValue("dc:description");
                } else {
                    LOGGER.error("Unable to adapt resource '{}' used by element '{}' to an asset.", assetPath);
                }
            } else {
                LOGGER.error("Unable to find resource '{}' used by image '{}'.", assetPath);
            }

        }
        return alt;
    }

}
