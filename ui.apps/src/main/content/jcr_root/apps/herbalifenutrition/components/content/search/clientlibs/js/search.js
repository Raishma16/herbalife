"use strict";
jQuery((function ($) {
    $(document).ready(function () {
    	var $searchForm = $('#search-form');
    	$searchForm.on('change', 'input', function() {
    		$searchForm.submit();
    	});
    });
}(jQuery)));