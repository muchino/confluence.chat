
var windowFocus = true;
var username;
var chatHeartbeatCount = 0;
var minChatHeartbeat = 1000;
var maxChatHeartbeat = 33000;
var chatHeartbeatTime = minChatHeartbeat;
var originalTitle;
var blinkOrder = 0;

var chatboxFocus = new Array();
var newMessages = new Array();
var newMessagesWin = new Array();
var chatBoxes = new Array();

jQuery(document).ready(function(){
    originalTitle = document.title;
    startChatSession();
    showOnlineUsers();

    jQuery([window, document]).blur(function(){
        windowFocus = false;
    }).focus(function(){
        windowFocus = true;
        document.title = originalTitle;
    });
});

function printChatUserDropDown(data){
    if(jQuery('#confluence-chat').size()== 0 ){
        
        var chatBar =   jQuery('<div/>') .attr('id', 'confluence-chat');
        var usernameDropDown =  jQuery('<ul/>') .attr('id', 'confluence-chat-dropdown').appendTo(chatBar);
        chatBar.appendTo('body');
        chatBar.css({
            bottom: '22px',
            display: 'block',
            'list-style': 'none outside none',
            margin: 0,
            padding: 0,
            position: 'absolute',
            right: '5px',
            width: '44px'
        }).attr('enter', 0);
        
        usernameDropDown.css({
            'list-style': 'none outside none',
            margin: 0,
            padding: 0,
        })
        usernameDropDown.mouseenter(function(){
            jQuery(this).attr('enter', 1);
        }).mouseleave(function(){
            jQuery(this).attr('enter', 0);
        })
    }
    var usernameDropDown = jQuery('#confluence-chat #confluence-chat-dropdown');
    if(usernameDropDown.attr('enter') != 1){
        usernameDropDown.empty();
        jQuery.each(data.users, function(i,user){
            var userLI =  jQuery('<li/>').appendTo(usernameDropDown);
            var link = jQuery('<a href="#" />')
            .text(user.username)
            .attr('chatuser', user.username)
            .css('color', '#000');
                
            link.click(function(){
                chatWith(jQuery(this).attr('chatuser'));
            })
            link.appendTo(userLI);
        });
    }
}

function showOnlineUsers(){
    jQuery.ajax({
        url: getBaseUrl()+"/chat/getonlineuser.action",
        cache: false,
        dataType: "json",
        success: printChatUserDropDown
    });
}

function restructureChatBoxes() {
    align = 0;
    for (x in chatBoxes) {
        chatboxtitle = chatBoxes[x];

        if (jQuery("#chatbox_"+chatboxtitle).css('display') != 'none') {
            if (align == 0) {
                jQuery("#chatbox_"+chatboxtitle).css('right', '20px');
            } else {
                width = (align)*(225+7)+20;
                jQuery("#chatbox_"+chatboxtitle).css('right', width+'px');
            }
            align++;
        }
    }
}

function chatWith(chatuser) {
    createChatBox(chatuser);
    jQuery("#chatbox_"+chatuser+" .chatboxtextarea").focus();
}

