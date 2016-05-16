(function($) {
    $.chatstyle = {
        insertRule: function(selector, rules, contxt)
        {
            var context = contxt || document, stylesheet;
            if (typeof context.styleSheets === 'object')
            {
                if (context.styleSheets.length)
                {
                    stylesheet = context.styleSheets[context.styleSheets.length - 1];
                }
                if (context.styleSheets.length)
                {
                    if (context.createStyleSheet)
                    {
                        stylesheet = context.createStyleSheet();
                    }
                    else
                    {
                        context.getElementsByTagName('head')[0].appendChild(context.createElement('style'));
                        stylesheet = context.styleSheets[context.styleSheets.length - 1];
                    }
                }
                if (stylesheet.addRule)
                {
                    for (var i = 0; i < selector.length; ++i)
                    {
                        stylesheet.addRule(selector[i], rules);
                    }
                }
                else
                {
                    stylesheet.insertRule(selector.join(',') + '{' + rules + '}', stylesheet.cssRules.length);
                }
            }
        }
    };
})(AJS.$);
(function($) {
    $(document).ready(function() {
        var forbiddenColors = ["rgb(0, 0, 0)", "rgba(0, 0, 0, 0)",
            "rgb(255, 255, 255)", "rgba(255, 255, 255, 0)",
            "#ffffff", "#fff",
            "transparent"];
        var headerColor = $('#header').css('backgroundColor');
        if ($.inArray(headerColor, forbiddenColors) < 0) {
            try {
                $.chatstyle.insertRule(['.cb-head'], 'background-color:' + headerColor + ";");
                $.chatstyle.insertRule(['.cb-head'], 'border-color:' + headerColor + ";");
                $.chatstyle.insertRule(['.cb-head'], 'color:' + $('#header a').css('color') + ";");
            } catch (ex) {
            }
        }
    });
})(AJS.$);

