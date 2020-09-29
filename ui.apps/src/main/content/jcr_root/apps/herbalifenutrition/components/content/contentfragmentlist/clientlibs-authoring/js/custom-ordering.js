(function ($, $document) {
    "use strict";
    $document.on("dialog-ready", function() {
        var $cfTitlesDelete = $("[name='./cfTitles@Delete']"),
        	cuiCFTitles = $cfTitlesDelete.closest(".coral-Multifield").data("multifield"),
            $cfTitleAdd = cuiCFTitles.$element.find(".js-coral-Multifield-add"),
			$enableCustomOrdering = $("[name='./enableCustomOrdering']"),
            $tagNames = $("[name='./tagNames']"),
            $parentPath = $("[name='./parentPath']"),
            $modelPath = $("[name='./modelPath']"),
            $maxItems = $("[name='./maxItems']"),
            $form = $cfTitlesDelete.closest("form"),
            elementsArray = [$enableCustomOrdering, $tagNames, $parentPath, $modelPath, $maxItems],
            isEnabled = $enableCustomOrdering.attr("checked") == "checked";
        if(!(isEnabled && ($modelPath.val() || $parentPath.val())))
        	$cfTitlesDelete.closest(".coral-Form-fieldwrapper").hide();
        $cfTitleAdd.hide();
        cuiCFTitles.$element.find(".js-coral-Multifield-remove").hide();
        elementsArray.forEach(function(element){
			element.on('change', function(){
                if(element == $enableCustomOrdering)
					isEnabled = !isEnabled;
                fillCFTitles();
            });
        });
        function fillCFTitles() {
            cuiCFTitles.$element.find(".js-coral-Multifield-remove").click();
            if(isEnabled && ($modelPath.val() || $parentPath.val())) {
                var url = "/bin/ContentFragmentListServlet.json";
                $.get(url, $form.serialize(), function(data) {
                    var $cfTitle;
                    if(data.cfTitles.length > 0) {
                        $cfTitlesDelete.closest(".coral-Form-fieldwrapper").show();
                        data.cfTitles.forEach(function(cfTitle){
                            $cfTitleAdd.click();
                            $cfTitle = cuiCFTitles.$element.find("[name='./cfTitles']:last");
                            $cfTitle.val(cfTitle);
                            cuiCFTitles.$element.find(".js-coral-Multifield-remove:last").hide();
                        });
                    }
                    else
                        $cfTitlesDelete.closest(".coral-Form-fieldwrapper").hide();
                });
            }
            else
				$cfTitlesDelete.closest(".coral-Form-fieldwrapper").hide();
        }
    });
})(jQuery, jQuery(document));
