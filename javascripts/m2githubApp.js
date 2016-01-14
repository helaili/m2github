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
        url: '',
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
    console.log('yessssss???');


    $scope.showStatus = function() {
      $scope.status = $stateParams.status;
      $scope.message = $stateParams.message;
      $scope.context = $stateParams.context;
    }

  }
]);
