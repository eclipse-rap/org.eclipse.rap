/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapter;
import org.eclipse.swt.widgets.Button;


public final class ButtonThemeAdapter extends ControlThemeAdapter {

  private static final Point CHECK_SIZE = new Point( 13, 13 );
  private static final int CHECK_SPACING = 4;

  protected void configureMatcher( final WidgetMatcher matcher ) {
    super.configureMatcher( matcher );
    matcher.addStyle( "FLAT", SWT.FLAT );
    matcher.addStyle( "PUSH", SWT.PUSH );
    matcher.addStyle( "TOGGLE", SWT.TOGGLE );
    matcher.addStyle( "CHECK", SWT.CHECK );
    matcher.addStyle( "RADIO", SWT.RADIO );
  }

  public Rectangle getPadding( final Button button ) {
    return getCssBoxDimensions( "Button", "padding", button );
  }

  public int getSpacing( final Button button ) {
    return getCssDimension( "Button", "spacing", button );
  }

  public int getCheckSpacing( final Button button ) {
    return CHECK_SPACING;
  }

  public Point getCheckSize( final Button button ) {
    return CHECK_SIZE;
  }
}
