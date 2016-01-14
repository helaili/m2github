var m2githubApp;
m2githubApp = angular.module('m2githubApp', []);

console.log('m2githubApp loaded');


m2githubApp.config(function($interpolateProvider) {
    return $interpolateProvider.startSymbol('{(').endSymbol(')}');
  });
