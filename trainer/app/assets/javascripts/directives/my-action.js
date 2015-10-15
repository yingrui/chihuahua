(function(module){
    'use strict';

    module.directive('myAction', ['$parse', function ($parse) {

        return {
            link:function (scope, iElement, iAttrs) {
                var action = iAttrs.myAction;
                var fn = $parse(action);

                iElement.bind('keypress', function (event) {
                    if (event.keyCode == 13) {
                        scope.$apply(function(){
                            fn(scope);
                        });
                    }
                });

            }
        };
    }]);

})(window.trainerCoreModule);

