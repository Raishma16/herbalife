
/**
 * Get the template the template name where the component
 * is being used.
 * String: template name
 */
"use strict";
use(function () {

    var templatePath    = currentPage.properties.get("cq:template");
    var templateName = new String(templatePath);
    templateName = templateName.replace(/(\/.*\/templates\/)/g,"");
    return {
        template: templateName
    }; 
});

