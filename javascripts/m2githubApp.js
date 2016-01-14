var m2githubApp;
m2githubApp = angular.module('m2githubApp', []);

console.log('m2githubApp loaded');


m2githubApp.config(function($interpolateProvider) {
    $interpolateProvider.startSymbol('//');
    $interpolateProvider.endSymbol('//');
});


m2githubApp.controller('DemoController', function() {
  console.log('DemoController');
  this.label = "This binding is brought you by // interpolation symbols.";
});
