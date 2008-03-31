/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.custom.ctabfolderkit;

import org.eclipse.rwt.internal.theme.ThemeAdapterUtil;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapter;
import org.eclipse.swt.widgets.Control;


public class CTabFolderThemeAdapter extends ControlThemeAdapter {

  public Color getBackground( Control control ) {
    return ThemeAdapterUtil.getColor( control, "ctabfolder.background" );
  }

  public Color getForeground( Control control ) {
    return ThemeAdapterUtil.getColor( control, "ctabfolder.foreground" );
  }

  public Color getSelectedBackground( Control control ) {
    return ThemeAdapterUtil.getColor( control, "ctabfolder.selection.background" );
  }

  public Color getSelectedForeground( Control control ) {
    return ThemeAdapterUtil.getColor( control, "ctabfolder.selection.foreground" );
  }
}
