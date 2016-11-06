
(function ($) {

    var CHAT_NEW_MESSAGE = "chat-new-message";

// request permission on page load
    document.addEventListener('DOMContentLoaded', function () {
	if (!Notification) {
	    alert('Desktop notifications not available in your browser. Try Chromium.');
	    return;
	}

	if (Notification.permission !== "granted")
	    Notification.requestPermission();
    });

    function showMessage(channel, message) {
	if (Notification.permission !== "granted")
	    Notification.requestPermission();
	else {
	    var notification = new Notification(message.f.fn + " " + ' ' + AJS.I18n.getText('chat.says.name'), {
		icon: AJS.params.baseUrl + '/download/resources/confluence.chat:chat-files/images-chat/plugin-logo.png',
		body: message.m,
	    });

	    notification.onclick = function (x) {
		window.focus();
		this.cancel();
	    };
	}
    }

    $.jStorage.subscribe(CHAT_NEW_MESSAGE, showMessage);
})(AJS.$);