ConfluenceChatAPI = new Object();

ConfluenceChatConfig = {
    margin : 25, 
    chatBoxWidth : 260,
    barWidth : 224,
    active : true
};

(function($) {
    
    var isTaCPage = window.location.href.indexOf('termsandconditions/termsandconditions.action') > 0;
    if(isTaCPage){
        AJS.log("CHAT: chat is not allowed on the Terms and Conditions , because of the behaviour of the TaC Plugin! -> Abort ")
    }
    
    var chatActive = ConfluenceChatConfig.active = ConfluenceChatConfig.active && !isTaCPage
    if(!chatActive){
        return;
    }
    
    function isChatBox(obj){
        return  obj instanceof ChatBox;
    }
    
    function ChatBar(){
        var that = this;
        this.debug = false;
        this.chatBoxes = new Array();
        this.heartBeatCount= 0;
        this.requestFailed= 0;
        this.chatDeactivated= false;
        this.windowFocus = true;
        this.originalTitle = null;
        this.lastHeartBeatServerdate = 0;
        this.mousemove = false;
        this.initCompatibility();
        this.version = "0";
        this.spaceKey = "";
        this.users = Object();
        jQuery(document).ready(function(){
            if(typeof(AJS.params.spaceKey) == "string"){
                that.log("Chat in space " +  AJS.params.spaceKey);
                that.spaceKey = AJS.params.spaceKey;
            }
            that.originalTitle = document.title;
            if(AJS.params.remoteUser && !this.chatDeactivated){
                jQuery.ajax({
                    url: AJS.contextPath()+"/ajax/chat/chatbar.action",
                    data: {
                        spaceKey : that.spaceKey ,
                        bodyClass: jQuery('body').attr('class')
                    },
                    error: function(){
                        that.chatDeactivated = true;
                        that.requestErrorHandler();
                    },
                    success: function(html){
                        var chatHTML = jQuery(html);
                        /**
         *If the reponse has the element #chatbar,
         *the the reposne is realy the chat response
         *https://github.com/muchino/confluence.chat/issues/41
         */
                        if (chatHTML.attr("id") == "chatbar"){
                            jQuery('body').append(chatHTML);
                            that.requestSuccessHandler();
                            that.init();
                            jQuery('body').trigger('chat_init');
                        }else {
                            that.chatDeactivated = true;
                            AJS.log("Deactivate chat,because the intial response isn't comming from the chat. ");
                        }
                        return;
                    }
                });
            }
        });    
    }
    ChatBar.prototype.requestSuccessHandler= function(){  
        this.requestFailed = 0;
    
    }
    
    ChatBar.prototype.isSound= function(){  
        return !jQuery('#chatbar .csound').hasClass('csound-off');
    }
    
    ChatBar.prototype.deactivateSound= function(){  
        jQuery('#chatbar .csound').addClass('csound-off');
        AJS.Cookie.save("chatsoundoff", "true");
    }
    ChatBar.prototype.activateSound= function(){  
        jQuery('#chatbar .csound').removeClass('csound-off');
        AJS.Cookie.erase("chatsoundoff");
    }        
    ChatBar.prototype.getStatusOfUser= function(username){  
        if(username in this.users){
            if('s' in this.users[username]){
                return this.users[username].s;
            }
        }
        return 'xa';
    }
    
    ChatBar.prototype.requestErrorHandler= function(){  
        this.requestFailed++;
        if(this.requestFailed >= 3){
            
            AJS.log('Deactivate Chat, because '+this.requestFailed +' failed! Perhaps chat is deactivated or uninstalled, and this javascript is in the browsercache');
            
            this.chatDeactivated = true;
            jQuery('#chatbar-button-online').text(this.getConfigParameter('chat.bar.deactivated'));
            this.setStatus('xa');
            jQuery('#chatbar .chatbar-box').hide();
        }
    }
    
    ChatBar.prototype.getHeartbeatCount = function(){  
        return this.heartBeatCount;
    }
    
    ChatBar.prototype.setDebugMode = function(debug){  
        this.debug = debug;
    }
    
    ChatBar.prototype.isOpen = function(){
        var cookie = 'cbar-open';
        var open = AJS.Cookie.read(cookie);
        if(open  == "true"){
            return true
        }else{
            return false;
        }
    }
    ChatBar.prototype.minimize = function () {
        AJS.Cookie.erase("cbar-open")
        this.bar.removeClass('open');
    }
    ChatBar.prototype.maximize = function () {
        var cookie = 'cbar-open';
        this.bar.addClass('open');
        AJS.Cookie.save(cookie,"true");
    }
    
    ChatBar.prototype.getOriginalTitle = function(){  
        return this.originalTitle;
    }
    ChatBar.prototype.startChatSession = function(){  
        var that = this;
        
        var pageId = null;
        if(typeof(AJS.params.pageId) != "undefined"){
            pageId = AJS.params.pageId;
        }
        if(AJS.params.remoteUser && !that.chatDeactivated){
            jQuery.ajax({
                url: AJS.contextPath()+"/chat/start.action",
                cache: false,
                dataType: "json",
                data: {
                    spaceKey : that.spaceKey,
                    currentUrl : window.location.href,
                    currentTitle: document.title,
                    status : that.configurationBox.find('select[name=status]').val(),
                    pageId: pageId
                },
                error: function(){
                    that.requestErrorHandler();
                },
                success: function(data) {
                    that.requestSuccessHandler();
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
    }
    
    ChatBar.prototype.chatHeartbeat = function(){
        if(this.chatDeactivated){
            return;
        }
        var that = this;
        this.heartBeatCount++;
        
        if(typeof(chatBar) != "undefined"){
            if(!this.isOnline()){
                return;
            }
        }
        
        jQuery.ajax({
            url: AJS.contextPath()+"/chat/heartbeat.action",
            cache: false,
            dataType: "json",
            data: {
                spaceKey : that.spaceKey ,
                lr: that.lastHeartBeatServerdate,
                mm: that.mousemove 
            },
            error: function(){
                that.requestErrorHandler();
            },        
            success: function(data) {
                that.requestSuccessHandler();
                that.lastHeartBeatServerdate = data.lr;
                if(typeof(data.chatboxes) != "undefined"){
                    that.retrieveChatMessages(data.chatboxes);
                }
            }
        });
        this.mousemove = false;
    }
    /**
         *
         */
    ChatBar.prototype.closeOldestChatBox = function() {
        this.log('closeOldestChatBox ');
        var lastBox= null;
        for (var x in this.chatBoxes) {
            if(isChatBox(this.chatBoxes[x])){
                if (!this.chatBoxes[x].isClosed()) {
                    if(lastBox == null){
                        lastBox = this.chatBoxes[x];
                    } else {
                        if(this.chatBoxes[x].isOlderThan(lastBox)){
                            lastBox = this.chatBoxes[x];
                        }
                    }
                }
            }
        }
        this.log(lastBox);
        if(isChatBox(lastBox)){
            lastBox.closeChatBox();
            return true;
        }
        return false;
    }
    
    ChatBar.prototype.restructureChatBoxes = function() {
        
        var winWidth = jQuery(window).width() - ConfluenceChatConfig.barWidth;
        
        var widthOneBox = ConfluenceChatConfig.chatBoxWidth + ConfluenceChatConfig.margin;
        var maxWindows = Math.floor(winWidth/widthOneBox);
        if(maxWindows <= 0){
            maxWindows = 1;
        }
        this.log("restructureChatBoxes maxWindows: " + maxWindows);
        
        // how many boxes are there ?
        var countChatBox = 0;
        for (var x in this.chatBoxes) {
            if(isChatBox(this.chatBoxes[x])){
                if (!this.chatBoxes[x].isClosed()) {
                    countChatBox ++;
                }
            }
        }
        var closeBoxes = countChatBox - maxWindows;
        if(closeBoxes > 0){
            this.log(' need to close ' + closeBoxes +' chatboxes, because place is not enough');
            for (var i=0; i< closeBoxes; i++){
                this.closeOldestChatBox();
            }
        }
        
        
        var chatBoxesPos= 0;
        for (var x in this.chatBoxes) {
            if(isChatBox(this.chatBoxes[x])){
                if (!this.chatBoxes[x].isClosed()) {
                    this.chatBoxes[x].position(chatBoxesPos);
                    chatBoxesPos++;
                }
            }else {
                this.log('restructureChatBoxes is no chatBox:');
                this.log(this.chatBoxes[x]);
            }
        }
    }
    
    ChatBar.prototype.log = function(msg){
        if(this.debug){
            AJS.log(msg);    
        }
    };
    
    ChatBar.prototype.showConfig = function(){
        this.bar.addClass('config');
        this.bar.removeClass('users');
    }
    ChatBar.prototype.showUser = function(){
        this.bar.removeClass('config');
        this.bar.addClass('users');
    }
    
    ChatBar.prototype.init = function(){
        var that = this;
        this.bar = jQuery('#chatbar');
        this.version = this.getConfigParameter('chat-version');
        this.debug = "true" == this.getConfigParameter('chat-debugMode');
        this.hideInEditMode = "true" == this.getConfigParameter('chat-hideInEditMode');
        AJS.log('Init Confluence Chat in version: ' + this.version);
        if(this.hideInEditMode){
            this.chatDeactivated = this.chatDeactivated  
            || jQuery('body').hasClass('edit') 
            || jQuery('body').hasClass('scaffoldingeditor') 
            || jQuery('body').hasClass('create');
            if(this.chatDeactivated ){
                this.log('Confluence Chat: Hide the bar in editor');
                this.bar.hide();
            }
        }else {
            this.log('Confluence Chat: Show the bar in editor');
        }
        
        this.username = AJS.params.remoteUser;
        this.onlineUsersBox =  this.bar.find('#chatbar-online-users');
        this.configurationBox =  this.bar.find('#chatbar-config');
        this.bar.find('#chatbar-buttons').click(function(event){
            if(that.chatDeactivated){
                that.bar.removeClass('open');    
                return false;
            }
            
            that.bar.toggleClass('open');
            if(that.bar.hasClass('open')){
                that.maximize();
            }else {
                that.minimize();    
            }
            
            event.preventDefault();
        });
        this.bar.find('.cb-close').click(function(){
            that.bar.removeClass('open');   
            that.minimize();
            return false;
        });
        
        this.bar.find('.cb-config').click(function(){
            that.showConfig();
            return false;
        });
        
        this.bar.find('.cb-users').click(function(){
            that.showUser();
            return false;
        });
        this.showUser();
        
        this.configurationBox.find('select, input ').change(function(){
            var status = jQuery(this).val();
            jQuery.ajax({
                url: AJS.contextPath()+"/chat/setstatus.action",
                cache: false,
                data: {
                    status:  that.configurationBox.find('select[name=status]').val()
                },
                dataType: "json",
                success: function(data){
                    that.setStatus(status);
                }
            });
        });
        
        this.chatOnlineUserDiv = this.onlineUsersBox.find('#chatbar-online-users-list');
        this.chatBox = this.chatOnlineUserDiv.find('.chat-user').clone(true);
        this.chatOnlineUserDiv.empty();
        
        
        if(!this.chatDeactivated ){
            var soundDeactivated = AJS.Cookie.read("chatsoundoff");
            if(soundDeactivated  == "true"  || (jQuery('html.audio').size() == 0)){
                that.deactivateSound(); 
            }else {
                that.activateSound(); 
            }
            if(jQuery('html.audio').size() == 0){
                this.bar.find('.csound').hide();
            }else {
                this.bar.find('.csound').click(function(){
                    if(that.isSound()){
                        that.deactivateSound();       
                    }else {
                        that.activateSound()
                    }
                });
            }
            
            this.getOnlineUsers();  
            this.intervall = setInterval(function(){
                that.getOnlineUsers();
            }, 5000);
            
            jQuery(window).mousemove(function(){
                that.mousemove = true;
                var chatStatus = that.bar.find('#chatbar-status');
                if(jQuery.trim(chatStatus.attr('oldStatus')).length > 0){
                    chatStatus.attr('class',chatStatus.attr('oldStatus'));
                    chatStatus.removeAttr('oldStatus');
                }
            }).resize(function(){
                that.restructureChatBoxes();
            });
            this.startChatSession();
            this.bindChatWithLinks();
            if(this.isOpen()){
                this.maximize();
            }
        }
    }
    
    ChatBar.prototype.getChatBoxes = function(){
        return this.chatBoxes;
    }
    
    ChatBar.prototype.isOnline = function(){
        return  this.bar.hasClass("online") && !this.chatDeactivated;   
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
                url: AJS.contextPath()+"/chat/getonlineuser.action",
                data: {
                    spaceKey : that.spaceKey ,
                    status : that.configurationBox.find('select[name=status]').val()
                },
                cache: false,
                dataType: "json",
                error: function(){
                    that.requestErrorHandler();
                },            
                success: function(data){
                    that.requestSuccessHandler();
                    that.refreshUser(data);
                }
            });
        }
    }
    
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
        this.log('Chat with:  '+ opts.dispayTitle + ' chatId: '+ opts.chatBoxId);
        
        if(chatBoxId != null){
            if(!isChatBox(this.chatBoxes[chatBoxId])){
                this.chatBoxes[chatBoxId] = new ChatBox(opts);
            }
            if(isChatBox(this.chatBoxes[chatBoxId])){
                if(opts.focus){
                    this.chatBoxes[chatBoxId].show(); 
                    this.chatBoxes[chatBoxId].focusChatBox(); 
                }
                
                this.restructureChatBoxes();    
            }else {
                this.log('could not create chatBox for '+ opts.dispayTitle + ' chatId: '+ opts.chatBoxId);
            }
        }else {
            AJS.log('ChatBar.prototype.chatWith: no chatBoxId given');
        }
    }
    
    ChatBar.prototype.bindChatWithLinks = function(){
        var that = this;
        var links = jQuery('.chatuser-link:not([data-user-chat-bound=true])');
        links .click(function(){
            var link = jQuery(this);
            that.chatWith({
                chatBoxId: link.attr('chatboxid'),
                chatUserList : link.attr('data-username'),
                dispayTitle : link.text(),
                focus: true
            });
            return false;
        }).attr('data-user-chat-bound', true);
    }
    ChatBar.prototype.reorderUser = function(){
        var sortByFullname = function (a,b){
            var val = jQuery(a).find('.user-hover-trigger').text().toLowerCase() > jQuery(b).find('.user-hover-trigger').text().toLowerCase();
            if(val) {
                return 1
            }else {
                return -1
            }
        }
        var that = this;
        var reorderEl = function (el){
            var container = that.chatOnlineUserDiv;
            container.empty();
            el.each(function(){
                jQuery(this).appendTo(container);
            });
        }
        reorderEl(this.chatOnlineUserDiv.find('.chat-user').sort(sortByFullname));
        
        this.chatOnlineUserDiv.find('.chat-user').click(function(){
            that.chatWith({
                chatBoxId: jQuery(this).attr('chatBoxId'),
                chatUserList : jQuery(this).attr('username'),
                dispayTitle : jQuery(this).find('.chat-user-info span.user-hover-trigger').text(),
                focus: true
            });
        });
    
    
    }
    ChatBar.prototype.refreshUser = function(data){
        var that = this;
        var tmpAttr = 'chatOfflineMeFlag-'+Math.round(Math.random() * 10000);
        this.chatOnlineUserDiv.find('.chat-user').attr(tmpAttr, 'true');
        jQuery('.chatbox > div').addClass('unknown');
        this.users = Object();
        var ownUserInList = false;
        jQuery('.chatuser-link-holder > span').attr('class', 'xa');
        jQuery.each(data.users, function(j,user){
            var username = user.un;
            var chatBoxId = user.id;
            that.users[username] = user;
            if( that.username != user.un){
                var chatUser = that.chatOnlineUserDiv.find('.chat-user[chatBoxId='+chatBoxId+']');
                if(!chatUser.size()){
                    chatUser =  that.chatBox.clone(true);
                    chatUser.show();
                    chatUser.attr('chatBoxId', chatBoxId);
                    chatUser.attr('username', username);
                    chatUser.find('.chat-user-info .is').text(user.fn).addClass('user-hover-trigger').attr('data-username', username);
                    chatUser.find('.chat-where').addClass('chat-where'+chatBoxId);
                    that.chatOnlineUserDiv.append(chatUser);
                    that.reorderUser();
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
                    userWhere.find('a').attr('href', user.su).attr('title',user.st );
                    userWhere.find('span').text(title);
                    userWhere.show();
                } else {
                    userWhere.find('a').attr('href', '').attr('title','');
                    userWhere.find('span').text('');
                    userWhere.hide();
                }
            }else {
                ownUserInList = true;
                var chatStatus = that.bar.find('#chatbar-status');
                if(!chatStatus.hasClass(user.s)){
                    chatStatus.attr('oldStatus', chatStatus.attr('class'));
                    chatStatus.attr('class', user.s);
                }
            }
            jQuery('#chatbox_'+chatBoxId+' > div , .chatuser-link-holder[chatboxid='+chatBoxId+"] > span").attr('class', user.s);
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
        return this.bar.find('.parameters input[name="'+param+'"]').val();
    
    }
    
    ChatBar.prototype.initCompatibility= function(){
        /**
             * Compatibility with Task List
             * The task list elements flickers
             */
        jQuery(document).ready(function(){
            // aus confluence.extra.dynamictasklist2:web-resources.js
            var S="input.taskname-text, button.add-button, button.uncheck-all, select.sort-select, button.sort-order, "
            +"input.complete, p.taskname input, radio.high-priority, radio.medium-priority, radio.low-priority, input.assignee";
            if(jQuery('.task-list').size()){
                var intervall = null;
                AJS.log('Chat: Init compatibility with Task List');
                jQuery('body').ajaxSend(function(e, xhr, opt){
                    if(typeof(opt.url) != "undefined" && opt.url.search('/chat/') >= 0){
                        var items = jQuery(S);
                        clearInterval(intervall);
                        intervall = setInterval(function(){
                            items .removeAttr('disabled');
                        },1);
                    }
                
                });
                jQuery('body').ajaxStop(function(e, xhr, opt){
                    clearInterval(intervall);
                });
            }
        });
    }
    
    ChatBar.prototype.retrieveChatMessages= function(chatboxes){
        var that = this;
        jQuery.each(chatboxes, function(j,chatbox){
            that.log(chatbox)
            if(typeof(chatbox.messages) != "undefined"){
                if(isChatBox(that.chatBoxes[chatbox.id])){
                    chatBar.log("chatbox exists " + chatbox.id + " loop over messages");
                    jQuery.each(chatbox.messages, function(i,item){
                        if (item)	{
                            that.chatBoxes[chatbox.id].retrieveMessage(item);      
                        }
                    });
                }else{
                    chatBar.log("create chatbox " + chatbox.id);
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
                            };
                            if (typeof(item.f) != "undefined"){
                                if (typeof(item.f.un) != "undefined"){
                                    if (typeof(item.f.fn) != "undefined"){
                                        if (item.f.un == chatPartner){
                                            chatTitle  = item.f.fn;
                                        }
                                    }
                                }
                            } ;                   
                        };
                    });
                
                    if(chatTitle == ''){
                        chatTitle = chatPartner;
                    }
                    // create box
                    chatBar.log("create chatbox " + chatbox.id + " function call");
                    that.chatWith({
                        chatBoxId: chatbox.id,
                        chatUserList : chatPartner ,
                        dispayTitle : chatTitle,
                        open: chatbox.open,
                        messages : chatbox.messages
                    });
                }
            }
        });
    }
    
    function ChatBox(options){
        
        this.opt = jQuery.extend({
            chatBoxId: null,
            chatUserList: null,
            open: true,
            dispayTitle: null,
            messages: new Array()
        }, options);
        
        chatBar.log("create chatbox " + this.opt .chatBoxId);
        
        this.chatBoxId = this.opt.chatBoxId;
        this.chatUserList = this.opt.chatUserList;
        this.box = null;
        this.textarea = null;
        this.initialized = false;
        this.init();
        this.blinkInterval = null;
        this.lastMessageDate = new Date();
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
        
        chatBar.log("created chatbox init with " + len + " messages");
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
    
    ChatBox.prototype.isOlderThan = function(chatbox){
        if(isChatBox(chatbox)){
            if(this.lastMessageDate instanceof Date && chatbox.lastMessageDate instanceof Date){
                return this.lastMessageDate < chatbox.lastMessageDate;
            }
        }
        return false;
    
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
            try{
                if(chatBar.isSound()){
                    var audio = document.getElementById('chatsound');
                    audio.play();
                }
            }
            catch(err){
                chatBar.log("Error playing sound");
            }
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
        this.box.width(ConfluenceChatConfig.chatBoxWidth);
        
        
        var box = jQuery('<div/>').appendTo(this.box);
        
        
        var header =  jQuery('<div/>').addClass('cb-head');
        header.appendTo(box);
        
        //        var subheader =  jQuery('<div/>').addClass('cb-head-sub');
        //        subheader.appendTo(box );
        //        
        //        jQuery('<a/>').attr('href', '#').text('+').addClass('opt-history').click(function(){
        //            new ChatHistory(that.opt);
        //        }).appendTo(subheader );
        
        var options = jQuery('<div/>').addClass('cb-opt');
        options.appendTo(header);
        
        
        
        var dropdownHolder = jQuery('<div/>');
        dropdownHolder.addClass('cb-actions-more');
        
        var dropdown = jQuery('<ul/>').addClass('cb-dropdown');
        dropdown.appendTo(dropdownHolder);
        var parent= jQuery('<li/>').addClass('aui-dd-parent');
        parent.appendTo(dropdown);
        var trigger =  jQuery('<a/>').attr('href', '#').addClass('aui-dd-trigger');
        trigger.attr('title', chatBar.getConfigParameter('chat.box.more'));
        trigger.appendTo(parent);
        
        
        var dropdownList = jQuery('<ul/>').addClass('aui-dropdown');
        dropdownList.appendTo(parent);
        var dropdownItem= jQuery('<li/>').addClass('dropdown-item');
        dropdownItem.appendTo(dropdownList);
        var dropdownItemLink =  jQuery('<a/>').attr('href', '#').text(chatBar.getConfigParameter('chat.box.more.history')).addClass('item-link').click(function(e){
            that.deleteHistory();
            return true;
        })
        dropdownItemLink.appendTo(dropdownItem);
        dropdownHolder.appendTo(options);
        dropdown.dropDown("Standard", {
            alignment : 'right'
        });
        
        jQuery('<a/>').attr('href', '#')
        .text('+')
        .attr('title', chatBar.getConfigParameter('chat.icon.max'))
        .addClass('opt-max').click(function(){
            that.toggleChatBoxGrowth();
        }).appendTo(options);
        
        
        
        jQuery('<a/>').attr('href', '#')
        .text('-')
        .attr('title', chatBar.getConfigParameter('chat.icon.min'))
        .addClass('opt-min').click(function(){
            that.toggleChatBoxGrowth();
        }).appendTo(options);
        
        jQuery('<a/>').attr('href', '#')
        .text('X')
        .attr('title', chatBar.getConfigParameter('chat.icon.close'))
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
        });
        jQuery('<div/>').addClass('cb-input').append(this.textarea).appendTo(contentHolder);
        this.box.appendTo(jQuery( "body" ));
        
        if (this.minimizeChatBox == 1 || this.isMinimized()) {
            this.minimize();
        }
        
        this.box.focus(function(){
            that.stopBlink();
        }).mouseover(function(){
            that.stopBlink();
        });
        this.box.find('textarea').chatAutogrow();
    
    }
    ChatBox.prototype.show= function() {
        if(this.initialized){
            if(this.isClosed()){
                this.box.removeClass('closed');
                this.lastMessageDate = new Date();
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
        var width = (number*(ConfluenceChatConfig.chatBoxWidth + ConfluenceChatConfig.margin))+250;
        this.box.css('right', width+'px');
    }
    
    ChatBox.prototype.closeChatBox = function() {
        this.hide();
        chatBar.restructureChatBoxes();
        jQuery.post(AJS.contextPath()+"/chat/close.action", {
            close: this.chatUserList
        } );
    }
    
    ChatBox.prototype.deleteHistory= function () {
        var that = this;
        if(confirm('Delete this history?')){
            jQuery.post(AJS.contextPath()+"/chat/delete.action", {
                deleteBox: this.chatUserList
            } , function(){
                that.box.find('.cb-content').empty();
            });
        }
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
        var that = this;
        var message = AJS.escapeHtml(this.textarea .val());
        this.textarea .val('').focus().css('height','44px');
        if (message != '') {
            jQuery.post(AJS.contextPath()+"/chat/send.action", {
                spaceKey : AJS.params.spaceKey ,
                to: this.chatUserList, 
                message: message, 
                id: Math.round(Math.random() * 10000000) + "" + that.chatBoxId
            } );
        }
        return false;
    }
    ChatBox.prototype.retrieveMessage = function(item){
        if(item == null){
            return;
        }

        if(this.isClosed() && this.initialized){
            this.show();
            chatBar.restructureChatBoxes();
        }        
        
        var id = "";
        // check if messages is already added 
        if(typeof(item.id) != "undefined"){
            id = "cm"+item.id;
            if(id.length > 0){
                if($("#"+id).size()){
                    return;
                }
            }
        }
        
        if(item.f.un != AJS.params.remoteUser){
            this.startBlink();    
        }
        

        var message = this.replaceChatMessage(item.m);
        var content = this.box.find('.cb-content');
        
        var dt = new Date(item.t);
        this.lastMessageDate = dt;
        
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
        // check if message is already added
        if(id.length > 0){
            messageItem.attr('id', id);    
        }
        messageItem.appendTo(messageHolder);
        
        content.scrollTop(content[0].scrollHeight);    
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
            '<img title="'+emoticons[match]+'" src="'+AJS.contextPath()+'/download/resources/confluence.chat/clear.png" class="smiley '+emoticons[match]+'"/>' :
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
    
    
    function ChatHistory(options){
        this.opt = jQuery.extend({
            chatBoxId: null,
            chatUserList: null,
            open: true,
            dispayTitle: null,
            messages: new Array()
        }, options);
        this.chatBoxId = this.opt.chatBoxId;
        this.init();
    }
    
    ChatHistory.prototype.init=  function(){
        
        //        create a dialog 860px wide x 530px high
        var dialog = new AJS.Dialog({
            width:860, 
            height:530, 
            id:"example-dialog", 
            closeOnOutsideClick: true
        });
        
        //        PAGE 0 (first page)
        //        adds header for first page
        dialog.addHeader("Dialog - Page 0");
        
        //        add panel 1
        dialog.addPanel("Panel 1", "<p>Some content for panel 1. This has no padding.</p>", "panel-body");
        dialog.get("panel:0").setPadding(0);
        
        dialog.addButton("Next", function (dialog) {
            
            });
        
        dialog.addButton("Next", function (dialog) {
            
            });
        dialog.addButton("Cancel", function (dialog) {
            dialog.hide();
        });
        
        
        dialog.addPage();
        
        //        Add events to dialog trigger elements
        
        dialog.gotoPage(0);
        dialog.gotoPanel(0);
        dialog.show();
    }
    
    
    var chatBar = new ChatBar();
    ConfluenceChatAPI.isOnline = function(){
        return chatBar.isOnline();
    }
    ConfluenceChatAPI.getStatusOfUser = function(username){
        return chatBar.getStatusOfUser(username);
    }
    
    ConfluenceChatAPI.getVersion = function(){
        return chatBar.version;
    }
    
    ConfluenceChatAPI.enableDebugMode = function(){
        return chatBar.setDebugMode(true);
    }


//    ConfluenceChatAPI.showHistory = function(username){
//        new ChatHistory({
//            displayTitle: username
//        });
//    }

})(jQuery);