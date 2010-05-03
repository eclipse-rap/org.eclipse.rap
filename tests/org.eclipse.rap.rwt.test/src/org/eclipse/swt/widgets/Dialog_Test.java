/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Ralf Zahn (ARS) - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.SWT;


public class Dialog_Test extends TestCase {

  private Display display;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
  }

  protected void tearDown() throws Exception {
    display.dispose();
    Fixture.tearDown();
  }

  public void testDefaults() {
    Shell parent = new Shell( display );
    Dialog dialog = new Dialog( parent ) {};
    assertSame( parent, dialog.getParent() );
    assertEquals( "", dialog.getText() );
    assertEquals( SWT.PRIMARY_MODAL, dialog.getStyle() );
  }

  public void testSetText() {
    Shell parent = new Shell( display );
    Dialog dialog = new Dialog( parent ) {};
    dialog.setText( "Test" );
    assertEquals( "Test", dialog.getText() );
  }

  public void testSetTextNull() {
    Shell parent = new Shell( display );
    Dialog dialog = new Dialog( parent ) {};
    try {
      dialog.setText( null );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertEquals( "Argument cannot be null", expected.getMessage() );
    }
  }
}
