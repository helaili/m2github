'use strict';

var m2githubApp = angular.module('m2githubApp', ['ui.router']);

angular.module('m2githubApp').config(['$interpolateProvider',
  function($interpolateProvider) {
    $interpolateProvider.startSymbol('//');
    $interpolateProvider.endSymbol('//');
  }
]);


angular.module('m2githubApp').config(['$stateProvider',
  function ($stateProvider) {
    $stateProvider
      .state('status', {
        abstract: true,
        url: '/m2github',
        template: '<ui-view/>'
      })
      .state('status.default', {
        url: '/',
        templateUrl: 'views/default.html'
      })
      .state('status.show', {
        url: 'status.html?status&message&context',
        templateUrl: 'views/status.html'
      });
  }
]);


angular.module('m2githubApp').controller('DemoController', ['$scope', '$stateParams',
  function ($scope, $stateParams) {
    console.log('yessssss?xx');


        var $rootScope = angular.element(document.querySelectorAll("[ui-view]")[0]).injector().get('$rootScope');

        console.log($rootScope);

    $rootScope.$on('$stateChangeStart',function(event, toState, toParams, fromState, fromParams){
      console.log('$stateChangeStart to '+toState.to+'- fired when the transition begins. toState,toParams : \n',toState, toParams);
    });

    $rootScope.$on('$stateChangeError',function(event, toState, toParams, fromState, fromParams){
      console.log('$stateChangeError - fired when an error occurs during transition.');
      console.log(arguments);
    });

    $rootScope.$on('$stateChangeSuccess',function(event, toState, toParams, fromState, fromParams){
      console.log('$stateChangeSuccess to '+toState.name+'- fired once the state transition is complete.');
    });

    $rootScope.$on('$viewContentLoaded',function(event){
      console.log('$viewContentLoaded - fired after dom rendered',event);
    });

    $rootScope.$on('$stateNotFound',function(event, unfoundState, fromState, fromParams){
      console.log('$stateNotFound '+unfoundState.to+'  - fired when a state cannot be found by its name.');
      console.log(unfoundState, fromState, fromParams);
    });


    $scope.showStatus = function() {
      $scope.status = $stateParams.status;
      $scope.message = $stateParams.message;
      $scope.context = $stateParams.context;
    }

  }
]);
