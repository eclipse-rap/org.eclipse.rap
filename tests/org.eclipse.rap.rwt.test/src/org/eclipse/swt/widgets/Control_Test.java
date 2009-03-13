/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.widgets;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.widgets.IControlAdapter;
import org.eclipse.swt.layout.FillLayout;


public class Control_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
  
  public void testStyle() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Control control = new Button( shell, SWT.NONE );
    assertTrue( ( control.getStyle() & SWT.LEFT_TO_RIGHT ) != 0 );
    control = new Button( shell, SWT.BORDER );
    assertTrue( ( control.getStyle() & SWT.LEFT_TO_RIGHT ) != 0 );
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
    Control control = new Button( composite, SWT.PUSH );

    // Initially the system font is set
    assertSame( display.getSystemFont(), control.getFont() );

    // Setting a parents' font must not affect the childrens font
    Font compositeFont = Graphics.getFont( "Composite Font", 12, SWT.NORMAL );
    composite.setFont( compositeFont );
    assertNotSame( control.getFont(), compositeFont );

    // (re)setting a font to null assigns the system font to the control
    Label label = new Label( composite, SWT.NONE );
    Font labelFont = Graphics.getFont( "label font", 14, SWT.BOLD );
    label.setFont( labelFont );
    assertSame( labelFont, label.getFont() );
    label.setFont( null );
    assertSame( display.getSystemFont(), label.getFont() );
  }

  public void testForeground() throws Exception {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Composite comp = new Composite( shell, SWT.NONE );
    Control control = new Label( comp, SWT.PUSH );

    // Initially the system widget foreground color is set
    assertSame( display.getSystemColor( SWT.COLOR_WIDGET_FOREGROUND ),
                control.getForeground() );

    // Setting a parents' color must not affect the children's color
    comp.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
    assertNotSame( display.getSystemColor( SWT.COLOR_RED ),
                   control.getForeground() );

    // setting the foreground changes the state persistently
    control.setForeground( display.getSystemColor( SWT.COLOR_BLUE ) );
    assertSame( display.getSystemColor( SWT.COLOR_BLUE ),
                control.getForeground() );

    // (re)setting the foreground to null assigns the system color again
    control.setForeground( null );
    assertSame( display.getSystemColor( SWT.COLOR_WIDGET_FOREGROUND ),
                control.getForeground() );
  }

  public void testBackground() throws Exception {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Composite comp = new Composite( shell, SWT.NONE );
    Control control = new Label( comp, SWT.PUSH );

    // Initially the system widget background color is set
    assertSame( display.getSystemColor( SWT.COLOR_WIDGET_BACKGROUND ),
                control.getBackground() );

    // Setting a parents' color must not affect the children's color
    comp.setBackground( display.getSystemColor( SWT.COLOR_RED ) );
    assertNotSame( display.getSystemColor( SWT.COLOR_RED ),
                   control.getBackground() );

    // setting the background changes the state persistently
    control.setBackground( display.getSystemColor( SWT.COLOR_BLUE ) );
    assertSame( display.getSystemColor( SWT.COLOR_BLUE ),
                control.getBackground() );

    // (re)setting the background to null assigns the system color again
    control.setBackground( null );
    assertSame( display.getSystemColor( SWT.COLOR_WIDGET_BACKGROUND ),
                control.getBackground() );
  }

  public void testBackgroundMode() throws Exception {
    Display display = new Display();
    Color red = display.getSystemColor( SWT.COLOR_RED );
    Color blue = display.getSystemColor( SWT.COLOR_BLUE );
    Color widgetBg = display.getSystemColor( SWT.COLOR_WIDGET_BACKGROUND );
    Image image = Graphics.getImage( RWTFixture.IMAGE1 );
    Shell shell = new Shell( display );
    Composite comp = new Composite( shell, SWT.NONE );
    Control control = new Label( comp, SWT.NONE );

    // no inheritance by default
    comp.setBackground( red );
    assertEquals( widgetBg, control.getBackground() );

    // inherited background
    comp.setBackgroundMode( SWT.INHERIT_DEFAULT );
    assertEquals( red, control.getBackground() );

    // newly created control also inherits background mode
    Control control2 = new Label( comp, SWT.NONE );
    assertEquals( red, control2.getBackground() );

    // no inheritance when bg image is set
    control.setBackgroundImage( image );
    assertEquals( widgetBg, control.getBackground() );

    // inherited background again
    control.setBackgroundImage( null );
    assertEquals( red, control.getBackground() );

    // no inheritance when bg color is set
    control.setBackground( blue );
    assertEquals( blue, control.getBackground() );
  }

  public void testBackgroundModeMultiLevel() throws Exception {
    Display display = new Display();
    Color red = display.getSystemColor( SWT.COLOR_RED );
    Color blue = display.getSystemColor( SWT.COLOR_BLUE );
    Shell shell = new Shell( display );
    Composite comp = new Composite( shell, SWT.NONE );
    Label label = new Label( comp, SWT.NONE );

    // label inherits background from shell
    shell.setBackground( red );
    shell.setBackgroundMode( SWT.INHERIT_DEFAULT );
    comp.setBackgroundMode( SWT.INHERIT_DEFAULT );
    assertEquals( red, label.getBackground() );

    // label inherits background from comp
    comp.setBackground( blue );
    assertEquals( blue, label.getBackground() );
  }

  public void testBackgroundTransparency() throws Exception {
    Display display = new Display();
    Shell shell = new Shell( display );
    Composite comp = new Composite( shell, SWT.NONE );
    Color blue = display.getSystemColor( SWT.COLOR_BLUE );
    comp.setBackground( blue );
    Image image = Graphics.getImage( RWTFixture.IMAGE1 );
    comp.setBackgroundImage( image );
    Control control = new Label( comp, SWT.NONE );
    IControlAdapter adapter
      = ( IControlAdapter )control.getAdapter( IControlAdapter.class );

    // initial state
    assertNull( adapter.getUserBackground() );
    assertNull( adapter.getUserBackgroundImage() );
    assertFalse( adapter.getBackgroundTransparency() );

    // set background mode on parent enables transparency
    comp.setBackgroundMode( SWT.INHERIT_DEFAULT );
    assertSame( blue, control.getBackground() );
    assertSame( image, control.getBackgroundImage() );
    assertNull( adapter.getUserBackground() );
    assertNull( adapter.getUserBackgroundImage() );
    assertTrue( adapter.getBackgroundTransparency() );

    // controls created after set background mode are also transparent
    Control control2 = new Label( comp, SWT.NONE );
    IControlAdapter adapter2
      = ( IControlAdapter )control2.getAdapter( IControlAdapter.class );
    assertSame( blue, control.getBackground() );
    assertSame( image, control.getBackgroundImage() );
    assertNull( adapter2.getUserBackground() );
    assertNull( adapter2.getUserBackgroundImage() );
    assertTrue( adapter2.getBackgroundTransparency() );

    // set color on control overrides transparency
    Color red = display.getSystemColor( SWT.COLOR_RED );
    control.setBackground( red );
    assertSame( red, control.getBackground() );
    assertNull( control.getBackgroundImage() );
    assertEquals( red, adapter.getUserBackground() );
    assertFalse( adapter.getBackgroundTransparency() );
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
  
  // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=265634
  public void testNoFocusOutOnDispose() {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display );
    Control control = new Button( shell, SWT.PUSH );
    control.addFocusListener( new FocusAdapter() {
      public void focusLost( FocusEvent event ) {
        log.append( "focusout" );
      }
    } );
    shell.open();
    control.dispose();
    assertEquals( "", log.toString() );
  }
  
  public void testHideFocusedControl() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    shell.setLayout( new FillLayout() );
    Composite composite = new Composite( shell, SWT.NONE );
    Control control = new Button( composite, SWT.PUSH );
    shell.setSize( 100, 100 );
    shell.open();
    
    // Hide control -> its parent (composite) must take focus
    control.setFocus();
    assertTrue( control.isFocusControl() );
    control.setVisible( false );
    assertFalse( control.isFocusControl() );
    assertTrue( composite.isFocusControl() );
    assertSame( composite, display.getFocusControl() );
    
    // Indirectly hide control -> shell must take focus  
    control.setVisible( true );
    control.setFocus();
    composite.setVisible( false );
    assertFalse( control.isVisible() );
    assertFalse( control.isFocusControl() );
    assertFalse( composite.isFocusControl() );
    assertTrue( shell.isFocusControl() );
    assertSame( shell, display.getFocusControl() );

    // Indirectly hide control and leave no visible parent 
    // no control must have focus  
    control.setVisible( true );
    control.setFocus();
    shell.setVisible( false );
    assertFalse( shell.isVisible() );
    assertFalse( shell.isFocusControl() );
    assertFalse( composite.isFocusControl() );
    assertNull( display.getFocusControl() );
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

  public void testToControl() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Control control = new Button( shell, SWT.PUSH );
    Point controlCoords = control.toControl( 0, 0 );
    assertEquals( new Point( 0, 0 ), control.toDisplay( controlCoords.x,
                                                        controlCoords.y ) );
    controlCoords = control.toControl( new Point( 0, 0 ) );
    assertEquals( new Point( 0, 0 ), control.toDisplay( controlCoords ) );
    try {
      control.toControl( null );
      fail( "No exception thrown for null-argument" );
    } catch( IllegalArgumentException e ) {
    }
  }

  public void testToDisplay() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Control control = new Button( shell, SWT.PUSH );
    Point displayCoords = control.toDisplay( 0, 0 );
    assertEquals( new Point( 0, 0 ), control.toControl( displayCoords.x,
                                                        displayCoords.y ) );
    displayCoords = control.toDisplay( new Point( 0, 0 ) );
    assertEquals( new Point( 0, 0 ), control.toControl( displayCoords ) );
    try {
      control.toDisplay( null );
      fail( "No exception thrown for null-argument" );
    } catch( IllegalArgumentException e ) {
    }
  }
  
  /**
   * each Control has to inherit the orientation from its parent (or sets the
   * orientation to SWT.LEFT_TO_RIGHT
   */
  public void testOrientation() {
    Display display = new Display();
    Shell shellDefault = new Shell( display, SWT.NONE );
    Composite childDefault = new Composite( shellDefault, SWT.NONE );
    assertTrue( "default orientation: SWT.LEFT_TO_RIGHT",
                ( shellDefault.getStyle() & SWT.LEFT_TO_RIGHT ) != 0 );
    assertTrue( "default orientation inherited: SWT.LEFT_TO_RIGHT",
                ( childDefault.getStyle() & SWT.LEFT_TO_RIGHT ) != 0 );
  }
  
  public void testShowEvent() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    final java.util.List log = new ArrayList();
    Listener showListener = new Listener() {
      public void handleEvent( final Event event ) {
        log.add( event );
      }
    };
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.addListener( SWT.Show, showListener );
    Control control = new Button( shell, SWT.NONE );
    control.addListener( SWT.Show, showListener );
    // Separate test for shell (since it overides setVisible); Show event is
    // only sent for the shell itself, not for its containing controls
    shell.setVisible( true );
    assertEquals( 1, log.size() );
    Event event = ( Event )log.get( 0 );
    assertSame( shell, event.widget );
    // Calling setVisible(true) on an already visible control does not trigger
    // event
    log.clear();
    control.setVisible( true );
    assertEquals( 0, log.size() );
    // Making an invisible control visible, sends the Show event
    control.setVisible( false );
    log.clear();
    control.setVisible( true );
    assertEquals( 1, log.size() );
    assertSame( control, ( ( Event )log.get( 0 ) ).widget );
  }
  
  public void testShowEventDetails() {
    Listener ensureInvisible = new Listener() {
      public void handleEvent( final Event event ) {
        assertFalse( ( ( Control )event.widget ).getVisible() );
      }
    };
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.addListener( SWT.Show, ensureInvisible );
    shell.setVisible( true );
  }
  
  public void testHideEvent() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    final java.util.List log = new ArrayList();
    Listener showListener = new Listener() {
      public void handleEvent( final Event event ) {
        log.add( event );
      }
    };
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.addListener( SWT.Hide, showListener );
    Control control = new Button( shell, SWT.NONE );
    control.addListener( SWT.Hide, showListener );
    shell.open();

    log.clear();
    control.setVisible( false );
    assertEquals( 1, log.size() );
    assertSame( control, ( ( Event )log.get( 0 ) ).widget );
    
    log.clear();
    shell.setVisible( false );
    assertEquals( 1, log.size() );
    assertSame( shell, ( ( Event )log.get( 0 ) ).widget );
    
    log.clear();
    shell.setVisible( true );
    control.setVisible( true );
    shell.setVisible( false );
    assertEquals( 1, log.size() );
    assertSame( shell, ( ( Event )log.get( 0 ) ).widget );
  }

  public void testCursor() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Control control = new Button( shell, SWT.PUSH );
    assertNull( control.getCursor() );
    Cursor handCursor = Graphics.getCursor( SWT.CURSOR_HAND );
    control.setCursor( handCursor );
    assertEquals( handCursor, control.getCursor() );
    Cursor crossCursor = Graphics.getCursor( SWT.CURSOR_CROSS );
    control.setCursor( crossCursor );
    assertEquals( crossCursor, control.getCursor() );
    control.setCursor( null );
    assertNull( control.getCursor() );
  }
  
  public void testGetMonitor() throws Exception {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Control control = new Button( shell, SWT.PUSH );
    Monitor monitor = control.getMonitor();
    assertNotNull( monitor );
    assertEquals( display.getPrimaryMonitor(), monitor );
  }
  
}
