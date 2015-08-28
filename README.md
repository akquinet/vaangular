vaangular
=========

Provide stellar integration of AngularJS with vaadin

## What is vaangular?
vaangular combines the power of [vaadin](https://vaadin.com) and Java with the power of [AngularJS](https://angularjs.org) to create awesome UIs. Effectively, you create three things: 
1. an HTML fragment with AngularJS attributes and directives in it
2. an AngularJS controller (along with tests in e.g. Jasmine)
3. a vaadin component to hook up the previous two with the backend

vaangular makes extensive use `com.vaadin.ui.AbstractJavaScriptComponent` and adds additional logic to bridge vaadin *states* with AngularJS *$scope(s)*. If you want, you can combine the [JavaScriptPlus for vaadin](https://github.com/akquinet/JavascriptPlusForvaadin) add-on with this add-on to use deferred variable changes in AngularJS.

A comprehensive demo is included that shows the full roundtrip (see the `vaangular-demo` folder for all the code - you can fire off this example by running `de.akquinet.engineering.vaadin.vaangular.demo.VaangularApplication.main` which invokes Spring Boot, pulls up a server and offers you an endpoint at `localhost:8090`)

We'll use this as an example throughout the page... (it's best viewed in Webkit = Chrome / Safari btw)

## Creating a component

### Project structure

We recommend using [Maven](http://maven.apache.org) as build tool - which results into src/main/java, src/main/resources, etc. as base directory structure along with a pom.xml

Furthermore, you can utilize Spring Boot, Eclipse, etc. (what our example does)

### Creating the package

Create a package (with identical names) in both src/main/java and src/main/resources - in our example: `de.akquinet.engineering.vaadin.vaangular.demo.wetter`

### Creating the HTML fragment

Create an HTML file with the same name as the last part of the package (here: 'wetter') ending with '.html'. The file can contain any markup as long as there is exactly one topmost tag.

So, your 'wetter.html' can initially look like this: 
```html
<div ng-controller="WetterController">
	<div ng-bind-html="content()"></div>
	<input style="width: 508px; margin-left: 18px;" type="range" min="0" max="{{userState.times.length-1}}" step="1" ng-model="sliderPos" ng-change="sliderUpdated()" />
	<div style="margin-left: 20px; ">
		<div style="display: inline-block; width: {{500/(userState.times.length-1)}}px;" ng-repeat="zeit in userState.times" ng-click="moveSlider($index)">
			<div style="display: inline-block; margin-left: -50px; width: 50px; text-align: right; ">{{zeit}}</div>:00
		</div>
	</div>
	<!-- vaadin-like Button -->
	<div tabindex="0" role="button" class="v-button v-widget" ng-click="clickButton()">
		<span class="v-button-wrap">
			<span class="v-button-caption">{{userState.buttonCaption}}</span>
		</span>
	</div>
</div>
```

What it does is: 
- declare a div to take up some dynamic values (rendered by *content()*)
- create a range input field
- create some labels below (yes, inline styling is bad, but verbose examples are, too)
- provide a button in AngularJS that looks exactly like a button in  vaadin

### Creating the AngularJS controller

Obviously, our file also needs a controller which mainly needs to do two things: 
- render the content based on where the slider is (we use inline HTML via ngSanitize)
- move the slider upon label click
- make the slider pos available to vaadin (we use deferred variable changes via `setDeferredVariable` - more explanation can be found in the next section)
- invoke some action on button click (i.e. call logic implemented in vaadin)

```javascript
angular.module('wetterModule', ['ngSanitize'])
.controller('WetterController', function($scope, $connector, $sce) {
	// $scope.userState bekommen wir
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
```

Nothing so far is mysterious - except that we use `$scope.userState` that seemingly comes out of nowhere. In fact, it is all the data vaadin ships to us - at our fingertips. How you fill the user state is actually explained right below:

### Providing a counterpart in vaadin (extending NgTemplate or NgTemplatePlus)

So what we're still missing from above example is
- filling `$scope.userState`
- something to do with `sliderPos`
- an implementation for `$connector.button_click()`

In order to do that, there has to be a class `de.akquinet.engineering.vaadin.vaangular.demo.wetter.Wetter` extending `de.akquinet.engineering.vaadin.vaangular.angular.NgTemplate(Plus)`

When extending `NgTemplatePlus` (that comes with the [JavaScriptPlus for vaadin](https://github.com/akquinet/JavascriptPlusForvaadin) add-on), you can use deferred variable changes: No call to the server is made until some button click or other (non-deferred) action happens. This dramatically increases responsiveness while reducing (unnecessary) network roundtrips and bandwidth usage. Many standard vaadin components provide this exact behavior via `immediate=false`.

With this many advantages of deferred variable changes: our example uses those and therefore has to use either a custom widgetset or the pre-compiled widgeset coming with the [JavaScriptPlus for vaadin](https://github.com/akquinet/JavascriptPlusForvaadin) add-on (we do the latter).

To fill up the user state with some times and weather infos, we provide the following method: 
```java
public void setDaten(int[] times, String[] entries) {
	validateParameters(times, entries);
	this.times = times;
	this.entries = entries;
	setUserState("times", times);
	setUserState("entries", entries);
	markAsDirty();
}
```

`setUserState` is provided by NgTemplate / NgTemplatePlus: adding `xyz` to it results in `$scope.userState.xyz` on the client - so `times` results in `$scope.userState.times`. Please observe that changes to this state on the client are not replicated back and overwritten without notice (the standard vaadin behavior). In order go get info from the client to the server, you can either use a method invocation or (provided you use NgTemplatePlus) a deferred variable change. So far, for providing infos to the client, `setUserState`is perfectly OK.

To work with the slider position (`sliderPos`), we use 	

```javascript
$connector.setDeferredVariable("sliderPos", parseInt($scope.sliderPos));
```

on the JavaScript side. On the Java side, we can pull this as follows: 

```java
public int getSliderPos() {
	return Integer.parseInt(getVariables().get("sliderPos").toString());
}
```

As the variable change is deferred, no sync to the server happens before a button click. Our example provides a standard vaadin button (`de.akquinet.engineering.vaadin.vaangular.demo.VaangularUI.javaSend`) which (when clicked) shows the currently selected weather info. The following code on click does this: 

```java
javaSend.addClickListener(new ClickListener() {

	private static final long serialVersionUID = 1L;

	@Override
	public void buttonClick(ClickEvent event) {
		int index = weatherInfo.getSliderPos();
		System.out.println("Button from w/in Java - value: " + index);
		showPopup(entries[index]);
	}
});
```

Upon clicking the button, vaadin does a server roundtrip (as it usually does for buttons). vaadin first processes the deferred variable changes (which is why `de.akquinet.engineering.vaadin.vaangular.angular.NgTemplatePlus.getVariables` yields a correct, up-to-date value) and the invokes the click listener. 

When you open the demo application (along with the Chrome inspector's Network tab), you don't see any server roundtrip when clicking or moving the slider. Only clicking the *E-Mail (from Java)* button produces one.

Finally, we also want so show how to call the server *immediately* (and how to create a vaadin-like button in pure JavaScript): the *E-Mail (from angular)* is written in AngularJS and invokes vaadin via the following line: 

```javascript
$connector.button_click();
```

On the Java side, we provide a counterpart for this as follows: 

```java
addService("button", new Object() {
	@ServiceMethod
	public void click() {
		int index = getSliderPos();
		System.out.println("Button from w/in angular - value: " + index);
		for (WetterClickListener listener : listeners) {
			listener.click(times[index], entries[index]);
		}
	}
});
```

So: any class can provide methods to the AngularJS-side as long as there methods are *public* and annotated with `@ServiceMethod` (in fact: `de.akquinet.engineering.vaadin.vaangular.angular.ServiceMethod`). Clicking the button now results in the Java method being called (again with deferred variable changes processed before that).

### Getting data from vaadin to AngularJS

Done by calling `de.akquinet.engineering.vaadin.vaangular.angular.NgTemplatePlus.setUserState(String, Object)` (2nd param must be something that can be turned into JSON). Angular then gets it via `$scope.userState`

### Calling vaadin from AngularJS

Done by calling `de.akquinet.engineering.vaadin.vaangular.angular.NgTemplatePlus.addService(String, Object)` with the 2nd parameter being an implementing a service. Methods to be exposed need to have params that can be derived from JSON and have to be annotated with `@ServiceMethod`. Call from JavaScript via `$connector.service_method`

### Deferred variable changes

Done by calling `$connector.setDeferredVariable` on the JavaScript side (the value will be JSON-serialized). Only available with NgTemplatePlus.

## Background

vaangular solves two essential (and non-obvious) problems: 
1. Delivering JavaScript in a pluggable fashion
2. Creating an AngularJS *$scope* along with a Controller in it that gets data from vaadin and can communicate back to vaadin

### Delivering JavaScript (using @JavaScript)

vaadin provides a standard method for this via the `@JavaScript` annotation (`com.vaadin.annotations.JavaScript`). In order to include AngularJS, one can e.g. use [WebJars](http://www.webjars.org/) or simply check in the file along with the project.

### Creating the AngularJS *$scope*

The main magic that happens within `NgTemplate.js` is creating a *$scope* for AngularJS. There is one essential challenge: the JavaScript invoked by vaadin is **outside** any AngularJS dependency injection or scope management. Meanwhile, all code you write for AngularJS is obviously **inside** dependency injection and scope management.

Furthermore, AngularJS assumes (at least per se) that the page's HTML is parsed once and wired up to AngularJS. With vaadin, however, the page's HTML is just some proxy to fire up vaadin magic, and there is nothing to wire up. Even more so, the part with AngularJS in it might be added to a vaadin UI a considerable amount of time after the application was started. 

So, what is needed is an approach to create some new AngularJS-wired-up DOM and Controller on demand from outside AngularJS dependency management / scoping magic.

vaangular achieves this (in bespoke `NgTemplate.js`) in two steps: 

First, a scope **within** AngularJS is created via 
```javascript
angular.injector([ 'ng', innerModuleName ]).invoke(function($compile, $rootScope) {
```

Second, the scope (called `scpe`) and DOM are brought together via
```javascript
var cmp = $compile(templateElement);
cmp(scpe);
scpe.$digest();
```

At the end of this process, we have a new part of the DOM wired up to a new controller - mission accomplished!
