var app = angular.module('myApp', []);
app
    .controller('myCtrl', function ($scope, kpointService) {
        $scope.name = "wangwei";

        kpointService.sayHello().then(function (response) {
            $scope.message = response.data.message;
        });

        $scope.predict = function () {
            if (!$scope.query) {
                alert("question id or stem can not be null!")
            }

            if (!isNaN($scope.query) && $scope.query.length == 16) {
                kpointService.predictedLabelsByQid($scope.query).then(function (response) {
                    $scope.kpoints = response.data.labels.join(",      ");
                    $scope.predictedKpoints = response.data.predictedLabels.join(",      ");
                    $scope.message = "you get " + response.data.predictedLabels.length + " knowledge points!";
                })
            }
            else {
                kpointService.predictedLabelsByStem($scope.query).then(function (response) {
                    $scope.kpoints = response.data.join(",      ");
                    $scope.message = "you get " + response.data.length + " knowledge points!";
                })
            }

        }
    })
    .service("kpointService", function ($http) {
        var url = "http://localhost:8088/kpoints";
        var services = {};

        services.sayHello = function () {
            return $http.get(url);
        };

        services.predictedLabelsByStem = function (stem) {
            return $http.post(url, {stem: stem});
        };

        services.predictedLabelsByQid = function (qid) {
            return $http.get(url + "/" + qid);
        };
        return services;
    });