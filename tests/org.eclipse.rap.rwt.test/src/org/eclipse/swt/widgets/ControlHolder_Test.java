/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.widgets;

import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;

public class ControlHolder_Test extends TestCase {

  public void testControlHolder() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Control control = new Text( shell, SWT.NONE );
    ControlHolder controlHolder = new ControlHolder();
    assertEquals( 0, controlHolder.size() );
    assertEquals( 0, controlHolder.getControls().length );
    controlHolder.add( control );
    assertEquals( 1, controlHolder.size() );
    assertSame( control, controlHolder.getControls()[ 0 ] );
    controlHolder.remove( control );
    assertEquals( 0, controlHolder.size() );
    assertEquals( 0, controlHolder.getControls().length );
    controlHolder.add( control );
    try {
      controlHolder.add( control );
      fail( "The same control must not be added twice." );
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
    try {
      controlHolder.add( null );
      fail( "Parameter control must not be null." );
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
    controlHolder.remove( control );
    try {
      controlHolder.remove( control );
      String msg
        = "Only controls that are contained in the item list must be removed.";
      fail( msg );
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
    try {
      controlHolder.remove( null );
      fail( "Parameter item must not be null" );
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
  }

  public void testControlHolderAccessors() {
    final Display outerDisplay = new Display();
    Composite shell = new Shell( outerDisplay, SWT.NONE );
    Control[] controls = ControlHolder.getControls( shell );
    assertEquals( 0, controls.length );
    Button control = new Button( shell, SWT.PUSH );
    controls = ControlHolder.getControls( shell );
    assertEquals( 1, controls.length );
    assertEquals( control, controls[ 0 ] );
    assertEquals( 1, shell.getChildren().length );
    control.dispose();
    controls = ControlHolder.getControls( shell );
    assertEquals( 0, controls.length );
    Control extended = new Control( null ) {
      public Display getDisplay() {
        return outerDisplay;
      }
    };
    try {
      ControlHolder.addControl( shell, extended );
      fail( "Control has no parent." );
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
  }

  public void testAddAtIndex() throws Exception {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Button button1 = new Button( shell, SWT.PUSH );
    Button button2 = new Button( shell, SWT.PUSH );
    Button button3 = new Button( shell, SWT.PUSH );
    ControlHolder controlHolder = new ControlHolder();
    controlHolder.add( button1 );
    controlHolder.add( button2, 0 );
    controlHolder.add( button3, 2 );
    assertEquals( 1, controlHolder.indexOf( button1 ) );
    assertEquals( 0, controlHolder.indexOf( button2 ) );
    assertEquals( 2, controlHolder.indexOf( button3 ) );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
