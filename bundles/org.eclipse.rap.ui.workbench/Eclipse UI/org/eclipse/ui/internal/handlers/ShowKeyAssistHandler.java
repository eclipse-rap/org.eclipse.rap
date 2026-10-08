// RAP [bm]: Bindings
///*******************************************************************************
// * Copyright (c) 2004, 2006 IBM Corporation and others.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License 2.0
// * which is available at
// * https://www.eclipse.org/legal/epl-2.0
// *
// * SPDX-License-Identifier: EPL-2.0
// *
// * Contributors:
// *     IBM Corporation - initial API and implementation
// *******************************************************************************/
//
//package org.eclipse.ui.internal.handlers;
//
//import org.eclipse.core.commands.ExecutionEvent;
//import org.eclipse.ui.IWorkbench;
//import org.eclipse.ui.PlatformUI;
//import org.eclipse.ui.keys.IBindingService;
//
///**
// * A handler that displays the key assist dialog when executed.
// */
//public class ShowKeyAssistHandler extends WorkbenchWindowHandlerDelegate {
//
//	/**
//	 * Opens the key assistant. This should never be called until initialization
//	 * occurs.
//	 * 
//	 * @param event
//	 *            Ignored
//	 * @return <code>null</code>
//	 */
//	public Object execute(final ExecutionEvent event) {
//		final IWorkbench workbench = PlatformUI.getWorkbench();
//		final IBindingService bindingService = (IBindingService) workbench.getService(IBindingService.class);
//		bindingService.openKeyAssistDialog();
//		return null;
//	}
//}
