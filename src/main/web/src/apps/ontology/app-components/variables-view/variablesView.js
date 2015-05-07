/*global angular*/
'use strict';

(function() {
	var app = angular.module('variablesView', ['list', 'panel', 'variables', 'variableDetails', 'utilities']),
		DELAY = 400;

	function transformDetailedVariableToDisplayFormat(variable, id) {
		return {
			id: id,
			name: variable.name,
			property: variable.propertySummary && variable.propertySummary.name || '',
			method: variable.methodSummary && variable.methodSummary.name || '',
			scale: variable.scale && variable.scale.name || '',
			'action-favourite': variable.favourite
		};
	}

	function transformVariableToDisplayFormat(variable) {
		return {
			id: variable.id,
			name: variable.name,
			alias: variable.alias,
			property: variable.propertySummary && variable.propertySummary.name || '',
			method: variable.methodSummary && variable.methodSummary.name || '',
			scale: variable.scaleSummary && variable.scaleSummary.name || '',
			'action-favourite': variable.favourite
		};
	}

	function transformToDisplayFormat(variables) {
		// TODO: check that variable has an ID and name
		return variables.map(transformVariableToDisplayFormat);
	}

	function findAndUpdate(list, id, updatedVariable, sortFunction) {
		var selectedVariableIndex = -1;

		list.some(function(variable, index) {
			if (variable.id === id) {
				selectedVariableIndex = index;
				return true;
			}
		});

		// Not much we can really do if we don't find it in the list. Just don't update.
		if (selectedVariableIndex !== -1) {
			if (updatedVariable) {
				list[selectedVariableIndex] = updatedVariable;
				sortFunction(list);
			} else {
				list.splice(selectedVariableIndex, 1);
			}
		}
	}

	function findAndRemove(list, id) {
		findAndUpdate(list, id);
	}

	app.controller('VariablesController', ['$scope', 'variablesService', 'panelService', '$timeout', 'collectionUtilities',
		function($scope, variablesService, panelService, $timeout, collectionUtilities) {
			var ctrl = this;

			ctrl.variables = [];
			ctrl.favouriteVariables = [];
			ctrl.showAllVariablesThrobberWrapper = true;
			ctrl.showFavouritesThrobberWrapper = true;
			ctrl.colHeaders = ['name', 'property', 'method', 'scale', 'action-favourite'];

			ctrl.transformToDisplayFormat = transformToDisplayFormat;
			/* Exposed for testing */
			ctrl.transformVariableToDisplayFormat = transformVariableToDisplayFormat;
			ctrl.transformDetailedVariableToDisplayFormat = transformDetailedVariableToDisplayFormat;

			$scope.panelName = 'variables';

			$timeout(function() {
				ctrl.showAllVariablesThrobber = true;
				ctrl.showFavouritesThrobber = true;
			}, DELAY);

			variablesService.getFavouriteVariables().then(function(variables) {
				ctrl.favouriteVariables = ctrl.transformToDisplayFormat(variables);
				ctrl.showFavouritesThrobberWrapper = false;

				if (ctrl.favouriteVariables.length === 0) {
					ctrl.showNoFavouritesMessage = true;
					return;
				}

				ctrl.addAliasToTableIfPresent(ctrl.favouriteVariables);
			});

			variablesService.getVariables().then(function(variables) {
				ctrl.variables = ctrl.transformToDisplayFormat(variables);
				ctrl.showAllVariablesThrobberWrapper = false;

				if (ctrl.variables.length === 0) {
					ctrl.showNoVariablesMessage = true;
					return;
				}

				ctrl.addAliasToTableIfPresent(ctrl.variables);
			});

			// Exposed for testing
			ctrl.addAliasToTableIfPresent = function(variables) {
				var ALIAS = 'alias';

				if (ctrl.colHeaders.indexOf(ALIAS) === -1) {

					// Add alias into the table if at least one variable has an alias
					variables.some(function(variable) {
						if (variable.alias) {
							// Add alias after name
							ctrl.colHeaders.splice(1, 0, ALIAS);
							return true;
						}
					});
				}
			};

			$scope.showVariableDetails = function() {

				// Ensure the previously selected variable doesn't show in the panel before we've retrieved the new one
				$scope.selectedVariable = null;

				variablesService.getVariable($scope.selectedItem.id).then(function(variable) {
					$scope.selectedVariable = variable;
				});

				panelService.showPanel($scope.panelName);
			};

			$scope.updateSelectedVariable = function(updatedVariable) {

				var transformedVariable;

				// If the
				if (updatedVariable) {
					transformedVariable = transformDetailedVariableToDisplayFormat(updatedVariable, $scope.selectedItem.id);

					findAndUpdate(ctrl.variables, $scope.selectedItem.id, transformedVariable, collectionUtilities.sortByName);

					// If the variable is not a favourite, we need to remove it if it's in the favourites list
					if (updatedVariable.favourite) {
						findAndUpdate(ctrl.favouriteVariables, $scope.selectedItem.id, transformedVariable, collectionUtilities.sortByName);
					} else {
						findAndRemove(ctrl.favouriteVariables, $scope.selectedItem.id);
					}

				} else {
					findAndRemove(ctrl.variables, $scope.selectedItem.id);
					findAndRemove(ctrl.favouriteVariables, $scope.selectedItem.id);
				}
			};

			$scope.selectedItem = {id: null};
			$scope.selectedVariable = {};
		}
	]);

}());
