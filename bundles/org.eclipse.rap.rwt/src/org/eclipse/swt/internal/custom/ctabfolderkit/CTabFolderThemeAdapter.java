/*******************************************************************************
 * Copyright (c) 2008, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.swt.internal.custom.ctabfolderkit;

import org.eclipse.rwt.internal.theme.*;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapter;


public class CTabFolderThemeAdapter extends ControlThemeAdapter {

  public Color getBackground( final CTabFolder folder ) {
    return getCssColor( "CTabItem", "background-color", folder );
  }

  public Color getForeground( final CTabFolder folder ) {
    return getCssColor( "CTabItem", "color", folder );
  }

  public Color getSelectedBackground( final CTabFolder folder ) {
    QxType cssValue = ThemeUtil.getCssValue( "CTabItem",
                                             "background-color",
                                             SimpleSelector.SELECTED );
    return QxColor.createColor( ( QxColor )cssValue );
  }

  public Color getSelectedForeground( final CTabFolder folder ) {
    QxType cssValue = ThemeUtil.getCssValue( "CTabItem",
                                             "color",
                                             SimpleSelector.SELECTED );
    return QxColor.createColor( ( QxColor )cssValue );
  }
}
