package org.eclipse.rap.e4.demo.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.EventTopic;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

@SuppressWarnings("restriction")
public class EventSamplePart {
	@Inject
	IEventBroker broker;
	
	@Inject
	UISynchronize uiSync;
	
	private ListViewer listViewer;
	
	@PostConstruct
	void init(Composite parent) {
		parent.setLayout(new GridLayout());

		listViewer = new ListViewer(parent);
		listViewer.getControl().setLayoutData(
				new GridData(GridData.FILL_BOTH));
		
		createSendGroup(parent, "EventTopic");
		createSendGroup(parent, "UIEventTopic");
	}

	private void createSendGroup(Composite parent, final String topicType) {
		Group g = new Group(parent, SWT.NONE);
		g.setText("@" + topicType);
		g.setLayout(new GridLayout(2, true));
		g.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button b = new Button(g, SWT.PUSH);
		b.setText("Sync sending");
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				broker.send("rape4/" + topicType, "@"+topicType+" - Event at " + System.currentTimeMillis());
			}
		});
		b.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		b = new Button(g, SWT.PUSH);
		b.setText("Async sending");
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				broker.post("rape4/" + topicType, "@"+topicType+" - Event at " + System.currentTimeMillis());
			}
		});
		b.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}
	
	@Inject
	@Optional
	void receiveEvent(@EventTopic("rape4/EventTopic") final String eventData) {
		if( listViewer.getControl().getDisplay().getThread() == Thread.currentThread() ) {
			listViewer.add(eventData);
		} else {
			uiSync.syncExec(new Runnable() {
				
				@Override
				public void run() {
					listViewer.add(eventData); 
				}
			});
		}
	}
	
	@Inject
	@Optional
	void receiveUIEvent(@UIEventTopic("rape4/UIEventTopic") final String eventData) {
		listViewer.add(eventData);
	}
}
