(function(module){
    'use strict';

    module.controller('TrainController', ['$scope', '$http', '$location', '$routeParams', function(scope, $http, $location, $routeParams){
        scope.messages = [];
        scope.content = "";

        scope.dialog = {
            id: $routeParams.dialogId,
            username: "",
            topic: "",
            relationship: ""
        };

        scope.shouldTeach = function() {
            if(scope.messages.length === 0) return false;

            var lastMessage = scope.messages[scope.messages.length - 1];
            return lastMessage.username === scope.dialog.username;
        };

        scope.speak = function() {
            var message = { dialogId: scope.dialog.id, username: scope.dialog.username, content: scope.content };

            $http.post('/messages', message).success(function(data, status, headers){
                loadMessages();
                scope.content = "";
            });
        };

        scope.teach = function() {
            var message = { dialogId: scope.dialog.id, username: 'Yoyo', content: scope.content};

            $http.post('/messages/teach', message).success(function(data, status, headers){
                loadMessages();
                scope.content = "";
            });
        };

        function loadMessages() {
           $http.get('/messages/dialog/' + $routeParams.dialogId).success(function(data){
               scope.messages = data;
           });
        }

        $http.get('/dialogs/' + $routeParams.dialogId).success(function(data){
            scope.dialog = data;
            loadMessages();
        });
    }]);

})(window.trainerCoreModule);