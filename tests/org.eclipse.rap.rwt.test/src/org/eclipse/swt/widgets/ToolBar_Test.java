/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.widgets;
import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;


public class ToolBar_Test extends TestCase {

  private Display display;
  private Shell shell;

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testCreation() {
    ToolBar toolBar = new ToolBar( shell, SWT.VERTICAL );
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
    } catch( final IllegalArgumentException iae ) {
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
    item2.setImage(Graphics.getImage( Fixture.IMAGE1 ) );
    assertSame( Graphics.getImage( Fixture.IMAGE1 ), item2.getImage() );
    assertEquals( 3, toolBar.getItemCount() );
    assertEquals( 3, toolBar.getItems().length );
    assertEquals( 1, toolBar.indexOf( item1 ) );
    assertEquals( item1, toolBar.getItem( 1 ) );
    assertEquals( item2, toolBar.getItem( 2 ) );
    ToolItem item3 = new ToolItem( toolBar, SWT.SEPARATOR );
    item3.setImage( Graphics.getImage( Fixture.IMAGE2 ) );
    assertNull( item3.getImage() );
  }

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

  public void testDispose() {
    ToolBar toolBar = new ToolBar( shell, SWT.VERTICAL );
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

  public void testDisposeWithFontDisposeInDisposeListener() {
    ToolBar folder = new ToolBar( shell, SWT.NONE );
    new ToolItem( folder, SWT.NONE );
    new ToolItem( folder, SWT.NONE );
    final Font font = new Font( display, "font-name", 10, SWT.NORMAL );
    folder.setFont( font );
    folder.addDisposeListener( new DisposeListener() {
      public void widgetDisposed( DisposeEvent event ) {
        font.dispose();
      }
    } );
    folder.dispose();
  }

  public void testIndexOf() {
    ToolBar bar = new ToolBar( shell, SWT.NONE );
    ToolItem item = new ToolItem( bar, SWT.NONE );
    assertEquals( 0, bar.indexOf( item ) );

    item.dispose();
    try {
      bar.indexOf( item );
      fail( "indexOf must not answer for a disposed item" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      bar.indexOf( null );
      fail( "indexOf must not answer for null item" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testComputeSize() {
    ToolBar toolbar = new ToolBar( shell, SWT.NONE );
    assertEquals( new Point( 24, 22 ), toolbar.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    ToolItem toolItem1 = new ToolItem( toolbar, SWT.PUSH );
    toolItem1.setText( "Item 1" );
    new ToolItem( toolbar, SWT.SEPARATOR );
    new ToolItem( toolbar, SWT.CHECK );
    ToolItem toolItem2 = new ToolItem( toolbar, SWT.PUSH );
    toolItem2.setText( "Item 2" );
    ToolItem separator = new ToolItem( toolbar, SWT.SEPARATOR );
    separator.setControl( new Text( toolbar, SWT.NONE ) );
    ToolItem toolItem3 = new ToolItem( toolbar, SWT.DROP_DOWN );
    toolItem3.setText( "Item 3" );
    assertEquals( new Point( 153, 22 ), toolbar.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    assertEquals( new Point( 100, 100 ), toolbar.computeSize( 100, 100 ) );
  }
} 
