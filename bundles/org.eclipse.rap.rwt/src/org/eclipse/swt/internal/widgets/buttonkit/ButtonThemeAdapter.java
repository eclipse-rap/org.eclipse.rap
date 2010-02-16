/*******************************************************************************
 * Copyright (c) 2007, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.buttonkit;

import org.eclipse.rwt.internal.theme.WidgetMatcher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapter;
import org.eclipse.swt.widgets.Button;


public final class ButtonThemeAdapter extends ControlThemeAdapter {

  protected void configureMatcher( final WidgetMatcher matcher ) {
    super.configureMatcher( matcher );
    matcher.addStyle( "FLAT", SWT.FLAT );
    matcher.addStyle( "PUSH", SWT.PUSH );
    matcher.addStyle( "TOGGLE", SWT.TOGGLE );
    matcher.addStyle( "CHECK", SWT.CHECK );
    matcher.addStyle( "RADIO", SWT.RADIO );
  }

  public int getSpacing( final Button button ) {
    return getCssDimension( "Button", "spacing", button );
  }

  public int getCheckSpacing( final Button button ) {
    return getCssDimension( "Button", "spacing", button );
  }

  public Point getCheckSize( final Button button ) {
    Point result = null;
    if( ( button.getStyle() & SWT.RADIO ) != 0) {
      result = getCssImageDimension( "Button-RadioIcon",
                                     "background-image",
                                     button );
    } else if( ( button.getStyle() & SWT.CHECK ) != 0) {
      result = getCssImageDimension( "Button-CheckIcon",
                                     "background-image",
                                     button );
    }
    return result;
  }

}