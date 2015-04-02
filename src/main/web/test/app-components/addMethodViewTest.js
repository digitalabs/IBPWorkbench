/*global expect, inject, spyOn*/
'use strict';

describe('Add Method View', function() {
	var fakeEvent = {
			preventDefault: function() {}
		},

		CUT_AND_DRY = {
			name: 'Cut and Dry',
			description: 'Self explanatory really'
		},

		methodsService,

		variableStateService = {
			updateInProgress: function() {},
			setMethod: function() {}
		},

		serviceUtilities = {
			formatErrorsForDisplay: function() {}
		},

		q,
		controller,
		location,
		scope,
		window,

		deferredAddMethod;

	beforeEach(function() {
		module('addMethod');
	});

	beforeEach(inject(function($q, $rootScope, $location, $controller, $window) {

		methodsService = {
			addMethod: function() {
				deferredAddMethod = q.defer();
				return deferredAddMethod.promise;
			}
		};

		spyOn(methodsService, 'addMethod').and.callThrough();

		controller = $controller('AddMethodController', {
			$scope: $rootScope,
			$location: $location,
			$window: $window,
			methodsService: methodsService,
			variableStateService: variableStateService,
			serviceUtilities: serviceUtilities
		});

		q = $q;
		location = $location;
		scope = $rootScope;
		window = $window;


		// Pretend our form is valid
		scope.amForm = {
			$valid: true
		};
	}));

	describe('$scope.saveMethod', function() {

		it('should call the method service to save the method', function() {
			// Pretend no edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(false);

			scope.saveMethod(fakeEvent, CUT_AND_DRY);

			expect(methodsService.addMethod).toHaveBeenCalledWith(CUT_AND_DRY);
		});

		it('should not call the method service if the form is not valid', function() {
			// Set the form to be invalid
			scope.amForm.$valid = false;

			scope.saveMethod(fakeEvent, CUT_AND_DRY);

			expect(methodsService.addMethod.calls.count()).toEqual(0);
		});

		it('should handle any errors and not redirect if the save was not successful', function() {

			var response = {
				status: 400,
				errors: [{fieldNames: [], message: 'An error'}]
			};

			spyOn(location, 'path');
			spyOn(serviceUtilities, 'formatErrorsForDisplay').and.callThrough();

			scope.saveMethod(fakeEvent, CUT_AND_DRY);

			deferredAddMethod.reject(response);
			scope.$apply();

			expect(serviceUtilities.formatErrorsForDisplay).toHaveBeenCalledWith(response);
			expect(location.path.calls.count()).toEqual(0);
		});

		it('should redirect to /methods after a successful save, if no variable is currently being edited', function() {
			// Pretend no edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(false);
			spyOn(location, 'path');

			scope.saveMethod(fakeEvent, CUT_AND_DRY);

			deferredAddMethod.resolve({id: '1'});
			scope.$apply();

			expect(location.path).toHaveBeenCalledWith('/methods');
		});

		it('should set the method on the variable and go back after a successful save, if a variable is being edited', function() {

			var deferred;

			variableStateService.setMethod = function() {
				deferred = q.defer();
				return deferred.promise;
			};

			// Variable edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(true);
			spyOn(variableStateService, 'setMethod').and.callThrough();
			spyOn(window.history, 'back');

			// Successful save
			scope.saveMethod(fakeEvent, CUT_AND_DRY);
			deferredAddMethod.resolve({id: '1'});
			scope.$apply();

			// Successfully set the method
			deferred.resolve();
			scope.$apply();

			expect(variableStateService.setMethod).toHaveBeenCalledWith(CUT_AND_DRY.id, CUT_AND_DRY.name);
			expect(window.history.back).toHaveBeenCalled();
		});
	});
});
