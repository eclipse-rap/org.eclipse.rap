/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.internal;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Shows a list of open editors in the current or last active workbook.
 * 
 * 
 */
public class WorkbookEditorsHandler extends AbstractHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow workbenchWindow = HandlerUtil
				.getActiveWorkbenchWindow(event);
		if (workbenchWindow == null) {
			// action has been disposed
			return null;
		}
		IWorkbenchPage page = workbenchWindow.getActivePage();
		if (page != null) {
			WorkbenchPage wbp = (WorkbenchPage) page;
			EditorAreaHelper eah = wbp.getEditorPresentation();
			if (eah != null) {
				eah.displayEditorList();
			}
		}
		return null;
	}

}
