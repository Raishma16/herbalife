jQuery(function($) {
    "use strict";

 	$(".goBack").on("click", function () {

        parent.history.back();
        return false;
            
	});
});