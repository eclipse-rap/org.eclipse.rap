/*******************************************************************************
 * Copyright (c) 2008, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapter;
import org.eclipse.swt.widgets.*;


public class ControlThemeAdapter_Test extends TestCase {

  public void testValues() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Label label = new Label( shell, SWT.BORDER );
    IThemeAdapter themeAdapter
      = ( IThemeAdapter )label.getAdapter( IThemeAdapter.class );
    assertTrue( themeAdapter instanceof ControlThemeAdapter );
    ControlThemeAdapter cta = ( ControlThemeAdapter )themeAdapter;
    assertEquals( 1, cta.getBorderWidth( label ) );
    assertEquals( Graphics.getColor( 74, 74, 74 ), cta.getForeground( label ) );
    assertEquals( Graphics.getColor( 255, 255, 255 ),
                  cta.getBackground( label ) );
    // even though the label is grayed out, getForeground() shows the original
    // color in SWT
    label.setEnabled( false );
    assertEquals( Graphics.getColor( 74, 74, 74 ), cta.getForeground( label ) );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeNewRequest();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
