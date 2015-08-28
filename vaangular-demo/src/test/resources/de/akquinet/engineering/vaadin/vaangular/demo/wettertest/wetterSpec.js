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
describe("Wetter base suite", function() {
	var $rootScope, $connector, $sce, createController;
	beforeEach(module("wetterModule"));
	beforeEach(inject(function($injector) {
		$rootScope = $injector.get('$rootScope');
		$rootScope.userState={};
		$connector = {
			setDeferredVariable : function() {
			},
			button_click : function() {
			}
		};
		$sce = {
			trustAsHtml : function(str){
				return str;
			}
		};
		var $controller = $injector.get('$controller');
		createController = function() {
			return $controller('WetterController', {
				'$scope' : $rootScope,
				'$connector' : $connector,
				'$sce' : $sce
			});
		};
	}));
	it("displays initial content", function() {
		var controller = createController();
		$rootScope.sliderPos = 2;
		$rootScope.userState.entries = [ "zero", "one", "two", "three" ];
		expect($rootScope.content()).toEqual("two");
	});
	it("displays content based on slider", function() {
		var controller = createController();
		$rootScope.sliderPos = 2;
		$rootScope.userState.entries = [ "zero", "one", "two", "three" ];
		spyOn($connector, 'setDeferredVariable');
		$rootScope.moveSlider(1);
		expect($rootScope.content()).toEqual("one");
		expect($connector.setDeferredVariable).toHaveBeenCalledWith("sliderPos", 1);
	});
	it("reacts on slider change", function() {
		var controller = createController();
		$rootScope.sliderPos = 2;
		$rootScope.userState.entries = [ "zero", "one", "two", "three" ];
		spyOn($connector, 'setDeferredVariable');
		$rootScope.sliderPos = 1;
		$rootScope.sliderUpdated();
		expect($rootScope.content()).toEqual("one");
		expect($connector.setDeferredVariable).toHaveBeenCalledWith("sliderPos", 1);
	});
	it("button calls to vaadin", function() {
		var controller = createController();
		spyOn($connector, 'button_click');
		$rootScope.clickButton();
		expect($connector.button_click).toHaveBeenCalled();
	});
});