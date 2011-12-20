
var windowFocus = true;
var originalTitle;
var blinkOrder = 0;

var chatboxFocus = new Array();
var newMessages = new Array();
var newMessagesWin = new Array();
var chatBoxes = new Array();

jQuery(document).ready(function(){
    if(AJS.params.remoteUser){
        originalTitle = document.title;
        startChatSession();
        jQuery([window, document]).blur(function(){
            windowFocus = false;
        }).focus(function(){
            windowFocus = true;
            document.title = originalTitle;
        });
    }
});

function restructureChatBoxes() {
    var  align = 0;
    for (var x in chatBoxes) {
        var chatboxtitleMd5 = chatBoxes[x];

        if (jQuery("#chatbox_"+chatboxtitleMd5).css('display') != 'none') {
            if (align == 0) {
                jQuery("#chatbox_"+chatboxtitleMd5).css('right', '250px');
            } else {
                var width = (align)*(225+7)+250;
                jQuery("#chatbox_"+chatboxtitleMd5).css('right', width+'px');
            }
            align++;
        }
    }
}

function chatWith(chatuser) {
    AJS.log(chatuser);
    var chatuserMd5 = jQuery.md5(chatuser);
    createChatBox(chatuserMd5  , chatuser);
    jQuery("#chatbox_"+chatuserMd5+" .chatboxtextarea").focus();
}

function createChatBox(chatboxtitleMd5, chatboxtitle , minimizeChatBox) {
    if (jQuery("#chatbox_"+chatboxtitleMd5).length > 0) {
        if (jQuery("#chatbox_"+chatboxtitleMd5).css('display') == 'none') {
            jQuery("#chatbox_"+chatboxtitleMd5).css('display','block');
            restructureChatBoxes();
        }
        jQuery("#chatbox_"+chatboxtitleMd5+" .chatboxtextarea").focus();
        return;
    }

    jQuery(" <div />" ).attr("id","chatbox_"+chatboxtitleMd5)
    .addClass("chatbox")
    .html('<div class="chatboxhead"><div class="chatboxtitle">'+chatboxtitle+'</div><div class="chatboxoptions"><a href="javascript:void(0)" onclick="javascript:toggleChatBoxGrowth(\''+chatboxtitleMd5+'\')">-</a> <a href="javascript:void(0)" onclick="javascript:closeChatBox(\''+chatboxtitleMd5+'\')">X</a></div><br clear="all"/></div><div class="chatboxcontent"></div><div class="chatboxinput"><textarea class="chatboxtextarea" onkeydown="javascript:return checkChatBoxInputKey(event,this,\''+chatboxtitle+'\');"></textarea></div>')
    .appendTo(jQuery( "body" ));
			   
    jQuery("#chatbox_"+chatboxtitleMd5).css('bottom', '0px');
	
    chatBoxeslength = 0;

    for (x in chatBoxes) {
        if (jQuery("#chatbox_"+chatBoxes[x]).css('display') != 'none') {
            chatBoxeslength++;
        }
    }

    if (chatBoxeslength == 0) {
        jQuery("#chatbox_"+chatboxtitleMd5).css('right', '250px');
    } else {
        width = (chatBoxeslength)*(225+7)+250;
        jQuery("#chatbox_"+chatboxtitleMd5).css('right', width+'px');
    }
	
    chatBoxes.push(chatboxtitleMd5);

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
            jQuery('#chatbox_'+chatboxtitleMd5+' .chatboxcontent').css('display','none');
            jQuery('#chatbox_'+chatboxtitleMd5+' .chatboxinput').css('display','none');
        }
    }

    chatboxFocus[chatboxtitleMd5] = false;

    jQuery("#chatbox_"+chatboxtitleMd5+" .chatboxtextarea").blur(function(){
        chatboxFocus[chatboxtitleMd5] = false;
        jQuery("#chatbox_"+chatboxtitleMd5+" .chatboxtextarea").removeClass('chatboxtextareaselected');
    }).focus(function(){
        chatboxFocus[chatboxtitleMd5] = true;
        newMessages[chatboxtitleMd5] = false;
        jQuery('#chatbox_'+chatboxtitleMd5+' .chatboxhead').removeClass('chatboxblink');
        jQuery("#chatbox_"+chatboxtitleMd5+" .chatboxtextarea").addClass('chatboxtextareaselected');
    });

    jQuery("#chatbox_"+chatboxtitleMd5).click(function() {
        if (jQuery('#chatbox_'+chatboxtitleMd5+' .chatboxcontent').css('display') != 'none') {
            jQuery("#chatbox_"+chatboxtitleMd5+" .chatboxtextarea").focus();
        }
    });

    jQuery("#chatbox_"+chatboxtitleMd5).show();
}


