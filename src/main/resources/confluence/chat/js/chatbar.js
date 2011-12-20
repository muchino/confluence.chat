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
    this.bar.find('#chatbar-button-online, #chatbar-online-users .chatboxoptions a').click(function(){
        if(!that.isOnline()){
            that.bar.find('#chatbar-button-config').click();
        } else {
            if(!that.configurationBox.is(':hidden')){
                that.configurationBox.fadeOut();
            }
            that.onlineUsersBox.fadeToggle();
        }
        return false;
        
        
    })
    this.bar.find('#chatbar-button-config ,#chatbar-config .chatboxoptions a').click(function(){
        if(!that.onlineUsersBox.is(':hidden')){
            that.onlineUsersBox.fadeOut();
        }
        that.configurationBox.fadeToggle();
        return false;
    })
    this.configurationBox.find('.chatbar-box-content  select[name=status]').change(function(){
        var status = jQuery(this).val();
        jQuery.ajax({
            url: getBaseUrl()+"/chat/setstatus.action",
            cache: false,
            data: {
                status: jQuery(this).val()
            },
            dataType: "json",
            success: function(data){
                that.setStatus(status);
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

ChatBar.prototype.isOnline = function(){
    return  this.bar.hasClass("online");   
}
ChatBar.prototype.setStatus = function(status) {
    if(status == "xa"){
        this.bar.removeClass("online").addClass("offline");
    }else {
        var online = this.isOnline();
        this.bar.removeClass("offline").addClass("online");
        if(!online){
            // aktualisieren die user
            this.getOnlineUsers();    
        }
    }
    this.bar.find('#chatbar-status').attr('class', status);
    
}

ChatBar.prototype.getOnlineUsers = function() {
    if(this.isOnline()){
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
}
ChatBar.prototype.refreshUser = function(data){
    var that = this;
    var tmpAttr = 'chatOfflineMeFlag-'+Math.round(Math.random() * 10000);
    this.chatOnlineUserDiv.find('.chat-user').attr(tmpAttr, 'true');
    var ownUserInList = false;
    jQuery.each(data.users, function(j,user){
        var username = user.un;
        var usernameMd5 = jQuery.md5(user.un);
        if( that.username != user.un){
            var chatBox = that.chatOnlineUserDiv.find('.chat-user[usernameMd5='+usernameMd5+']');
            if(!chatBox.size()){
                chatBox =  that.chatBox.clone(true);
                chatBox.show();
                chatBox.attr('usernameMd5', usernameMd5);
                chatBox.attr('username', username);
                chatBox.find('img').attr('src', user.p);
                chatBox.find('.chat-user-info span').text(user.fn);
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