function createChatBox(chatboxtitle,minimizeChatBox) {
    if (jQuery("#chatbox_"+chatboxtitle).length > 0) {
        if (jQuery("#chatbox_"+chatboxtitle).css('display') == 'none') {
            jQuery("#chatbox_"+chatboxtitle).css('display','block');
            restructureChatBoxes();
        }
        jQuery("#chatbox_"+chatboxtitle+" .chatboxtextarea").focus();
        return;
    }

    jQuery(" <div />" ).attr("id","chatbox_"+chatboxtitle)
    .addClass("chatbox")
    .html('<div class="chatboxhead"><div class="chatboxtitle">'+chatboxtitle+'</div><div class="chatboxoptions"><a href="javascript:void(0)" onclick="javascript:toggleChatBoxGrowth(\''+chatboxtitle+'\')">-</a> <a href="javascript:void(0)" onclick="javascript:closeChatBox(\''+chatboxtitle+'\')">X</a></div><br clear="all"/></div><div class="chatboxcontent"></div><div class="chatboxinput"><textarea class="chatboxtextarea" onkeydown="javascript:return checkChatBoxInputKey(event,this,\''+chatboxtitle+'\');"></textarea></div>')
    .appendTo(jQuery( "body" ));
			   
    jQuery("#chatbox_"+chatboxtitle).css('bottom', '0px');
	
    chatBoxeslength = 0;

    for (x in chatBoxes) {
        if (jQuery("#chatbox_"+chatBoxes[x]).css('display') != 'none') {
            chatBoxeslength++;
        }
    }

    if (chatBoxeslength == 0) {
        jQuery("#chatbox_"+chatboxtitle).css('right', '20px');
    } else {
        width = (chatBoxeslength)*(225+7)+20;
        jQuery("#chatbox_"+chatboxtitle).css('right', width+'px');
    }
	
    chatBoxes.push(chatboxtitle);

    if (minimizeChatBox == 1) {
        minimizedChatBoxes = new Array();

        if (AJS.Cookie.read('chatbox_minimized')) {
            minimizedChatBoxes = AJS.Cookie.read('chatbox_minimized').split(/\|/);
        }
        minimize = 0;
        for (j=0;j<minimizedChatBoxes.length;j++) {
            if (minimizedChatBoxes[j] == chatboxtitle) {
                minimize = 1;
            }
        }

        if (minimize == 1) {
            jQuery('#chatbox_'+chatboxtitle+' .chatboxcontent').css('display','none');
            jQuery('#chatbox_'+chatboxtitle+' .chatboxinput').css('display','none');
        }
    }

    chatboxFocus[chatboxtitle] = false;

    jQuery("#chatbox_"+chatboxtitle+" .chatboxtextarea").blur(function(){
        chatboxFocus[chatboxtitle] = false;
        jQuery("#chatbox_"+chatboxtitle+" .chatboxtextarea").removeClass('chatboxtextareaselected');
    }).focus(function(){
        chatboxFocus[chatboxtitle] = true;
        newMessages[chatboxtitle] = false;
        jQuery('#chatbox_'+chatboxtitle+' .chatboxhead').removeClass('chatboxblink');
        jQuery("#chatbox_"+chatboxtitle+" .chatboxtextarea").addClass('chatboxtextareaselected');
    });

    jQuery("#chatbox_"+chatboxtitle).click(function() {
        if (jQuery('#chatbox_'+chatboxtitle+' .chatboxcontent').css('display') != 'none') {
            jQuery("#chatbox_"+chatboxtitle+" .chatboxtextarea").focus();
        }
    });

    jQuery("#chatbox_"+chatboxtitle).show();
}


function chatHeartbeat(){
    var itemsfound = 0;
	
    if (windowFocus == false) {
 
        var blinkNumber = 0;
        var titleChanged = 0;
        for (x in newMessagesWin) {
            if (newMessagesWin[x] == true) {
                ++blinkNumber;
                if (blinkNumber >= blinkOrder) {
                    document.title = x+' says...';
                    titleChanged = 1;
                    break;	
                }
            }
        }
		
        if (titleChanged == 0) {
            document.title = originalTitle;
            blinkOrder = 0;
        } else {
            ++blinkOrder;
        }

    } else {
        for (x in newMessagesWin) {
            newMessagesWin[x] = false;
        }
    }

    for (x in newMessages) {
        if (newMessages[x] == true) {
            if (chatboxFocus[x] == false) {
                //FIXME: add toggle all or none policy, otherwise it looks funny
                jQuery('#chatbox_'+x+' .chatboxhead').toggleClass('chatboxblink');
            }
        }
    }
	
    jQuery.ajax({
        url: getBaseUrl()+"/chat/heartbeat.action",
        cache: false,
        dataType: "json",
        success: function(data) {
            jQuery.each(data.chatboxes, function(j,chatbox){
                var chatboxtitle = chatbox.usernameOfChatPartner;
                jQuery.each(chatbox.messages, function(i,item){
                    if (item)	{ // fix strange ie bug
                        if (jQuery("#chatbox_"+chatboxtitle).length <= 0) {
                            createChatBox(chatboxtitle);
                        }
                        if (jQuery("#chatbox_"+chatboxtitle).css('display') == 'none') {
                            jQuery("#chatbox_"+chatboxtitle).css('display','block');
                            restructureChatBoxes();
                        }
                        newMessages[chatboxtitle] = true;
                        newMessagesWin[chatboxtitle] = true;
                        var message = replaceChatMessage(item.message);
                        jQuery("#chatbox_"+chatboxtitle+" .chatboxcontent").append('<div class="chatboxmessage"><span class="chatboxmessagefrom">'+item.from+':&nbsp;&nbsp;</span><span class="chatboxmessagecontent">'+message+'</span></div>');
                        jQuery("#chatbox_"+chatboxtitle+" .chatboxcontent").scrollTop(jQuery("#chatbox_"+chatboxtitle+" .chatboxcontent")[0].scrollHeight);
                        itemsfound += 1;
                    }
                });
            
            });

            chatHeartbeatCount++;

            if (itemsfound > 0) {
                chatHeartbeatTime = minChatHeartbeat;
                chatHeartbeatCount = 1;
            } else if (chatHeartbeatCount >= 10) {
                chatHeartbeatTime *= 2;
                chatHeartbeatCount = 1;
                if (chatHeartbeatTime > maxChatHeartbeat) {
                    chatHeartbeatTime = maxChatHeartbeat;
                }
            }
		
            printChatUserDropDown(data);
            setTimeout('chatHeartbeat();',chatHeartbeatTime);
        }
    });
}

