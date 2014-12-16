/*******************************************************************************
 * Copyright (c) 2008, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.theme.ControlThemeAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapterImpl;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ControlThemeAdapter_Test {

  private Display display;
  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakeNewRequest();
    display = new Display();
    shell = new Shell( display );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testValues() {
    Label label = new Label( shell, SWT.BORDER );
    ControlThemeAdapter cta = getControlThemeAdapter( label );

    assertEquals( new Rectangle( 1, 1, 2, 2 ), cta.getBorder( label ) );
    assertEquals( new Color( display, 74, 74, 74 ), cta.getForeground( label ) );
    assertEquals( new Color( display, 255, 255, 255 ), cta.getBackground( label ) );
  }

  @Test
  public void testForegroundWhenDisabled() {
    Label label = new Label( shell, SWT.BORDER );
    label.setEnabled( false );
    ControlThemeAdapter cta = getControlThemeAdapter( label );

    // even though the label is grayed out, getForeground() shows the original color in SWT
    assertEquals( new Color( display, 74, 74, 74 ), cta.getForeground( label ) );
  }

  @Test
  public void testGetBorderWidth_forCompositeSubclass() throws IOException {
    String css = "Composite.special { border: 23px solid gray }";
    ThemeTestUtil.registerTheme( "custom", css, null );
    ThemeTestUtil.setCurrentThemeId( "custom" );

    Composite subclassedComposite = new Composite( shell, SWT.BORDER ) {
      // empty subclass
    };
    subclassedComposite.setData( RWT.CUSTOM_VARIANT, "special" );

    assertEquals( 23, subclassedComposite.getBorderWidth() );
  }

  @Test
  public void testGetBorder() throws IOException {
    String css = "Composite { border: 1px solid black; border-top: 3px solid black; }";
    ThemeTestUtil.registerTheme( "custom", css, null );
    ThemeTestUtil.setCurrentThemeId( "custom" );

    Composite composite = new Composite( shell, SWT.BORDER );

    Rectangle expected = new Rectangle( 1, 3, 2, 4 );
    assertEquals( expected , getControlThemeAdapter( composite ).getBorder( composite ) );
  }

  private ControlThemeAdapter getControlThemeAdapter( Control control ) {
    return ( ControlThemeAdapterImpl )control.getAdapter( IThemeAdapter.class );
  }

}
