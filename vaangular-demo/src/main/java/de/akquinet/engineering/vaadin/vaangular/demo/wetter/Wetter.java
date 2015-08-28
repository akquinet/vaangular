package de.akquinet.engineering.vaadin.vaangular.demo.wetter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.annotations.JavaScript;
import com.vaadin.event.ConnectorEventListener;

import de.akquinet.engineering.vaadin.vaangular.angular.NgTemplatePlus;
import de.akquinet.engineering.vaadin.vaangular.angular.ServiceMethod;

@JavaScript({ "META-INF/resources/webjars/angularjs/1.3.15/angular.js",
		"META-INF/resources/webjars/angularjs/1.3.15/angular-sanitize.js",
		"wetter.js" })
public class Wetter extends NgTemplatePlus {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1857536974366619130L;

	private List<WetterClickListener> listeners = new ArrayList<Wetter.WetterClickListener>();

	private int[] times;
	private String[] entries;

	public Wetter() throws IOException, URISyntaxException {
		super(Wetter.class.getPackage(), "wetterModule");
		addService("button", new Object() {
			@ServiceMethod
			public void click() {
				int index = getSliderPos();
				System.out
						.println("Button from w/in angular - value: " + index);
				for (WetterClickListener listener : listeners) {
					listener.click(times[index], entries[index]);
				}
			}
		});
		setButtonCaption("senden");
	}

	public void setButtonCaption(String caption) {
		setUserState("buttonCaption", caption);
	}

	public int getSliderPos() {
		return Integer.parseInt(getVariables().get("sliderPos").toString());
	}

	public void setDaten(int[] times, String[] entries) {
		validateParameters(times, entries);
		this.times = times;
		this.entries = entries;
		setUserState("times", times);
		setUserState("entries", entries);
		markAsDirty();
	}

	static void validateParameters(int[] times, String[] entries) {
		if (times.length != entries.length) {
			throw new IllegalArgumentException("#times does not match #entries");
		}
		if (times.length < 2) {
			throw new IllegalArgumentException(
					"#times/#entries needs to be >=2");
		}
		int step = calcStep(times);
		for (int i = 1; i < times.length; i++) {
			if (!(times[i - 1] < times[i])) {
				throw new IllegalArgumentException("Times must be in order");
			}
			if ((times[i] - times[i - 1]) != step) {
				throw new IllegalArgumentException(
						"Times must have same delta between one another");
			}
		}
	}

	private static int calcStep(int[] times) {
		int step = times[1] - times[0];
		return step;
	}

	public void addClickListener(WetterClickListener listener) {
		listeners.add(listener);
	}

	public void removeClickListener(WetterClickListener listener) {
		listeners.remove(listener);
	}

	public interface WetterClickListener extends ConnectorEventListener {

		public void click(int time, String entry);
	}

}
