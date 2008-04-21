/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.internal.statushandlers;

import org.eclipse.jface.util.Policy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.statushandlers.AbstractStatusAreaProvider;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.WorkbenchErrorHandler;
import org.eclipse.ui.statushandlers.WorkbenchStatusDialogManager;

/**
 * This class is responsible for displaying stack trace retrieved from IStatus.
 * It has similar functionality as details area in {@link WorkbenchStatusDialogManager}.
 * This class will be visible only if it is enabled in
 * {@link WorkbenchStatusDialogManager} and no support provider is passed by
 * {@link Policy}
 * 
 * @see Policy#setErrorSupportProvider
 * @see Policy#getErrorSupportProvider()
 * @see WorkbenchStatusDialogManager#enableDefaultSupportArea
 * @see WorkbenchErrorHandler
 * 
 */
public class StackTraceSupportArea extends AbstractStatusAreaProvider {

	/*
	 * Displays statuses.
	 */
	private List list;

	// RAP [bm]: Clipboard
//	private Clipboard clipboard;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.statushandlers.AbstractStatusAreaProvider#createSupportArea(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.ui.statushandlers.StatusAdapter)
	 */
	public Control createSupportArea(final Composite parent,
			StatusAdapter statusAdapter) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(WorkbenchMessages.get().StackTraceSupportArea_Title);

		list = new List(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.widthHint = 250;
		list.setLayoutData(gd);
		// RAP [bm]: 
//		list.addDisposeListener(new DisposeListener() {
//			public void widgetDisposed(DisposeEvent e) {
//				if (clipboard != null) {
//					clipboard.dispose();
//				}
//			}
//		});
		// RAPEND: [bm] 

		list.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e) {
				list.selectAll();
				super.widgetSelected(e);
			}
		});
		list.removeAll();
		populateList(statusAdapter.getStatus().getException());
		createDNDSource();
		createCopyAction(parent);
		return parent;
	}

	/**
	 * Creates DND source for the list
	 */
	private void createDNDSource() {
		// RAP [bm]: 
//		DragSource ds = new DragSource(list, DND.DROP_COPY);
//		ds.setTransfer(new Transfer[] { TextTransfer.getInstance() });
//		ds.addDragListener(new DragSourceListener() {
//			public void dragFinished(DragSourceEvent event) {
//
//			}
//
//			public void dragSetData(DragSourceEvent event) {
//				if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
//					event.data = prepareCopyString();
//				}
//			}
//
//			public void dragStart(DragSourceEvent event) {
//				list.selectAll();
//			}
//		});
		// RAPEND: [bm] 
	}

	private void createCopyAction(final Composite parent) {
		// RAP [bm]: 
//		Menu menu = new Menu(parent.getShell(), SWT.POP_UP);
//		MenuItem copyAction = new MenuItem(menu, SWT.PUSH);
//		copyAction.setText("&Copy"); //$NON-NLS-1$
//		copyAction.addSelectionListener(new SelectionAdapter() {
//			/*
//			 * (non-Javadoc)
//			 * 
//			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
//			 */
//			public void widgetSelected(SelectionEvent e) {
//				clipboard = new Clipboard(parent.getDisplay());
//				clipboard.setContents(new Object[] { prepareCopyString() },
//						new Transfer[] { TextTransfer.getInstance() });
//				super.widgetSelected(e);
//			}
//		});
//		list.setMenu(menu);
		// RAPEND: [bm] 
	}

	// RAP [bm]: 
//	private String prepareCopyString() {
//		if (list == null || list.isDisposed()) {
//			return ""; //$NON-NLS-1$
//		}
//		StringBuffer sb = new StringBuffer();
//		String newLine = System.getProperty("line.separator"); //$NON-NLS-1$
//		for (int i = 0; i < list.getItemCount(); i++) {
//			sb.append(list.getItem(i));
//			sb.append(newLine);
//		}
//		return sb.toString();
//	}

	private void populateList(Throwable t) {
		if (t == null) {
			list.add(WorkbenchMessages.get().StackTraceSupportArea_NoStackTrace);
			return;
		}
		list.add(t.toString());
		StackTraceElement[] ste = t.getStackTrace();
		for (int i = 0; i < ste.length; i++) {
			list.add(ste[i].toString());
		}
		if (t.getCause() != null) {
			list.add(WorkbenchMessages.get().StackTraceSupportArea_CausedBy);
			populateList(t.getCause());
		}
	}

	/**
	 * @return Returns the list.
	 */
	public List getList() {
		return list;
	}
}
