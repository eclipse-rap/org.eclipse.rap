package org.eclipse.rap.e4.demo.lifecycle;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

@SuppressWarnings("restriction")
public class LoginLifcecycle {
	@PostContextCreate
	boolean login(Display d) {
		final AtomicBoolean rv = new AtomicBoolean(false);
		final Shell s = new Shell(d);
		s.setText("Login");
		s.setLayout(new GridLayout(2, false));
		
		{
			Label l = new Label(s, SWT.NONE);
			l.setText("Username");
			
			Text t = new Text(s, SWT.BORDER);
			t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}
		
		{
			Label l = new Label(s, SWT.NONE);
			l.setText("Password");
			
			Text t = new Text(s, SWT.BORDER);
			t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}
		
		{
			Composite buttonContainer = new Composite(s, SWT.NONE);
			buttonContainer.setLayout(new GridLayout(2, true));
			buttonContainer.setLayoutData(new GridData(SWT.TRAIL, SWT.CENTER, false, false, 2, 1));

			{
				Button b = new Button(buttonContainer, SWT.PUSH);
				b.setText("Abort");
				b.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						rv.set(false);
						s.dispose();
					}
				});
			}

			{
				Button b = new Button(buttonContainer, SWT.PUSH);
				b.setText("Login");
				b.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						rv.set(true);
						s.dispose();
					}
				});
			}
		}
		s.pack();
		s.setSize(300, s.getSize().y + 10);
		Rectangle bounds = d.getPrimaryMonitor().getBounds();
		
		Point size = s.getSize();
		s.setLocation(bounds.width / 2 - size.x / 2, bounds.height / 2 - size.y / 2);
		
		s.open();
		while( !s.isDisposed() && ! d.isDisposed() ) {
			if( ! d.readAndDispatch() ) {
				d.sleep();
			}
		}
		
		return rv.get();
	}
}