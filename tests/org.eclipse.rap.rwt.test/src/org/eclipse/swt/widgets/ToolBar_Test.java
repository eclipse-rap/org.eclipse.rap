/*******************************************************************************
 * Copyright (c) 2002, 2021 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.widgets;
import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serializeAndDeserialize;
import static org.eclipse.rap.rwt.testfixture.internal.TestUtil.createImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.toolbarkit.ToolBarLCA;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class ToolBar_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;
  private Shell shell;
  private ToolBar toolBar;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display );
    toolBar = new ToolBar( shell, SWT.NONE );
  }

  @Test
  public void testStyle() {
    toolBar = new ToolBar( shell, SWT.NONE );

    assertTrue( ( toolBar.getStyle() & SWT.NO_FOCUS ) != 0 );

    toolBar = new ToolBar( shell, SWT.FLAT );

    assertTrue( ( toolBar.getStyle() & SWT.NO_FOCUS ) == 0 );
  }

  @Test
  public void testCreation() throws IOException {
    assertEquals( 0, toolBar.getItemCount() );
    assertEquals( 0, toolBar.getItems().length );
    ToolItem item0 = new ToolItem( toolBar, SWT.CHECK );
    assertEquals( 1, toolBar.getItemCount() );
    assertEquals( 1, toolBar.getItems().length );
    assertEquals( item0, toolBar.getItem( 0 ) );
    assertEquals( item0, toolBar.getItems()[ 0 ] );
    try {
      toolBar.getItem( 4 );
      fail( "Index out of bounds" );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
    assertSame( display, item0.getDisplay() );
    item0.dispose();
    assertEquals( 0, toolBar.getItemCount() );
    assertEquals( 0, toolBar.getItems().length );
    item0 = new ToolItem( toolBar, SWT.CHECK );
    assertEquals( 1, toolBar.getItemCount() );

    // search operation indexOf
    ToolItem item1 = new ToolItem( toolBar, SWT.PUSH );
    ToolItem item2 = new ToolItem( toolBar, SWT.RADIO );
    Image image = createImage( display, Fixture.IMAGE1 );
    item2.setImage( image );
    assertSame( image, item2.getImage() );
    assertEquals( 3, toolBar.getItemCount() );
    assertEquals( 3, toolBar.getItems().length );
    assertEquals( 1, toolBar.indexOf( item1 ) );
    assertEquals( item1, toolBar.getItem( 1 ) );
    assertEquals( item2, toolBar.getItem( 2 ) );
    ToolItem item3 = new ToolItem( toolBar, SWT.SEPARATOR );
    item3.setImage( createImage( display, Fixture.IMAGE2 ) );
    assertNull( item3.getImage() );
  }

  @Test
  public void testHorizontal() {
    ToolBar toolBar = new ToolBar( shell, SWT.HORIZONTAL );
    ToolItem toolItem1 = new ToolItem( toolBar, SWT.PUSH );
    ToolItem toolItem2 = new ToolItem( toolBar, SWT.PUSH );
    ToolItem toolItem3 = new ToolItem( toolBar, SWT.PUSH );
    toolBar.pack();
    assertEquals( toolItem1.getBounds().y, toolItem2.getBounds().y );
    assertEquals( toolItem1.getBounds().y, toolItem3.getBounds().y );
    assertEquals( toolItem1.getBounds().height, toolItem2.getBounds().height );
    assertEquals( toolItem1.getBounds().height, toolItem3.getBounds().height );
    int offsetItem2 = toolItem1.getBounds().x + toolItem1.getBounds().width;
    assertTrue( offsetItem2 <= toolItem2.getBounds().x );
    int offsetItem3 = toolItem2.getBounds().x + toolItem2.getBounds().width;
    assertTrue( offsetItem3 <= toolItem3.getBounds().x );
    int minBarWidth = toolItem3.getBounds().x + toolItem3.getBounds().width;
    assertTrue( toolBar.getBounds().width >=  minBarWidth );
  }

  @Test
  public void testVertical() {
    ToolBar toolBar = new ToolBar( shell, SWT.VERTICAL );
    ToolItem toolItem1 = new ToolItem( toolBar, SWT.PUSH );
    ToolItem toolItem2 = new ToolItem( toolBar, SWT.PUSH );
    ToolItem toolItem3 = new ToolItem( toolBar, SWT.PUSH );
    ToolItem toolItem4 = new ToolItem( toolBar, SWT.SEPARATOR );
    toolBar.pack();
    // Separators are NOT respected when synchronizing width:
    toolItem4.setWidth( toolItem1.getWidth() + 100 );
    // TODO [tb] : this is not SWT-behaviour
//    assertTrue( toolItem1.getWidth() < toolItem4.getWidth() );
    assertEquals( toolItem1.getBounds().width, toolItem2.getBounds().width );
    assertEquals( toolItem1.getBounds().width, toolItem3.getBounds().width );
    assertEquals( toolItem1.getBounds().height, toolItem2.getBounds().height );
    assertEquals( toolItem1.getBounds().height, toolItem3.getBounds().height );
    assertEquals( toolItem1.getBounds().x, toolItem2.getBounds().x );
    assertEquals( toolItem1.getBounds().x, toolItem3.getBounds().x );
    int offsetItem2 = toolItem1.getBounds().y + toolItem1.getBounds().height;
    assertTrue( offsetItem2 <= toolItem2.getBounds().y );
    int offsetItem3 = toolItem2.getBounds().y + toolItem2.getBounds().height;
    assertTrue( offsetItem3 <= toolItem3.getBounds().y );
    int minBarHeight = toolItem3.getBounds().y + toolItem3.getBounds().height;
    assertTrue( toolBar.getBounds().height >=  minBarHeight );
  }

  @Test
  public void testDispose() {
    assertEquals( 0, toolBar.getItemCount() );
    assertEquals( 0, toolBar.getItems().length );
    ToolItem item0 = new ToolItem( toolBar, SWT.CHECK );
    ToolItem item1 = new ToolItem( toolBar, SWT.PUSH );
    assertEquals( 2, toolBar.getItems().length );
    toolBar.dispose();
    assertTrue( toolBar.isDisposed() );
    assertTrue( item0.isDisposed() );
    assertTrue( item1.isDisposed() );
  }

  @Test
  public void testDisposeWithFontDisposeInDisposeListener() {
    new ToolItem( toolBar, SWT.NONE );
    new ToolItem( toolBar, SWT.NONE );
    final Font font = new Font( display, "font-name", 10, SWT.NORMAL );
    toolBar.setFont( font );
    toolBar.addDisposeListener( new DisposeListener() {
      @Override
      public void widgetDisposed( DisposeEvent event ) {
        font.dispose();
      }
    } );
    toolBar.dispose();
  }

  @Test
  public void testIndexOf() {
    ToolItem item = new ToolItem( toolBar, SWT.NONE );
    assertEquals( 0, toolBar.indexOf( item ) );

    item.dispose();
    try {
      toolBar.indexOf( item );
      fail( "indexOf must not answer for a disposed item" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      toolBar.indexOf( null );
      fail( "indexOf must not answer for null item" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testComputeSize() {
    assertEquals( new Point( 24, 22 ), toolBar.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    ToolItem toolItem1 = new ToolItem( toolBar, SWT.PUSH );
    toolItem1.setText( "Item 1" );
    new ToolItem( toolBar, SWT.SEPARATOR );
    new ToolItem( toolBar, SWT.CHECK );
    ToolItem toolItem2 = new ToolItem( toolBar, SWT.PUSH );
    toolItem2.setText( "Item 2" );
    ToolItem separator = new ToolItem( toolBar, SWT.SEPARATOR );
    separator.setControl( new Text( toolBar, SWT.NONE ) );
    ToolItem toolItem3 = new ToolItem( toolBar, SWT.DROP_DOWN );
    toolItem3.setText( "Item 3" );
    assertEquals( new Point( 208, 30 ), toolBar.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    assertEquals( new Point( 100, 100 ), toolBar.computeSize( 100, 100 ) );
  }

  @Test
  public void testIsSerializable() throws Exception {
    new ToolItem( toolBar, SWT.PUSH );

    ToolBar deserializedToolBar = serializeAndDeserialize( toolBar );

    assertEquals( 1, deserializedToolBar.getItemCount() );
  }

  @Test
  public void testGetAdapter_LCA() {
    assertTrue( toolBar.getAdapter( WidgetLCA.class ) instanceof ToolBarLCA );
    assertSame( toolBar.getAdapter( WidgetLCA.class ), toolBar.getAdapter( WidgetLCA.class ) );
  }

}
