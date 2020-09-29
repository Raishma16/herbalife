// Wrap bindings in anonymous namespace to prevent collisions
jQuery(function($) {
    "use strict";

 	$("#menu-button").on("click", function () {

        $(this).toggleClass('menu-opened');
            var mainmenu = $(this).next('ul');
            if (mainmenu.hasClass('open')) { 
               // mainmenu.hide().removeClass('open');
            	mainmenu.removeClass('open');
            }
            else {
            	mainmenu.addClass('open');        
            }
            
	});

    $("#menu-button + ul").find('.submenu').on('click', function() {
            $(this).toggleClass('submenu-opened');
            if ($(this).siblings('ul').hasClass('open')) {
              $(this).siblings('ul').removeClass('open');
            }
            else {
              $(this).siblings('ul').addClass('open');
            }
          });


    $(window).on('resize',function(){
           if ($(this).width() >= 992) {  
              $(".cmp-navigation").children('ul').removeClass('open');
			  $(".submenu").siblings('ul').removeClass('open');
			  $("#menu-button").next('ul').removeClass('open');
           }

    });

});

$.noConflict(); 
