"use strict";
jQuery((function ($) {
  	var $readMoreLabel = $('#read-more').val();
	var $readLessLabel = $('#read-less').val();
    console.log($readMoreLabel);
    console.log($readLessLabel);
	$("#showmore-button-").click(function() {
		$("#showmore").toggleClass('showMore-ext');
		$("#showmore").toggleClass('showMore');
		$("#showmore-button-").hide;
		$("#showmore-button").show;
        if ($("#showmore").hasClass("showMore-ext")) {
            $("#showmore-button-").html($readLessLabel)
        } else {
            $("#showmore-button-").html($readMoreLabel)
        }
   	});
}(jQuery)));