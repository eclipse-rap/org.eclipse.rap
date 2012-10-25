/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;


public class ScrollBarLCAUtil_Test extends TestCase {

  private Display display;
  private Shell shell;
  private Table table;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display , SWT.NONE );
    table = new Table( shell, SWT.NONE );
    createTableItems( table, 3 );
    table.setSize( 100, 100 );
    Fixture.fakeNewRequest();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testRenderCreateScrollBars() {
    ScrollBarLCAUtil.renderInitialization( table );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table.getHorizontalBar() );
    assertEquals( "rwt.widgets.ScrollBar", operation.getType() );
    assertTrue( Arrays.asList( operation.getStyles() ).contains( "HORIZONTAL" ) );
    assertEquals( getId( table ), operation.getParent() );
    operation = message.findCreateOperation( table.getVerticalBar() );
    assertEquals( "rwt.widgets.ScrollBar", operation.getType() );
    assertTrue( Arrays.asList( operation.getStyles() ).contains( "VERTICAL" ) );
    assertEquals( getId( table ), operation.getParent() );
  }

  public void testRenderInitialVisibility() {
    ScrollBarLCAUtil.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table.getHorizontalBar(), "visibility" ) );
    assertNull( message.findSetOperation( table.getVerticalBar(), "visibility" ) );
  }

  public void testRenderVisibility_Horizontal() {
    table.setSize( 50, 100 );

    ScrollBarLCAUtil.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( table.getHorizontalBar(), "visibility" ) );
    assertNull( message.findSetOperation( table.getVerticalBar(), "visibility" ) );
  }

  public void testRenderVisibility_Vertical() {
    table.setSize( 100, 50 );

    ScrollBarLCAUtil.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table.getHorizontalBar(), "visibility" ) );
    assertEquals( Boolean.TRUE, message.findSetProperty( table.getVerticalBar(), "visibility" ) );
  }

  public void testRenderVisibilityUnchanged() {
    ScrollBarLCAUtil.markInitialized( table );
    table.setSize( 50, 50 );

    ScrollBarLCAUtil.preserveValues( table );
    ScrollBarLCAUtil.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table.getHorizontalBar(), "visibility" ) );
    assertNull( message.findSetOperation( table.getVerticalBar(), "visibility" ) );
  }

  public void testRenderAddSelectionListener_Horizontal() {
    ScrollBar hScroll = table.getHorizontalBar();

    hScroll.addListener( SWT.Selection, mock( Listener.class ) );
    ScrollBarLCAUtil.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( hScroll, "Selection" ) );
  }

  public void testRenderRemoveSelectionListener_Horizontal() {
    ScrollBarLCAUtil.markInitialized( table );
    ScrollBar hScroll = table.getHorizontalBar();
    Listener listener = mock( Listener.class );
    hScroll.addListener( SWT.Selection, listener );
    ScrollBarLCAUtil.preserveValues( table );

    hScroll.removeListener( SWT.Selection, listener );
    ScrollBarLCAUtil.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( hScroll, "Selection" ) );
  }

  public void testRenderAddSelectionListener_Vertical() {
    ScrollBar vScroll = table.getVerticalBar();

    vScroll.addListener( SWT.Selection, mock( Listener.class ) );
    ScrollBarLCAUtil.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( vScroll, "Selection" ) );
  }

  public void testRenderAddSelectionListener_VerticalWithVirtualParent() {
    table = new Table( shell, SWT.VIRTUAL );
    ScrollBar vScroll = table.getVerticalBar();

    ScrollBarLCAUtil.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( vScroll, "Selection" ) );
  }

  public void testRenderRemoveSelectionListener_Vertical() {
    ScrollBarLCAUtil.markInitialized( table );
    ScrollBar vScroll = table.getVerticalBar();
    Listener listener = mock( Listener.class );
    vScroll.addListener( SWT.Selection, listener );
    ScrollBarLCAUtil.preserveValues( table );

    vScroll.removeListener( SWT.Selection, listener );
    ScrollBarLCAUtil.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( vScroll, "Selection" ) );
  }

  public void testReadSelection_Horizontal() {
    ScrollBar hScroll = table.getHorizontalBar();

    Fixture.fakeSetParameter( getId( hScroll ), "selection", Integer.valueOf( 3 ) );
    Integer selection = ScrollBarLCAUtil.readSelection( hScroll );

    assertEquals( 3, selection.intValue() );
    assertEquals( 3, hScroll.getSelection() );
  }

  public void testReadSelection_Vertical() {
    ScrollBar vScroll = table.getVerticalBar();

    Fixture.fakeSetParameter( getId( vScroll ), "selection", Integer.valueOf( 3 ) );
    Integer selection = ScrollBarLCAUtil.readSelection( vScroll );

    assertEquals( 3, selection.intValue() );
    assertEquals( 3, vScroll.getSelection() );
  }

  public void testProcessSelectionEvent_Horizontal() {
    SelectionListener listener = mock( SelectionListener.class );
    table.getHorizontalBar().addSelectionListener( listener );

    Fixture.fakeNotifyOperation( getId( table.getHorizontalBar() ), "Selection", null );
    Fixture.readDataAndProcessAction( table );

    verify( listener, times( 1 ) ).widgetSelected( any( SelectionEvent.class ) );
  }

  public void testProcessSelectionEvent_Vertical() {
    SelectionListener listener = mock( SelectionListener.class );
    table.getVerticalBar().addSelectionListener( listener );

    Fixture.fakeNotifyOperation( getId( table.getVerticalBar() ), "Selection", null );
    Fixture.readDataAndProcessAction( table );

    verify( listener, times( 1 ) ).widgetSelected( any( SelectionEvent.class ) );
  }

  //////////////////
  // Helping methods

  private static void createTableItems( Table table, int count ) {
    for( int i = 0; i < count; i++ ) {
      TableItem item = new TableItem( table, SWT.NONE );
      item.setText( "item text: " + i );
    }
  }


}
