package com.herbalife.nutrition.sites.core.helpers;

import com.day.cq.commons.LanguageUtil;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.herbalife.nutrition.sites.core.constants.HeaderFooterConstants;
import com.herbalife.nutrition.sites.core.constants.PathConstants;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SiteStructureHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SiteStructureHelper.class);

    public static String getLogoPath(SlingHttpServletRequest request) {
        ResourceResolver resourceResolver = request.getResourceResolver();
        ContentPolicyManager contentPolicyManager = resourceResolver.adaptTo(ContentPolicyManager.class);
        Resource headerResource = getHomeResource(SiteStructureHelper.getPageResource(request)).getChild(
                PathConstants.generatePath(false, null, JcrConstants.JCR_CONTENT, PathConstants.ROOT, PathConstants.HEADER));
        ContentPolicy headerPolicy = contentPolicyManager.getPolicy(headerResource);
        String logoPath = headerPolicy.getProperties().get(PathConstants.LOGOPATH, String.class);
        return logoPath;
    }

    public static String getLanguageCountryFromPath(SlingHttpServletRequest request) {
        Resource pageResource = getPageResource(request);
        String languageRoot = LanguageUtil.getLanguageRoot(pageResource.getPath());
        if (StringUtils.isBlank(languageRoot)) {
            return null;
        } else {
            String[] languageRootPathSplit = languageRoot.split("/");
            return languageRootPathSplit[languageRootPathSplit.length - 1];
        }
    }

    public static Locale getExtendedLocale(SlingHttpServletRequest request) {
        String languageCountryCode = getLanguageCountryFromPath(request);
        if (StringUtils.isNotBlank(languageCountryCode)) {
            return LanguageUtil.getLocale(languageCountryCode);
        } else {
            return null;
        }
    }

    public static Resource getPageResource(SlingHttpServletRequest request) {
        return request.getResourceResolver().resolve(request, request.getPathInfo());
    }

    public static Resource getHomeResource(Resource pageResource) {
        return pageResource.getResourceResolver().getResource(LanguageUtil.getLanguageRoot(pageResource.getPath()) + "/home");
    }

    public static Resource getRootChildResource(SlingHttpServletRequest request, String resourceSubpath) {
        Resource rootChildResource = null;

        Resource homeResource = getHomeResource(getPageResource(request));
        try {
            if (homeResource != null) {
                rootChildResource = homeResource.getChild(JcrConstants.JCR_CONTENT).getChild(PathConstants.ROOT).getChild(resourceSubpath);
            }
        } catch (NullPointerException e) {
            LOGGER.error("No child {} generated for home page {}", resourceSubpath, homeResource.getPath());
        }

        return rootChildResource;
    }

    public static Resource getHeaderResource(SlingHttpServletRequest request) {
        return getRootChildResource(request, HeaderFooterConstants.HEADER_SUBPATH);
    }

    public static Resource getFooterResource(SlingHttpServletRequest request) {
        return getRootChildResource(request, HeaderFooterConstants.FOOTER_SUBPATH);
    }
}