function closeChatBox(chatboxtitle) {
    jQuery('#chatbox_'+chatboxtitle).css('display','none');
    restructureChatBoxes();

    jQuery.post(getBaseUrl()+"/chat/close.action", {
        close: chatboxtitle
    } );

}

function toggleChatBoxGrowth(chatboxtitle) {
    if (jQuery('#chatbox_'+chatboxtitle+' .chatboxcontent').css('display') == 'none') {  
		
        var minimizedChatBoxes = new Array();
		
        if (AJS.Cookie.read('chatbox_minimized')) {
            minimizedChatBoxes = AJS.Cookie.read('chatbox_minimized').split(/\|/);
        }

        var newCookie = '';

        for (i=0;i<minimizedChatBoxes.length;i++) {
            if (minimizedChatBoxes[i] != chatboxtitle) {
                newCookie += chatboxtitle+'|';
            }
        }

        newCookie = newCookie.slice(0, -1)


        AJS.Cookie.save('chatbox_minimized', newCookie);
        jQuery('#chatbox_'+chatboxtitle+' .chatboxcontent').css('display','block');
        jQuery('#chatbox_'+chatboxtitle+' .chatboxinput').css('display','block');
        jQuery("#chatbox_"+chatboxtitle+" .chatboxcontent").scrollTop(jQuery("#chatbox_"+chatboxtitle+" .chatboxcontent")[0].scrollHeight);
    } else {
		
        var newCookie = chatboxtitle;

        if (AJS.Cookie.read('chatbox_minimized')) {
            newCookie += '|'+AJS.Cookie.read('chatbox_minimized');
        }


        AJS.Cookie.save('chatbox_minimized',newCookie);
        jQuery('#chatbox_'+chatboxtitle+' .chatboxcontent').css('display','none');
        jQuery('#chatbox_'+chatboxtitle+' .chatboxinput').css('display','none');
    }
	
}

