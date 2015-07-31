package de.akquinet.engineering.vaadin.vaangular.demo;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

@WebServlet()
@VaadinServletConfiguration(ui = VaangularUI.class, productionMode = false, widgetset = "de.akquinet.engineering.vaadin.javascriptplus.JavaScriptPlusForVaadin")
public class VaangularServlet extends VaadinServlet {

	private static final long serialVersionUID = 1L;

}
