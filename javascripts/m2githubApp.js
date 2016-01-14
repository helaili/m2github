var m2githubApp = angular.module('m2githubApp', []);

console.log('m2githubApp loaded');


m2githubApp.config(function($interpolateProvider) {
    $interpolateProvider.startSymbol('//');
    $interpolateProvider.endSymbol('//');
});


angular.module('m2githubApp').controller('DemoController', ['$scope',
  function ($scope) {
    $scope.label = "This binding is brought you by // interpolation symbols.";
  }
]);
