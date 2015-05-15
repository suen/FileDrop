

var app = angular.module('filedrop', []);

app.controller('mainController', function($scope,$http){

	$scope.filelist = {};
	$http.get("/query?list=root").success(function(data){
		$scope.filelist = data;
	});
})
