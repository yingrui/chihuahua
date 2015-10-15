(function(module){
    'use strict';

    module.controller('DialogController', ['$scope', '$http', '$location', function(scope, $http, $location){
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

        $http.get('/dialogs').success(function(data){
            scope.dialogs = data;
        });

    }]);

})(window.trainerCoreModule);