function checkChatBoxInputKey(event,chatboxtextarea,chatboxtitle) {
	 
    if(event.keyCode == 13 && event.shiftKey == 0)  {
        var message = AJS.escapeHtml(jQuery(chatboxtextarea).val());
        //        message = message.replace(/^\s+|\s+jQuery/g,"");


        jQuery(chatboxtextarea).val('');
        jQuery(chatboxtextarea).focus();
        jQuery(chatboxtextarea).css('height','44px');
        if (message != '') {
            jQuery.post(getBaseUrl()+"/chat/send.action", {
                to: chatboxtitle, 
                message: message
            } , function(data){
                jQuery.each(data.chatboxes, function(j,chatbox){
                    var chatboxtitle = chatbox.usernameOfChatPartner;
                    jQuery.each(chatbox.messages, function(i,item){
                        if (item)	{ // fix strange ie bug
                            if (jQuery("#chatbox_"+chatboxtitle).length <= 0) {
                                createChatBox(chatboxtitle);
                            }
                            if (jQuery("#chatbox_"+chatboxtitle).css('display') == 'none') {
                                jQuery("#chatbox_"+chatboxtitle).css('display','block');
                                restructureChatBoxes();
                            }
                            //                            newMessages[chatboxtitle] = true;
                            //                            newMessagesWin[chatboxtitle] = true;
                            var message = replaceChatMessage(item.message);
                            jQuery("#chatbox_"+chatboxtitle+" .chatboxcontent").append('<div class="chatboxmessage"><span class="chatboxmessagefrom">'+item.from+':&nbsp;&nbsp;</span><span class="chatboxmessagecontent">'+message+'</span></div>');
                            jQuery("#chatbox_"+chatboxtitle+" .chatboxcontent").scrollTop(jQuery("#chatbox_"+chatboxtitle+" .chatboxcontent")[0].scrollHeight);
                        //                            itemsfound += 1;
                        }
                    });
            
                });
            });
        }
        chatHeartbeatTime = minChatHeartbeat;
        chatHeartbeatCount = 1;

        return false;
    }

    var adjustedHeight = chatboxtextarea.clientHeight;
    var maxHeight = 94;

    if (maxHeight > adjustedHeight) {
        adjustedHeight = Math.max(chatboxtextarea.scrollHeight, adjustedHeight);
        if (maxHeight)
            adjustedHeight = Math.min(maxHeight, adjustedHeight);
        if (adjustedHeight > chatboxtextarea.clientHeight)
            jQuery(chatboxtextarea).css('height',adjustedHeight+8 +'px');
    } else {
        jQuery(chatboxtextarea).css('overflow','auto');
    }
	 
}

function startChatSession(){  
    jQuery.ajax({
        url: getBaseUrl()+"/chat/start.action",
        cache: false,
        dataType: "json",
        success: function(data) {
            
            jQuery('#chatbar').livequery(function(){
                
            
            jQuery.each(data.chatboxes, function(j,chatbox){
                var chatboxtitle = chatbox.usernameOfChatPartner;
                jQuery.each(chatbox.messages, function(i,item){
                    if (item)	{ // fix strange ie bug
                        if (jQuery("#chatbox_"+chatboxtitle).length <= 0) {
                            createChatBox(chatboxtitle,1);
                        }
                        var message = replaceChatMessage(item.message);
                        jQuery("#chatbox_"+chatboxtitle+" .chatboxcontent").append('<div class="chatboxmessage"><span class="chatboxmessagefrom">'+item.from+':&nbsp;&nbsp;</span><span class="chatboxmessagecontent">'+message+'</span></div>');
                    }
                });
            });
		
            for (i=0;i<chatBoxes.length;i++) {
                chatboxtitle = chatBoxes[i];
                jQuery("#chatbox_"+chatboxtitle+" .chatboxcontent").scrollTop(jQuery("#chatbox_"+chatboxtitle+" .chatboxcontent")[0].scrollHeight);
                setTimeout('jQuery("#chatbox_"+chatboxtitle+" .chatboxcontent").scrollTop(jQuery("#chatbox_"+chatboxtitle+" .chatboxcontent")[0].scrollHeight);', 100); // yet another strange ie bug
            }
	
            setTimeout('chatHeartbeat();',chatHeartbeatTime);
            });
		
        }
    });
}

function replaceChatMessage(text){
    text = urlify(text);
    text = replaceEmoticons(text);
    return text;
    
}

function replaceEmoticons(text) {
    var emoticons = {
        ':-)' : 'happy',
        ':)'  : 'happy',
        ':]'  : 'happy',
        '=)'  : 'happy',
        ':-*' : 'kiss',
        ':*' : 'kiss',
        ';-)*' : 'winking',
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
        '<img title="'+match+'" src="'+getBaseUrl()+'/download/resources/confluence.chat/clear.png" class="smiley '+emoticons[match]+'"/>' :
        match;
    });
}

function urlify(text) {
    var urlRegex = /(https?:\/\/[^\s]+)/g;
    return text.replace(urlRegex, function(url) {
        return '<a href="' + url + '" target="_blank" title="'+url+'">' + url + '</a>';
    })
// or alternatively
// return text.replace(urlRegex, '<a href="$1">$1</a>')
}

