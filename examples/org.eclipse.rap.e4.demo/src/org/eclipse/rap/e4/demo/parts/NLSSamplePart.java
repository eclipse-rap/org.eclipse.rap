package org.eclipse.rap.e4.demo.parts;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.services.nls.ILocaleChangeService;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NLSSamplePart {
	private Label currentLangLabel;
	private Label currentLangValue;
	private Label updateLangLabel;
	private Text updateLangValue;
	private Button updateLangButton;
	
	@Inject
	ILocaleChangeService localeService;

	@Inject
	public NLSSamplePart(Composite parent) {
		parent.setLayout(new GridLayout(3, false));
		
		currentLangLabel = new Label(parent, SWT.NONE);
		currentLangValue = new Label(parent, SWT.NONE);
		currentLangValue.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false,2,1));
		
		updateLangLabel = new Label(parent, SWT.NONE);
		updateLangValue = new Text(parent, SWT.BORDER);
		updateLangButton = new Button(parent, SWT.PUSH);
		updateLangButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				localeService.changeApplicationLocale(updateLangValue.getText());
			}
		});
	}
	
	@Inject
	public void setMessages(@Translation NLSMessages messages, @Named(TranslationService.LOCALE) Locale locale) {
		currentLangLabel.setText(messages.NLSSamplePart_currentLangLabel);
		currentLangValue.setText(locale.toString());
		
		updateLangLabel.setText(messages.NLSSamplePart_updateLangLabel);
		updateLangValue.setText(locale.toString());
		updateLangButton.setText(messages.NLSSamplePart_updateLangButton);
		
		currentLangLabel.getParent().layout(true);
	}
}
