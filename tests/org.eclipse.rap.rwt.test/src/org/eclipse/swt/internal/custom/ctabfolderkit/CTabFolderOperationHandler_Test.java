/*******************************************************************************
 * Copyright (c) 2013, 2017 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.custom.ctabfolderkit;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_DEFAULT_SELECTION;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SELECTION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.custom.ICTabFolderAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class CTabFolderOperationHandler_Test {

  private Shell shell;
  private CTabFolder folder;
  private CTabFolderOperationHandler handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    shell = new Shell( display, SWT.NONE );
    folder = new CTabFolder( shell, SWT.MULTI );
    createCTabFolderItems( folder, 3 );
    handler = new CTabFolderOperationHandler( folder );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHandleSetMinimized() {
    handler.handleSet( new JsonObject().add( "minimized", JsonObject.TRUE ) );

    assertTrue( folder.getMinimized() );
  }

  @Test
  public void testHandleSetMaximized() {
    handler.handleSet( new JsonObject().add( "maximized", JsonObject.TRUE ) );

    assertTrue( folder.getMaximized() );
  }

  @Test
  public void testHandleSetSelection() {
    CTabItem item = folder.getItem( 1 );

    handler.handleSet( new JsonObject().add( "selection", getId( item ) ) );

    assertSame( item, folder.getSelection() );
  }

  @Test
  public void testHandleNotifySelection() {
    CTabItem item = folder.getItem( 1 );
    Listener listener = mock( Listener.class );
    folder.addListener( SWT.Selection, listener  );

    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "item", getId( item ) );
    handler.handleNotify( EVENT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( listener ).handleEvent( captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.ALT | SWT.SHIFT, event.stateMask );
    assertSame( item, event.item );
  }

  @Test
  public void testHandleNotifyDefaultSelection() {
    CTabItem item = folder.getItem( 1 );
    Listener listener = mock( Listener.class );
    folder.addListener( SWT.DefaultSelection, listener  );

    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "item", getId( item ) );
    handler.handleNotify( EVENT_DEFAULT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( listener ).handleEvent( captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.ALT | SWT.SHIFT, event.stateMask );
    assertSame( item, event.item );
  }

  @Test
  public void testHandleNotifyFolder_minimize() {
    CTabFolder2Listener listener = mock( CTabFolder2Listener.class );
    folder.addCTabFolder2Listener( listener );

    JsonObject properties = new JsonObject().add( "detail", "minimize" );
    handler.handleNotify( ClientMessageConst.EVENT_FOLDER, properties );

    verify( listener ).minimize( any( CTabFolderEvent.class ) );
  }

  @Test
  public void testHandleNotifyFolder_maximize() {
    CTabFolder2Listener listener = mock( CTabFolder2Listener.class );
    folder.addCTabFolder2Listener( listener );

    JsonObject properties = new JsonObject().add( "detail", "maximize" );
    handler.handleNotify( ClientMessageConst.EVENT_FOLDER, properties );

    verify( listener ).maximize( any( CTabFolderEvent.class ) );
  }

  @Test
  public void testHandleNotifyFolder_restore() {
    CTabFolder2Listener listener = mock( CTabFolder2Listener.class );
    folder.addCTabFolder2Listener( listener );

    JsonObject properties = new JsonObject().add( "detail", "restore" );
    handler.handleNotify( ClientMessageConst.EVENT_FOLDER, properties );

    verify( listener ).restore( any( CTabFolderEvent.class ) );
  }

  @Test
  public void testHandleNotifyFolder_closeWithoutVeto() {
    CTabItem item = folder.getItem( 1 );
    CTabFolder2Listener listener = mock( CTabFolder2Listener.class );
    folder.addCTabFolder2Listener( listener );

    JsonObject properties = new JsonObject().add( "detail", "close" ).add( "item", getId( item ) );
    handler.handleNotify( ClientMessageConst.EVENT_FOLDER, properties );

    verify( listener ).close( any( CTabFolderEvent.class ) );
    assertTrue( item.isDisposed() );
  }

  @Test
  public void testHandleNotifyFolder_closeWithVeto() {
    CTabItem item = folder.getItem( 1 );
    CTabFolder2Listener listener = spy( new CTabFolder2Adapter() {
      @Override
      public void close( CTabFolderEvent event ) {
        event.doit = false;
      }
    } );
    folder.addCTabFolder2Listener( listener );

    JsonObject properties = new JsonObject().add( "detail", "close" ).add( "item", getId( item ) );
    handler.handleNotify( ClientMessageConst.EVENT_FOLDER, properties );

    verify( listener ).close( any( CTabFolderEvent.class ) );
    assertFalse( item.isDisposed() );
  }

  @Test
  public void testHandleNotifyFolder_closeWithoutDisposedItem() {
    CTabItem item = folder.getItem( 1 );
    CTabFolder2Listener listener = mock( CTabFolder2Listener.class );
    folder.addCTabFolder2Listener( listener );

    item.dispose();
    JsonObject properties = new JsonObject().add( "detail", "close" ).add( "item", getId( item ) );
    handler.handleNotify( ClientMessageConst.EVENT_FOLDER, properties );

    verify( listener, never() ).close( any( CTabFolderEvent.class ) );
  }

  @Test
  public void testHandleNotifyFolder_showListWithoutVeto() {
    folder = new CTabFolder( shell, SWT.SINGLE );
    folder.setSize( 50, 130 );
    createCTabFolderItems( folder, 3 );
    handler = new CTabFolderOperationHandler( folder );
    CTabFolder2Listener listener = mock( CTabFolder2Listener.class );
    folder.addCTabFolder2Listener( listener );
    folder.setSelection( folder.getItem( 0 ) );

    JsonObject properties = new JsonObject().add( "detail", "showList" );
    handler.handleNotify( ClientMessageConst.EVENT_FOLDER, properties );

    ArgumentCaptor<CTabFolderEvent> captor = ArgumentCaptor.forClass( CTabFolderEvent.class );
    verify( listener ).showList( captor.capture() );
    assertEquals( getChevronRect( folder ), getEventRect( captor.getValue() ) );
    assertEquals( 2, getShowListMenu( folder ).getItemCount() );
  }

  @Test
  public void testHandleNotifyFolder_showListWithVeto() {
    folder = new CTabFolder( shell, SWT.SINGLE );
    folder.setSize( 50, 130 );
    createCTabFolderItems( folder, 3 );
    handler = new CTabFolderOperationHandler( folder );
    CTabFolder2Listener listener = spy( new CTabFolder2Adapter() {
      @Override
      public void showList( CTabFolderEvent event ) {
        event.doit = false;
      }
    } );
    folder.addCTabFolder2Listener( listener );
    folder.setSelection( folder.getItem( 0 ) );

    JsonObject properties = new JsonObject().add( "detail", "showList" );
    handler.handleNotify( ClientMessageConst.EVENT_FOLDER, properties );

    ArgumentCaptor<CTabFolderEvent> captor = ArgumentCaptor.forClass( CTabFolderEvent.class );
    verify( listener ).showList( captor.capture() );
    assertEquals( getChevronRect( folder ), getEventRect( captor.getValue() ) );
    assertNull( getShowListMenu( folder ) );
  }

  private Rectangle getChevronRect( CTabFolder folder ) {
    return folder.getAdapter( ICTabFolderAdapter.class ).getChevronRect();
  }

  private Rectangle getEventRect( CTabFolderEvent event ) {
    return new Rectangle( event.x, event.y, event.width, event.height );
  }

  private static Menu getShowListMenu( CTabFolder folder ) {
    Menu result = null;
    try {
      Field field = CTabFolder.class.getDeclaredField( "showMenu" );
      field.setAccessible( true );
      result = ( Menu )field.get( folder );
    } catch( Exception e ) {
      e.printStackTrace();
    }
    return result;
  }

  private static void createCTabFolderItems( CTabFolder folder, int number ) {
    for( int i = 0; i < number; i++ ) {
      CTabItem item = new CTabItem( folder, SWT.NONE );
      item.setText( "item " + i );
    }
  }

}
