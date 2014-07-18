(function($) {

    /*
     * Auto-growing textareas; technique ripped from Facebook
     */
    $.fn.chatAutogrow = function(options) {
        
        this.filter('textarea').each(function() {
            
            var $this       = $(this),
            maxHeight       = 132,
            minHeight       = $this.height(),
            lineHeight      = $this.css('lineHeight');
            var shadow = $('<div></div>').css({
                position:   'absolute',
                top:        -10000,
                left:       -10000,
                width:      248,
                fontSize:   $this.css('fontSize'),
                fontFamily: $this.css('fontFamily'),
                lineHeight: $this.css('lineHeight'),
                resize:     'none'
            }).appendTo(document.body);
            var update = function() {
    
                var times = function(string, number) {
                    var _res = '';
                    for(var i = 0; i < number; i ++) {
                        _res = _res + string;
                    }
                    return _res;
                };
                
                var val = this.value.replace(/</g, '&lt;')
                .replace(/>/g, '&gt;')
                .replace(/&/g, '&amp;')
                .replace(/\n$/, '<br/>&nbsp;')
                .replace(/\n/g, '<br/>')
                .replace(/ {2,}/g, function(space) {
                    return times('&nbsp;', space.length -1) + ' ';
                });
                
                shadow.html(val);
                $(this).css('height', Math.min(maxHeight, Math.max(shadow.height() + 20, minHeight)));
            
            };
            
            $(this).change(update).keyup(update);
            
            update.apply(this);
            
        });
        
        return this;
        
    };
})(AJS.$);