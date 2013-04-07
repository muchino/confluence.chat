(function($)     {
    $.chatstyle={
        insertRule:function(selector,rules,contxt)
        {
            var context=contxt||document,stylesheet;

            if(typeof context.styleSheets=='object')
            {
                if(context.styleSheets.length)
                {
                    stylesheet=context.styleSheets[context.styleSheets.length-1];
                }
                if(context.styleSheets.length)
                {
                    if(context.createStyleSheet)
                    {
                        stylesheet=context.createStyleSheet();
                    }
                    else
                    {
                        context.getElementsByTagName('head')[0].appendChild(context.createElement('style'));
                        stylesheet=context.styleSheets[context.styleSheets.length-1];
                    }
                }
                if(stylesheet.addRule)
                {
                    for(var i=0;i<selector.length;++i)
                    {
                        stylesheet.addRule(selector[i],rules);
                    }
                }
                else
                {
                    stylesheet.insertRule(selector.join(',') + '{' + rules + '}', stylesheet.cssRules.length);  
                }
            }
        }
    };
})(jQuery);


(function($)     {
    
    jQuery(document).ready(function(){
        $.chatstyle.insertRule(['div.cb-head','#chatbar'], 'background-color:'+$('#header').css('backgroundColor')+";");
        $.chatstyle.insertRule(['div.cb-head'], 'border-color:'+$('#header').css('backgroundColor')+";");
        $.chatstyle.insertRule(['div.cb-head'], 'color:'+$('#header a').css('color')+";");

    });
    
    
    
})(jQuery);
    
