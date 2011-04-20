/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.shellkit;import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.theme.*;
import org.eclipse.rwt.internal.theme.WidgetMatcher.Constraint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapter;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
public final class ShellThemeAdapter extends ControlThemeAdapter {  private static final int MENU_BAR_MIN_HEIGHT = 20;

  protected void configureMatcher( WidgetMatcher matcher ) {
    super.configureMatcher( matcher );
    matcher.addStyle( "TITLE", SWT.TITLE );
    matcher.addState( "maximized", new Constraint() {

      public boolean matches( Widget widget ) {
        return ( ( Shell )widget ).getMaximized();
      }
    } );
  }
  public Rectangle getTitleBarMargin( Shell shell ) {
    Rectangle result;
    if( ( shell.getStyle() & SWT.TITLE ) != 0 ) {
      result = getCssBoxDimensions( "Shell-Titlebar", "margin", shell );
    } else {      result = new Rectangle( 0, 0, 0, 0 );    }    return result;  }  public int getTitleBarHeight( Shell shell ) {    int result = 0;    if( ( shell.getStyle() & SWT.TITLE ) != 0 ) {
      result = getCssDimension( "Shell-Titlebar", "height", shell );
    }    return result;  }  public int getMenuBarHeight( Shell shell ) {    int result = 0;    if( shell.getMenuBar() != null ) {      Font font = getCssFont( "Shell", "font", shell );      int fontHeight = Graphics.getCharHeight( font );      result = Math.max( MENU_BAR_MIN_HEIGHT, fontHeight );    }    return result;  }}
