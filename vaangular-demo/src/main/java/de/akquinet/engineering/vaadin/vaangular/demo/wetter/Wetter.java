package de.akquinet.engineering.vaadin.vaangular.demo.wetter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.annotations.JavaScript;
import com.vaadin.event.ConnectorEventListener;

import de.akquinet.engineering.vaadin.vaangular.angular.NgTemplatePlus;
import de.akquinet.engineering.vaadin.vaangular.angular.ServiceMethod;

@JavaScript({ "META-INF/resources/webjars/angularjs/1.3.15/angular.js", "META-INF/resources/webjars/angularjs/1.3.15/angular-sanitize.js", "wetter.js" })
public class Wetter extends NgTemplatePlus {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1857536974366619130L;

	private List<WetterClickListener> listeners = new ArrayList<Wetter.WetterClickListener>();

	private int[] zeiten;
	private String[] eintraege;

	public Wetter() throws IOException, URISyntaxException {
		super(Wetter.class.getPackage(), "wetterModule");
		addService("button", new Object() {
			@ServiceMethod
			public void click() {
				System.out.println(getVariables().get("sliderPos").getClass());
				int index = Integer.parseInt(getVariables().get("sliderPos").toString());
				for (WetterClickListener listener : listeners) {
					listener.click(zeiten[index], eintraege[index]);
				}
			}
		});
		setButtonCaption("senden");
	}

	public void setButtonCaption(String caption) {
		setUserState("buttonCaption", caption);
	}

	public void setDaten(int[] zeiten, String[] eintraege) {
		validateParameters(zeiten, eintraege);
		this.zeiten = zeiten;
		this.eintraege = eintraege;
		setUserState("zeiten", zeiten);
		setUserState("eintraege", eintraege);
		markAsDirty();
	}

	static void validateParameters(int[] zeiten, String[] eintraege) {
		if (zeiten.length != eintraege.length) {
			throw new IllegalArgumentException(
					"Nicht so viele Zeiten wie Einträge");
		}
		if (zeiten.length < 2) {
			throw new IllegalArgumentException("Mindestens zwei Zeiten nötig");
		}
		int step = calcStep(zeiten);
		for (int i = 1; i < zeiten.length; i++) {
			if (!(zeiten[i - 1] < zeiten[i])) {
				throw new IllegalArgumentException(
						"Zeiten müssen aufeinander aufbauen");
			}
			if ((zeiten[i] - zeiten[i - 1]) != step) {
				throw new IllegalArgumentException(
						"Zeiten müssen äquidistant sein");
			}
		}
	}

	private static int calcStep(int[] zeiten) {
		int step = zeiten[1] - zeiten[0];
		return step;
	}

	public void addClickListener(WetterClickListener listener) {
		listeners.add(listener);
	}

	public void removeClickListener(WetterClickListener listener) {
		listeners.remove(listener);
	}

	public interface WetterClickListener extends ConnectorEventListener {

		public void click(int zeit, String eintrag);
	}

}
