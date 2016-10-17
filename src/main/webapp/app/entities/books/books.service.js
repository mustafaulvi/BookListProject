(function() {
    'use strict';
    angular
        .module('bookListApp')
        .factory('Books', Books);

    Books.$inject = ['$resource'];

    function Books ($resource) {
        var resourceUrl =  'api/books/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
