function ChatBar(){
    var that = this;
    jQuery(document).ready(function(){
        if(AJS.params.remoteUser){
            jQuery.ajax({
                url: getBaseUrl()+"/ajax/chat/chatbar.action",
                success: function(html){
                    jQuery('body').append(html);
                    that.init();
                }
            });
        }
    });    
}
ChatBar.prototype.init = function(){
    var that = this;
    this.username = AJS.params.remoteUser;
    this.bar = jQuery('#chatbar');
    this.onlineUsersBox =  this.bar.find('#chatbar-online-users');
    this.configurationBox =  this.bar.find('#chatbar-config');
    this.bar.find('#chatbar-button-online, #chatbar-online-users-title').click(function(){
        if(!that.configurationBox.is(':hidden')){
            that.configurationBox.fadeOut();
        }
        that.onlineUsersBox.fadeToggle();
    })
    this.bar.find('#chatbar-button-config').click(function(){
        if(!that.onlineUsersBox.is(':hidden')){
            that.onlineUsersBox.fadeOut();
        }
        that.configurationBox.fadeToggle();
    })
    this.configurationBox.find('.chatbar-box-content a').click(function(){
        jQuery.ajax({
            url: getBaseUrl()+"/chat/setstatus.action",
            cache: false,
            data: {
                status: jQuery(this).attr('rel')
            },
            dataType: "json",
            success: function(data){
            //                that.refreshUser(data);
            }
        });
    })
   
    
    this.chatOnlineUserDiv = this.onlineUsersBox.find('#chatbar-online-users-list');
    this.chatBox = this.chatOnlineUserDiv.find('.chat-user').clone(true);
    this.chatOnlineUserDiv.empty();
    this.getOnlineUsers();  
    this.intervall = setInterval(function(){
        that.getOnlineUsers();
    }, 5000);
}


ChatBar.prototype.getOnlineUsers = function() {
    var that = this;
    jQuery.ajax({
        url: getBaseUrl()+"/chat/getonlineuser.action",
        cache: false,
        dataType: "json",
        success: function(data){
            that.refreshUser(data);
        }
    });
}
ChatBar.prototype.refreshUser = function(data){
    var that = this;
    var tmpAttr = 'chatOfflineMeFlag-'+Math.round(Math.random() * 10000);
    this.chatOnlineUserDiv.find('.chat-user').attr(tmpAttr, 'true');
    var ownUserInList = false;
    jQuery.each(data.users, function(j,user){
        var username = AJS.escapeHtml(user.un);
        if( that.username != user.un){
            var chatBox = that.chatOnlineUserDiv.find('.chat-user[username='+username+']');
            if(!chatBox.size()){
                chatBox =  that.chatBox.clone(true);
                chatBox.show();
                chatBox.attr('username', username);
                chatBox.find('img').attr('src', user.p);
                chatBox.find('.chat-user-info a').text(user.fn).attr('href', getBaseUrl()+'/display/~'+username);
                chatBox.find('> div').attr('class', user.s);
                chatBox.click(function(){
                    chatWith(chatBox.attr('username'));
                })
                that.chatOnlineUserDiv.append(chatBox);
            } else {
                chatBox.find('> div').attr('class', user.s);
                var img = chatBox.find('img');
                if(img.attr('src') != user.p){
                    img.attr('src', user.p);
                }
                chatBox.removeAttr(tmpAttr);
            }
        }else {
            ownUserInList = true;
        }
    });
    
    this.chatOnlineUserDiv.find('.chat-user['+tmpAttr+']').remove();
    var count = data.users.length;
    if(count > 0 && ownUserInList){
        count--; 
    }
    this.bar.find('#chatbar-button-online span').text(count);
}


ChatBar.prototype.getConfigParameter= function(param){
    return this.elem.find('.parameters input[name='+param+']').val();
    
}

var chatBar = new ChatBar();

