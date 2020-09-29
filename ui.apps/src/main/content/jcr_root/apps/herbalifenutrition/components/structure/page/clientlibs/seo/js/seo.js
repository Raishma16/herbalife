(function(window, document, $, Granite) {
    "use strict";

    $(document).on('change', 'coral-checkbox[name="./robots-noindex"]', function(e) {
        $('input[name="./sitemap-exclude"]').val($(this).get(0).checked);
    });
})(window, document, Granite.$, Granite);
