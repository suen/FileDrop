
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
	$scope.cwd = "/"; 
	$scope.directory_name = "";

    $scope.uploadFile = function(){
        var file = $scope.myFile;
        console.log('file is ' + JSON.stringify(file));
        var uploadUrl = "/upload";
        fileUpload.uploadFileToUrl(file, uploadUrl);
    };

	$scope.getfilelist = function(path) {
		
		path = path.replace("//", "/");

		$http.get("/query?list="+path).success(function(data){
			$scope.filelist = data.filelist;

			if ($scope.pwd != "/")
				$scope.filelist.unshift({'name': '..', 'type': 'link', 'size': '', 'path': ''});
			$scope.cwd = data.path; 
		});

	}

	$scope.refresh = function() {
		$scope.getfilelist($scope.cwd);
	}

	$scope.fileitemclick = function(f) {
		if (f.type == "dir"){
			console.log("Getting: " + f.path)
			$scope.getfilelist(f.path)
			return;
		}
		if(f.name == ".."){
			cwd = $scope.cwd;
			if (cwd.substr(cwd.length - 1) == "/")
				cwd = cwd.substr(0, cwd.length-1);

			cwd = cwd.substr(0, cwd.lastIndexOf("/"));
			if (cwd == "")
				cwd = "/";
			$scope.cwd = cwd;
			$scope.refresh();
		}

		if (f.type=="file"){
			console.log("HTTP get /query?getfileurl=" + f.id);
			$http.get("/query?getfileurl="+f.id).success(function(data){
				
				if (data['result'] == ""){
					console.log("No url returned");
					return;
				}
				window.open(data['result']);
			});
		}
	};

	$scope.createDirectory = function() {

		if ($scope.directory_name.trim() == "") {
			return;
		}

		console.log("Creating directory: " + $scope.directory_name);

		new_dir_path = ($scope.cwd + "/"+$scope.directory_name).replace("//","/");

		$http.get("/query?mkdir="+new_dir_path).success(function(data){
			
			$scope.directory_name = "";
			$scope.refresh();
		});
	};

	$scope.fileitemdelete = function(f) {

		if(f.type == "link")
			return;

		if(f.type == "file") {
			$http.get("/query?rm="+f.path+"&id="+f.id).success(function(data){
				$scope.refresh();
			});
		};

		if(f.type == "dir") {
			$http.get("/query?rm="+f.path).success(function(data){
				$scope.refresh();
			});
		};

	};

	$scope.refresh();
}]);

var scope;
$(document).ready(function(){
	scope = angular.element('[ng-controller=mainController]').scope();
});

