function ChatBar(){
    var that = this;
    this.bar = jQuery('#chatbar');
   // var winWinWidth = jQuery(window).width();
   // this.bar.width((winWinWidth-80)+'px');

   
}
ChatBar.prototype.initSpaceDir = function() {
    
    }


jQuery(document).ready(function(){
    jQuery.ajax({
        url: getBaseUrl()+"/ajax/chat/chatbar.action",
        cache: false,
        success: function(html){
            jQuery('body').append(html);
            var chatbar = new ChatBar();
        }
    });
});