function chatHeartbeat(){
    // quick fix, bis es in chatbar integriert ist
    
    if(typeof(chatBar) != "undefined"){
        if(!chatBar.isOnline()){
            return;
        }
    }
    
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
            if(typeof(data.chatboxes) != "undefined"){
                jQuery.each(data.chatboxes, function(j,chatbox){
                    var fullname = chatbox.un;
                    var chatboxtitleMd5 = jQuery.md5(fullname);
                    if(typeof(chatbox.f) != "undefined" && chatbox.f != ""){
                        fullname = chatbox.f;
                    }
                    if(typeof(chatbox.messages) != "undefined"){
                        jQuery.each(chatbox.messages, function(i,item){
                            if (item)	{ // fix strange ie bug
                                if (jQuery("#chatbox_"+chatboxtitleMd5).length <= 0) {
                                    createChatBox(chatboxtitleMd5 , fullname);
                                }
                                if (jQuery("#chatbox_"+chatboxtitleMd5).css('display') == 'none') {
                                    jQuery("#chatbox_"+chatboxtitleMd5).css('display','block');
                                    restructureChatBoxes();
                                }
                                newMessages[chatboxtitleMd5] = true;
                                newMessagesWin[chatboxtitleMd5] = true;
                                var message = replaceChatMessage(item.m);
                                jQuery("#chatbox_"+chatboxtitleMd5+" .chatboxcontent").append('<div class="chatboxmessage"><span class="chatboxmessagefrom">'+item.f+':&nbsp;&nbsp;</span><span class="chatboxmessagecontent">'+message+'</span></div>');
                                jQuery("#chatbox_"+chatboxtitleMd5+" .chatboxcontent").scrollTop(jQuery("#chatbox_"+chatboxtitleMd5+" .chatboxcontent")[0].scrollHeight);
                                itemsfound += 1;
                            }
                        });
                    }
                });
            }
       
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

function toggleChatBoxGrowth(chatboxtitleMd5) {
    if (jQuery('#chatbox_'+chatboxtitleMd5+' .chatboxcontent').css('display') == 'none') {  
		
        var minimizedChatBoxes = new Array();
		
        if (AJS.Cookie.read('chatbox_minimized')) {
            minimizedChatBoxes = AJS.Cookie.read('chatbox_minimized').split(/\|/);
        }

        var newCookie = '';

        for (i=0;i<minimizedChatBoxes.length;i++) {
            if (minimizedChatBoxes[i] != chatboxtitleMd5) {
                newCookie += chatboxtitleMd5+'|';
            }
        }

        newCookie = newCookie.slice(0, -1)


        AJS.Cookie.save('chatbox_minimized', newCookie);
        jQuery('#chatbox_'+chatboxtitleMd5+' .chatboxcontent').css('display','block');
        jQuery('#chatbox_'+chatboxtitleMd5+' .chatboxinput').css('display','block');
        jQuery("#chatbox_"+chatboxtitleMd5+" .chatboxcontent").scrollTop(jQuery("#chatbox_"+chatboxtitleMd5+" .chatboxcontent")[0].scrollHeight);
    } else {
		
        var newCookie = chatboxtitleMd5;

        if (AJS.Cookie.read('chatbox_minimized')) {
            newCookie += '|'+AJS.Cookie.read('chatbox_minimized');
        }


        AJS.Cookie.save('chatbox_minimized',newCookie);
        jQuery('#chatbox_'+chatboxtitleMd5+' .chatboxcontent').css('display','none');
        jQuery('#chatbox_'+chatboxtitleMd5+' .chatboxinput').css('display','none');
    }
	
}

function checkChatBoxInputKey(event,chatboxtextarea,username) {
	 
    if(event.keyCode == 13 && event.shiftKey == 0)  {
        var message = AJS.escapeHtml(jQuery(chatboxtextarea).val());
        //        message = message.replace(/^\s+|\s+jQuery/g,"");


        jQuery(chatboxtextarea).val('');
        jQuery(chatboxtextarea).focus();
        jQuery(chatboxtextarea).css('height','44px');
        if (message != '') {
            jQuery.post(getBaseUrl()+"/chat/send.action", {
                to: username, 
                message: message
            } , function(data){
                if(typeof(data.chatboxes) != "undefined"){
                    jQuery.each(data.chatboxes, function(j,chatbox){
                        var chatboxtitleMd5 = jQuery.md5(chatbox.un);
                        if(typeof(chatbox.messages) != "undefined"){
                            jQuery.each(chatbox.messages, function(i,item){
                                if (item)	{ // fix strange ie bug
                                    if (jQuery("#chatbox_"+chatboxtitleMd5).length <= 0) {
                                        createChatBox(chatboxtitleMd5 , chatbox.f);
                                    }
                                    if (jQuery("#chatbox_"+chatboxtitleMd5).css('display') == 'none') {
                                        jQuery("#chatbox_"+chatboxtitleMd5).css('display','block');
                                        restructureChatBoxes();
                                    }
                                    //                            newMessages[chatboxtitle] = true;
                                    //                            newMessagesWin[chatboxtitle] = true;
                                    var message = replaceChatMessage(item.m);
                                    jQuery("#chatbox_"+chatboxtitleMd5+" .chatboxcontent").append('<div class="chatboxmessage"><span class="chatboxmessagefrom">'+item.f+':&nbsp;&nbsp;</span><span class="chatboxmessagecontent">'+message+'</span></div>');
                                    jQuery("#chatbox_"+chatboxtitleMd5+" .chatboxcontent").scrollTop(jQuery("#chatbox_"+chatboxtitleMd5+" .chatboxcontent")[0].scrollHeight);
                                //                            itemsfound += 1;
                                }
                            });
                        }
            
                    });
                }
            });
        }
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
            if(typeof(data.chatboxes) != "undefined"){
                jQuery.each(data.chatboxes, function(j,chatbox){
                    var chatboxtitleMd5 = jQuery.md5(chatbox.un);
                    if(typeof(chatbox.messages) != "undefined"){
                        jQuery.each(chatbox.messages, function(i,item){
                            if (item)	{ // fix strange ie bug
                                if (jQuery("#chatbox_"+chatboxtitleMd5).length <= 0) {
                                    createChatBox(chatboxtitleMd5,chatbox.f , 1);
                                }
                                var message = replaceChatMessage(item.m);
                                jQuery("#chatbox_"+chatboxtitleMd5+" .chatboxcontent").append('<div class="chatboxmessage"><span class="chatboxmessagefrom">'+item.f+':&nbsp;&nbsp;</span><span class="chatboxmessagecontent">'+message+'</span></div>');
                            }
                        });
                    }
                });
            }
            for (i=0;i<chatBoxes.length;i++) {
                chatboxtitleMd5 = chatBoxes[i];
                jQuery("#chatbox_"+chatboxtitleMd5+" .chatboxcontent").scrollTop(jQuery("#chatbox_"+chatboxtitleMd5+" .chatboxcontent")[0].scrollHeight);
                setTimeout('jQuery("#chatbox_"+chatboxtitleMd5+" .chatboxcontent").scrollTop(jQuery("#chatbox_"+chatboxtitleMd5+" .chatboxcontent")[0].scrollHeight);', 100); // yet another strange ie bug
            }
	
            setInterval(function(){
                chatHeartbeat();
            }, 1000);
        
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

