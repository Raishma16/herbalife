package com.herbalife.nutrition.sites.core.dtos;

import com.google.gson.Gson;
import com.herbalife.nutrition.sites.core.constants.SchemaConstants;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;

public class SchemaTO {

    private String type;
    private Map<String, Object> properties;

    public SchemaTO() {
        properties = new HashMap<>();
    }

    public SchemaTO(ResourceResolver resourceResolver, ArticleTO article) {
        this();
        type = SchemaConstants.ARTICLE_TYPE;
        properties.put("image", resourceResolver.map(article.getImage()));
        properties.put("author", createAuthor(article.getAuthor()));
        Map<String, Object> publisher = createOrganization();
        publisher.put("logo", createLogo(resourceResolver, article.getLogoPath()));
        properties.put("publisher", publisher);
        properties.put("headline", article.getTitle());
        properties.put("articleBody", article.getContent());
        properties.put("datePublished", article.getDatePublished());
    }

    public SchemaTO(ResourceResolver resourceResolver, ProductTO product) {
        this();
        type = SchemaConstants.PRODUCT_TYPE;
        properties.put("name", product.getTitle());
        properties.put("image", resourceResolver.map(product.getImage()));
        properties.put("description", product.getOverview());
        properties.put("sku", product.getSku());
        properties.put("brand", createOrganization());
        Matcher m = Pattern.compile("\\p{Sc}").matcher(product.getPrice());
        if (m.find()) {
            String symbol = m.group();
            String amount = product.getPrice().replaceAll(symbol, "");
            if (StringUtils.isNotBlank(symbol) && StringUtils.isNotBlank(amount)) {
                properties.put("offers", createOffers(amount, symbol));
            }
        }
    }

    public SchemaTO(ResourceResolver resourceResolver, RecipeTO recipe) {
        this();
        type = SchemaConstants.RECIPE_TYPE;
        properties.put("name", recipe.getTitle());
        properties.put("image", resourceResolver.map(recipe.getImage()));
        properties.put("description", recipe.getLongDescription());
        properties.put("recipeIngredient", recipe.getIngredients());
        properties.put("author", createAuthor(recipe.getAuthor()));
        properties.put("recipeInstructions", recipe.getPreparation());
        properties.put("recipeYield", recipe.getServes());
        properties.put("cookTime", createDuration(recipe.getCookTime()));
        properties.put("prepTime", createDuration(recipe.getPrepTime()));
        properties.put("nutrition", createNutrition(
                recipe.getCalories(), recipe.getFat(), recipe.getCarbohydrates(),
                recipe.getSugar(), recipe.getFiber(), recipe.getProtein()));
    }

    private String createDuration(String minutesString) {
        if (StringUtils.isNotBlank(minutesString)) {
            Duration duration = Duration.ofMinutes(Long.parseLong(minutesString));
            long hours = duration.toHours();
            duration = duration.minusHours(hours);
            long minutes = duration.toMinutes();
            return String.format("PT%sH%sM", hours, minutes);
        } else {
            return null;
        }
    }

    private Map<String, Object> createOrganization() {
        Map<String, Object> organization;
        organization = new HashMap<>();
        organization.put("@type", "Organization");
        organization.put("name", "Herbalife Nutrition");
        return organization;
    }

    private Map<String, Object> createAuthor(String authorName) {
        Map<String, Object> author = new HashMap<>();
        if (StringUtils.isNotBlank(authorName)) {
            author.put("@type", "Person");
            author.put("name", authorName);
            return author;
        } else {
            return createOrganization();
        }
    }

    private Map<String, Object> createLogo(ResourceResolver resourceResolver, String logoUrl) {
        Map<String, Object> logo = new HashMap<>();
        logo.put("@type", "ImageObject");
        logo.put("url", resourceResolver.map(logoUrl));
        return logo;
    }

    private Map<String, Object> createOffers(String amount, String currency) {
        Map<String, Object> offers = new HashMap<>();
        offers.put("priceCurrency", currency);
        offers.put("price", amount);
        offers.put("seller", createOrganization());
        return offers;
    }

    private Map<String, Object> createNutrition(String calories, String fat, String carbohydrates, String sugar, String fiber, String protein) {
        Map<String, Object> nutrition = new HashMap<>();
        nutrition.put("@type", "NutritionInformation");
        nutrition.put("calories", calories);
        nutrition.put("fatContent", fat);
        nutrition.put("carbohydrateContent", carbohydrates);
        nutrition.put("sugarContent", sugar);
        nutrition.put("fiberContent", fiber);
        nutrition.put("proteinContent", protein);
        return nutrition;
    }

    public boolean isValid() {
        return type != null && properties != null && !properties.isEmpty();
    }

    public String getType() {
        return type;
    }

    public String getJsonProperties() {
        Gson parser = new Gson();
        String jsonProperties = parser.toJson(properties);
        return jsonProperties.substring(1, jsonProperties.length() - 1);
    }
}
