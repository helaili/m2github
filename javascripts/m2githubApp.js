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
        url: '/views/',
        template: '<ui-view/>'
      })
      .state('status.show', {
        url: 'status.html?status&message&context',
        templateUrl: 'views/status.html'
      });
  }
]);


angular.module('m2githubApp').controller('DemoController', ['$scope', '$stateParams',
  function ($scope, $stateParams) {
    console.log("yessssss")
    $scope.label = "This binding is brought you by // interpolation symbols.";

    $scope.showStatus = function() {
      $scope.status = $stateParams.status;
      $scope.message = $stateParams.message;
      $scope.context = $stateParams.context;
      }

  }
]);
