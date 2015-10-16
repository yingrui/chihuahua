(function(module){
    'use strict';

    module.controller('TrainDialogMessageController', ['$scope', '$http', function(scope, $http){
        var message = scope.message;
        scope.vm = {
            mode : "display",
            newMessage : {
                content: "",
                username: "system"
            }
        };
        scope.isSystemUser = message.username === "system";

        scope.enterEditingMode = function() { scope.vm.mode = "edit"; };
        scope.enterInsertMode = function() { scope.vm.mode = "insert"; };
        scope.enterDisplayMode = function() { scope.vm.mode = "display"; };

        scope.updateMessage = function() {
            var request = {dialogId: message.dialogId, content: message.content, username: message.username, tags: message.tags};
            $http.put('/messages/' + message.id, request).success(function(data){
                scope.loadMessages();
                scope.enterDisplayMode();
            });
        };

        scope.addNewMessage = function() {
            var request = {dialogId: message.dialogId, content: scope.vm.newMessage.content, username: scope.vm.newMessage.username};
            $http.post('/messages/' + message.id + '/teach', request).success(function(data){
                scope.loadMessages();
                scope.enterDisplayMode();
                scope.vm.newMessage = {content : "", username: "system"};
            });
        };

        scope.deleteMessage = function() {
            $http.delete('/messages/' + message.id).success(function(data){
                scope.loadMessages();
            });
        };
    }]);

})(window.trainerCoreModule);