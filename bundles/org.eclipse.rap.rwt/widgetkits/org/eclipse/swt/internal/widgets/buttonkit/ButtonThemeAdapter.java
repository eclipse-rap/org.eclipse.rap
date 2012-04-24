/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.buttonkit;

import org.eclipse.rwt.internal.theme.WidgetMatcher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapter;
import org.eclipse.swt.widgets.Button;


public final class ButtonThemeAdapter extends ControlThemeAdapter {

  @Override
  protected void configureMatcher( WidgetMatcher matcher ) {
    super.configureMatcher( matcher );
    matcher.addStyle( "FLAT", SWT.FLAT );
    matcher.addStyle( "ARROW", SWT.ARROW );
    matcher.addStyle( "PUSH", SWT.PUSH );
    matcher.addStyle( "TOGGLE", SWT.TOGGLE );
    matcher.addStyle( "CHECK", SWT.CHECK );
    matcher.addStyle( "RADIO", SWT.RADIO );
    matcher.addStyle( "UP", SWT.UP );
    matcher.addStyle( "DOWN", SWT.DOWN );
    matcher.addStyle( "LEFT", SWT.LEFT );
    matcher.addStyle( "RIGHT", SWT.RIGHT );
  }

  public int getSpacing( Button button ) {
    return getCssDimension( "Button", "spacing", button );
  }

  public int getCheckSpacing( Button button ) {
    return getCssDimension( "Button", "spacing", button );
  }

  public Point getCheckSize( Button button ) {
    Point result = null;
    if( ( button.getStyle() & SWT.RADIO ) != 0) {
      result = getCssImageDimension( "Button-RadioIcon", "background-image", button );
    } else if( ( button.getStyle() & SWT.CHECK ) != 0) {
      result = getCssImageDimension( "Button-CheckIcon", "background-image", button );
    }
    return result;
  }

  public Point getArrowSize( Button button ) {
    Point result = new Point( 0, 0 );
    if( ( button.getStyle() & SWT.ARROW ) != 0) {
      result = getCssImageDimension( "Button-ArrowIcon", "background-image", button );
    }
    return result;
  }

}