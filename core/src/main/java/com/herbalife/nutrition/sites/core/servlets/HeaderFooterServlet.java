package com.herbalife.nutrition.sites.core.servlets;

import com.day.cq.commons.jcr.JcrConstants;
import com.herbalife.nutrition.sites.core.constants.PathConstants;
import com.herbalife.nutrition.sites.core.constants.ServletConstants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(configurationPolicy = ConfigurationPolicy.REQUIRE, service = Servlet.class,
        property = {
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + "=" + HeaderFooterServlet.RESOURCE_TYPE
        })
public class HeaderFooterServlet extends SlingSafeMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeaderFooterServlet.class);

    public static final String RESOURCE_TYPE = "herbalifenutrition/multilanguage";
    private static final String LANGUAGE_COUNTRY_PARAMETER = "languageCountry";

    // Error messages
    private static final String NOT_IMPLEMENTED_ERROR_MSG = "Element requested not implemented.";
    private static final String BAD_REQUEST_ERROR_MSG = "The parameter languageCountry is mandatory.";
    private static final String NOT_FOUND_ERROR_MSG_PATTERN = "The languageCountry %s does not exist.";

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
        List<String> errorMsgs = new ArrayList<>();

        try {
            ResourceResolver resourceResolver = request.getResourceResolver();

            String element = null;
            if (request.getPathInfo().endsWith(PathConstants.HEADER)) {
                element = PathConstants.HEADER;
            } else if (request.getPathInfo().endsWith(PathConstants.FOOTER)) {
                element = PathConstants.FOOTER;
            } else {
                errorMsgs.add(NOT_IMPLEMENTED_ERROR_MSG);
                response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
            }

            Resource countryResource = null;
            String languageCountry = request.getParameter(LANGUAGE_COUNTRY_PARAMETER);
            if (languageCountry == null || languageCountry.isEmpty()) {
                errorMsgs.add(BAD_REQUEST_ERROR_MSG);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else {
                Resource herbalifeNutritionSiteResource = resourceResolver.getResource(PathConstants.HERBALIFE_NUTRITION_SITE_ROOT);
                Iterator<Resource> regionIterator = herbalifeNutritionSiteResource.listChildren();
                while (regionIterator.hasNext() && countryResource == null) {
                    countryResource = regionIterator.next().getChild(languageCountry);
                }
                if (countryResource == null) {
                    errorMsgs.add(String.format(NOT_FOUND_ERROR_MSG_PATTERN, languageCountry));
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            }

            if (errorMsgs.size() == 0) {
                String headerFooterPath = countryResource.getPath() +
                        PathConstants.generatePath(true, PathConstants.HTML,
                                PathConstants.HOME, JcrConstants.JCR_CONTENT, PathConstants.ROOT, element);
                response.sendRedirect(headerFooterPath);
            } else {
                response.setContentType(ServletConstants.JSON_CONTENT_TYPE);
                response.setCharacterEncoding(ServletConstants.UTF8_CHARACTER_ENCODING);
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("errors", errorMsgs);
                response.getWriter().write(errorResponse.toString());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new IOException(e);
        }
    }
}
