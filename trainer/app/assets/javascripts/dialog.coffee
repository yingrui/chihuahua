$ ->
  dialogId = $("#dialog-id").attr("value")


  $("#form-send").submit((event) ->
      event.preventDefault()
      callback = (data, status) -> reload()
      username = $("#input-username").attr("value")
      data = { dialogId: dialogId, username: username, content: document.getElementById("content").value }
      $.ajax {url: "/messages", type:"POST", data: JSON.stringify(data), success: callback, dataType: 'json', contentType: 'application/json; charset=UTF-8'}
  )

  $("#form-teach").submit((event) ->
        event.preventDefault()
        callback = (data, status) -> reload()
        username = $("#input-username").attr("value")
        data = { dialogId: dialogId, username: username, content: document.getElementById("teach-content").value }
        $.ajax {url: "/messages/teach", type:"POST", data: JSON.stringify(data), success: callback, dataType: 'json', contentType: 'application/json; charset=UTF-8'}
  )

  reload = () ->
    $("#messages").html("")
    $.get "/messages/dialog/" + dialogId, (messages) ->
        $.each messages, (index, message) ->
          username = $("<div>").addClass("topic").text message.username + ": " + message.content
          $("#messages").append $("<li>").append(username)
  reload()

