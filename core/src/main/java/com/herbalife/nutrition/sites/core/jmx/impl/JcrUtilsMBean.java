package com.herbalife.nutrition.sites.core.jmx.impl;

import com.adobe.granite.jmx.annotation.AnnotatedStandardMBean;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.PredicateConverter;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.eval.JcrPropertyPredicateEvaluator;
import com.day.cq.search.eval.PathPredicateEvaluator;
import com.day.cq.search.eval.TypePredicateEvaluator;
import com.day.cq.search.result.Hit;
import com.herbalife.nutrition.sites.core.constants.PathConstants;
import com.herbalife.nutrition.sites.core.jmx.JcrUtilsMBeanI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.management.NotCompliantMBeanException;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        immediate = true,
        service = JcrUtilsMBeanI.class,
        property = {"jmx.objectname=com.herbalife.nutrition.sites.core:type=Herbalife Nutrition - JCR Utils"}
)
public class JcrUtilsMBean extends AnnotatedStandardMBean implements JcrUtilsMBeanI {

    private static Logger LOGGER = LoggerFactory.getLogger(JcrUtilsMBean.class);

    @Reference
    private SlingRepository slingRepository;

    @Reference
    private QueryBuilder queryBuilder;

    private static final String SUBSERVICE_NAME = "jmxServiceUser";

    public JcrUtilsMBean() throws NotCompliantMBeanException {
        super(JcrUtilsMBeanI.class);
    }

    @Override
    public String updateArticlesPagesStructure(String path, String limit, boolean dryRun) {
        StringJoiner response = new StringJoiner("\n\r");
        try {
            Session session = slingRepository.loginService(SUBSERVICE_NAME, null);
            List<Hit> hits = executeQuery(session, path, "cq:PageContent", limit,
                    "cq:template", "/conf/herbalifenutrition/settings/wcm/templates/articles-page");
            for (Hit hit : hits) {
                updateArticlePageStructure(session, hit, dryRun, response);
            }
        } catch (RepositoryException e) {
            LOGGER.error("Error login service user.\n\r{}.", e);
            response.add("Error login service user.").add(e.getMessage());
        }
        return response.toString();
    }

    private void updateArticlePageStructure(Session session, Hit hit, boolean dryRun, StringJoiner response) {
        try {
            Workspace ws = session.getWorkspace();
            String rootPath = hit.getNode().getNode(PathConstants.ROOT).getPath();
            response.add("moving node: " + rootPath);
            moveNode(session, ws, response, dryRun, rootPath,
                    "/responsivegrid_1362471450/responsivegrid",
                    "/responsivegrid_1362471450/banner-content");
            moveNode(session, ws, response, dryRun, rootPath,
                    "/responsivegrid_1362471450/responsivegrid_54206",
                    "/responsivegrid_1362471450/sidebar");
            moveNode(session, ws, response, dryRun, rootPath,
                    "/responsivegrid_1362471450/responsivegrid_1840826989",
                    "/responsivegrid_1362471450/main-content");
            moveNode(session, ws, response, dryRun, rootPath,
                    "/responsivegrid_1362471450",
                    "/content");
        } catch (RepositoryException e) {
            LOGGER.error("Error accessing the nodes.\n\r{}.", e);
            response.add("Error accessing the nodes.").add(e.getMessage());
        }
    }

    private void moveNode(Session session, Workspace ws, StringJoiner response, boolean dryRun, String rootPath, String originalSubpath, String destSubpath) throws RepositoryException {
        String originalPath = rootPath + originalSubpath;
        if (session.nodeExists(originalPath)) {
            response.add("-- from: " + originalPath).add("-- to: " + rootPath + destSubpath);
            if (!dryRun) {
                ws.move(originalPath, rootPath + destSubpath);
                session.save();
            }
        }
    }

    @Override
    public String removeContentfragmentFromProducts(String path, String limit, boolean dryRun) {
        StringJoiner response = new StringJoiner("\n\r");
        try {
            Session session = slingRepository.loginService(SUBSERVICE_NAME, null);
            List<Hit> hits = executeQuery(session, path, JcrConstants.NT_UNSTRUCTURED, limit,
                    JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, "herbalifenutrition/components/content/contentfragment");
            for (Hit hit : hits) {
                response.add(hit.getPath());
                hit.getNode().remove();
            }
            if (!dryRun) {
                session.save();
            }
        } catch (RepositoryException e) {
            LOGGER.error("Error login service user.\n\r{}.", e);
            response.add("Error login service user.").add(e.getMessage());
        }
        return response.toString();
    }

    private List<Hit> executeQuery(Session session, String path, String type, String limit, String propertyName, String propertyValue) throws RepositoryException {
        Map<String, String> predicates = new HashMap<>();
        predicates.put(PathPredicateEvaluator.PATH, path);
        predicates.put(TypePredicateEvaluator.TYPE, type);
        predicates.put(PredicateConverter.GROUP_PARAMETER_PREFIX + "." + PredicateGroup.PARAM_LIMIT, limit);
        predicates.put(JcrPropertyPredicateEvaluator.PROPERTY, propertyName);
        predicates.put(JcrPropertyPredicateEvaluator.PROPERTY + "." + JcrPropertyPredicateEvaluator.VALUE, propertyValue);

        Query query = queryBuilder.createQuery(PredicateGroup.create(predicates), session);
        return query.getResult().getHits();
    }
}
