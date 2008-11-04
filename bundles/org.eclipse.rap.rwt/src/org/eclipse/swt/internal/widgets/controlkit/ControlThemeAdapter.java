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
package org.eclipse.swt.internal.widgets.controlkit;

import org.eclipse.rwt.internal.theme.AbstractThemeAdapter;
import org.eclipse.rwt.internal.theme.WidgetMatcher;
import org.eclipse.rwt.theme.IControlThemeAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Control;


public class ControlThemeAdapter extends AbstractThemeAdapter
  implements IControlThemeAdapter
{

  protected void configureMatcher( final WidgetMatcher matcher ) {
    matcher.addStyle( "BORDER", SWT.BORDER );
    matcher.addState( "enabled", WidgetMatcher.CONTROL_ENABLED );
  }

  public int getBorderWidth( final Control control ) {
    return getCssBorderWidth( getPrimaryElement( control ), "border", control );
  }

  public Color getForeground( final Control control ) {
    return getCssColor( getPrimaryElement( control ), "color", control );
  }

  public Color getBackground( final Control control ) {
    return getCssColor( getPrimaryElement( control ), "background-color", control );
  }

  public Font getFont( final Control control ) {
    return getCssFont( getPrimaryElement( control ), "font", control );
  }
}
