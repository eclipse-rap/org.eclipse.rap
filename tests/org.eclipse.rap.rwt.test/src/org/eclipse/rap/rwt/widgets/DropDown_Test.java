/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.internal.protocol.JsonUtil;
import org.eclipse.rap.rwt.internal.scripting.ClientListenerUtil;
import org.eclipse.rap.rwt.remote.Connection;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.scripting.ClientListener;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class DropDown_Test {

  private static final String[] FOUR_ITEMS = new String[] {
    "a",
    "b",
    "c",
    "d"
  };
  private Text text;
  private DropDown dropdown;
  private RemoteObject remoteObject;
  private Connection connection;
  private OperationHandler handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    Display display = new Display();
    Shell shell = new Shell( display );
    text = new Text( shell, SWT.NONE );
    Fixture.fakeNewRequest();
    remoteObject = mock( RemoteObject.class );
    connection = mock( Connection.class );
    when( connection.createRemoteObject( anyString() ) ).thenReturn( remoteObject );
    Fixture.fakeConnection( connection );
    doAnswer( new Answer<Object>(){
      public Object answer( InvocationOnMock invocation ) throws Throwable {
        handler = ( OperationHandler )invocation.getArguments()[ 0 ];
        return null;
      }
    } ).when( remoteObject ).setHandler( any( OperationHandler.class ) );
    dropdown = new DropDown( text );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testContructor_CreatesRemoteObjectWithCorrentType() {
    verify( connection ).createRemoteObject( "rwt.widgets.DropDown" );
  }

  @Test
  public void testContructor_SetsReferenceWidget() {
    verify( remoteObject ).set( "parent", WidgetUtil.getId( text ) );
  }

  @Test( expected = SWTException.class )
  public void testGetParent_ThrowsExceptionIfDisposed() {
    dropdown.dispose();
    dropdown.getParent();
  }

  @Test
  public void testGetParent_ReturnsParent() {
    assertSame( text, dropdown.getParent() );
  }

  @Test
  public void testDipose_RendersDetroy() {
    dropdown.dispose();
    verify( remoteObject ).destroy();
  }

  @Test
  public void testDipose_RemovesParentListener() {
    dropdown.dispose();

    assertFalse( text.isListening( SWT.Dispose ) );
  }

  @Test
  public void testDispose_FiresDispose() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Listener listener = mock( Listener.class );
    dropdown.addListener( SWT.Dispose, listener );

    dropdown.dispose();

    verify( listener ).handleEvent( any( Event.class ) );
  }

  @Test
  public void testDipose_CalledOnControlDispose() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );

    text.dispose();

    verify( remoteObject ).destroy();
  }

  @Test( expected = SWTException.class )
  public void testSetVisible_ThrowsExceptionIfDisposed() {
    dropdown.dispose();
    dropdown.setVisible( true );
  }

  @Test
  public void testSetVisible_SetsVisibility() {
    dropdown.setVisible( true );

    assertTrue( dropdown.getVisible() );
  }

  @Test
  public void testSetVisible_RendersVisibleTrue() {
    dropdown.setVisible( true );
    verify( remoteObject ).set( "visible", true );
  }

  @Test
  public void testVisible_SetToTrueTwiceRenderVisibleOnce() {
    dropdown.setVisible( true );
    dropdown.setVisible( true );

    verify( remoteObject, times( 1 ) ).set( "visible", true );
  }

  @Test
  public void testHide_SetsVisible() {
    dropdown.setVisible( true );
    dropdown.setVisible( false );

    assertFalse( dropdown.getVisible() );
  }

  @Test
  public void testHide_RendersVisibleFalse() {
    dropdown.setVisible( true );

    dropdown.setVisible( false );

    verify( remoteObject ).set( "visible", false );
  }

  @Test( expected = SWTException.class )
  public void testGetVisible_ThrowsExceptionIfDisposed() {
    dropdown.dispose();
    dropdown.getVisible();
  }

  @Test
  public void testGetVisible_InitialValueIsFalse() {
    assertFalse( dropdown.getVisible() );
  }

  @Test( expected = SWTException.class )
  public void testGetSelectionIndex_ThrowsExceptionIfDisposed() {
    dropdown.dispose();
    dropdown.getSelectionIndex();
  }

  @Test( expected = SWTException.class )
  public void testSetSelectionIndex_ThrowsExceptionIfDisposed() {
    dropdown.dispose();
    dropdown.setSelectionIndex( 1 );
  }

  @Test
  public void testGetSelectionIndex_InitialValue() {
    assertEquals( -1, dropdown.getSelectionIndex() );
  }

  @Test
  public void testSetSelectionIndex_SetsSelection() {
    dropdown.setItems( FOUR_ITEMS );

    dropdown.setSelectionIndex( 2 );

    assertEquals( 2, dropdown.getSelectionIndex() );
  }

  @Test
  public void testSetSelectionIndex_RendersSelection() {
    dropdown.setItems( FOUR_ITEMS );

    dropdown.setSelectionIndex( 2 );

    verify( remoteObject ).set( "selectionIndex", 2 );
  }

  @Test
  public void testSetSelectionIndex_ResetsSelection() {
    dropdown.setItems( FOUR_ITEMS );
    dropdown.setSelectionIndex( 2 );

    dropdown.setSelectionIndex( -1 );

    assertEquals( -1, dropdown.getSelectionIndex() );
  }

  @Test
  public void testSetSelectionIndex_IgnoredForTooSmallValue() {
    dropdown.setItems( FOUR_ITEMS );
    dropdown.setSelectionIndex( 2 );

    dropdown.setSelectionIndex( -2 );

    assertEquals( 2, dropdown.getSelectionIndex() );
  }

  @Test
  public void testSetSelectionIndex_IgnoredForTooBigValue() {
    dropdown.setItems( FOUR_ITEMS );
    dropdown.setSelectionIndex( 2 );

    dropdown.setSelectionIndex( 4 );

    assertEquals( 2, dropdown.getSelectionIndex() );
  }

  @Test( expected = SWTException.class )
  public void testSetItems_ThrowsExceptionIfDisposed() {
    dropdown.dispose();
    dropdown.setItems( new String[]{ "a", "b", "c" } );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetItems_ThrowsExceptionForNullArgument() {
    dropdown.setItems( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetItems_ThrowsExceptionForNullItem() {
    dropdown.setItems( new String[]{ "a", null, "b" } );
  }

  @Test
  public void testGetItems_returnsItems() {
    String[] items = new String[]{ "a", "b", "c" };
    dropdown.setItems( items );

    assertTrue( Arrays.equals( items, dropdown.getItems() ) );
  }

  @Test
  public void testGetItems_returnsSaveCopy() {
    String[] items = new String[]{ "a", "b", "c" };
    dropdown.setItems( items );

    dropdown.getItems()[ 1 ] = "x";

    assertEquals( "b", dropdown.getItems()[ 1 ] );
  }

  @Test
  public void testSetItems_storesSaveCopy() {
    String[] items = new String[]{ "a", "b", "c" };
    dropdown.setItems( items );

    items[ 1 ] = "x";

    assertEquals( "b", dropdown.getItems()[ 1 ] );
  }

  @Test
  public void testSetItems_RenderItems() {
    dropdown.setItems( new String[]{ "a", "b", "c" } );

    JsonArray expected = JsonUtil.createJsonArray( new String[]{ "a", "b", "c" } );
    verify( remoteObject ).set( eq( "items" ), eq( expected ) );
  }

  @Test
  public void testSetItems_ResetsSelectionIndex() {
    handler.handleSet( new JsonObject().add( "selectionIndex", 7) );
    dropdown.setItems( new String[]{ "a" } );

    assertEquals( -1, dropdown.getSelectionIndex() );
  }

  @Test( expected = SWTException.class )
  public void testSetVisibleItemCount_ThrowsExceptionIfDisposed() {
    dropdown.dispose();
    dropdown.setVisibleItemCount( 7 );
  }

  @Test
  public void testSetVisibleItemCount_RendersVisibleItemCount() {
    dropdown.setVisibleItemCount( 7 );
    verify( remoteObject ).set( "visibleItemCount", 7 );
  }

  @Test
  public void testSetVisibleItemCount_DoesNotRenderVisibleItemCountIfUnchanged() {
    dropdown.setVisibleItemCount( 7 );
    dropdown.setVisibleItemCount( 7 );

    verify( remoteObject, times( 1 ) ).set( "visibleItemCount", 7 );
  }

  @Test( expected = SWTException.class )
  public void testGetVisibleItemCount_ThrowsExceptionIfDisposed() {
    dropdown.dispose();
    dropdown.getVisibleItemCount();
  }

  @Test
  public void testGetVisibleItemCount_ReturnInitialValue() {
    assertEquals( 5, dropdown.getVisibleItemCount() );
  }

  @Test
  public void testGetVisibleItemCount_ReturnUserValue() {
    dropdown.setVisibleItemCount( 23 );

    assertEquals( 23, dropdown.getVisibleItemCount() );
  }

  @Test( expected = SWTException.class )
  public void testSetData_ThrowsExceptionIfDiposed() {
    dropdown.dispose();
    dropdown.setData( "foo", "bar" );
  }

  @Test
  public void testSetData_RendersDataInWhiteList() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );

    WidgetUtil.registerDataKeys( "foo", "bar" );
    dropdown.setData( "foo", "bar" );

    verify( remoteObject ).set( eq( "data" ), eq( new JsonObject().add( "foo", "bar" ) ) );
  }

  @Test
  public void testSetData_RendersMarkupEnabled() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );

    dropdown.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );

    verify( remoteObject ).set( eq( "markupEnabled" ), eq( true ) );
  }

  @Test
  public void testSetData_RendersColumns() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );

    dropdown.setData( "columns", new int[]{ 10, 20 } );

    verify( remoteObject ).set( eq( "columns" ), eq( new JsonArray().add( 10 ).add( 20 ) ) );
  }

  @Test
  public void testSetData_RendersIncorrectTypeAsNull() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );

    dropdown.setData( "columns", Boolean.TRUE );

    verify( remoteObject ).set( eq( "columns" ), eq( JsonValue.NULL ) );
  }

  @Test
  public void testSetData_DoesNotRenderDataNotInWhiteList() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );

    WidgetUtil.registerDataKeys( "foo", "bar" );
    dropdown.setData( "fool", "bar" );

    verify( remoteObject, never() ).set( eq( "data" ), any( JsonObject.class ) );
  }

  @Test
  public void testAddListener_SelectionRenderListenTrue() {
    Listener listener = mock( Listener.class );

    dropdown.addListener( SWT.Selection, listener );

    verify( remoteObject ).listen( eq( "Selection" ), eq( true ) );
  }

  @Test
  public void testAddListener_DefaultSelectionRenderListenTrue() {
    Listener listener = mock( Listener.class );

    dropdown.addListener( SWT.DefaultSelection, listener );

    verify( remoteObject ).listen( eq( "DefaultSelection" ), eq( true ) );
  }

  @Test
  public void testAddListener_Selection_doesNotSendListenTwice() {
    dropdown.addListener( SWT.Selection, mock( Listener.class ) );
    dropdown.addListener( SWT.Selection, mock( Listener.class ) );

    verify( remoteObject, times( 1 ) ).listen( eq( "Selection" ), eq( true ) );
  }

  @Test
  public void testAddListener_Selection_doesNotSendListenForClientListener() {
    dropdown.addListener( SWT.Selection, new ClientListener( "foo" ) );

    verify( remoteObject, times( 0 ) ).listen( eq( "Selection" ), eq( true ) );
  }

  @Test
  public void testAddListener_rendersClientListeners() {
    ClientListener listener = new ClientListener( "" );
    String listenerId = ClientListenerUtil.getRemoteId( listener );

    dropdown.addListener( SWT.Show, listener );

    JsonObject expectedParameters
      = new JsonObject().add( "eventType", "Show" ).add( "listenerId", listenerId );
    verify( remoteObject ).call( eq( "addListener" ), eq( expectedParameters ) );
  }

  @Test
  public void testRemoveListener_SelectionRenderListenFalse() {
    Listener listener = mock( Listener.class );
    dropdown.addListener( SWT.Selection, listener );
    //Mockito.reset( remoteObject );
    dropdown.removeListener( SWT.Selection, listener );

    verify( remoteObject ).listen( eq( "Selection" ), eq( false ) );
  }

  @Test
  public void testRemoveListener_DefaultSelectionRenderListenFalse() {
    Listener listener = mock( Listener.class );
    dropdown.addListener( SWT.DefaultSelection, listener );
    dropdown.removeListener( SWT.DefaultSelection, listener );

    verify( remoteObject ).listen( eq( "DefaultSelection" ), eq( false ) );
  }

  @Test
  public void testRemoveListener_rendersClientListeners() {
    ClientListener listener = new ClientListener( "" );
    String listenerId = ClientListenerUtil.getRemoteId( listener );

    dropdown.removeListener( SWT.Show, listener );

    JsonObject expectedParameters
      = new JsonObject().add( "eventType", "Show" ).add( "listenerId", listenerId );
    verify( remoteObject ).call( eq( "removeListener" ), eq( expectedParameters ) );
  }

  @Test
  public void testProcessSetVisible_ValueIsTrue() {
    handler.handleSet( new JsonObject().add( "visible", true ) );

    assertTrue( dropdown.getVisible() );
  }

  @Test
  public void testProcessSetVisible_ValueIsFalse() {
    dropdown.setVisible( true );

    handler.handleSet( new JsonObject().add( "visible", false ) );

    assertFalse( dropdown.getVisible() );
  }

  @Test
  public void testProcessSetVisible_DoNotRenderToRemoteObject() {
    handler.handleSet( new JsonObject().add( "visible", true ) );

    verify( remoteObject, never() ).set( eq( "visible" ), anyBoolean() );
  }

  @Test
  public void testProcessSetSelectionIndex() {
    handler.handleSet( new JsonObject().add( "selectionIndex", 7) );

    assertEquals( 7, dropdown.getSelectionIndex() );
  }

  @Test
  public void testFireSelectionEvent() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final List<Event> log = new ArrayList<Event>();
    dropdown.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        log.add( event );
      }
    } );

    handler.handleNotify( "Selection", new JsonObject()
      .add( "index", 2 )
      .add( "text", "foo" )
    );

    assertEquals( 1, log.size() );
    assertEquals( 2, log.get( 0 ).index );
    assertEquals( "foo", log.get( 0 ).text );
  }

  @Test
  public void testFireDefaultSelectionEvent() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final List<Event> log = new ArrayList<Event>();
    dropdown.addListener( SWT.DefaultSelection, new Listener() {
      public void handleEvent( Event event ) {
        log.add( event );
      }
    } );

    handler.handleNotify( "DefaultSelection", new JsonObject()
      .add( "index", 2 )
      .add( "text", "foo" )
    );

    assertEquals( 1, log.size() );
    assertEquals( 2, log.get( 0 ).index );
    assertEquals( "foo", log.get( 0 ).text );
  }

}
