function ChatBar(){
    var that = this;
    this.chatBoxes = new Array();
    this.heartBeatCount= 0;
    this.windowFocus = true;
    this.originalTitle = null;
    this.lastHeartBeatServerdate = 0;
    that.startChatSession();
    jQuery(document).ready(function(){
        that.originalTitle = document.title;
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
ChatBar.prototype.getHeartbeatCount = function(){  
    return this.heartBeatCount;
}
ChatBar.prototype.getOriginalTitle = function(){  
    return this.originalTitle;
}
ChatBar.prototype.startChatSession = function(){  
    var that = this;
    jQuery(document).ready(function(){
        
        if(AJS.params.remoteUser){
            
            jQuery.ajax({
                url: getBaseUrl()+"/chat/start.action",
                cache: false,
                dataType: "json",
                data: {
                    currentUrl : window.location.href,
                    currentTitle: document.title
                },
                success: function(data) {
                    that.lastHeartBeatServerdate = data.lr;
                    if(typeof(data.chatboxes) != "undefined"){
                        that.retrieveChatMessages(data.chatboxes);
                    }
                    setInterval(function(){
                        that.chatHeartbeat();
                    }, 650);
                }
            });
            jQuery([window, document]).blur(function(){
                that.windowFocus = false;
            }).focus(function(){
                that.windowFocus = true;
                document.title = that.originalTitle;
            });
        }
    });    
}

ChatBar.prototype.chatHeartbeat = function(){
    var that = this;
    this.heartBeatCount++;
    
    if(typeof(chatBar) != "undefined"){
        if(!this.isOnline()){
            return;
        }
    }
    
    jQuery.ajax({
        url: getBaseUrl()+"/chat/heartbeat.action",
        cache: false,
        dataType: "json",
        data: {
            lr: this.lastHeartBeatServerdate
        },
        success: function(data) {
            
            that.lastHeartBeatServerdate = data.lr;
            if(typeof(data.chatboxes) != "undefined"){
                that.retrieveChatMessages(data.chatboxes);
            }
        }
    });
}


ChatBar.prototype.restructureChatBoxes = function() {
    var chatBoxesPos= 0;
    for (var x in this.chatBoxes) {
        if (!this.chatBoxes[x].isClosed()) {
            this.chatBoxes[x].position(chatBoxesPos);
            chatBoxesPos++;
        }
    }
}

ChatBar.prototype.init = function(){
    var that = this;

    this.username = AJS.params.remoteUser;
    this.bar = jQuery('#chatbar');
    this.bar.find('.aui-dd-parent').dropDown("Standard", {
        alignment: "left", 
        useDisabled: true
    });
    this.onlineUsersBox =  this.bar.find('#chatbar-online-users');
    this.configurationBox =  this.bar.find('#chatbar-config');
    this.bar.find('#chatbar-button-online, #chatbar-online-users .cb-opt a').click(function(){
        if(!that.isOnline()){
            that.bar.find('#chatbar-button-config').click();
        } else {
            if(!that.configurationBox.is(':hidden')){
                that.configurationBox.slideUp();
            }
            that.onlineUsersBox.slideToggle();
        }
        return false;
        
        
    })
    this.bar.find('#chatbar-button-config ,#chatbar-config .cb-opt a').click(function(){
        if(!that.onlineUsersBox.is(':hidden')){
            that.onlineUsersBox.slideUp();
        }
        that.configurationBox.slideToggle();
        return false;
    })
    this.configurationBox.find('.chat-options select, .chat-options input ').change(function(){
        var status = jQuery(this).val();
        jQuery.ajax({
            url: getBaseUrl()+"/chat/setstatus.action",
            cache: false,
            data: {
                status:  that.configurationBox.find('.chat-options select[name=status]').val(),
                showCurrentSite:  that.configurationBox.find('.chat-options #chat-site:checked').size()
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

ChatBar.prototype.getChatBoxes = function(){
    return this.chatBoxes;
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


/**
 *  optjext like: 
 *  chatboxid: ''
 *  chatuserList: ''
 *  dispayTitle: ''
 *  message
 */
ChatBar.prototype.chatWith = function(options){
    var opts= jQuery.extend({
        chatBoxId: null,
        chatUserList: null,
        dispayTitle: null,
        messages: new Array(),
        open: true,
        focus: false,
        minimizeChatBox: false
    }, options);

    var chatBoxId = opts.chatBoxId;
    if(chatBoxId != null){
        if(typeof(this.chatBoxes[chatBoxId]) == "undefined"){
            this.chatBoxes[chatBoxId] = new ChatBox(opts);
        }
        if(opts.focus){
            this.chatBoxes[chatBoxId].show(); 
            this.chatBoxes[chatBoxId].focusChatBox(); 
        }
        
        this.restructureChatBoxes();
    }else {
        AJS.log('ChatBar.prototype.chatWith: no chatBoxId given');
    }
    
}

ChatBar.prototype.refreshUser = function(data){
    var that = this;
    var tmpAttr = 'chatOfflineMeFlag-'+Math.round(Math.random() * 10000);
    this.chatOnlineUserDiv.find('.chat-user').attr(tmpAttr, 'true');
    jQuery('.chatbox > div').addClass('unknown');
    var ownUserInList = false;
    jQuery.each(data.users, function(j,user){
        var username = user.un;
        var chatBoxId = user.id;
        if( that.username != user.un){
            var chatUser = that.chatOnlineUserDiv.find('.chat-user[chatBoxId='+chatBoxId+']');
            if(!chatUser.size()){
                chatUser =  that.chatBox.clone(true);
                chatUser.show();
                chatUser.attr('chatBoxId', chatBoxId);
                chatUser.attr('username', username);
                chatUser.find('.chat-user-info .is').text(user.fn).addClass('user-hover-trigger').attr('data-username', username);
                chatUser.find('.chat-where').addClass('chat-where'+chatBoxId);
                chatUser.click(function(){
                    that.chatWith({
                        chatBoxId: jQuery(this).attr('chatBoxId'),
                        chatUserList : jQuery(this).attr('username'),
                        dispayTitle : jQuery(this).find('.chat-user-info span.user-hover-trigger').text(),
                        focus: true
                    });
                });
                that.chatOnlineUserDiv.append(chatUser);
                try{
                    AJS.Confluence.Binder.userHover();
                }catch(e){}
            } else {
                chatUser.removeAttr(tmpAttr);
            }
            // aktionen die immer gemacht werden müssen
            chatUser.find('> div').attr('class', user.s);
            var img = chatUser.find('img');
            if(img.attr('src') != user.p){
                img.attr('src', user.p);
            }
            /**
             *wo befindet sich der user
             */
            var userWhere = jQuery('.chat-where'+chatBoxId);
            
            if(typeof user.su != "undefined" && typeof user.st != "undefined" ){
                
                var title = user.st;
                var pos = title.indexOf('-');
                if(pos > 0){
                    title = title.substr(0, pos);
                    if(jQuery.trim(title).length == 0){
                        title = user.st;
                    }
                }
                userWhere.find('a').attr('href', user.su).attr('title',user.st );
                userWhere.find('span').text(title);
                userWhere.show();
            } else {
                userWhere.find('a').attr('href', '').attr('title','');
                userWhere.find('span').text('');
                userWhere.hide();
            }
            
            /**
             *Userstatus
             */
            
            jQuery('#chatbox_'+chatBoxId+' > div ').attr('class', user.s);
        }else {
            ownUserInList = true;
        }
    });
    jQuery('.chatbox > div.unknown').attr('class', '');
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

ChatBar.prototype.retrieveChatMessages= function(chatboxes){
    var that = this;
    jQuery.each(chatboxes, function(j,chatbox){
        if(typeof(chatbox.messages) != "undefined" && typeof(that.chatBoxes[chatbox.id]) != "undefined"){
            jQuery.each(chatbox.messages, function(i,item){
                if (item)	{
                    that.chatBoxes[chatbox.id].retrieveMessage(item);      
                }
            });
        }else if (typeof(that.chatBoxes[chatbox.id]) == "undefined"){
            var chatPartner =  chatbox.un[0];
            var chatTitle = '';
            // retrieve name
            jQuery.each(chatbox.messages, function(i,item){
                if (item){
                    if (typeof(item.to) != "undefined"){
                        if (typeof(item.to.un) != "undefined"){
                            if (typeof(item.to.fn) != "undefined"){
                                if (item.to.un == chatPartner){
                                    chatTitle  = item.to.fn;
                                }
                            }
                        }
                    }
                    if (typeof(item.f) != "undefined"){
                        if (typeof(item.f.un) != "undefined"){
                            if (typeof(item.f.fn) != "undefined"){
                                if (item.f.un == chatPartner){
                                    chatTitle  = item.f.fn;
                                }
                            }
                        }
                    }                    
                }
            });
            if(chatTitle == ''){
                chatTitle = chatPartner;
            }
            // create box
            that.chatWith({
                chatBoxId: chatbox.id,
                chatUserList : chatPartner ,
                dispayTitle : chatTitle,
                open: chatbox.open,
                messages : chatbox.messages
            });
        }
    });
}

var chatBar = new ChatBar();

/**
 * chatBoxId : chatboxid, 
 * chatUserList: chatUserList,
 *  minimizeChatBox: false,
 *  messages: messages,
 *   dispayTitle: dispayTitle
 */
function ChatBox(options){
    
    this.opt = jQuery.extend({
        chatBoxId: null,
        chatUserList: null,
        open: true,
        dispayTitle: null,
        messages: new Array()
    }, options);
    this.chatBoxId = this.opt.chatBoxId;
    this.chatUserList = this.opt.chatUserList;
    this.box = null;
    this.textarea = null;
    this.initialized = false;
    this.init();
    this.blinkInterval = null;
    var len=this.opt.messages.length;
    for ( var i=0; i<len; i++ ){
        this.retrieveMessage(this.opt.messages[i]);
    }
    this.initialized = true;

    if(this.opt.open){
        this.show(); 
    }else {
        this.hide();
    }
}
ChatBox.prototype.getId = function(){
    return this.chatBoxId;
}

ChatBox.prototype.isMinimized = function(){
    var cookie = 'cb-min'+this.chatBoxId;
    var min = AJS.Cookie.read(cookie);
    if(min  == "true"){
        return true
    }else{
        return false;
    }
}

ChatBox.prototype.isClosed= function(){
    return this.box.hasClass('closed');
}

ChatBox.prototype.focusChatBox = function(){
    this.maximize();
    if(this.isClosed()){
        this.show();
        
    }
    this.textarea.focus();
}
ChatBox.prototype.startBlink = function(){
    this.show();
    if(!this.textarea.hasClass('cb-ts')
        && this.blinkInterval == null
        && chatBar.getHeartbeatCount() > 0
        ){
        var that = this
        this.blinkInterval = window.setInterval(function(){
            that.blink();
        }, 1000);
        
    }
}
ChatBox.prototype.stopBlink = function(){
    window.clearInterval(this.blinkInterval);
    this.blinkInterval = null;
    document.title = chatBar.getOriginalTitle();   
    this.box.removeClass('blink');
}
ChatBox.prototype.blink = function(){
    if(this.box.hasClass('blink')){
        document.title = this.opt.dispayTitle + ' says...';
    }else {
        document.title = chatBar.getOriginalTitle();    
    }
    this.box.toggleClass('blink');
}

ChatBox.prototype.init = function(){
    var that = this;
    this.box = jQuery('<div/>');
    this.hide();
    this.box.addClass('chatbox').attr('id', 'chatbox_'+this.chatBoxId);
    
    
    var box = jQuery('<div/>').appendTo(this.box);
    
    
    var header =  jQuery('<div/>').addClass('cb-head');
    header.appendTo(box);
    var options = jQuery('<div/>').addClass('cb-opt');
    options.appendTo(header);
    jQuery('<a/>').attr('href', '#').text('+').addClass('opt-max').click(function(){
        that.toggleChatBoxGrowth();
    }).appendTo(options);
    jQuery('<a/>').attr('href', '#').text('-').addClass('opt-min').click(function(){
        that.toggleChatBoxGrowth();
    }).appendTo(options);
    jQuery('<a/>').attr('href', '#').text('X')
    .click(function(){
        that.closeChatBox();
    }).appendTo(options);
    var titleBox =  jQuery('<div/>').addClass('cb-title').text(this.opt.dispayTitle);
    titleBox.appendTo(header);
   
   
    var contentHolder = jQuery('<div/>').addClass('cb-content-hold');
    contentHolder.appendTo(box);

    jQuery('<div/>').addClass('cb-content').appendTo(contentHolder);
    /**
     * Who ist der User gerade
     */
    
    var chatWhere = jQuery('<div/>').addClass('chat-where chat-where'+this.chatBoxId).hide();
    jQuery('<span/>').appendTo(jQuery('<a/>').attr('href', '#').addClass('icon icon-page').text('').appendTo(chatWhere));
    jQuery('<span/>').appendTo(jQuery('<a/>').attr('href', '#').addClass('chat-where-text').text('').appendTo(chatWhere));
    
    chatWhere.appendTo(contentHolder);

    this.textarea= jQuery('<textarea/>');
    this.textarea.keydown(function(event) {
        if (event.keyCode == 13) {
            event.preventDefault();
            that.send();
        }
    });
    
    this.textarea.blur(function(){
        jQuery(this).removeClass('cb-ts');
    }).focus(function(){
        jQuery(this).addClass('cb-ts');
        that.stopBlink();
    });
    jQuery('<div/>').addClass('cb-input').append(this.textarea).appendTo(contentHolder);
    that.box.appendTo(jQuery( "body" ));
  
    if (this.minimizeChatBox == 1 || this.isMinimized()) {
        this.minimize();
    }
}
ChatBox.prototype.show= function() {
    if(this.initialized){
        if(this.isClosed()){
            this.box.removeClass('closed');
            chatBar.restructureChatBoxes();
        }
    }
    this.box.find(".cb-content").scrollTop(this.box.find(".cb-content")[0].scrollHeight);
}

ChatBox.prototype.hide= function() {
    this.box.addClass('closed');
    chatBar.restructureChatBoxes();
}
ChatBox.prototype.position = function(number) {
    var width = (number*(this.box.width()+20))+250;
    this.box.css('right', width+'px');
}

ChatBox.prototype.closeChatBox = function() {
    this.hide();
    chatBar.restructureChatBoxes();
    jQuery.post(getBaseUrl()+"/chat/close.action", {
        close: this.chatUserList
    } );
}
ChatBox.prototype.minimize = function () {
    
    var cookie = 'cb-min'+this.chatBoxId;
    this.box.addClass('min');
    AJS.Cookie.save(cookie,"true");
}
ChatBox.prototype.maximize = function () {
    var cookie = 'cb-min'+this.chatBoxId;
    this.box.removeClass('min');
    this.textarea.focus();
    AJS.Cookie.save(cookie,"false");
}


ChatBox.prototype.toggleChatBoxGrowth = function () {
    if (this.isMinimized()) {  
        this.maximize();
    } else {
        this.minimize();
    }
	
}


ChatBox.prototype.send = function() {
    var message = AJS.escapeHtml(this.textarea .val());
    this.textarea .val('').focus().css('height','44px');
    if (message != '') {
        jQuery.post(getBaseUrl()+"/chat/send.action", {
            to: this.chatUserList, 
            message: message
        } );
    }
    return false;
}
ChatBox.prototype.retrieveMessage = function(item){
    if(item == null){
        return;
    }
    
    if(item.f.un != AJS.params.remoteUser){
        this.startBlink();    
    }
    
    if(this.isClosed() && this.initialized){
        this.show();
        chatBar.restructureChatBoxes();
    }
    var message = this.replaceChatMessage(item.m);
    var content = this.box.find('.cb-content');
    
    var dt = new Date(item.t);
    var holderId = dt.getFullYear()+''+dt.getMonth()+dt.getDate()+dt.getHours()+dt.getMinutes();
    var messageBox = content.find('.cb-mc[slot='+holderId+']');
    if(messageBox.size() == 0){
        // pro zeit / datum eine box
        messageBox = jQuery('<div/>').addClass('cb-mc').attr('slot', holderId);    
        messageBox.appendTo(content);
    
        var messageTime= jQuery('<div/>').addClass('cb-mt');    
        messageTime.text(this.formatTime(dt));
        messageTime.appendTo(messageBox)
    
    }
    // habe nun aktuellen messageBox -> ist letzter eintrag auch von item.f.un  user?
    var userBox =  messageBox.find('.cb-ut:last');
    if(userBox.attr('unid') != item.f.id){
        userBox = null;
    }
    
    var messageHolder ;
    if(userBox == null ){
        userBox = jQuery('<div/>').addClass('cb-ut').attr('unid', item.f.id);    
        userBox.appendTo(messageBox)
        var userLink = jQuery('<a/>').attr('href', AJS.contextPath() + '/display/~'+item.f.un )
        userLink.addClass('userLogoLink').attr('data-username', item.f.un)
        var userLogo = jQuery('<img/>').attr('src', AJS.contextPath() + item.f.p).attr('alt','User icon: ' + item.f.un).attr('title',item.f.fn);
        userLogo.appendTo(userLink);
        // user image am content
        userLink.appendTo(userBox)
        messageHolder =   jQuery('<div/>').addClass('cb-mh');
        var from = jQuery('<div/>').addClass('cb-f').text(item.f.fn);
        from.appendTo(messageHolder)
        messageHolder.appendTo(userBox)
        try{
            AJS.Confluence.Binder.userHover();
        }catch(e){}
    }else {
        messageHolder =    userBox.find('.cb-mh') ;
    }
    //     nun einfach die nachricht noch drann
    var messageItem = jQuery('<div/>').addClass('cb-mtext').html(message).attr('t',item.t);
    messageItem.appendTo(messageHolder);
    
    content.scrollTop(content[0].scrollHeight);    
    AJS.log('scroll');
    
}

ChatBox.prototype.formatTime =  function(dt) {
    if(typeof dt == "object"){
        var hours = dt.getHours();
        var minutes = dt.getMinutes();

        // the above dt.get...() functions return a single digit
        // so I prepend the zero here when needed
        if (hours < 10) 
            hours = '0' + hours;

        if (minutes < 10) 
            minutes = '0' + minutes;

        return hours + ":" + minutes;
    }
    return "";
} 

ChatBox.prototype.replaceChatMessage =  function(text){
    
    text = this.urlify(text);
    text = this.replaceEmoticons(text);
    return text;
    
}
ChatBox.prototype.replaceEmoticons =  function(text){
    var emoticons = {
        ':-)' : 'happy',
        ':)'  : 'happy',
        ':]'  : 'happy',
        ':-D' : 'big-grin',
        ':D' : 'big-grin',
        '=)'  : 'happy',
        ':-*' : 'kiss',
        ':*' : 'kiss',
        ';-)' : 'winking',
        ';)' : 'winking',
        ':-P' : 'tongue',
        ':-p' : 'tongue',
        ':P' : 'tongue',
        ':p' : 'tongue',
        '=P' : 'tongue',
        ':-O' : 'surprise',
        ':-o' : 'surprise',
        ':O' : 'surprise',
        ':o' : 'surprise',
        ':D'  : 'big-grin',
        ':-|'  : 'straight-face',
        ':whistle:'  : 'whistle',
        ':oops:'  : 'oops',
        ':">': 'oops'
 
    },  patterns = [],
    metachars = /[[\]{}()*+?.\\|^$\-,&#\s]/g;

    // build a regex pattern for each defined property
    for (var i in emoticons) {
        if (emoticons.hasOwnProperty(i)){ // escape metacharacters
            patterns.push('('+i.replace(metachars, "\\$&")+')');
        }
    }

    // build the regular expression and replace
    
    return text.replace(new RegExp(patterns.join('|'),'g'), function (match) {
        return typeof emoticons[match] != 'undefined' ?
        '<img title="'+emoticons[match]+'" src="'+getBaseUrl()+'/download/resources/confluence.chat/clear.png" class="smiley '+emoticons[match]+'"/>' :
        match;
    });
    
}
ChatBox.prototype.urlify =  function(text){
    if(typeof text != 'undefined'){
        var urlRegex = /(https?:\/\/[^\s]+)/g;
        return text.replace(urlRegex, function(url) {
            return '<a href="' + url + '" target="_blank" title="'+url+'">' + url + '</a>';
        })
    }else {
        return "";
    }
}