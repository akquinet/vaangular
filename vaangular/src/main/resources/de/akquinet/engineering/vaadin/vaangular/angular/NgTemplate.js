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
var de_akquinet_engineering_vaadin_vaangular_angular_NgTemplate = function() {

	var connector = this;
	var initialized = false;
	var scpe;

	this.onStateChange = function() {

		if (!initialized) {

			initialized = true;

			var connectorElement = angular.element(connector.getElement());

			var templateSource = connector.getState().templateSource;
			var templateElement = angular.element(templateSource);
			
			var innerModuleName = connector.getState().moduleName + "_inner" + connector.getConnectorId();
			angular.module(innerModuleName, [connector.getState().moduleName]);
			angular.module(innerModuleName).factory("$connector", function() {
				return connector;
			});
			
			angular.injector([ 'ng', innerModuleName ]).invoke(function($compile, $rootScope) {
				var cmp = $compile(templateElement);
				scpe = $rootScope.$new(false);
				if (connector.getState().userState) {
					scpe.userState = JSON.parse(connector.getState().userState);
				}
				cmp(scpe);
				connectorElement.append(templateElement);
				scpe.$digest();
			});
		}

		var changeType = connector.getState().changeType;

		if (changeType == "userState") {
			scpe.$apply(function() {
				scpe.userState = JSON.parse(connector.getState().userState);
			});
		}
	}

};
// also the 2nd connector
var de_akquinet_engineering_vaadin_vaangular_angular_NgTemplatePlus = de_akquinet_engineering_vaadin_vaangular_angular_NgTemplate;
