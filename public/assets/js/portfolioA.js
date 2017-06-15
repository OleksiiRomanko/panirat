var module = angular.module('portfolio',['Mydropzone','slick','ngAnimate','ui.bootstrap','ui.bootstrap.tpls','ngRoute']);
module.config(['$routeProvider',function($routeProvider){
  $routeProvider.when('/home',{
    templateUrl : 'views/home.html'
  }).when('/deals',{
    templateUrl : 'views/deals.html'
  });
}]);
module.controller('listOfporf',['$scope',function($scope){
 $scope.images = [{url:'pictures/img/img/s-l500 (1).jpg',descr:'lalalala'},{url:'pictures/img/img/s-l500 (1).jpg',descr:'lalalala'},{url:'pictures/img/img/s-l500 (1).jpg',descr:'lalalala'},
{url:'pictures/img/img/s-l500 (1).jpg',descr:'lalalala'},{url:'pictures/img/img/s-l500 (1).jpg',descr:'lalalala'},{url:'pictures/img/img/s-l500 (1).jpg',descr:'lalalala'}]}]);
module.controller('offersList',['$scope','$uibModal',function($scope,$uibModal){
  $scope.currentPage = 1;
  $scope.pageSize = 3;
  $scope.offers = [{url:'pictures/img/img/s-l500 (1).jpg',desrc:''},{url:'pictures/img/img/s-l500 (2).jpg',desrc:''},{url:'pictures/img/img/s-l500 (3).jpg',desrc:''}
  ,{url:'pictures/img/img/s-l500 (4).jpg',desrc:''},{url:'pictures/img/img/s-l500 (5).jpg',desrc:''},
  {url:'pictures/img/img/s-l500 (6).jpg',desrc:''},{url:'pictures/img/img/s-l500 (7).jpg',desrc:''}];
  $scope.resetSlick = true;
  $scope.open = function(size)
    {
      $scope.resetSlick = false;
      var modalInstance = $uibModal.open({
      template: '<div class="container"'+
      '<div class="row">'+
        '<div class="col-md-7 col-md-offset-1 addPortfolioHeader">'+
          '<h3> Додати Пропозицію</h3> '+
        '</div> '+
      '</div>'+
        '<div class="row">'+
          '<div class="col-md-3 col-md-offset-1 divForImage">'+
            '<div drop-zone ng-model="data.upload" class="one">'+
              '<p>Drop files here <span>(or click to upload)</span></p>'+
            '</div>'+
            '<div class="portfImage">'+
              '<img ng-repeat="file in data.upload" src="{{file.data}}" />'+
            '</div>'+
          '</div>'+
          '<div class="col-md-5  imageInfo">'+
              '<div> <span>Натисніть на кватрат, щоб додати фотографію своєї роботи у форматі .jpg або .png</span> </div>'+
          '</div>'+
        '</div>'+
      '</div>'+
      '<div class="row">'+
      '<div class="col-md-8 col-md-offset-1 imageDescr">'+
        '<h3>Опис роботи (до 15 слів)</h3>'+
        '<textarea ng-model="descrOfNewPortfolio" name="name" rows="8" cols="65"> </textarea>'+
      '</div>'+
      '<div class="row">'+
          '<div class="col-md-7 col-md-offset-1 addPortfolioButtons">'+
            '<ul class="list-inline">'+
              '<li> <button ng-click="closeMod()" class="btn"><span>Скасувати</span></button> </li>'+
              '<li class="pull-right"> <button ng-click="savePortf()" class="btn"><span>Зберегти</span></button> </li>'+
            '</ul>'+
          '</div>'+
      '</div>'+
      '</div> </div>',
      controller: 'CreateOfferModule',
      size: size,
      resolve: {
        images: function () {
          return $scope.offers;
        }
      }
    });
    modalInstance.result.then(function (images) {
      $scope.offers = images;
      $scope.resetSlick = true;
      console.log($scope.images);
    }, function () {
      $log.info('Modal dismissed at: ' + new Date());
    });
  }
}]);
module.filter('startFrom',function(){
  return function(data,start){
    return data.slice(start);
  }
});
module.controller('CreateOfferModule',['$scope','$uibModalInstance','images',function($scope,$uibModalInstance,images){
    $scope.data = {upload :[]};
    $scope.images = images;
    $scope.closeMod = function(){
      $uibModalInstance.close();
    }
    $scope.savePortf = function(){
      $scope.images.push({url:$scope.data.upload[0].data,descr:$scope.descrOfNewPortfolio});
      $uibModalInstance.close($scope.images);
    }
}]);
module.controller('CreatePortfModule',['$scope','$uibModalInstance','images',function($scope,$uibModalInstance,images){
    $scope.data = {upload :[]};
    $scope.images = images;
    $scope.closeMod = function(){
      $uibModalInstance.close();
    }
    $scope.savePortf = function(){
      $scope.images.push({url:$scope.data.upload[0].data,descr:$scope.descrOfNewPortfolio});
      $uibModalInstance.close($scope.images);
    }
}]);
module.controller('createOrChangePorf',['$scope','$uibModal','$log',function($scope,$uibModal,$log){
  $scope.resetSlick = true;
  $scope.images = [{url:'pictures/img/img/s-l500 (1).jpg',descr:'lalalala'}];
  $scope.open = function(size)
    {
      $scope.resetSlick = false;
      var modalInstance = $uibModal.open({
      template: '<div class="container"'+
      '<div class="row">'+
        '<div class="col-md-7 col-md-offset-1 addPortfolioHeader">'+
          '<h3> Додати Портфоліо </h3> '+
        '</div> '+
      '</div>'+
        '<div class="row">'+
          '<div class="col-md-3 col-md-offset-1 divForImage">'+
            '<div drop-zone ng-model="data.upload" class="one">'+
              '<p>Drop files here <span>(or click to upload)</span></p>'+
            '</div>'+
            '<div class="portfImage">'+
              '<img ng-repeat="file in data.upload" src="{{file.data}}" />'+
            '</div>'+
          '</div>'+
          '<div class="col-md-5  imageInfo">'+
              '<div> <span>Натисніть на кватрат, щоб додати фотографію своєї роботи у форматі .jpg або .png</span> </div>'+
          '</div>'+
        '</div>'+
      '</div>'+
      '<div class="row">'+
      '<div class="col-md-8 col-md-offset-1 imageDescr">'+
        '<h3>Опис роботи (до 15 слів)</h3>'+
        '<textarea ng-model="descrOfNewPortfolio" name="name" rows="8" cols="65"> </textarea>'+
      '</div>'+
      '<div class="row">'+
          '<div class="col-md-7 col-md-offset-1 addPortfolioButtons">'+
            '<ul class="list-inline">'+
              '<li> <button ng-click="closeMod()" class="btn"><span>Скасувати</span></button> </li>'+
              '<li class="pull-right"> <button ng-click="savePortf()" class="btn"><span>Зберегти</span></button> </li>'+
            '</ul>'+
          '</div>'+
      '</div>'+
      '</div> </div>',
      controller: 'CreatePortfModule',
      size: size,
      resolve: {
        images: function () {
          return $scope.images;
        }
      }
    });
    modalInstance.result.then(function (images) {
      $scope.images = images;
      $scope.resetSlick = true;
      console.log($scope.images);
    }, function () {
      $log.info('Modal dismissed at: ' + new Date());
    });
  }
  $scope.edit = function(size)
  {
    $scope.resetSlick = false;
    var modalInstance = $uibModal.open({
      template:'<div class="container"'+
      '<div class="row">'+
        '<div class="col-md-7 col-md-offset-1 addPortfolioHeader">'+
          '<h3> Додати Портфоліо </h3> '+
        '</div> '+
      '</div>'+
        '<div class="row">'+
          '<div class="col-md-3 col-md-offset-1 divForImage">'+
            '<div drop-zone ng-model="data.upload" class="one">'+
              '<p>Drop files here <span>(or click to upload)</span></p>'+
            '</div>'+
            '<div class="portfImage">'+
              '<img ng-repeat="file in data.upload" src="{{file.data}}" />'+
            '</div>'+
          '</div>'+
          '<div class="col-md-5  imageInfo">'+
              '<div> <span>Натисніть на кватрат, щоб додати фотографію своєї роботи у форматі .jpg або .png</span> </div>'+
          '</div>'+
        '</div>'+
      '</div>'+
      '<div class="row">'+
      '<div class="col-md-8 col-md-offset-1 imageDescr">'+
        '<h3>Опис роботи (до 15 слів)</h3>'+
        '<textarea ng-model="descrOfNewPortfolio" name="name" rows="8" cols="65"> </textarea>'+
      '</div>'+
      '<div class="row">'+
          '<div class="col-md-7 col-md-offset-1 addPortfolioButtons">'+
            '<ul class="list-inline">'+
              '<li> <button ng-click="closeMod()" class="btn"><span>Скасувати</span></button> </li>'+
              '<li class="pull-right"> <button ng-click="savePortf()" class="btn"><span>Зберегти</span></button> </li>'+
            '</ul>'+
          '</div>'+
      '</div>'+
      '</div> </div>',
      // controller: '',
      size: size,
      resolve: {
        images: function () {
          return '';
        }
      }
    });
    modalInstance.result.then(function () {
      // $scope.images = images;
      // $scope.resetSlick = true;
      console.log($scope.images);
    }, function () {
      $log.info('Modal dismissed at: ' + new Date());
    });
  }
}]);
