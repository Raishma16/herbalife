package com.herbalife.nutrition.sites.core.dtos;

import com.adobe.cq.wcm.core.components.models.contentfragment.DAMContentFragment.DAMContentElement;
import com.herbalife.nutrition.sites.core.constants.ContentFragmentConstants;
import com.herbalife.nutrition.sites.core.helpers.AssetHelper;
import java.util.Map;
import org.apache.sling.api.resource.ResourceResolver;

public class RecipeTO { 

    public static final String AUTHOR = "recipe-author";
    public static final String IMAGE = "recipe-image";
    public static final String SHORT_DESCRIPTION = "short-description";
    public static final String LONG_DESCRIPTION = "long-description";
    public static final String SERVES = "serves";
    public static final String PREP_TIME = "prep-time";
    public static final String COOK_TIME = "cook-time";
    public static final String INGREDIENTS = "ingredients-list";
    public static final String PREPARATION = "preparation";
    public static final String CALORIES = "calories";
    public static final String FAT = "fat";
    public static final String CARBOHYDRATES = "carbohydrates";
    public static final String SUGAR = "sugar";
    public static final String FIBER = "fiber";
    public static final String PROTEIN = "protein";
    public static final String ANNOTATIONS = "annotations";

    private String title;
    private String titleLink;
    private String author;
    private String image;
    private String assetUuid;
    private String altText;
    private String shortDescription;
    private String longDescription;
    private String serves;
    private String prepTime;
    private String cookTime;
    private String[] ingredients;
    private String preparation;
    private String calories;
    private String fat;
    private String carbohydrates;
    private String sugar;
    private String fiber;
    private String protein;
    private String annotations;
    private String readmoreTextLink;

    public RecipeTO(ResourceResolver resourceResolver, Map<String, DAMContentElement> elementMap) {
        title = (String) elementMap.get(ContentFragmentConstants.TITLE_TEXT).getValue();
        titleLink = (String) elementMap.get(ContentFragmentConstants.TITLE_TEXT_LINK).getValue();
        author = (String) elementMap.get(AUTHOR).getValue();
        image = (String) elementMap.get(IMAGE).getValue();
        assetUuid = AssetHelper.getUuidForImage(resourceResolver, image);
        altText = AssetHelper.getAltForImage(resourceResolver, image);
        shortDescription = (String) elementMap.get(SHORT_DESCRIPTION).getValue();
        longDescription = (String) elementMap.get(LONG_DESCRIPTION).getValue();
        serves = (String) elementMap.get(SERVES).getValue();
        prepTime = (String) elementMap.get(PREP_TIME).getValue();
        cookTime = (String) elementMap.get(COOK_TIME).getValue();
        ingredients = (String[]) elementMap.get(INGREDIENTS).getValue();
        preparation = (String) elementMap.get(PREPARATION).getValue();
        calories = (String) elementMap.get(CALORIES).getValue();
        fat = (String) elementMap.get(FAT).getValue();
        carbohydrates = (String) elementMap.get(CARBOHYDRATES).getValue();
        sugar = (String) elementMap.get(SUGAR).getValue();
        fiber = (String) elementMap.get(FIBER).getValue();
        protein = (String) elementMap.get(PROTEIN).getValue();
        annotations = (String) elementMap.get(ANNOTATIONS).getValue();
        readmoreTextLink = (String) elementMap.get(ContentFragmentConstants.READMORE_TEXT_LINK).getValue();
    }

    public String getTitle() {
        return title;
    }

    public String getTitleLink() {
        return titleLink;
    }

    public String getAuthor() {
        return author;
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

    public String getShortDescription() {
        return shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public String getServes() {
        return serves;
    }

    public String getPrepTime() {
        return prepTime;
    }

    public String getCookTime() {
        return cookTime;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public String getPreparation() {
        return preparation;
    }

    public String getCalories() {
        return calories;
    }

    public String getFat() {
        return fat;
    }

    public String getCarbohydrates() {
        return carbohydrates;
    }

    public String getSugar() {
        return sugar;
    }

    public String getFiber() {
        return fiber;
    }

    public String getProtein() {
        return protein;
    }

    public String getAnnotations() {
        return annotations;
    }

    public String getReadmoreTextLink() {
        return readmoreTextLink;
    }
}
