(function() {
    'use strict';

    angular
        .module('bookListApp')
        .controller('BooksDeleteController',BooksDeleteController);

    BooksDeleteController.$inject = ['$uibModalInstance', 'entity', 'Books'];

    function BooksDeleteController($uibModalInstance, entity, Books) {
        var vm = this;

        vm.books = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Books.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
