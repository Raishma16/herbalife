package com.herbalife.nutrition.sites.core.models;

import com.adobe.cq.wcm.core.components.models.contentfragment.DAMContentFragment;
import com.adobe.cq.wcm.core.components.models.contentfragment.DAMContentFragment.DAMContentElement;
import com.herbalife.nutrition.sites.core.constants.ContentFragmentConstants;
import com.herbalife.nutrition.sites.core.dtos.ArticleTO;
import com.herbalife.nutrition.sites.core.dtos.ProductTO;
import com.herbalife.nutrition.sites.core.dtos.RecipeTO;
import com.herbalife.nutrition.sites.core.dtos.SchemaTO;
import com.herbalife.nutrition.sites.core.helpers.SiteStructureHelper;
import org.apache.sling.api.SlingHttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = SlingHttpServletRequest.class)
public class FragmentDetailModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(FragmentDetailModel.class);

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private DAMContentFragment fragment;
    
    @Self(injectionStrategy = InjectionStrategy.REQUIRED)
    private SlingHttpServletRequest slingHttpServletRequest;
    
	@SlingObject
	private ResourceResolver resolver;

    private ArticleTO article;
    private ProductTO product;
    private RecipeTO recipe;
    private SchemaTO schema;
    private boolean apacregion;

    @PostConstruct
    private void init() {
        if (fragment.getElements() != null) {
            ResourceResolver resourceResolver = request.getResourceResolver();
            String logoPath = SiteStructureHelper.getLogoPath(request);

            Map<String, DAMContentElement> elements = new HashMap<>();
            fragment.getElements().forEach(damContentElement ->
                    elements.put(damContentElement.getName(), damContentElement)
            );
            switch (fragment.getType()) {
                case ContentFragmentConstants.CF_MODEL_ARTICLE:
                    article = new ArticleTO(resourceResolver, elements, logoPath);
                    schema = new SchemaTO(resourceResolver, article);
                    LOGGER.trace("The fragment is an Article.");
                    break;
                case ContentFragmentConstants.CF_MODEL_PRODUCT:
                    product = new ProductTO(resourceResolver, elements);
                    schema = new SchemaTO(resourceResolver, product);
                    LOGGER.trace("The fragment is a Product.");
                    break;
                case ContentFragmentConstants.CF_MODEL_RECIPE:
                    recipe = new RecipeTO(resourceResolver, elements);
                    schema = new SchemaTO(resourceResolver, recipe);
                    LOGGER.trace("The fragment is a Recipe.");
                    break;
            }
            
        	Resource requestPageResource = resolver.resolve(slingHttpServletRequest, slingHttpServletRequest.getPathInfo());
        	LOGGER.debug("ContentFragmentListModel: requestPageResource: "+requestPageResource.getPath());
        	if (requestPageResource.getPath().contains(ContentFragmentConstants.REGION_APAC)) {
        		apacregion = true;
        		LOGGER.debug("ContentFragmentListModel: apac region ");
        	}
        }
    }

    public boolean isArticleType() {
        return article != null;
    }

    public boolean isProductType() {
        return product != null;
    }

    public boolean isRecipeType() {
        return recipe != null;
    }

    public ArticleTO getArticle() {
        return article;
    }

    public ProductTO getProduct() {
        return product;
    }

    public RecipeTO getRecipe() {
        return recipe;
    }

    public SchemaTO getSchema() {
        return schema;
    }
    
    public boolean getApacRegion() {
        return apacregion;
    }
}
