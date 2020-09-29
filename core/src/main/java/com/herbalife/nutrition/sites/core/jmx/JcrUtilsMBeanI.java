package com.herbalife.nutrition.sites.core.jmx;

import com.adobe.granite.jmx.annotation.Description;
import com.adobe.granite.jmx.annotation.Name;

public interface JcrUtilsMBeanI {

    @Description("Update articles template node names from old created pages.")
    String updateArticlesPagesStructure(
            @Name("Base path") @Description("e.g. '/content/herbalifenutrition'")
                    String path,
            @Name("Batch") @Description("Limit the pages updated or 0 for all")
                    String limit,
            @Name("Dry Run?") @Description("Default value is false, so updates are done in repository")
                    boolean dryRun
    );

    @Description("Remove contentfragment from product list pages to add contentfragmentlist.")
    String removeContentfragmentFromProducts(
            @Name("Base path") @Description("e.g. '/content/herbalifenutrition'")
                    String path,
            @Name("Batch") @Description("Limit the pages updated or 0 for all")
                    String limit,
            @Name("Dry Run?") @Description("Default value is false, so updates are done in repository")
                    boolean dryRun
    );
}
