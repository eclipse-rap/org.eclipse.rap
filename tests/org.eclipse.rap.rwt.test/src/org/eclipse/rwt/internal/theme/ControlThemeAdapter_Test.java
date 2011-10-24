/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapter;
import org.eclipse.swt.widgets.*;


public class ControlThemeAdapter_Test extends TestCase {

	private Shell shell;

	protected void setUp() throws Exception {
	  Fixture.setUp();
	  Fixture.fakeNewRequest();
	  Display display = new Display();
	  shell = new Shell( display );
	}

	protected void tearDown() throws Exception {
	  Fixture.tearDown();
	}

  public void testValues() {
    Label label = new Label( shell, SWT.BORDER );
    ControlThemeAdapter cta = ( ControlThemeAdapter )label.getAdapter( IThemeAdapter.class );

    assertEquals( 1, cta.getBorderWidth( label ) );
    assertEquals( Graphics.getColor( 74, 74, 74 ), cta.getForeground( label ) );
    assertEquals( Graphics.getColor( 255, 255, 255 ), cta.getBackground( label ) );
  }

  public void testForegroundWhenDisabled() {
    Label label = new Label( shell, SWT.BORDER );
    label.setEnabled( false );
    ControlThemeAdapter cta = ( ControlThemeAdapter )label.getAdapter( IThemeAdapter.class );

    // even though the label is grayed out, getForeground() shows the original color in SWT
    assertEquals( Graphics.getColor( 74, 74, 74 ), cta.getForeground( label ) );
  }

  public void testGetBorderWidthForCompositeSubclass() throws IOException {
    String css = "Composite.special { border: 23px solid gray }";
    ThemeTestUtil.registerCustomTheme( "custom", css, null );
    ThemeUtil.setCurrentThemeId( "custom" );

    Composite subclassedComposite = new Composite( shell, SWT.BORDER ) {
      // empty subclass
    };
    subclassedComposite.setData( WidgetUtil.CUSTOM_VARIANT, "special" );

    assertEquals( 23, subclassedComposite.getBorderWidth() );
	}

}
