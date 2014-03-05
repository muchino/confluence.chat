AJS.toInit(function($) {
    var form = $('#chat-configuration');
    $('#chat-edit').click(function(e) {
        e.preventDefault();
        form.addClass('editing');
    });

});