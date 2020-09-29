jQuery(function($){
  $(document).ready(function(){
	  if (navigator.appName == 'Microsoft Internet Explorer' ||  !!(navigator.userAgent.match(/Trident/) || navigator.userAgent.match(/rv:11/)) || (typeof $.browser !== "undefined" && $.browser.msie == 1)){
	  var src;
	  var alt;
	 $(".cmp-teaser--billboard .cmp-teaser .cmp-image .cmp-image__image").each(function(){
      src=$(this).attr('src');
      alt=$(this).attr('alt');
      $(this).removeAttr( "src" );
      $(this).removeAttr( "alt" );
    });
	  $(".cmp-teaser--billboard .cmp-teaser .cmp-image").each(function(){
	     $(this).attr({ "style":
	        "background-image:url("+src+");"
	      });
	     $(this).attr({ "alt":
		        "'"+alt+"';"
		      });
	    });
	}
  });
});
