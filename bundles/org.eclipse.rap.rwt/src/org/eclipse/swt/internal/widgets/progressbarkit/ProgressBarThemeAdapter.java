/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.progressbarkit;

import org.eclipse.swt.internal.theme.*;
import org.eclipse.swt.widgets.Control;

public class ProgressBarThemeAdapter implements IProgressBarThemeAdapter {

	public QxColor getBackground(Control control) {
		Theme theme = ThemeUtil.getTheme();
	    return theme.getColor( "progressbar.background" );
	}

	public int getBorderWidth(Control control) {
		return 0;
	}

	public QxFont getFont(Control control) {
		return null;
	}

	public QxColor getForeground(Control control) {
		Theme theme = ThemeUtil.getTheme();
	    return theme.getColor( "progressbar.foreground" );
	}

}
