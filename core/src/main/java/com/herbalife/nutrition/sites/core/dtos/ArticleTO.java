package com.herbalife.nutrition.sites.core.dtos;

import com.adobe.cq.wcm.core.components.models.contentfragment.DAMContentFragment.DAMContentElement;
import com.herbalife.nutrition.sites.core.constants.ContentFragmentConstants;
import com.herbalife.nutrition.sites.core.constants.SchemaConstants;
import com.herbalife.nutrition.sites.core.helpers.AssetHelper;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import org.apache.sling.api.resource.ResourceResolver;

public class ArticleTO { 

    public static final String IMAGE = "article-image";
    public static final String DATE_PUBLISHED = "date-published";
    public static final String AUTHOR = "article-author";
    public static final String DESCRIPTION = "article-description";
    public static final String CONTENT = "article-content";

    private String title;
    private String titleLink;
    private String image;
    private String assetUuid;
    private String altText;
    private String datePublished;
    private String author;
    private String description;
    private String content;
    private String readmoreTextLink;
    private String logoPath;

    public ArticleTO(ResourceResolver resourceResolver, Map<String, DAMContentElement> elementMap, String logoPath) {
        title = (String) elementMap.get(ContentFragmentConstants.TITLE_TEXT).getValue();
        titleLink = (String) elementMap.get(ContentFragmentConstants.TITLE_TEXT_LINK).getValue();
        image = (String) elementMap.get(IMAGE).getValue();
        assetUuid = AssetHelper.getUuidForImage(resourceResolver, image);
        altText = AssetHelper.getAltForImage(resourceResolver, image);
        Calendar datePublishedCalendar = (Calendar) elementMap.get(DATE_PUBLISHED).getValue();
        if (datePublishedCalendar != null) {
            datePublished = new SimpleDateFormat(SchemaConstants.DATE_FORMAT).format(datePublishedCalendar.getTime());
        }
        author = (String) elementMap.get(AUTHOR).getValue();
        description = (String) elementMap.get(DESCRIPTION).getValue();
        content = (String) elementMap.get(CONTENT).getValue();
        readmoreTextLink = (String) elementMap.get(ContentFragmentConstants.READMORE_TEXT_LINK).getValue();
        this.logoPath = logoPath;
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

    public String getDatePublished() {
        return datePublished;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getContent() {
        return content;
    }

    public String getReadmoreTextLink() {
        return readmoreTextLink;
    }

    public String getLogoPath() {
        return logoPath;
    }
}
