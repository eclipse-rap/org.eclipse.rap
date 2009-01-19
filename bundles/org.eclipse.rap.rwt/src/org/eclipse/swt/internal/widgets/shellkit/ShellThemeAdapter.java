/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.shellkit;import org.eclipse.rwt.internal.theme.*;
import org.eclipse.rwt.internal.theme.WidgetMatcher.Constraint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapter;
import org.eclipse.swt.widgets.*;
public final class ShellThemeAdapter extends ControlThemeAdapter {  private static final int MENU_BAR_MIN_HEIGHT = 20;

  protected void configureMatcher( final WidgetMatcher matcher ) {
    super.configureMatcher( matcher );
    matcher.addStyle( "TITLE", SWT.TITLE );
    matcher.addState( "maximized", new Constraint() {

      public boolean matches( final Widget widget ) {
        return ( ( Shell )widget ).getMaximized();
      }
    } );
  }
  public Rectangle getPadding( final Shell shell ) {    return getCssBoxDimensions( "Shell", "padding", shell );  }  public Rectangle getTitleBarMargin( final Shell shell ) {
    Rectangle result;
    if( ( shell.getStyle() & SWT.TITLE ) != 0 ) {
      QxType cssValue = ThemeUtil.getCssValue( "Shell-Titlebar",
                                               "margin",
                                               SimpleSelector.DEFAULT );
      result = QxBoxDimensions.createRectangle( ( QxBoxDimensions )cssValue );
    } else {      result = new Rectangle( 0, 0, 0, 0 );    }    return result;  }  public int getTitleBarHeight( final Shell shell ) {    int result = 0;    if( ( shell.getStyle() & SWT.TITLE ) != 0 ) {
      QxType cssValue = ThemeUtil.getCssValue( "Shell-Titlebar",
                                               "height",
                                               SimpleSelector.DEFAULT );
      result = ( ( QxDimension )cssValue ).value;
    }    return result;  }  public int getMenuBarHeight( final Shell shell ) {    int result = 0;    if( shell.getMenuBar() != null ) {      Font font = getCssFont( "Shell", "font", shell );      int fontHeight = TextSizeDetermination.getCharHeight( font );      result = Math.max( MENU_BAR_MIN_HEIGHT, fontHeight );    }    return result;  }}
