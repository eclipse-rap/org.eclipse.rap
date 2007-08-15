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

import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;

public class Control_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testBounds() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Control control = new Button( shell, SWT.PUSH );
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
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
    control.setBounds( 0, 0, -1, -1 );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), control.getBounds() );
  }

  public void testLocation() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Control control = new Button( shell, SWT.PUSH );
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
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
  }

  public void testSize() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Control control = new Button( shell, SWT.PUSH );
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
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
    control.setSize( -2, -2 );
    assertEquals( new Point( 0, 0 ), control.getSize() );
  }

  public void testGetShell() {
    Display display = new Display();
    Composite shell1 = new Shell( display, SWT.NONE );
    Button button1 = new Button( shell1, SWT.PUSH );
    Composite shell2 = new Shell( display, SWT.NONE );
    Button button2 = new Button( shell2, SWT.PUSH );
    assertSame( shell1, shell1.getShell() );
    assertSame( shell1, button1.getShell() );
    assertSame( shell2, shell2.getShell() );
    assertSame( shell2, button2.getShell() );
    assertNotSame( shell2, shell1.getShell() );
    assertNotSame( shell2, button1.getShell() );
  }

  public void testToolTipText() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Control control1 = new Button( shell, SWT.PUSH );
    control1.setToolTipText( null );
    assertEquals( null, control1.getToolTipText() );
    control1.setToolTipText( "" );
    assertEquals( "", control1.getToolTipText() );
    control1.setToolTipText( "xyz" );
    assertEquals( "xyz", control1.getToolTipText() );
    Control control2 = new Button( shell, SWT.PUSH );
    assertEquals( null, control2.getToolTipText() );
  }

  public void testMenu() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Control control = new Button( shell, SWT.PUSH );
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
    Shell anotherShell = new Shell( display , SWT.NONE );
    menu = new Menu( anotherShell, SWT.POP_UP );
    try {
      control.setMenu( menu );
      fail( "Must not allow to set menu from different shell" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    menu = new Menu( shell, SWT.BAR );
    try {
      control.setMenu( menu );
      fail( "Menu to be set must have style SWT.POP_UP" );
    } catch( RuntimeException e ) {
      // expected
    }
    menu = new Menu( shell, SWT.POP_UP );
    control.setMenu( menu );
    menu.dispose();
    assertEquals( null, control.getMenu() );
  }

  public void testDisposeWithMenu() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Control control = new Button( shell, SWT.PUSH );
    Menu menu = new Menu( control );
    control.setMenu( menu );
    control.dispose();
    assertEquals( true, menu.isDisposed() );
    Control controlA = new Button( shell, SWT.PUSH );
    Control controlB = new Button( shell, SWT.PUSH );
    menu = new Menu( shell, SWT.POP_UP );
    controlA.setMenu( menu );
    controlB.setMenu( menu );
    controlA.dispose();
    assertEquals( true, menu.isDisposed() );
  }
  
  public void testFont() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Composite composite = new Composite( shell, SWT.NONE );
    Control control = new Button( shell, SWT.PUSH );

    // Initially the system font is set
    assertSame( display.getSystemFont(), control.getFont() );
    
    // Setting a parents' font must not affect the childrens font
    Font compositeFont = Font.getFont( "Composite Font", 12, SWT.NORMAL );
    composite.setFont( compositeFont );
    assertNotSame( control.getFont(), compositeFont );
    
    // (re)setting a font to null assigns the system font to the control 
    Label label = new Label( composite, SWT.NONE );
    Font labelFont = Font.getFont( "label font", 14, SWT.BOLD );
    label.setFont( labelFont );
    assertSame( labelFont, label.getFont() );
    label.setFont( null );
    assertSame( display.getSystemFont(), label.getFont() );
  }
  
  public void testEnabled() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Composite composite = new Composite( shell, SWT.NONE );
    Control control = new Button( composite, SWT.PUSH );

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
    Shell shell = new Shell( display, SWT.NONE );
    Composite composite = new Composite( shell, SWT.NONE );
    Control control = new Button( composite, SWT.PUSH );
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
    Composite shell = new Shell( display, SWT.NONE );
    Control control1 = new Button( shell, SWT.PUSH );
    Control control2 = new Button( shell, SWT.PUSH );
    Control control3 = new Button( shell, SWT.PUSH );
    assertEquals( 0, ControlHolder.indexOf( shell, control1 ) );
    assertEquals( 1, ControlHolder.indexOf( shell, control2 ) );
    assertEquals( 2, ControlHolder.indexOf( shell, control3 ) );
    control3.moveAbove( control2 );
    assertEquals( 0, ControlHolder.indexOf( shell, control1 ) );
    assertEquals( 1, ControlHolder.indexOf( shell, control3 ) );
    assertEquals( 2, ControlHolder.indexOf( shell, control2 ) );
    control1.moveBelow( control3 );
    assertEquals( 0, ControlHolder.indexOf( shell, control3 ) );
    assertEquals( 1, ControlHolder.indexOf( shell, control1 ) );
    assertEquals( 2, ControlHolder.indexOf( shell, control2 ) );
    control2.moveAbove( null );
    assertEquals( 0, ControlHolder.indexOf( shell, control2 ) );
    assertEquals( 1, ControlHolder.indexOf( shell, control3 ) );
    assertEquals( 2, ControlHolder.indexOf( shell, control1 ) );
    control2.moveBelow( null );
    assertEquals( 0, ControlHolder.indexOf( shell, control3 ) );
    assertEquals( 1, ControlHolder.indexOf( shell, control1 ) );
    assertEquals( 2, ControlHolder.indexOf( shell, control2 ) );
    // control is already at the top / bottom
    control3.moveAbove( null );
    assertEquals( 0, ControlHolder.indexOf( shell, control3 ) );
    control2.moveBelow( null );
    assertEquals( 0, ControlHolder.indexOf( shell, control3 ) );
    // try to move control above / below itself
    control1.moveAbove( control1 );
    assertEquals( 1, ControlHolder.indexOf( shell, control1 ) );
    control1.moveBelow( control1 );
    assertEquals( 1, ControlHolder.indexOf( shell, control1 ) );
    shell.dispose();
  }
  
  public void testFocus() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Control control1 = new Button( shell, SWT.PUSH );
    // When shell is closed, creating a control does not affect its focus
    assertSame( null, display.getFocusControl() );
    // When no focus was set, the first control is forceFocus'ed
    shell.open();
    assertSame( control1, display.getFocusControl() );
    assertTrue( control1.isFocusControl() );
  }
  
  public void testFocusOnClosedShell() {
    Display display = new Display();
    final Shell shell = new Shell( display, SWT.NONE );
    Control control1 = new Button( shell, SWT.PUSH );
    final Control control2 = new Button( shell, SWT.PUSH );
    final StringBuffer log = new StringBuffer();
    FocusListener focusListener = new FocusListener() {
      public void focusGained( final FocusEvent event ) {
        if( event.getSource() == shell ) {
          log.append( "shell.focusGained|" );
        } else if( event.getSource() == control2 ) {
          log.append( "control2.focusGained|" );
        } else {
          fail( "Unexpected event: focusGained" );
        }
      }
      public void focusLost( final FocusEvent event ) {
        if( event.getSource() == shell ) {
          log.append( "shell.focusLost|" );
        } else if( event.getSource() == control2 ) {
          log.append( "control2.focusLost|" );
        } else {
          fail( "Unexpected event: focusLost" );
        }
      }
    };
    shell.addFocusListener( focusListener );
    control2.addFocusListener( focusListener );
    // focus control on closed shell, returns false 
    boolean result = control2.forceFocus();
    assertFalse( result );
    assertNotSame( control2, display.getFocusControl() );
    // ...but will set the focus once the shell is opened
    shell.open();
    assertSame( control2, display.getFocusControl() );
    assertFalse( control1.isFocusControl() );
    assertTrue( control2.isFocusControl() );
    assertEquals( "shell.focusGained|shell.focusLost|control2.focusGained|", 
                  log.toString() );
  }
  
  public void testNoFocusControls() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Control control = new Button( shell, SWT.NONE );
    control.forceFocus();
    Control noFocusControl = new Label( shell, SWT.NONE );
    shell.open();
    // calling setFocus on controls with NO_FOCUS bit has no effect
    boolean result = noFocusControl.setFocus();
    assertFalse( result );
    assertFalse( noFocusControl.isFocusControl() );
    // ... but calling forceFocus marks even the NO_FOCUS control as focused 
    result = noFocusControl.forceFocus();
    assertTrue( result );
    assertTrue( noFocusControl.isFocusControl() );
  }
  
  public void testDisposeOfFocused() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Control control1 = new Button( shell, SWT.PUSH );
    Composite composite = new Composite( shell, SWT.NONE );
    Control control2 = new Button( composite, SWT.PUSH );
    Control control3 = new Button( composite, SWT.PUSH );
    shell.open();
    // Disposing of a control focuses its parent if the parent itself was not
    // disposed of
    control1.dispose();
    assertSame( shell, display.getFocusControl() );
    control3.setFocus();
    control3.dispose();
    assertSame( composite, display.getFocusControl() );
    control2.setFocus();
    composite.dispose();
    assertSame( shell, display.getFocusControl() );
    shell.dispose();
    assertSame( null, display.getFocusControl() );
  }
  
  public void testFocusEventsForForceFocus() {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Control control1 = new Button( shell, SWT.PUSH );
    control1.addFocusListener( new FocusAdapter() {
      public void focusGained( final FocusEvent event ) {
        assertSame( control1, event.getSource() );
        log.append( "focusGained" );
      }
      public void focusLost( final FocusEvent event ) {
        log.append( "focusLost" );
      }
    } );
    shell.open();
    // Changing focus programmatically must throw event 
    control1.forceFocus();
    assertEquals( "focusGained", log.toString() );
    // Focusing the same control again must not cause an event. 
    log.setLength( 0 );
    control1.forceFocus();
    assertEquals( "", log.toString() );
  }
}
