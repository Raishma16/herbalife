       var s_header_height = document.getElementsByClassName('header')[0].clientHeight;
       
       
    function onScrollEventHandler(ev) {
        var scrollPos = window.scrollY || window.scrollTop || document.getElementsByTagName("html")[0].scrollTop;
                             if (scrollPos > s_header_height) {
                                           document.getElementById("top-sticky-banner").className = "sticky";
                             } else {
                                           document.getElementById("top-sticky-banner").className = "";
                             }
    }  
              var el=window;
              if(el.addEventListener) {
        el.addEventListener('scroll', onScrollEventHandler, false);   
    } else if (el.attachEvent) {
        el.attachEvent('onscroll', onScrollEventHandler); 
              }
