$ ->
  $.get "/dialogs", (dialogs) ->
    $.each dialogs, (index, dialog) ->
      topic = $("<div>").addClass("topic").text dialog.topic
      relationship = $("<div>").addClass("relationship").text dialog.relationship
      $("#dialogs").append $("<li>").append(topic).append(relationship)