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
package org.eclipse.rwt.internal.theme;

import java.awt.Button;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Composite;


public class AbstractThemeAdapter_Test extends TestCase {

  public void testWithElement() throws Exception {
    AbstractThemeAdapter adapter = new AbstractThemeAdapter() {

      protected void configureMatcher( final WidgetMatcher matcher ) {
      }
    };
    ThemeableWidget widget = new ThemeableWidget( Composite.class, null );
    IThemeCssElement element = new ThemeCssElement( "MyWidget" );
    widget.elements = new IThemeCssElement[] { element  };
    adapter.init( widget );
    assertEquals( "MyWidget", adapter.getPrimaryElement() );
  }

  public void testDerivedElementName() throws Exception {
    AbstractThemeAdapter adapter = new AbstractThemeAdapter() {

      protected void configureMatcher( final WidgetMatcher matcher ) {
      }
    };
    ThemeableWidget widget = new ThemeableWidget( Button.class, null );
    adapter.init( widget );
    assertEquals( "Button", adapter.getPrimaryElement() );
  }
}
