(function(global){
'use strict';

global.trainerCoreModule = angular.module('trainer', []).
    config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/', {
            templateUrl: '/assets/views/main.htm'
            }).
        when('/dialogs/:dialogId', {
            templateUrl: '/assets/views/train.htm'
            }).
        otherwise({redirectTo:'/'});
}]);

})(window);
