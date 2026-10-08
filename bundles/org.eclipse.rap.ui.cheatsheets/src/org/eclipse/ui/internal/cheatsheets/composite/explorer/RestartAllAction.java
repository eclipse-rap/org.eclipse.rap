/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.internal.cheatsheets.composite.explorer;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.cheatsheets.CheatSheetPlugin;
import org.eclipse.ui.internal.cheatsheets.Messages;
import org.eclipse.ui.internal.cheatsheets.composite.model.CompositeCheatSheetModel;
import org.eclipse.ui.internal.provisional.cheatsheets.ICompositeCheatSheet;

public class RestartAllAction extends Action {

	private ICompositeCheatSheet model;

	public RestartAllAction(ICompositeCheatSheet model) {
        this.model = model;
		this.setText(Messages.get().RESTART_ALL_MENU);
		IPath path = CheatSheetPlugin.ICONS_PATH.append(CheatSheetPlugin.T_ELCL).append("restart_all.gif");//$NON-NLS-1$
		ImageDescriptor restartImage = CheatSheetPlugin.createImageDescriptor(CheatSheetPlugin.getPlugin().getBundle(), path);	
		this.setImageDescriptor(restartImage);
	}
	
	public void run() {
		if (confirmRestart()) {
		    ((CompositeCheatSheetModel)model).resetAllTasks(null);
		}
	}
	
	public static boolean confirmRestart() {
		return MessageDialog.openConfirm(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
				Messages.get().COMPOSITE_RESTART_DIALOG_TITLE, 
				Messages.get().COMPOSITE_RESTART_CONFIRM_MESSAGE); 
	}

}
