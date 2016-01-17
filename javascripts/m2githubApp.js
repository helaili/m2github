'use strict';

var m2githubApp = angular.module('m2githubApp', ['ui.router']);

angular.module('m2githubApp').config(['$interpolateProvider', '$stateProvider',
  function($interpolateProvider, $stateProvider) {
    $interpolateProvider.startSymbol('{[');
    $interpolateProvider.endSymbol(']}');

    $stateProvider
      .state('status', {
        abstract: true,
        template: '<ui-view/>'
      })
      .state('status.slashm2github', {
        url: 'test.html?message',
        template: '<h1>My Contacts</h1>'
      })
      .state('status.slash', {
        url: '/?message',
        template: '<h1>My Contacts</h1>'
      })
      .state('status.emptyx', {
        url: '?message',
        templateUrl: 'views/status.html'
      })
      .state('status.test', {
        url: '/test.html?message',
        template: '<h1>My Contacts</h1>'
      });
  }
]);




angular.module('m2githubApp').run(['$rootScope', '$state', '$stateParams',
    function ($rootScope,   $state,   $stateParams) {
      $rootScope.$state = $state;
      $rootScope.$stateParams = $stateParams;
    }
  ]
);


angular.module('m2githubApp').controller('DemoController', ['$scope', '$rootScope', '$stateParams',
  function ($scope, $rootScope, $stateParams) {


    $scope.showStatus = function() {
      $scope.status = $stateParams.status;
      $scope.message = $stateParams.message;
      $scope.context = $stateParams.context;
    };

  }
]);
