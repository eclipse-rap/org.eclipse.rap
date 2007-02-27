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

package org.eclipse.rap.rwt.widgets;

import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;

public class ControlHolder_Test extends TestCase {

  public void testControlHolder() {
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    Control control = new Text( shell, RWT.NONE );
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
    } catch( final NullPointerException npe ) {
      // expected
    }
    controlHolder.remove( control );
    try {
      controlHolder.remove( control );
      String msg = "Only controls that are contained in the item list must be removed.";
      fail( msg );
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
    try {
      controlHolder.remove( null );
      fail( "Parameter item must not be null" );
    } catch( final NullPointerException npe ) {
      // expected
    }
  }

  public void testControlHolderAccessors() {
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    Control[] controls = ControlHolder.getControls( shell );
    assertEquals( 0, controls.length );
    Button control = new Button( shell, RWT.PUSH );
    controls = ControlHolder.getControls( shell );
    assertEquals( 1, controls.length );
    assertEquals( control, controls[ 0 ] );
    assertEquals( 1, shell.getChildrenCount() );
    control.dispose();
    controls = ControlHolder.getControls( shell );
    assertEquals( 0, controls.length );
    Control extended = new Control( null ) {
    };
    try {
      ControlHolder.addControl( shell, extended );
      fail( "Control has no parent." );
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
  }

  // TODO: Test add with index
  public void testAddAtIndex() throws Exception {
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    Button b1 = new Button( shell, RWT.PUSH );
    Button b2 = new Button( shell, RWT.PUSH );
    Button b3 = new Button( shell, RWT.PUSH );
    ControlHolder controlHolder = new ControlHolder();
    controlHolder.add( b1 );
    controlHolder.add( b2, 0 );
    controlHolder.add( b3, 2 );
    assertEquals( 1, controlHolder.indexOf( b1 ) );
    assertEquals( 0, controlHolder.indexOf( b2 ) );
    assertEquals( 2, controlHolder.indexOf( b3 ) );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
