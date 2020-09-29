package com.herbalife.nutrition.sites.core.models;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.herbalife.nutrition.sites.core.constants.HeaderFooterConstants;
import com.herbalife.nutrition.sites.core.helpers.ContentFragmentHelper;
import com.herbalife.nutrition.sites.core.helpers.SiteStructureHelper;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;

@Model(
        adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class FooterModel {

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Resource resource;

    @Inject
    @Via("resource")
    private String copyright;

    @Inject
    @Named(HeaderFooterConstants.FOOTER_SOCIAL_NETWORK_CF_PATH)
    @Via("resource")
    private String socialNetwork;

    private String copyrightText;
    private String[] legalImagesPaths;
    private String facebook;
    private String twitter;
    private String instagram;
    private String youtube;

    @PostConstruct
    private void init() {
        // If not Home, get Home settings
        if (copyright == null || socialNetwork == null) {
            Resource footerResource = SiteStructureHelper.getFooterResource(request);
            if (footerResource != null) {
                copyright = (String) footerResource.getValueMap().get(HeaderFooterConstants.FOOTER_COPYRIGHT_CF_PATH);
                socialNetwork = (String) footerResource.getValueMap().get(HeaderFooterConstants.FOOTER_SOCIAL_NETWORK_CF_PATH);
            }
        }

        // If variation site, get variation values
        String languageCountryCode = SiteStructureHelper.getLanguageCountryFromPath(request);

        if (copyright != null) {
            // Get ContentFragments
            Resource copyrightResource = request.getResourceResolver().getResource(copyright);
            if (copyrightResource != null) {
                ContentFragment copyrightFragment = copyrightResource.adaptTo(ContentFragment.class);

                if (StringUtils.isNotBlank(languageCountryCode)) {
                    copyrightText = (String) ContentFragmentHelper.getValueByVariation(copyrightFragment, HeaderFooterConstants.FOOTER_CF_COPYRIGHT_TEXT, languageCountryCode);
                    legalImagesPaths = (String[]) ContentFragmentHelper.getValueByVariation(copyrightFragment, HeaderFooterConstants.FOOTER_CF_LEGAL_IMAGES, languageCountryCode);
                }
            }
        }

        if (socialNetwork != null) {
            // Get ContentFragments
            Resource socialnetworkResource = request.getResourceResolver().getResource(socialNetwork);
            if (socialnetworkResource != null) {
                ContentFragment socialnetworkFragment = socialnetworkResource.adaptTo(ContentFragment.class);

                if (StringUtils.isNotBlank(languageCountryCode)) {
                    facebook = (String) ContentFragmentHelper.getValueByVariation(socialnetworkFragment, HeaderFooterConstants.FOOTER_CF_FACEBOOK, languageCountryCode);
                    twitter = (String) ContentFragmentHelper.getValueByVariation(socialnetworkFragment, HeaderFooterConstants.FOOTER_CF_TWITTER, languageCountryCode);
                    instagram = (String) ContentFragmentHelper.getValueByVariation(socialnetworkFragment, HeaderFooterConstants.FOOTER_CF_INSTAGRAM, languageCountryCode);
                    youtube = (String) ContentFragmentHelper.getValueByVariation(socialnetworkFragment, HeaderFooterConstants.FOOTER_CF_YOUTUBE, languageCountryCode);
                }
            }
        }

    }

    public String getCopyrightText() {
        return copyrightText;
    }

    public String[] getLegalImages() {
        return legalImagesPaths;
    }

    public String getFacebook() {
        return facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getInstagram() {
        return instagram;
    }

    public String getYoutube() {
        return youtube;
    }
}
