package org.eclipse.rap.e4.demo.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class HelloWorldHandler {
	@Execute
	public void hello(Shell s) {
		MessageBox b = new MessageBox(s,SWT.ICON_INFORMATION);
		b.setText("Hello e4");
		b.setMessage("e4 and RAP are a perfect match!!");
		b.open();
	}
}
