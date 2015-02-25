/*global expect, inject, spyOn*/
'use strict';

describe('Variables Controller', function() {
	var q,
		controller,
		scope,
		deferred,
		variablesService;

	beforeEach(function() {
		module('variablesView');
	});

	beforeEach(inject(function($q, $controller, $rootScope) {
		variablesService = {
			getVariables: function() {
				deferred = q.defer();
				return deferred.promise;
			},
			getFavouriteVariables: function() {
				deferred = q.defer();
				return deferred.promise;
			}
		};
		spyOn(variablesService, 'getVariables').and.callThrough();

		q = $q;
		scope = $rootScope;
		controller = $controller('VariablesController', {
			$scope: scope,
			variablesService: variablesService
		});
	}));

	it('toDisplayFormat should transform variables into display format', function() {
		var variable = {
				id: 'var1',
				name: 'var1',
				favourite: 'true',
				method: {
					name: 'method'
				},
				property: {
					name: 'property'
				},
				scale: {
					name: 'scale'
				}
			},
			transformedVariable = {
				id: 'var1',
				Name: 'var1',
				'action-favourite': 'true',
				Method: 'method',
				Property: 'property',
				Scale: 'scale'
			};

		expect(controller.toDisplayFormat(variable)).toEqual(transformedVariable);
	});

});

describe('Variables Service', function() {
	var variablesServiceUrl = 'http://private-905fc7-ontologymanagement.apiary-mock.com/variables',
		variablesService,
		httpBackend;

	beforeEach(function() {
		module('variables');

		inject(function(_variablesService_, $httpBackend) {
			variablesService = _variablesService_;
			httpBackend = $httpBackend;
		});
	});

	afterEach(function() {
		httpBackend.verifyNoOutstandingExpectation();
		httpBackend.verifyNoOutstandingRequest();
	});

	it('should return an array of objects', function() {
		var variables = [{
				name: 'Var1',
				description: 'This is var1'
			}, {
				name: 'Var2',
				description: 'This is var2'
			}],
			result;

		httpBackend.expectGET(variablesServiceUrl).respond(variables);

		variablesService.getVariables().then(function(response) {
			result = response;
		});

		httpBackend.flush();

		expect(result instanceof Array).toBeTruthy();
		expect(result).toEqual(variables);
	});

	it('should return an error message when 500 response recieved', function() {
		var result;

		httpBackend.expectGET(variablesServiceUrl).respond(500);

		variablesService.getVariables().then(function(response) {
			result = response;
		}, function(reason) {
			result = reason;
		});

		httpBackend.flush();

		expect(result).toEqual('An unknown error occurred.');
	});

	it('should return an error message when 400 response recieved', function() {
		var result;

		httpBackend.expectGET(variablesServiceUrl).respond(400);

		variablesService.getVariables().then(function(response) {
			result = response;
		}, function(reason) {
			result = reason;
		});

		httpBackend.flush();

		expect(result).toEqual('Request was malformed.');
	});
});
