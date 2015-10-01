/*
 * Copyright 2015 akquinet engineering GmbH
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.akquinet.engineering.vaadin.vaangular.demo;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.akquinet.engineering.vaadin.vaangular.demo.weather.Weather;
import de.akquinet.engineering.vaadin.vaangular.demo.weather.Weather.WeatherClickListener;

@Theme("valo")
@PreserveOnRefresh
public class VaangularUI extends UI {

	private static final long serialVersionUID = 1L;

	protected Weather weatherInfo;
	protected Button javaSend;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.ui.UI#init(com.vaadin.server.VaadinRequest)
	 */
	@Override
	protected void init(VaadinRequest request) {

		try {

			VerticalLayout mainLayout = new VerticalLayout();
			mainLayout.setMargin(true);
			mainLayout.setSpacing(true);
			Accordion accordion = new Accordion();

			weatherInfo = new Weather();
			final int[] times = new int[] { 10, 12, 14, 16 };
			final String[] entries = new String[] {
					"<strong>10°</strong> sunny", "<strong>12°</strong> windy",
					"<strong>14°</strong> cold", "<strong>20°</strong> superb" };
			weatherInfo.setDaten(times, entries);
			weatherInfo.addClickListener(new WeatherClickListener() {

				private static final long serialVersionUID = 1L;

				@Override
				public void click(int time, String entry) {
					showPopup(entry);
				}
			});
			weatherInfo.setButtonCaption("E-Mail (from angular)");
			accordion.addTab(weatherInfo, "Weather-Demo");
			mainLayout.addComponent(accordion);

			javaSend = new Button();
			javaSend.setCaption("E-Mail (from Java)");
			javaSend.addClickListener(new ClickListener() {

				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(ClickEvent event) {
					int index = weatherInfo.getSliderPos();
					System.out.println("Button from w/in Java - value: "
							+ index);
					showPopup(entries[index]);
				}
			});
			mainLayout.addComponent(javaSend);

			setContent(mainLayout);
		} catch (Exception e) {
			throw new RuntimeException("some stupid error occured!", e);
		}
	}

	private void showPopup(String eintrag) {
		Window modalWin = new Window("E-Mail is being sent...");
		modalWin.setContent(new Label("<div style=\"margin: 10px; \">"
				+ "<h2>Season's greetings</h2>" + "<p>" + eintrag + "</p>"
				+ "</div>", ContentMode.HTML));
		modalWin.setModal(true);
		modalWin.setWidth("400px");
		modalWin.setHeight("250px");
		modalWin.center();
		UI.getCurrent().addWindow(modalWin);
	}

}
