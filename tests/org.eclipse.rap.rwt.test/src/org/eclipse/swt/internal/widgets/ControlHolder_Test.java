/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ControlHolder_Test {

  private Display display;
  private Composite shell;
  private ControlHolder controlHolder;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display , SWT.NONE );
    controlHolder = new ControlHolder();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testEmpty() {
    assertEquals( 0, controlHolder.size() );
    assertEquals( 0, controlHolder.getControls().length );
  }

  @Test
  public void testAddControl() {
    Control control = new Text( shell, SWT.NONE );
    controlHolder.add( control );

    assertEquals( 1, controlHolder.size() );
    assertSame( control, controlHolder.getControls()[ 0 ] );
  }

  @Test
  public void testRemoveControl() {
    Control control = new Text( shell, SWT.NONE );
    controlHolder.add( control );
    controlHolder.remove( control );

    assertEquals( 0, controlHolder.size() );
    assertEquals( 0, controlHolder.getControls().length );
  }

  @Test
  public void testAddControlTwice() {
    Control control = new Text( shell, SWT.NONE );
    controlHolder.add( control );

    try {
      controlHolder.add( control );
      fail( "The same control must not be added twice." );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
  }

  @Test
  public void testRemoveNonExistingControl() {
    Control control = new Text( shell, SWT.NONE );

    try {
      controlHolder.remove( control );
      String msg = "Only controls that are contained in the item list can be removed.";
      fail( msg );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
  }

  @Test
  public void testAddControlWithNull() {
    try {
      controlHolder.add( null );
      fail( "Parameter item must not be null" );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
  }

  @Test
  public void testRemoveControlWithNull() {
    try {
      controlHolder.remove( null );
      fail( "Parameter item must not be null" );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
  }

  @Test
  public void testStaticSizeWithEmptyShell() {
    assertEquals( 0, ControlHolder.size( shell ) );
  }

  @Test
  public void testStaticSizeWithOneChild() {
    new Button( shell, SWT.PUSH );

    assertEquals( 1, ControlHolder.size( shell ) );
  }

  @Test
  public void testStaticSizeWithDisposedChild() {
    Button control = new Button( shell, SWT.PUSH );
    control.dispose();

    assertEquals( 0, ControlHolder.size( shell ) );
  }

  @Test
  public void testStaticAddControlWithWrongParent() {
    Composite otherParent = new Composite( shell, SWT.NONE );
    Control button = new Button( otherParent, SWT.PUSH );
    try {
      ControlHolder.addControl( shell, button );
      fail( "Control has wrong parent." );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
  }

  @Test
  public void testAddAtIndex() {
    Button button1 = new Button( shell, SWT.PUSH );
    Button button2 = new Button( shell, SWT.PUSH );
    Button button3 = new Button( shell, SWT.PUSH );
    controlHolder.add( button1 );
    controlHolder.add( button2, 0 );
    controlHolder.add( button3, 2 );

    assertEquals( 1, controlHolder.indexOf( button1 ) );
    assertEquals( 0, controlHolder.indexOf( button2 ) );
    assertEquals( 2, controlHolder.indexOf( button3 ) );
  }

}
