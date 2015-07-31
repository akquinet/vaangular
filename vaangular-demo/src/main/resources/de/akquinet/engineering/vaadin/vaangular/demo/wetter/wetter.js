angular.module('wetterModule', ['ngSanitize'])
.controller('WetterController', function($scope, $connector, $sce) {
	// $scope.userState bekommen wir
	$scope.sliderPos = 0;
	$scope.content = function() {
		var res = $scope.userState.eintraege[$scope.sliderPos];
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