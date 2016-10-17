/**
 * Created by usr on 10/8/16.
 */

var bookModule = angular.module('bookApp',['ngMaterial', 'md.data.table']);
bookModule.controller('bookController', ['$http', '$mdEditDialog', '$q', '$timeout', '$scope', function ($http, $mdEditDialog, $q, $timeout, $scope) {
    'use strict';

    $scope.options = {
        rowSelection: true,
        multiSelect: true,
        autoSelect: true,
        decapitate: false,
        largeEditDialog: false,
        boundaryLinks: false,
        limitSelect: true,
        pageSelect: true
    };

    $scope.selected = [];
    $scope.limitOptions = [5, 10, 15, {
        label: 'All',
        value: function () {
            return $scope.desserts ? $scope.desserts.count : 0;
        }
    }];

    $scope.query = {
        order: 'name',
        limit: 5,
        page: 1
    };

    $scope.editComment = function (event, dessert) {
        event.stopPropagation();

        var dialog = {
            // messages: {
            //   test: 'I don\'t like tests!'
            // },
            modelValue: dessert.comment,
            placeholder: 'Add a comment',
            save: function (input) {
                dessert.comment = input.$modelValue;
            },
            targetEvent: event,
            title: 'Add a comment',
            validators: {
                'md-maxlength': 30
            }
        };

        var promise = $scope.options.largeEditDialog ? $mdEditDialog.large(dialog) : $mdEditDialog.small(dialog);

        promise.then(function (ctrl) {
            var input = ctrl.getInput();

            input.$viewChangeListeners.push(function () {
                input.$setValidity('test', input.$modelValue !== 'test');
            });
        });
    };

    $scope.toggleLimitOptions = function () {
        $scope.limitOptions = $scope.limitOptions ? undefined : [5, 10, 15];
    };

    $scope.getTypes = function () {
        return ['Candy', 'Ice cream', 'Other', 'Pastry'];
    };

    $scope.onPaginate = function(page, limit) {
        console.log('Scope Page: ' + $scope.query.page + ' Scope Limit: ' + $scope.query.limit);
        console.log('Page: ' + page + ' Limit: ' + limit);

        $scope.promise = $timeout(function () {

        }, 2000);
    };

    $scope.deselect = function (item) {
        console.log(item.name, 'was deselected');
    };

    $scope.log = function (item) {
        console.log(item.name, 'was selected');
    };

    $scope.loadStuff = function () {
        $scope.promise = $timeout(function () {

        }, 2000);
    };

    $scope.onReorder = function(order) {

        console.log('Scope Order: ' + $scope.query.order);
        console.log('Order: ' + order);

        $scope.promise = $timeout(function () {

        }, 2000);
    };


    var urlBase="";
    $scope.books = [];

    $http.get(urlBase + '/api/books').
    success(function (data) {
        $scope.books = data;
    });

}]);
