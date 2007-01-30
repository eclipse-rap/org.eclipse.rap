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
import org.eclipse.rap.rwt.graphics.*;
import com.w4t.Fixture;
import com.w4t.engine.lifecycle.PhaseId;

public class Control_Test extends TestCase {

  protected void setUp() throws Exception {
    Fixture.setUp();
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testBounds() {
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    Control control = new Button( shell, RWT.PUSH );
    Rectangle controlBounds = control.getBounds();
    Rectangle expected = new Rectangle( 0, 0, 0, 0 );
    assertEquals( expected, controlBounds );
    Rectangle newBounds = new Rectangle( 10, 20, 30, 40 );
    control.setBounds( newBounds );
    controlBounds = control.getBounds();
    expected = new Rectangle( 10, 20, 30, 40 );
    assertEquals( expected, controlBounds );
    assertNotSame( newBounds, controlBounds );
    assertNotSame( controlBounds, control.getBounds() );
    newBounds.x = 100;
    controlBounds = control.getBounds();
    expected = new Rectangle( 10, 20, 30, 40 );
    assertEquals( expected, controlBounds );
    control.setBounds( 5, 6, 7, 8 );
    controlBounds = control.getBounds();
    expected = new Rectangle( 5, 6, 7, 8 );
    assertEquals( expected, controlBounds );
    control.setLocation( 11, 12 );
    control.setSize( 13, 14 );
    controlBounds = control.getBounds();
    expected = new Rectangle( 11, 12, 13, 14 );
    assertEquals( expected, controlBounds );
    try {
      control.setBounds( null );
      fail( "Rectangle parameter must not be null." );
    } catch( final NullPointerException npe ) {
      // expected
    }
    control.setBounds( 0, 0, -1, -1 );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), control.getBounds() );
  }

  public void testLocation() {
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    Control control = new Button( shell, RWT.PUSH );
    Point expectedLocation = new Point( 10, 20 );
    control.setLocation( expectedLocation );
    Rectangle controlBounds = control.getBounds();
    Rectangle expectedBounds = new Rectangle( 10, 20, 0, 0 );
    assertEquals( expectedBounds, controlBounds );
    Point controlLocation = control.getLocation();
    assertEquals( expectedLocation, controlLocation );
    assertNotSame( expectedLocation, controlLocation );
    expectedLocation.x = 15;
    controlLocation = control.getLocation();
    assertEquals( new Point( 10, 20 ), controlLocation );
    try {
      control.setLocation( null );
      fail( "Point parameter must not be null." );
    } catch( final NullPointerException npe ) {
      // expected
    }
  }

  public void testSize() {
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    Control control = new Button( shell, RWT.PUSH );
    Point expectedSize = new Point( 10, 20 );
    control.setSize( expectedSize );
    Rectangle controlBounds = control.getBounds();
    Rectangle expectedBounds = new Rectangle( 0, 0, 10, 20 );
    assertEquals( expectedBounds, controlBounds );
    Point controlSize = control.getSize();
    assertEquals( expectedSize, controlSize );
    assertNotSame( expectedSize, controlSize );
    expectedSize.x = 15;
    controlSize = control.getSize();
    assertEquals( new Point( 10, 20 ), controlSize );
    try {
      control.setSize( null );
      fail( "Point parameter must not be null." );
    } catch( final NullPointerException npe ) {
      // expected
    }
    control.setSize( -2, -2 );
    assertEquals( new Point( 0, 0 ), control.getSize() );
  }

  public void testGetShell() {
    Display display = new Display();
    Composite shell1 = new Shell( display , RWT.NONE );
    Button button1 = new Button( shell1, RWT.PUSH );
    Composite shell2 = new Shell( display , RWT.NONE );
    Button button2 = new Button( shell2, RWT.PUSH );
    assertSame( shell1, shell1.getShell() );
    assertSame( shell1, button1.getShell() );
    assertSame( shell2, shell2.getShell() );
    assertSame( shell2, button2.getShell() );
    assertNotSame( shell2, shell1.getShell() );
    assertNotSame( shell2, button1.getShell() );
  }

  public void testToolTipText() {
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    Control control1 = new Button( shell, RWT.PUSH );
    control1.setToolTipText( null );
    assertEquals( null, control1.getToolTipText() );
    control1.setToolTipText( "" );
    assertEquals( "", control1.getToolTipText() );
    control1.setToolTipText( "xyz" );
    assertEquals( "xyz", control1.getToolTipText() );
    Control control2 = new Button( shell, RWT.PUSH );
    assertEquals( null, control2.getToolTipText() );
  }

  public void testMenu() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Control control = new Button( shell, RWT.PUSH );
    Menu menu = new Menu( control );
    control.setMenu( menu );
    assertSame( menu, control.getMenu() );
    menu.dispose();
    try {
      control.setMenu( menu );
      fail( "Must not allow to set dipsosed of menu" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    Shell anotherShell = new Shell( display , RWT.NONE );
    menu = new Menu( anotherShell, RWT.POP_UP );
    try {
      control.setMenu( menu );
      fail( "Must not allow to set menu from different shell" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    menu = new Menu( shell, RWT.BAR );
    try {
      control.setMenu( menu );
      fail( "Menu to be set must have style RWT.POP_UP" );
    } catch( RuntimeException e ) {
      // expected
    }
    menu = new Menu( shell, RWT.POP_UP );
    control.setMenu( menu );
    menu.dispose();
    assertEquals( null, control.getMenu() );
  }

  public void testDisposeWithMenu() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Control control = new Button( shell, RWT.PUSH );
    Menu menu = new Menu( control );
    control.setMenu( menu );
    control.dispose();
    assertEquals( true, menu.isDisposed() );
    Control controlA = new Button( shell, RWT.PUSH );
    Control controlB = new Button( shell, RWT.PUSH );
    menu = new Menu( shell, RWT.POP_UP );
    controlA.setMenu( menu );
    controlB.setMenu( menu );
    controlA.dispose();
    assertEquals( true, menu.isDisposed() );
  }
  
  public void testFont() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Composite composite = new Composite( shell, RWT.NONE );
    Control control = new Button( shell, RWT.PUSH );

    // Initially the system font is set
    assertSame( display.getSystemFont(), control.getFont() );
    
    // Setting a parents' font must not affect the childrens font
    Font compositeFont = Font.getFont( "Composite Font", 12, RWT.NORMAL );
    composite.setFont( compositeFont );
    assertNotSame( control.getFont(), compositeFont );
    
    // (re)setting a font to null assigns the system font to the control 
    Label label = new Label( composite, RWT.NONE );
    Font labelFont = Font.getFont( "label font", 14, RWT.BOLD );
    label.setFont( labelFont );
    assertSame( labelFont, label.getFont() );
    label.setFont( null );
    assertSame( display.getSystemFont(), label.getFont() );
  }
  
  public void testEnabled() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Composite composite = new Composite( shell, RWT.NONE );
    Control control = new Button( composite, RWT.PUSH );

    // Must be enabled, initially
    assertEquals( true, control.getEnabled() );
    assertEquals( true, control.isEnabled() );
    
    // Must react on setEnabled() 
    control.setEnabled( false );
    assertEquals( false, control.getEnabled() );
    assertEquals( false, control.isEnabled() );
    
    // Test the difference between is- and getEnabled
    control.setEnabled( true );
    composite.setEnabled( false );
    assertEquals( false, composite.getEnabled() );
    assertEquals( false, composite.isEnabled() );
    assertEquals( true, control.getEnabled() );
    assertEquals( false, control.isEnabled() );
  }

  public void testVisible() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Composite composite = new Composite( shell, RWT.NONE );
    Control control = new Button( composite, RWT.PUSH );
    shell.open();
    
    // Must be enabled, initially
    assertEquals( true, control.getVisible() );
    assertEquals( true, control.isVisible() );
    
    // Must react on setEnabled() 
    control.setVisible( false );
    assertEquals( false, control.getVisible() );
    assertEquals( false, control.isVisible() );
    
    // Test the difference between is- and getEnabled
    control.setVisible( true );
    composite.setVisible( false );
    assertEquals( false, composite.getVisible() );
    assertEquals( false, composite.isVisible() );
    assertEquals( true, control.getVisible() );
    assertEquals( false, control.isVisible() );
  }
  
  public void testZOrder() {
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    Button b1 = new Button( shell, RWT.PUSH );
    Button b2 = new Button( shell, RWT.PUSH );
    Button b3 = new Button( shell, RWT.PUSH );
    assertEquals( 0, ControlHolder.indexOf( shell, b1 ) );
    assertEquals( 1, ControlHolder.indexOf( shell, b2 ) );
    assertEquals( 2, ControlHolder.indexOf( shell, b3 ) );
    b3.moveAbove( b2 );
    assertEquals( 0, ControlHolder.indexOf( shell, b1 ) );
    assertEquals( 1, ControlHolder.indexOf( shell, b3 ) );
    assertEquals( 2, ControlHolder.indexOf( shell, b2 ) );
    b1.moveBelow( b3 );
    assertEquals( 0, ControlHolder.indexOf( shell, b3 ) );
    assertEquals( 1, ControlHolder.indexOf( shell, b1 ) );
    assertEquals( 2, ControlHolder.indexOf( shell, b2 ) );
    b2.moveAbove( null );
    assertEquals( 0, ControlHolder.indexOf( shell, b2 ) );
    assertEquals( 1, ControlHolder.indexOf( shell, b3 ) );
    assertEquals( 2, ControlHolder.indexOf( shell, b1 ) );
    b2.moveBelow( null );
    assertEquals( 0, ControlHolder.indexOf( shell, b3 ) );
    assertEquals( 1, ControlHolder.indexOf( shell, b1 ) );
    assertEquals( 2, ControlHolder.indexOf( shell, b2 ) );
    // control is already at the top / bottom
    b3.moveAbove( null );
    assertEquals( 0, ControlHolder.indexOf( shell, b3 ) );
    b2.moveBelow( null );
    assertEquals( 0, ControlHolder.indexOf( shell, b3 ) );
    // try to move control above / below itself
    b1.moveAbove( b1 );
    assertEquals( 1, ControlHolder.indexOf( shell, b1 ) );
    b1.moveBelow( b1 );
    assertEquals( 1, ControlHolder.indexOf( shell, b1 ) );
    shell.dispose();
  }
}
