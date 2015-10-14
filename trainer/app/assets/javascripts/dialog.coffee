$ ->
  dialogId = $("#dialog-id").attr("value")
  $.get "/messages/dialog/" + dialogId, (messages) ->
    $.each messages, (index, message) ->
      username = $("<div>").addClass("topic").text message.username + ": " + message.content
      $("#messages").append $("<li>").append(username)