angular.module('Mydropzone',[])
    .directive('dropZone',[
        function(){
            var config = {
                template:'<label id="portfolioImage" class="drop-zone">'+
                         '<input type="file" accept="jpg" />'+
                         '<div ng-transclude></div>'+
                         '</label>',
                transclude:true,
                replace: true,                
                require: '?ngModel',
                link: function(scope, element, attributes, ngModel){
                    var upload = element[0].querySelector('input');
                        upload.addEventListener('dragover', uploadDragOver, false);
                        upload.addEventListener('drop', uploadFileSelect, false);
                        upload.addEventListener('change', uploadFileSelect, false);
                        config.scope = scope;
                        config.model = ngModel;
                }
            }
            return config;
            function uploadDragOver(e) { e.stopPropagation(); e.preventDefault(); e.dataTransfer.dropEffect = 'copy'; }
            function uploadFileSelect(e) {
                console.log(e.target.baseURI);
                e.stopPropagation();
                e.preventDefault();
                var files = e.dataTransfer ? e.dataTransfer.files: e.target.files;
                for (var i = 0, file; file = files[i]; ++i) {
                    console.log(file);
                    var reader = new FileReader();
                    reader.onload = (function(file) {
                        return function(e) {
                          console.log(e);
                            var data={
                                data:e.target.result,
                                dataSize: e.target.result.length
                            };
                            for(var p in file){ data[p] = file[p] }
                            config.scope.$apply(function(){ config.model.$viewValue.push(data) });
                            document.getElementById('portfolioImage').style.display='none';
                        }
                    })(file);
                    reader.readAsDataURL(file);
                }
            }
        }
    ]);
