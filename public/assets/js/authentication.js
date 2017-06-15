var module = angular.module('authentication',[]);
module.controller('RegCntrl',['$scope','$http',function($scope,$http){
 $scope.register = function()
 {
   $scope.openModal = true;
   var dataToServer = {password:$scope.user.password,email:$scope.user.email,businessType:$scope.typeOfIndv};
   $http({
       method : 'post',
       url: 'http://localhost:8083/users/create',
       data: angular.toJson(dataToServer),
       success : function(data, textStatus, xhr) {
           console.log(data);
     }
  });
 };
}]);
module.controller('AuthCntrl',['$scope','$http',function($scope,$http){
  $scope.authorize = function(){
    var dataToServer = {email:$scope.user.email,password:$scope.user.password};
    console.log(dataToServer);
    $http({
        method : 'post',
        url: 'http://193.33.64.97:8083/users/auth',
        data: angular.toJson(dataToServer),
        withCredentials:true,
        success : function(data, textStatus, xhr) {
      }
   });
  }
}]);
