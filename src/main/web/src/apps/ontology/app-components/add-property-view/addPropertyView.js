/*global angular*/
'use strict';

(function() {
	var app = angular.module('addProperty', ['tagSelect', 'input', 'textArea', 'properties', 'variableState', 'utilities']);

	app.controller('AddPropertyController', ['$scope', '$location', '$window', 'propertiesService', 'variableStateService',
		'propertyFormService', 'serviceUtilities', 'formUtilities',
		function($scope, $location, $window, propertiesService, variableStateService, propertyFormService, serviceUtilities,
			formUtilities) {

			var ADD_VARIABLE_PATH = '/add/variable',
				PROPERTIES_PATH = '/properties';

			$scope.property = {
				classes: []
			};
			$scope.classes = [];

			// Reset server errors
			$scope.serverErrors = {};
			$scope.someListsNotLoaded = false;

			propertiesService.getClasses().then(function(classes) {
				$scope.classes = classes;
			}, function(response) {
				$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
				$scope.someListsNotLoaded = true;
			});

			$scope.saveProperty = function(e, property) {
				e.preventDefault();

				// Reset server errors
				$scope.serverErrors = {};

				if ($scope.apForm.$valid) {
					$scope.submitted = true;

					propertiesService.addProperty(property).then(function(response) {
						property.id = response.id;
						if (variableStateService.updateInProgress()) {
							variableStateService.setProperty(property.id, property.name).finally(function() {
								$location.path(ADD_VARIABLE_PATH);
							});
						} else {
							$location.path(PROPERTIES_PATH);
						}
					}, function(response) {
						$scope.apForm.$setUntouched();
						$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
						$scope.submitted = false;
					});
				}
			};

			$scope.cancel = function(e) {
				var path = variableStateService.updateInProgress() ? ADD_VARIABLE_PATH : PROPERTIES_PATH;

				e.preventDefault();
				formUtilities.cancelAddHandler($scope, !propertyFormService.formEmpty($scope.property), path);
			};

			$scope.formGroupClass = formUtilities.formGroupClassGenerator($scope, 'apForm');
		}
	]);

	app.factory('propertyFormService', [function() {
		return {
			//TODO: fix logic
			formEmpty: function(model) {
				return !!!model.name &&
					!!!model.description &&
					!!!model.cropOntologyId &&
					(angular.isUndefined(model.classes) ||
						(angular.isArray(model.classes) && model.classes.length === 0));
			}
		};
	}]);
} ());
