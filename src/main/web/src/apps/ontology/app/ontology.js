/*global angular*/
'use strict';

(function() {
	var VIEWS_LOCATION = '../static/views/ontology/',
		app = angular.module('ontology', ['ngRoute', 'variablesView', 'propertiesView', 'methodsView', 'scalesView', 'addVariable',
		'addProperty', 'addMethod', 'addScale']);

	app.config(['$routeProvider', function($routeProvider) {

		$routeProvider
			.when('/properties', {
				controller: 'PropertiesController',
				controllerAs: 'propsCtrl',
				templateUrl: VIEWS_LOCATION + 'propertiesView.html'
			})
			.when('/variables', {
				controller: 'VariablesController',
				controllerAs: 'varsCtrl',
				templateUrl: VIEWS_LOCATION + 'variablesView.html'
			})
			.when('/methods', {
				controller: 'MethodsController',
				controllerAs: 'methodsCtrl',
				templateUrl: VIEWS_LOCATION + 'methodsView.html'
			})
			.when('/scales', {
				controller: 'ScalesController',
				controllerAs: 'scalesCtrl',
				templateUrl: VIEWS_LOCATION + 'scalesView.html'
			})
			.when('/add/variable', {
				controller: 'AddVariableController',
				templateUrl: VIEWS_LOCATION + 'addVariableView.html'
			})
			.when('/add/property', {
				controller: 'AddPropertyController',
				templateUrl: VIEWS_LOCATION + 'addPropertyView.html'
			})
			.when('/add/method', {
				controller: 'AddMethodController',
				templateUrl: VIEWS_LOCATION + 'addMethodView.html'
			})
			.when('/add/scale', {
				controller: 'AddScaleController',
				templateUrl: VIEWS_LOCATION + 'addScaleView.html'
			})
			.otherwise({
				redirectTo: '/variables'
			});
	}]);

	app.controller('OntologyController', ['$scope', '$location', '$window', 'panelService',
		function($scope, $location, $window, panelService) {
			$scope.panelName = 'addNew';

			$scope.addNewSelection = function() {
				panelService.visible = {show: $scope.panelName};
			};

			$scope.addNew = function(e, path) {
				e.preventDefault();
				panelService.visible = {show: null};
				$location.path('/add/' + path);
			};

			// Storing the location so we can implement back functionality on our nested views
			$scope.$on('$locationChangeStart', function(event, newUrl, oldUrl) {
				$scope.previousUrl = oldUrl;
			});

			// Back functionality for our nested views. Used in the add-* modules
			$scope.goBack = function() {
				$window.history.back();
			};
		}
	]);

}());
