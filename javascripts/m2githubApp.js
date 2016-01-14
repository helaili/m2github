var m2githubApp;
m2githubApp = angular.module('m2githubApp', []);

m2githubApp.config([
  '$interpolateProvider', function($interpolateProvider) {
    return $interpolateProvider.startSymbol('{(').endSymbol(')}');
  }
]);
