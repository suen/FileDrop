
var app = angular.module('filedrop', []);

app.directive('fileModel', ['$parse', function ($parse) {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            var model = $parse(attrs.fileModel);
            var modelSetter = model.assign;
            
            element.bind('change', function(){
                scope.$apply(function(){
                    modelSetter(scope, element[0].files[0]);
                });
            });
        }
    };
}]);

app.service('fileUpload', ['$http', function ($http) {
    this.uploadFileToUrl = function(file, uploadUrl){
        var fd = new FormData();
        fd.append('file', file);
        $http.post(uploadUrl, fd, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}
        })
        .success(function(){
        })
        .error(function(){
        });
    }
}]);

app.controller('mainController', ['$scope', 'fileUpload', '$http', function($scope, fileUpload, $http){

	$scope.filelist = {};
	$scope.cwd = "/" 

    $scope.uploadFile = function(){
        var file = $scope.myFile;
        console.log('file is ' + JSON.stringify(file));
        var uploadUrl = "/upload";
        fileUpload.uploadFileToUrl(file, uploadUrl);
    };

	$scope.refresh = function() {
		$http.get("/query?list=root").success(function(data){
			$scope.filelist = data;
		});

	}

	$scope.createDirectory = function() {
		console.log($scope.directory_name);
	};

	$scope.refresh();
}]);

var scope;
$(document).ready(function(){
	scope = angular.element('[ng-controller=mainController]').scope();
});

