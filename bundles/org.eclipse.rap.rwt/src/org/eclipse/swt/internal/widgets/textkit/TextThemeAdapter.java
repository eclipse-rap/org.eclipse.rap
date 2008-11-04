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
package org.eclipse.swt.internal.widgets.textkit;

import org.eclipse.rwt.internal.theme.WidgetMatcher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapter;
import org.eclipse.swt.widgets.Text;

public final class TextThemeAdapter extends ControlThemeAdapter {

  protected void configureMatcher( final WidgetMatcher matcher ) {
    super.configureMatcher( matcher );
    matcher.addStyle( "SINGLE", SWT.SINGLE );
    matcher.addStyle( "MULTI", SWT.MULTI );
  }

  public Rectangle getPadding( final Text text ) {
    return getCssBoxDimensions( "Text", "padding", text );
  }
}
