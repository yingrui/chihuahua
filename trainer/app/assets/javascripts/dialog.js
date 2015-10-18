(function(module){
    'use strict';

    module.controller('DialogViewController', ['$scope', '$http', '$location', function(scope, $http, $location){
        scope.dialogs = [];

        scope.dialog = {
            username: "",
            topic: "",
            relationship: ""
        };

        scope.create = function() {
            $http.post('/dialogs', scope.dialog).success(function(data, status, headers){
                $location.path(headers('Location'));
                console.log($location.absUrl());
            });
        };

        scope.loadDialogs = function() {
            $http.get('/dialogs').success(function(data){
                scope.dialogs = data;
            });
        };

        scope.loadDialogs();
    }]);

    module.controller('DialogController', ['$scope', '$http', '$location', function(scope, $http, $location){
        scope.vm = {
            mode: "display"
        };

        scope.enterEditingMode = function() { scope.vm.mode = "edit"; };
        scope.enterDisplayMode = function() { scope.vm.mode = "display"; };

        scope.deleteDialog = function() {
            $http.delete('/dialogs/' + scope.dialog.id).success(function(data){
                scope.loadDialogs();
            });
        };

        scope.updateDialog = function() {
            var request = {topic: scope.dialog.topic, username: scope.dialog.username, relationship: scope.dialog.relationship};
            $http.put('/dialogs/' + scope.dialog.id, request).success(function(data){
                scope.loadDialogs();
                scope.enterDisplayMode();
            });
        };
    }]);

})(window.trainerCoreModule);