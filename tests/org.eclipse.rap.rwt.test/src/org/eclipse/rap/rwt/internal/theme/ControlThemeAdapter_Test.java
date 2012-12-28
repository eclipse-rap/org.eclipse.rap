/*******************************************************************************
 * Copyright (c) 2008, 2012 Innoopract Informationssysteme GmbH and others.
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
import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapterImpl;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ControlThemeAdapter_Test {

  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakeNewRequest();
    Display display = new Display();
    shell = new Shell( display );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testValues() {
    Label label = new Label( shell, SWT.BORDER );
    ControlThemeAdapterImpl cta = ( ControlThemeAdapterImpl )label.getAdapter( IThemeAdapter.class );

    assertEquals( 1, cta.getBorderWidth( label ) );
    assertEquals( Graphics.getColor( 74, 74, 74 ), cta.getForeground( label ) );
    assertEquals( Graphics.getColor( 255, 255, 255 ), cta.getBackground( label ) );
  }

  @Test
  public void testForegroundWhenDisabled() {
    Label label = new Label( shell, SWT.BORDER );
    label.setEnabled( false );
    ControlThemeAdapterImpl cta = ( ControlThemeAdapterImpl )label.getAdapter( IThemeAdapter.class );

    // even though the label is grayed out, getForeground() shows the original color in SWT
    assertEquals( Graphics.getColor( 74, 74, 74 ), cta.getForeground( label ) );
  }

  @Test
  public void testGetBorderWidthForCompositeSubclass() throws IOException {
    String css = "Composite.special { border: 23px solid gray }";
    ThemeTestUtil.registerTheme( "custom", css, null );
    ThemeTestUtil.setCurrentThemeId( "custom" );

    Composite subclassedComposite = new Composite( shell, SWT.BORDER ) {
      // empty subclass
    };
    subclassedComposite.setData( RWT.CUSTOM_VARIANT, "special" );

    assertEquals( 23, subclassedComposite.getBorderWidth() );
  }

}
