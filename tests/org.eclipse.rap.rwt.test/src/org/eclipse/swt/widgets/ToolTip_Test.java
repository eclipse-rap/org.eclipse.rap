/*******************************************************************************
 * Copyright (c) 2011, 2019 Rüdiger Herrmann and others.
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

import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serializeAndDeserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.IToolTipAdapter;
import org.eclipse.swt.internal.widgets.MarkupValidator;
import org.eclipse.swt.internal.widgets.tooltipkit.ToolTipLCA;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class ToolTip_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;
  private Shell shell;
  private ToolTip toolTip;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display , SWT.NONE );
    toolTip = new ToolTip( shell, SWT.NONE );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testConstructorWithNullParent() {
    new ToolTip( null, SWT.NONE ) ;
  }

  @Test
  public void testInitialValue() {
    assertTrue( toolTip.getAutoHide() );
    assertFalse( toolTip.isVisible() );
    assertEquals( "", toolTip.getText() );
    assertEquals( "", toolTip.getMessage() );
    Point location = getToolTipAdapter( toolTip ).getLocation();
    assertEquals( display.getCursorLocation(), location );
  }

  @Test
  public void testGetParent() {
    assertSame( shell, toolTip.getParent() );
  }

  @Test
  public void testGetDisplay() {
    assertSame( shell.getDisplay(), toolTip.getDisplay() );
  }

  @Test
  public void testAutoHide() {
    toolTip.setAutoHide( false );

    assertFalse( toolTip.getAutoHide() );
  }

  @Test
  public void testVisible() {
    toolTip.setVisible( true );

    assertTrue( toolTip.isVisible() );
  }

  @Test
  public void testText() {
    final String text = "text";

    toolTip.setText( text );

    assertEquals( text, toolTip.getText() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetTextWithNullArgument() {
    toolTip.setText( null );
  }

  @Test
  public void testMessage() {
    final String message = "message";

    toolTip.setMessage( message );

    assertEquals( message, toolTip.getMessage() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetMessageWithNullArgument() {
    toolTip.setMessage( null );
  }

  @Test
  public void testSetLocationXY() {
    toolTip.setLocation( 1, 2 );

    Point location = getToolTipAdapter( toolTip ).getLocation();
    assertEquals( 1, location.x );
    assertEquals( 2, location.y );
  }

  @Test
  public void testSetLocationPoint() {
    Point location = new Point( 1, 2 );

    toolTip.setLocation( location );

    Point returnedLocation = getToolTipAdapter( toolTip ).getLocation();
    assertNotSame( location, returnedLocation );
    assertEquals( 1, returnedLocation.x );
    assertEquals( 2, returnedLocation.y );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetLocationPointWithNullArgument() {
    toolTip.setLocation( null );
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
    SelectionListener selectionListener = mock( SelectionListener.class );

    toolTip.addSelectionListener( selectionListener );

    assertTrue( toolTip.isListening( SWT.Selection ) );
    assertTrue( toolTip.isListening( SWT.DefaultSelection ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddSelectionListenerWithNullArgument() {
    toolTip.addSelectionListener( null );
  }

  @Test
  public void testRemoveSelectionListener() {
    SelectionListener selectionListener = mock( SelectionListener.class );
    toolTip.addSelectionListener( selectionListener );

    toolTip.removeSelectionListener( selectionListener );

    assertFalse( toolTip.isListening( SWT.Selection ) );
    assertFalse( toolTip.isListening( SWT.DefaultSelection ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemoveSelectionListenerWithNullArgument() {
    toolTip.removeSelectionListener( null );
  }

  @Test
  public void testDisposeParent() {
    shell.dispose();

    assertTrue( toolTip.isDisposed() );
  }

  @Test
  public void testIsSerializable() throws Exception {
    toolTip.setMessage( "message" );

    ToolTip deserializedToolTip = serializeAndDeserialize( toolTip );

    assertEquals( toolTip.getMessage(), deserializedToolTip.getMessage() );
  }

  @Test
  public void testMarkupTextWithoutMarkupEnabled() {
    toolTip.setData( RWT.MARKUP_ENABLED, Boolean.FALSE );

    try {
      toolTip.setText( "invalid xhtml: <<&>>" );
    } catch( IllegalArgumentException notExpected ) {
      fail();
    }
  }

  @Test( expected = IllegalArgumentException.class )
  public void testMarkupTextWithMarkupEnabled() {
    toolTip.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );

    toolTip.setText( "invalid xhtml: <<&>>" );
  }

  @Test
  public void testMarkupTextWithMarkupEnabled_ValidationDisabled() {
    toolTip.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    toolTip.setData( MarkupValidator.MARKUP_VALIDATION_DISABLED, Boolean.TRUE );

    try {
      toolTip.setText( "invalid xhtml: <<&>>" );
    } catch( IllegalArgumentException notExpected ) {
      fail();
    }
  }

  @Test
  public void testSetMarkupEnabled_onDirtyWidget() {
    toolTip.setText( "something" );

    try {
      toolTip.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
      fail();
    } catch( SWTException expected ) {
      assertTrue( expected.throwable instanceof IllegalStateException );
    }
  }

  @Test
  public void testSetMarkupEnabled_onDirtyWidget_onceEnabledBefore() {
    toolTip.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    toolTip.setText( "something" );

    toolTip.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
  }

  @Test
  public void testDisableMarkupIsIgnored() {
    toolTip.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );

    toolTip.setData( RWT.MARKUP_ENABLED, Boolean.FALSE );

    assertEquals( Boolean.TRUE, toolTip.getData( RWT.MARKUP_ENABLED ) );
  }

  @Test
  public void testGetAdapter_LCA() {
    assertTrue( toolTip.getAdapter( WidgetLCA.class ) instanceof ToolTipLCA );
    assertSame( toolTip.getAdapter( WidgetLCA.class ), toolTip.getAdapter( WidgetLCA.class ) );
  }

  private static IToolTipAdapter getToolTipAdapter( ToolTip toolTip ) {
    return toolTip.getAdapter( IToolTipAdapter.class );
  }

}
