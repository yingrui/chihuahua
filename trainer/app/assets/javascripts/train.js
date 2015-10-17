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
                var location = headers('Location');
                var messageId = location.substring(location.lastIndexOf('/') + 1);
                $http.put('/messages/' + messageId + '/reply').success(function(data, status) {
                    scope.loadMessages();
                    scope.content = "";
                });
            });
        };

        scope.teach = function() {
            var message = { dialogId: scope.dialog.id, username: 'system', content: scope.content};
            var lastMessage = scope.messages[scope.messages.length - 1];

            $http.post('/messages/' + lastMessage.id + '/teach', message).success(function(data, status, headers){
                scope.loadMessages();
                scope.content = "";
            });
        };

        scope.loadMessages = function () {
           $http.get('/dialogs/' + $routeParams.dialogId + "/messages").success(function(data){
               scope.messages = data;
           });
        };

        $http.get('/dialogs/' + $routeParams.dialogId).success(function(data){
            scope.dialog = data;
            scope.loadMessages();
        });
    }]);

})(window.trainerCoreModule);