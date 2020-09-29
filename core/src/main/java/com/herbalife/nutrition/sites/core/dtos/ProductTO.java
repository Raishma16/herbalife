package com.herbalife.nutrition.sites.core.dtos;

import com.adobe.cq.wcm.core.components.models.contentfragment.DAMContentFragment.DAMContentElement;
import com.herbalife.nutrition.sites.core.constants.ContentFragmentConstants;
import com.herbalife.nutrition.sites.core.helpers.AssetHelper;
import java.util.Map;
import org.apache.sling.api.resource.ResourceResolver;

public class ProductTO {

    public static final String IMAGE = "product-image";
    public static final String SKU = "sku";
    public static final String PRICE = "price";
    public static final String BUTTON_TEXT_LINK = "button-text-link";
    public static final String BUTTON_TEXT_LABEL = "button-label";
    public static final String OVERVIEW = "product-overview";

    private String title;
    private String titleLink;
    private String image;
    private String assetUuid;
    private String altText;
    private String sku;
    private String price;
    private String buttonTextLink;
    private String buttonLabel;
    private String overview;

    public ProductTO(ResourceResolver resourceResolver, Map<String, DAMContentElement> elementMap) {
        title = (String) elementMap.get(ContentFragmentConstants.TITLE_TEXT).getValue();
        titleLink = (String) elementMap.get(ContentFragmentConstants.TITLE_TEXT_LINK).getValue();
        image = (String) elementMap.get(IMAGE).getValue();
        assetUuid = AssetHelper.getUuidForImage(resourceResolver, image);
        altText = AssetHelper.getAltForImage(resourceResolver, image);
        sku = (String) elementMap.get(SKU).getValue();
        price = (String) elementMap.get(PRICE).getValue();
        buttonTextLink = (String) elementMap.get(BUTTON_TEXT_LINK).getValue();
        buttonLabel = (String) elementMap.get(BUTTON_TEXT_LABEL).getValue();
        overview = (String) elementMap.get(OVERVIEW).getValue();
    }

    public String getTitle() {
        return title;
    }

    public String getTitleLink() {
        return titleLink;
    }

    public String getImage() {
        return image;
    }

    public String getAssetUuid() {
        return assetUuid;
    }

    public String getAltText() {
        return altText;
    }

    public String getSku() {
        return sku;
    }

    public String getPrice() {
        return price;
    }

    public String getButtonTextLink() {
        return buttonTextLink;
    }
    
    public String getButtonLabel() {
        return buttonLabel;
    }

    public String getOverview() {
        return overview;
    }
}
