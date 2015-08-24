/*global angular*/
'use strict';

(function() {
	var filterModule = angular.module('filter', ['panel', 'variableTypes', 'dataTypes', 'utilities', 'multiSelect', 'select',
		'ui.bootstrap']);

	filterModule.directive('omFilter', ['panelService', 'variableTypesService', 'serviceUtilities', 'dataTypesService',
		function(panelService, variableTypesService, serviceUtilities, dataTypesService)  {
			return {
				controller: function($scope) {
					$scope.smallPanelName = 'filters';
					$scope.data = {
						types: [],
						scaleDataTypes: [],
						calendarOpened1: false,
						calendarOpened2: false
					};
					$scope.dateOptions = {
						formatYear: 'yy',
						startingDay: 1
					};
					$scope.todaysDate = new Date();

					variableTypesService.getTypes().then(function(types) {
						$scope.data.types = types;
					}, function(response) {
						$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
						$scope.someListsNotLoaded = true;
					});

					dataTypesService.getNonSystemDataTypes().then(function(types) {
						$scope.data.scaleDataTypes = $scope.data.scaleDataTypes.concat(types);
					}, function(response) {
						$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
						$scope.someListsNotLoaded = true;
					});

					$scope.addNewFilter = function() {
						panelService.showPanel($scope.smallPanelName);
					};

					$scope.clearFilters = function() {
						$scope.filterOptions = {
							variableTypes: [],
							scaleDataType: null,
							dateCreatedFrom: null,
							dateCreatedTo: null
						};
					};

					$scope.isFilterActive = function() {
						var filterOptionsValued = $scope.filterOptions,
							variableTypesActive,
							scaleDataTypesActive,
							dateCreatedFromActive,
							dateCreatedToActive;

						if (!filterOptionsValued) {
							return false;
						}

						variableTypesActive = $scope.filterOptions.variableTypes && $scope.filterOptions.variableTypes.length !== 0;

						scaleDataTypesActive = !!$scope.filterOptions.scaleDataType;

						dateCreatedFromActive = $scope.filterOptions.dateCreatedFrom &&
							$scope.filterOptions.dateCreatedFrom.getTime !== undefined;

						dateCreatedToActive = $scope.filterOptions.dateCreatedTo &&
							$scope.filterOptions.dateCreatedTo.getTime !== undefined;

						return variableTypesActive || scaleDataTypesActive || dateCreatedFromActive || dateCreatedToActive;
					};

					$scope.open1 = function($event) {
						$event.preventDefault();
						$event.stopPropagation();

						$scope.data.calendarOpened1 = true;
					};

					$scope.open2 = function($event) {
						$event.preventDefault();
						$event.stopPropagation();

						$scope.data.calendarOpened2 = true;
					};

				},
				restrict: 'E',
				scope: {
					filterOptions: '=omFilterOptions'
				},
				templateUrl: 'static/views/ontology/filter.html'
			};
		}
	]);

}());
