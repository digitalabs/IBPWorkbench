/*global angular*/
'use strict';

(function() {
	var scaleDetailsModule = angular.module('scaleDetails', ['formFields', 'scales', 'utilities', 'categories']);

	scaleDetailsModule.directive('omScaleDetails', ['scalesService', 'serviceUtilities', function(scalesService, serviceUtilities) {

		return {
			controller: function($scope) {
				$scope.editing = false;
				$scope.showRangeWidget = false;
				$scope.showCategoriesWidget = false;

				$scope.$watch('selectedScale', function(scale) {
					$scope.model = angular.copy(scale);

					if (scale && scale.dataType) {
						$scope.showRangeWidget = scale.dataType.name === 'Numeric';
						$scope.showCategoriesWidget = scale.dataType.name === 'Categorical';
					}
				});

				$scope.$watch('selectedItem', function(selected) {
					$scope.scaleId = selected && selected.id || null;
				});

				$scope.editScale = function(e) {
					e.preventDefault();
					$scope.editing = true;
				};

				$scope.cancel = function(e) {
					e.preventDefault();
					$scope.editing = false;
					$scope.model = angular.copy($scope.selectedScale);
				};

				$scope.saveChanges = function(e, id, model) {
					e.preventDefault();

					scalesService.updateScale(id, model).then(function() {

						// Update scale on parent scope if we succeeded
						$scope.updateSelectedScale(model);

						$scope.editing = false;
					}, serviceUtilities.genericAndRatherUselessErrorHandler);
				};
			},
			restrict: 'E',
			templateUrl: 'static/views/ontology/scaleDetails.html'
		};
	}]);
})();
