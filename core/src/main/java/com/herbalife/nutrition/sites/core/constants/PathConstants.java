package com.herbalife.nutrition.sites.core.constants;

import java.util.StringJoiner;

public class PathConstants {

    /**
     * Generates and absolute or relative path adding extension when filled with all the subpaths adding the slash delimiter between each.
     * A subpath can be a relative path, the only requirement is that the start and end of the subpath has not the slash delimiter;<br/>
     * <br/>
     * generatePath(false, null, "content/dam", "project")<br/>
     * "content/dam/project"<br/>
     * <br/>
     * generatePath(true, ".html", "content", "site", "home")<br/>
     * "/content/site/home.html"
     *
     * @param absolutePath true adds slash at the first position of the result, false don't
     * @param extension if null or empty will be ignored
     * @param subpaths all the subpaths/relative paths that will be joined using the slash delimiter
     * @return
     */
    public static String generatePath(boolean absolutePath, String extension, String... subpaths) {
        StringJoiner generatedPath = new StringJoiner(SLASH, absolutePath ? SLASH : "", extension != null ? extension : "");
        for (String subpath : subpaths) {
            generatedPath.add(subpath);
        }
        return generatedPath.toString();
    }

    // Special characters
    public static final String SLASH = "/";

    // Content paths
    public static final String HERBALIFE_NUTRITION_SITE_ROOT = "/content/herbalifenutrition";

    // Supaths
    public static final String ROOT = "root";
    public static final String HOME = "home";
    public static final String HEADER = "header";
    public static final String FOOTER = "footer";

    // Properties
    public static final String LOGOPATH = "logoPath";

    // Extensions
    public static final String HTML = ".html";
}
