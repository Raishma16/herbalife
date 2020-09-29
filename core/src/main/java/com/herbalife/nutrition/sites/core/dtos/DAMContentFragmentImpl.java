package com.herbalife.nutrition.sites.core.dtos;

import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT;
import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.ContentFragmentException;
import com.adobe.cq.dam.cfm.ContentVariation;
import com.adobe.cq.dam.cfm.FragmentData;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.adobe.cq.dam.cfm.converter.ContentTypeConverter;
import com.adobe.cq.wcm.core.components.models.contentfragment.DAMContentFragment;
import com.day.text.Text;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DAMContentFragmentImpl implements DAMContentFragment {

    private static final Logger LOG = LoggerFactory.getLogger(DAMContentFragmentImpl.class);

    private final com.adobe.cq.dam.cfm.ContentFragment contentFragment;
    private final String[] elementNames;
    private final String resourceType;

    private String type;
    private List<DAMContentElement> elements;
    private Map<String, DAMContentElement> exportedElements;

    /**
     * Creates a new instance of a content fragment. Requires the content fragment's resource and a content type converter, and optional variation name and element names to be filtered.
     *
     * @param contentFragmentResource resource of the content fragment
     * @param contentTypeConverter content type converter
     * @param elementNames names of optional elements to be filtered
     */
    public DAMContentFragmentImpl(Resource contentFragmentResource, ContentTypeConverter contentTypeConverter, String[] elementNames) {
        this.resourceType = contentFragmentResource.getResourceType();
        if (elementNames == null) {
            this.elementNames = null;
        } else {
            this.elementNames = new String[elementNames.length];
            System.arraycopy(elementNames, 0, this.elementNames, 0, elementNames.length);
        }

        this.contentFragment = contentFragmentResource.adaptTo(com.adobe.cq.dam.cfm.ContentFragment.class);
        if (contentFragment == null) {
            LOG.error("Content Fragment can not be initialized because '{}' is not a content fragment.",
                    contentFragmentResource.getPath());
        } else {
            // Type cannot be determined lazily due to leaking resource resolver (query builder)
            this.type = getType(contentFragment);

            final Iterator<ContentElement> contentElementIterator = filterElements(contentFragment, elementNames);

            // Wrap elements and get their configured variation (if any)
            this.exportedElements = new LinkedHashMap<>();
            while (contentElementIterator.hasNext()) {
                final ContentElement contentElement = contentElementIterator.next();
                ContentVariation variation = null;
                this.exportedElements.put(contentElement.getName(),
                        new DAMContentFragmentImpl.DAMContentElementImpl(contentTypeConverter, contentElement, variation));
            }

            this.elements = new ArrayList<>(exportedElements.values());
        }
    }

    public static Iterator<ContentElement> filterElements(final ContentFragment contentFragment, final String[] elementNames) {
        if (ArrayUtils.isNotEmpty(elementNames)) {
            List<ContentElement> elements = new LinkedList<>();
            for (String name : elementNames) {
                if (!contentFragment.hasElement(name)) {
                    // skip non-existing element
                    LOG.warn("Skipping non-existing element '{}'", name);
                    continue;
                }
                elements.add(contentFragment.getElement(name));
            }
            return elements.iterator();
        }
        return contentFragment.getElements();
    }

    public static String getType(ContentFragment contentFragment) {
        String type = "";
        if (contentFragment == null) {
            return type;
        }

        Resource fragmentResource = contentFragment.adaptTo(Resource.class);
        FragmentTemplate fragmentTemplate = contentFragment.getTemplate();
        if (fragmentTemplate == null) {
            return type;
        }
        Resource templateResource = fragmentTemplate.adaptTo(Resource.class);
        if (fragmentResource == null || templateResource == null) {
            LOG.warn("Unable to return type: fragment or template resource is null");
            type = contentFragment.getName();
        } else {
            // use the parent if the template resource is the jcr:content child
            Resource parent = templateResource.getParent();
            if (JCR_CONTENT.equals(templateResource.getName()) && parent != null) {
                templateResource = parent;
            }
            // get data node to check if this is a text-only or structured content fragment
            Resource data = fragmentResource.getChild(JCR_CONTENT + "/data");
            if (data == null || data.getValueMap().get("cq:model") == null) {
                // this is a text-only content fragment, for which we use the model path as the type
                type = templateResource.getPath();
            } else {
                // this is a structured content fragment, assemble type string (e.g. "my-project/models/my-model" or
                // "my-project/nested/models/my-model")
                StringBuilder prefix = new StringBuilder();
                String[] segments = Text.explode(templateResource.getPath(), '/', false);
                // get the configuration names (e.g. for "my-project/" or "my-project/nested/")
                for (int i = 1; i < segments.length - 5; i++) {
                    prefix.append(segments[i]);
                    prefix.append("/");
                }
                type = prefix.toString() + "models/" + templateResource.getName();
            }
        }

        return type;
    }

    @Override
    public String[] getExportedElementsOrder() {
        if (exportedElements == null || exportedElements.isEmpty()) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }

        return exportedElements.keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    @Override
    public String getTitle() {
        return contentFragment.getTitle();
    }

    @Override
    public String getDescription() {
        return contentFragment.getDescription();
    }

    /**
     * Returns the type of a {@link ContentFragment content fragment}. The type is a string that uniquely identifies the model or template of the content fragment (CF) (e.g.
     * <code>my-project/models/my-model</code> for a structured CF or <code>/content/dam/my-cf/jcr:content/model</code> for a text-only CF).
     *
     * @return the type of the content fragment
     */
    @Override
    public String getType() {
        return type;
    }

    @Override
    public List<DAMContentElement> getElements() {
        return elements;
    }

    @Override
    public Map<String, DAMContentElement> getExportedElements() {
        return exportedElements;
    }

    @Override
    public List<Resource> getAssociatedContent() {
        return IteratorUtils.toList(contentFragment.getAssociatedContent());
    }

    @Override
    public String getExportedType() {
        return resourceType;
    }

    @Override
    public String getEditorJSON() {
        try {
            JSONObject jsonObjectBuilder = new JSONObject();
            jsonObjectBuilder.put("title", contentFragment.getTitle());

            Resource contentFragmentResource = contentFragment.adaptTo(Resource.class);
            if (contentFragmentResource != null) {
                jsonObjectBuilder.put("path", contentFragmentResource.getPath());
            }

            if (elementNames != null) {
                JSONArray arrayBuilder = new JSONArray();
                for (String elementName : elementNames) {
                    arrayBuilder.put(elementName);
                }
                jsonObjectBuilder.put("elements", arrayBuilder);
            }

            Iterator<Resource> associatedContentIterator = contentFragment.getAssociatedContent();
            if (associatedContentIterator.hasNext()) {
                JSONArray associatedContentArray = new JSONArray();
                while (associatedContentIterator.hasNext()) {
                    Resource resource = associatedContentIterator.next();
                    ValueMap valueMap = resource.adaptTo(ValueMap.class);
                    JSONObject contentObject = new JSONObject();
                    if (valueMap != null && valueMap.containsKey(JCR_TITLE)) {
                        contentObject.put("title", valueMap.get(JCR_TITLE, String.class));
                    }
                    contentObject.put("path", resource.getPath());
                    associatedContentArray.put(contentObject);
                }
                jsonObjectBuilder.put("associatedContent", associatedContentArray);
            }
            return jsonObjectBuilder.toString();
        } catch (Exception e) {
            LOG.error("Could not export JSON.", e);
        }
        return null;
    }

    /**
     * Represents a content element of a content fragment.
     */
    public static class DAMContentElementImpl implements DAMContentElement {

        private static final Logger LOG = LoggerFactory.getLogger(DAMContentElementImpl.class);

        private static final String TEXT_HTML = "text/html";

        private final ContentTypeConverter converter;
        private final ContentElement element;
        private final ContentVariation variation;

        private String htmlValue;

        /**
         * @param converter the converter to use to convert the value of text elements to HTML
         * @param element the original element
         * @param variation the configured variation of the element, or {@code null}
         */
        public DAMContentElementImpl(ContentTypeConverter converter,
                ContentElement element,
                ContentVariation variation) {
            this.converter = converter;
            this.element = element;
            this.variation = variation;
        }

        @Override
        public String getName() {
            return element.getName();
        }

        @Override
        public String getTitle() {
            return element.getTitle();
        }

        private FragmentData getData() {
            if (variation != null) {
                return variation.getValue();
            }
            return element.getValue();
        }

        @Override
        public String getDataType() {
            return getData().getDataType().getTypeString();
        }

        @Override
        public Object getValue() {
            return getData().getValue();
        }

        @Override
        public String getExportedType() {
            final FragmentData value = getData();
            // Mime type for text-based data types
            String type = value.getContentType();

            // Data type for non text-based data types
            if (type == null) {
                type = value.getDataType().getTypeString();
            }

            return type;
        }

        private String getContentType() {
            return getData().getContentType();
        }

        @Override
        public boolean isMultiLine() {
            String contentType = getContentType();
            // a text element is defined as a single-valued element with a certain content type (e.g. "text/plain",
            // "text/html", "text/x-markdown", potentially others)
            return contentType != null && contentType.startsWith("text/") && !isMultiValue();
        }

        @Override
        public boolean isMultiValue() {
            return getData().getDataType().isMultiValue();
        }

        @Override
        public String getHtml() {
            // restrict this method to text elements
            if (!isMultiLine()) {
                return null;
            }

            String contentType = getContentType();
            String[] values = getData().getValue(String[].class);
            String value = null;
            if (values != null) {
                value = StringUtils.join(values, ", ");
            }

            if (TEXT_HTML.equals(contentType)) {
                // return HTML as is
                return value;
            } else {
                if (htmlValue == null) {
                    try {
                        // convert element value to HTML
                        htmlValue = converter.convertToHTML(value, contentType);
                    } catch (ContentFragmentException e) {
                        LOG.warn("Could not convert value to HTML", e);
                        return null;
                    }
                }
                return htmlValue;
            }
        }
    }
}
