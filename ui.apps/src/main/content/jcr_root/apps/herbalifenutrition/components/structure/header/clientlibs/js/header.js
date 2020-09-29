"use strict";
jQuery((function ($) {
    $(document).ready(function () {
        console.log("dialog ready");
        /*var $homeLink = $(document).find(".cmp-navigation--header .cmp-navigation__item:first a").attr("href");

        if(!$homeLink.endsWith("/"))
            $homeLink = $homeLink+"/";
		console.log($homeLink);
        var url = $homeLink + "bin/SuggestionsServlet.json";*/
        var url = "/bin/SuggestionsServlet.json";
        var suggestions = [];
        var $searchTerm = $("[name='searchTerm']");
        var $contentFragmentSearchPath = $("[name='contentFragmentSearchPath']");
        var $categoryTagNames = $("[name='categoryTagNames']");

        $searchTerm.bind('input propertychange', function() {
           if($searchTerm.val() && $contentFragmentSearchPath.val() && $categoryTagNames.val()) {
               $searchTerm.autocomplete({
                   source: function(request, response){
						$.get(url, $('#search-field-form').serialize(), function(data) {
                            console.log(data.suggestions);
                    		response(data.suggestions);
                		});
                   },
                   select:function(e,ui) {
                       location.href = ui.item.the_link;
                   }
               });

            }
        });
    });
}(jQuery)));