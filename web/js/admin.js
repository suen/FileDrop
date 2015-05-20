
var app = angular.module('filedrop', []);

app.controller('mainController', ['$scope', '$http', function($scope, $http){

	$scope.filelist = {};
	$scope.datanodes = {};

	$scope.getfilelist = function() {
		
		console.log("sending filelist request")
		$http.get("/query?replication_status").success(function(data){
			$scope.filelist = data.filelist;
			console.log("filelist replied")
			console.log(data);
		});
	}

	$scope.shortid = function(id){
		return id.substr(0,5) + "...."+ id.substr(-5);
	}
	$scope.getdatanodelist = function() {
		
		console.log("sending dn request")
		$http.get("/query?nodemanager_status").success(function(data){
			$scope.datanodes = data.datanodes;
			console.log("dn replied")
			console.log(data);
		});
	}

	$scope.getfilelist();
	$scope.getdatanodelist();
}]);

var scope;
$(document).ready(function(){
	scope = angular.element('[ng-controller=mainController]').scope();
});

