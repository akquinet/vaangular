package de.akquinet.engineering.vaadin.vaangular.demo;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.akquinet.engineering.vaadin.vaangular.demo.wetter.Wetter;
import de.akquinet.engineering.vaadin.vaangular.demo.wetter.Wetter.WetterClickListener;

@Theme("valo")
@PreserveOnRefresh
public class VaangularUI extends UI {

	private static final long serialVersionUID = 1L;

	@Override
	protected void init(VaadinRequest request) {

		try {

			VerticalLayout mainLayout = new VerticalLayout();
			mainLayout.setMargin(true);
			Accordion accordion = new Accordion();

			Wetter wetter = new Wetter();
			wetter.setDaten(new int[] { 10, 12, 14, 16 }, new String[] {
					"<strong>10°</strong> sonnig",
					"<strong>12°</strong> windig", 
					"<strong>14°</strong> kalt",
					"<strong>20°</strong> super" });
			wetter.addClickListener(new WetterClickListener() {

				private static final long serialVersionUID = 1L;

				@Override
				public void click(int zeit, String eintrag) {
					Window modalWin = new Window("Wird gesendet...");
					modalWin.setContent(new Label("<div>"
							+ "<h2>Season's greetings</h2>" + "<p>" + eintrag
							+ "</p>" + "</div>", ContentMode.HTML));
					modalWin.setModal(true);
					modalWin.setWidth("400px");
					modalWin.setHeight("250px");
					modalWin.center();
					UI.getCurrent().addWindow(modalWin);
				}
			});
			accordion.addTab(wetter, "Wetter-Demo");

			mainLayout.addComponent(accordion);

			setContent(mainLayout);
		} catch (Exception e) {
			throw new RuntimeException("some stupid error occured!", e);
		}
	}

}
