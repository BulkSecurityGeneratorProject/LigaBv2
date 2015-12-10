'use strict';

angular.module('ligabApp')
    .controller('LigaDetailController', function ($scope, $rootScope, $stateParams, entity, Liga) {
        $scope.liga = entity;
        $scope.load = function (id) {
            Liga.get({id: id}, function(result) {
                $scope.liga = result;
            });
        };
        $rootScope.$on('ligabApp:ligaUpdate', function(event, result) {
            $scope.liga = result;
        });
    });
