/*******************************************************************************
 * Copyright (c) 2011 Rüdiger Herrmann and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Rüdiger Herrmann - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.IToolTipAdapter;


public class ToolTip_Test extends TestCase {
  
  private Display display;
  private Shell shell;
  
  public void testConstructorWithNullParent() {
    try {
      new ToolTip( null, SWT.NONE ) ;
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testInitialValue() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE ) ;
    assertTrue( toolTip.getAutoHide() );
    assertFalse( toolTip.isVisible() );
    assertEquals( "", toolTip.getText() );
    assertEquals( "", toolTip.getMessage() );
    Point location = getToolTipAdapter( toolTip ).getLocation();
    assertEquals( display.getCursorLocation(), location );
  }
  
  public void testGetParent() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE ) ;
    assertSame( shell, toolTip.getParent() );
  }
  
  public void testGetDisplay() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE ) ;
    assertSame( shell.getDisplay(), toolTip.getDisplay() );
  }
  
  public void testAutoHide() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE ) ;
    toolTip.setAutoHide( false );
    assertFalse( toolTip.getAutoHide() );
  }
  
  public void testVisible() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE ) ;
    toolTip.setVisible( true );
    assertTrue( toolTip.isVisible() );
  }
  
  public void testText() {
    final String text = "text";
    ToolTip toolTip = new ToolTip( shell, SWT.NONE ) ;
    toolTip.setText( text );
    assertEquals( text, toolTip.getText() );
  }
  
  public void testSetTextWithNullArgument() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE ) ;
    try {
      toolTip.setText( null );
    } catch( Exception expected ) {
    }
  }
  
  public void testMessage() {
    final String message = "message";
    ToolTip toolTip = new ToolTip( shell, SWT.NONE ) ;
    toolTip.setMessage( message );
    assertEquals( message, toolTip.getMessage() );
  }
  
  public void testSetMessageWithNullArgument() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE ) ;
    try {
      toolTip.setMessage( null );
    } catch( Exception expected ) {
    }
  }
  
  public void testSetLocationXY() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE ) ;
    toolTip.setLocation( 1, 2 );
    IToolTipAdapter adapter = getToolTipAdapter( toolTip );
    Point location = adapter.getLocation();
    assertEquals( 1, location.x );
    assertEquals( 2, location.y );
  }

  public void testSetLocationPoint() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE ) ;
    Point location = new Point( 1, 2 );
    toolTip.setLocation( location );
    IToolTipAdapter adapter = getToolTipAdapter( toolTip );
    Point returnedLocation = adapter.getLocation();
    assertNotSame( location, returnedLocation );
    assertEquals( 1, returnedLocation.x );
    assertEquals( 2, returnedLocation.y );
  }
  
  public void testSetLocationPointWithNullArgument() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE ) ;
    try {
      toolTip.setLocation( null );
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testGetStyle() {
    ToolTip toolTip = new ToolTip( shell, SWT.BALLOON );
    assertTrue( ( toolTip.getStyle() & SWT.BALLOON ) != 0 );
    toolTip = new ToolTip( shell, SWT.ICON_ERROR );
    assertTrue( ( toolTip.getStyle() & SWT.ICON_ERROR ) != 0 );
    toolTip = new ToolTip( shell, SWT.ICON_INFORMATION );
    assertTrue( ( toolTip.getStyle() & SWT.ICON_INFORMATION ) != 0 );
    toolTip = new ToolTip( shell, SWT.ICON_WARNING );
    assertTrue( ( toolTip.getStyle() & SWT.ICON_WARNING ) != 0 );
  }
  
  public void testGetStyleWithOverlappingIconBits() {
    int style = SWT.ICON_ERROR | SWT.ICON_INFORMATION;
    ToolTip toolTip = new ToolTip( shell, style );
    assertTrue( ( toolTip.getStyle() & SWT.ICON_ERROR ) == 0 );
    assertTrue( ( toolTip.getStyle() & SWT.ICON_INFORMATION ) != 0 );
  }
  
  public void testAddSelectionListener() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE );
    SelectionAdapter selectionListener = new SelectionAdapter() { };
    toolTip.addSelectionListener( selectionListener );
    Object[] listeners = SelectionEvent.getListeners( toolTip );
    assertSame( selectionListener, listeners[ 0 ] );
  }

  public void testAddSelectionListenerWithNullArgument() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE );
    try {
      toolTip.addSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testRemoveSelectionListener() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE );
    SelectionAdapter selectionListener = new SelectionAdapter() { };
    toolTip.addSelectionListener( selectionListener );
    toolTip.removeSelectionListener( selectionListener );
    Object[] listeners = SelectionEvent.getListeners( toolTip );
    assertEquals( 0, listeners.length );
  }

  public void testRemoveSelectionListenerWithNullArgument() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE );
    try {
      toolTip.removeSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testDisposeParent() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE );
    shell.dispose();
    assertTrue( toolTip.isDisposed() );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display , SWT.NONE );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  private static IToolTipAdapter getToolTipAdapter( ToolTip toolTip ) {
    return ( IToolTipAdapter )toolTip.getAdapter( IToolTipAdapter.class );
  }
}
