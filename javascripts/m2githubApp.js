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
        url: '/status',
        template: '<ui-view/>'
      })
      .state('status.show', {
        url: '?status&message&context',
        templateUrl: 'show-status.html'
      });
  }
]);


angular.module('m2githubApp').controller('DemoController', ['$scope', '$stateParams',
  function ($scope, $stateParams) {
    $scope.label = "This binding is brought you by // interpolation symbols.";

    $scope.status = $stateParams.status;
    $scope.message = $stateParams.message;
    $scope.context = $stateParams.context;

  }
]);
