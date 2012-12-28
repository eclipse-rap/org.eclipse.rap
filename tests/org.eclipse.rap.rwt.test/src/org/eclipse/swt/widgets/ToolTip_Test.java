/*******************************************************************************
 * Copyright (c) 2011, 2012 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.IToolTipAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ToolTip_Test {

  private Display display;
  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display , SWT.NONE );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testConstructorWithNullParent() {
    try {
      new ToolTip( null, SWT.NONE ) ;
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testInitialValue() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE ) ;
    assertTrue( toolTip.getAutoHide() );
    assertFalse( toolTip.isVisible() );
    assertEquals( "", toolTip.getText() );
    assertEquals( "", toolTip.getMessage() );
    Point location = getToolTipAdapter( toolTip ).getLocation();
    assertEquals( display.getCursorLocation(), location );
  }

  @Test
  public void testGetParent() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE ) ;
    assertSame( shell, toolTip.getParent() );
  }

  @Test
  public void testGetDisplay() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE ) ;
    assertSame( shell.getDisplay(), toolTip.getDisplay() );
  }

  @Test
  public void testAutoHide() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE ) ;
    toolTip.setAutoHide( false );
    assertFalse( toolTip.getAutoHide() );
  }

  @Test
  public void testVisible() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE ) ;
    toolTip.setVisible( true );
    assertTrue( toolTip.isVisible() );
  }

  @Test
  public void testText() {
    final String text = "text";
    ToolTip toolTip = new ToolTip( shell, SWT.NONE ) ;
    toolTip.setText( text );
    assertEquals( text, toolTip.getText() );
  }

  @Test
  public void testSetTextWithNullArgument() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE ) ;
    try {
      toolTip.setText( null );
    } catch( Exception expected ) {
    }
  }

  @Test
  public void testMessage() {
    final String message = "message";
    ToolTip toolTip = new ToolTip( shell, SWT.NONE ) ;
    toolTip.setMessage( message );
    assertEquals( message, toolTip.getMessage() );
  }

  @Test
  public void testSetMessageWithNullArgument() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE ) ;
    try {
      toolTip.setMessage( null );
    } catch( Exception expected ) {
    }
  }

  @Test
  public void testSetLocationXY() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE ) ;
    toolTip.setLocation( 1, 2 );
    IToolTipAdapter adapter = getToolTipAdapter( toolTip );
    Point location = adapter.getLocation();
    assertEquals( 1, location.x );
    assertEquals( 2, location.y );
  }

  @Test
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

  @Test
  public void testSetLocationPointWithNullArgument() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE ) ;
    try {
      toolTip.setLocation( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
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

  @Test
  public void testGetStyleWithOverlappingIconBits() {
    int style = SWT.ICON_ERROR | SWT.ICON_INFORMATION;
    ToolTip toolTip = new ToolTip( shell, style );
    assertTrue( ( toolTip.getStyle() & SWT.ICON_ERROR ) == 0 );
    assertTrue( ( toolTip.getStyle() & SWT.ICON_INFORMATION ) != 0 );
  }

  @Test
  public void testAddSelectionListener() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE );
    SelectionListener selectionListener = mock( SelectionListener.class );

    toolTip.addSelectionListener( selectionListener );

    assertTrue( toolTip.isListening( SWT.Selection ) );
    assertTrue( toolTip.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testAddSelectionListenerWithNullArgument() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE );
    try {
      toolTip.addSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testRemoveSelectionListener() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE );
    SelectionListener selectionListener = mock( SelectionListener.class );
    toolTip.addSelectionListener( selectionListener );

    toolTip.removeSelectionListener( selectionListener );

    assertFalse( toolTip.isListening( SWT.Selection ) );
    assertFalse( toolTip.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testRemoveSelectionListenerWithNullArgument() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE );
    try {
      toolTip.removeSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testDisposeParent() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE );
    shell.dispose();
    assertTrue( toolTip.isDisposed() );
  }

  @Test
  public void testIsSerializable() throws Exception {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE );
    toolTip.setMessage( "message" );

    ToolTip deserializedToolTip = Fixture.serializeAndDeserialize( toolTip );

    assertEquals( toolTip.getMessage(), deserializedToolTip.getMessage() );
  }

  private static IToolTipAdapter getToolTipAdapter( ToolTip toolTip ) {
    return toolTip.getAdapter( IToolTipAdapter.class );
  }

}
