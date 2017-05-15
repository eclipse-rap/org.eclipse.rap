package org.eclipse.rap.e4.demo.parts;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.rap.e4.preferences.EPreference;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

@SuppressWarnings({ "restriction", "serial" })
public class PreferenceSamplePart {

	private Label colorLabel;
	private Color currentColor;

	@Optional
	@Inject
	@Preference
	EPreference prefs;

	@Inject
	public PreferenceSamplePart(Composite parent) {
		parent.setLayout(new GridLayout(2, false));

		Label l1 = new Label(parent, SWT.NONE);
		l1.setText("Current Value:");

		colorLabel = new Label(parent, SWT.NONE);
		colorLabel.setText("-");

		Label l2 = new Label(parent, SWT.NONE);
		l2.setText("Color:");

		final Text t = new Text(parent, SWT.BORDER);
		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				prefs.setString("colorPref", t.getText());
			}
		};
		t.addListener(SWT.DefaultSelection, listener);
		t.addListener(SWT.FocusOut, listener);
		t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	@Inject
	public void updatePreference(@Optional @Preference("colorPref") String v) {
		if (v == null) {
			return;
		}
		colorLabel.setText(v);
		RGB rgb = toRGB(v);
		Color c = rgb != null ? new Color(colorLabel.getDisplay(), rgb) : null;
		colorLabel.setBackground(c);
		if (currentColor != null) {
			currentColor.dispose();
		}
		currentColor = c;

		colorLabel.getParent().layout(true);
	}

	private static RGB toRGB(String hexcolor) {
		try {
			if (hexcolor.length() == 3) {
				int r = Integer.parseInt(hexcolor.substring(0, 1), 16) * 17;
				int g = Integer.parseInt(hexcolor.substring(1, 2), 16) * 17;
				int b = Integer.parseInt(hexcolor.substring(2, 3), 16) * 17;
				return new RGB(r, g, b);
			}
			if (hexcolor.length() == 6) {
				int r = Integer.parseInt(hexcolor.substring(0, 2), 16);
				int g = Integer.parseInt(hexcolor.substring(2, 4), 16);
				int b = Integer.parseInt(hexcolor.substring(4, 6), 16);
				return new RGB(r, g, b);
			}
		} catch (NumberFormatException e) {
			// return fall back
		}
		return null;
	}
}
