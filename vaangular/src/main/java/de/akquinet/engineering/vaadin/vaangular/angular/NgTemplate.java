package de.akquinet.engineering.vaadin.vaangular.angular;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Files;

import com.google.gson.Gson;
import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;

import elemental.json.JsonObject;
import elemental.json.JsonValue;
import elemental.json.impl.JreJsonFactory;

/**
 * Use AngularJS with Vaadin - lightweight take which does not need a Widgetset 
 * - but does not offer additional functionality from {@link de.akquinet.engineering.vaadinjavascriptplus.AbstractJavaScriptComponentPlus}  
 */
@JavaScript({ "angular.js", "angular-sanitize.js", "NgTemplate.js" })
public abstract class NgTemplate extends AbstractJavaScriptComponent
{

	private static final long serialVersionUID = 1L;

	private JsonObject userState;

    public NgTemplate(String templateSource, String moduleName) {
    	userState = new JreJsonFactory().createObject();
    	
		getState().templateSource = templateSource;
    	getState().moduleName = moduleName;
	}
    
	public NgTemplate(Package templatePackage, String moduleName)
			throws URISyntaxException, UnsupportedEncodingException,
			IOException {
		userState = new JreJsonFactory().createObject();

		File templateFolder = new File(NgTemplate.class
				.getClassLoader()
				.getResource(templatePackage.getName().replace('.', '/'))
				.getPath());

		initFromFile(templateFolder, moduleName);
	}

	public NgTemplate(File templateFolder, String moduleName) throws IOException {
		userState = new JreJsonFactory().createObject();
		
		initFromFile(templateFolder, moduleName);
	}

	private void initFromFile(File templateFolder, String moduleName)
			throws UnsupportedEncodingException, IOException {
		if (templateFolder == null) {
			throw new IllegalArgumentException(
					"param 'templateFolder' must be null!");
		}
		if (templateFolder.exists() == false) {
			throw new IllegalArgumentException(
					"param 'templateFolder' must exist!");
		}
		if (templateFolder.isDirectory() == false) {
			throw new IllegalArgumentException(
					"param 'templateFolder' must represent a directory!");
		}
		String templateSource = new String(Files.readAllBytes(new File(
				templateFolder, templateFolder.getName() + ".html").toPath()),
				"UTF-8");

		getState().templateSource = templateSource;
    	getState().moduleName = moduleName;
	}

	@Override
    public NgTemplateState getState()
    {
		return (NgTemplateState) super.getState();
	}

    public void addService(final String name, final Object callback)
    {
        Class<?> type = callback.getClass();
        for (final Method method : type.getMethods())
        {
            if (method.isAnnotationPresent(ServiceMethod.class))
            {
                final String methodName = method.getName();
                addFunction(name + "_" + methodName, new MethodInvoker(callback, method));
            }
        }
    }

	public void setUserState(String key, String value) {
		this.userState.put(key, value);
		pushUserStateChange();
	}
	
	public void setUserState(String key, Object value) {
		
		String json = new Gson().toJson(value);
		this.userState.put(key, (JsonValue)new JreJsonFactory().parse(json));
		pushUserStateChange();
	}

	private void pushUserStateChange()
	{
		getState().changeType = "userState";
		getState().userState = this.userState.toJson();
	}

}
