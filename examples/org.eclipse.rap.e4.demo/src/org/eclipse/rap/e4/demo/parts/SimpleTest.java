package org.eclipse.rap.e4.demo.parts;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class SimpleTest {
	@PostConstruct
	void init(MPart part, Composite parent) {
		parent.setLayout(new GridLayout());
		Label l = new Label(parent,SWT.NONE);
		l.setText(part.getLabel());
	}
}
