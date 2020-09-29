(function(window, document, $, Granite) {
    "use strict";

    $(document).ready( function(e) {
    	var vars = [], hash;
    	var rawPath = window.location.href.slice(window.location.href.indexOf('?') + 1);
    	if (rawPath.includes('%2F')){
        	var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('%2F');
    	}else if(rawPath.includes('/')){
        	var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('/');
    	}
    	for(var i = 0; i < hashes.length; i++)
    	{
    	hash = hashes[i].split('=');
    	vars.push(hash[0]);
    	vars[hash[0]] = hash[1];
    	}
    	var ISO = vars[4].split('_');
    	var country = ISO[1];
    	if (country == 'gb'){
    		country = 'uk';
    	}
    	var language = ISO[0];
    	var pageRaw=[];
    	var page;// = vars[5];
    	for(var i = 0; i < vars.length; i++){
    		if (i > 4){
    			pageRaw.push(vars[i]);
    		}
    		
    	}
    	page= pageRaw[0];
    	for(var i = 1; i < pageRaw.length; i++){
    		page = page +':'+ pageRaw[i];
    	}
    	
    	 
    	
    	var site = 'hl';
    	var pageName = site +':'+ country +':'+ language +':'+ page;
        $('input[name="./pageName"]').val(pageName);
    });
})(window, document, Granite.$, Granite);
