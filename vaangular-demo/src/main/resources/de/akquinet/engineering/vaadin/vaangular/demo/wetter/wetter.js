/* 
   Licensed under the Apache License, Version 2.0 (the "License"); you may not
   use this file except in compliance with the License. You may obtain a copy of
   the License at
  
     http://www.apache.org/licenses/LICENSE-2.0
  
   Unless required by applicable law or agreed to in writing, software 
   distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
   WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
   License for the specific language governing permissions and limitations under
   the License. 
 */
angular.module('wetterModule', ['ngSanitize'])
.controller('WetterController', function($scope, $connector, $sce) {
	$scope.sliderPos = 0;
	$scope.content = function() {
		var res = $scope.userState.entries[$scope.sliderPos];
		return $sce.trustAsHtml(res);
	};
	$scope.moveSlider = function(val) {
		$scope.sliderPos = val;
		$scope.sliderUpdated();
	};
	$scope.sliderUpdated = function() {
		$connector.setDeferredVariable("sliderPos", parseInt($scope.sliderPos));
	};
	$scope.clickButton = function() {
		$connector.button_click();
	};
});