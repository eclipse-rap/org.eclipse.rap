/*******************************************************************************
 * Copyright (c) 2013, 2018 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.widgets;

import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
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
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class DropDown_Test {

  @Rule
  public TestContext context = new TestContext();

  private static final String[] FOUR_ITEMS = { "a", "b", "c", "d" };
  private Text text;
  private DropDown dropDown;
  private RemoteObject remoteObject;
  private Connection connection;
  private OperationHandler handler;

  @Before
  public void setUp() {
    Display display = new Display();
    Shell shell = new Shell( display );
    text = new Text( shell, SWT.NONE );
    remoteObject = mock( RemoteObject.class );
    connection = mock( Connection.class );
    when( connection.createRemoteObject( anyString() ) ).thenReturn( remoteObject );
    Fixture.fakeConnection( connection );
    doAnswer( new Answer<Object>(){
      @Override
      public Object answer( InvocationOnMock invocation ) throws Throwable {
        handler = ( OperationHandler )invocation.getArguments()[ 0 ];
        return null;
      }
    } ).when( remoteObject ).setHandler( any( OperationHandler.class ) );
    dropDown = new DropDown( text );
  }

  @Test
  public void testContructor_CreatesRemoteObjectWithCorrentType() {
    verify( connection ).createRemoteObject( "rwt.widgets.DropDown" );
  }

  @Test
  public void testStyles_hasVSrollByDefault() {
    assertEquals( SWT.V_SCROLL, dropDown.getStyle() & SWT.V_SCROLL );
  }

  @Test
  public void testStyles_addsHSroll() {
    dropDown = new DropDown( text, SWT.H_SCROLL );

    assertEquals( SWT.V_SCROLL, dropDown.getStyle() & SWT.V_SCROLL );
    assertEquals( SWT.H_SCROLL, dropDown.getStyle() & SWT.H_SCROLL );
  }

  @Test
  public void testContructor_rendersStyle() {
    reset( remoteObject );
    dropDown = new DropDown( text, SWT.H_SCROLL );

    JsonArray expected = createJsonArray( new String[]{ "V_SCROLL", "H_SCROLL" } );
    verify( remoteObject ).set( eq( "style" ), eq( expected ) );
  }

  @Test
  public void testContructor_SetsReferenceWidget() {
    verify( remoteObject ).set( "parent", WidgetUtil.getId( text ) );
  }

  @Test( expected = SWTException.class )
  public void testGetParent_ThrowsExceptionIfDisposed() {
    dropDown.dispose();
    dropDown.getParent();
  }

  @Test
  public void testGetParent_ReturnsParent() {
    assertSame( text, dropDown.getParent() );
  }

  @Test
  public void testDipose_RendersDetroy() {
    dropDown.dispose();
    verify( remoteObject ).destroy();
  }

  @Test
  public void testDipose_RemovesParentListener() {
    dropDown.dispose();

    assertFalse( text.isListening( SWT.Dispose ) );
  }

  @Test
  public void testDispose_FiresDispose() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Listener listener = mock( Listener.class );
    dropDown.addListener( SWT.Dispose, listener );

    dropDown.dispose();

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
    dropDown.dispose();
    dropDown.setVisible( true );
  }

  @Test
  public void testSetVisible_SetsVisibility() {
    dropDown.setVisible( true );

    assertTrue( dropDown.getVisible() );
  }

  @Test
  public void testSetVisible_RendersVisibleTrue() {
    dropDown.setVisible( true );
    verify( remoteObject ).set( "visible", true );
  }

  @Test
  public void testVisible_SetToTrueTwiceRenderVisibleOnce() {
    dropDown.setVisible( true );
    dropDown.setVisible( true );

    verify( remoteObject, times( 1 ) ).set( "visible", true );
  }

  @Test
  public void testHide_SetsVisible() {
    dropDown.setVisible( true );
    dropDown.setVisible( false );

    assertFalse( dropDown.getVisible() );
  }

  @Test
  public void testHide_RendersVisibleFalse() {
    dropDown.setVisible( true );

    dropDown.setVisible( false );

    verify( remoteObject ).set( "visible", false );
  }

  @Test( expected = SWTException.class )
  public void testGetVisible_ThrowsExceptionIfDisposed() {
    dropDown.dispose();
    dropDown.getVisible();
  }

  @Test
  public void testGetVisible_InitialValueIsFalse() {
    assertFalse( dropDown.getVisible() );
  }

  @Test( expected = SWTException.class )
  public void testGetSelectionIndex_ThrowsExceptionIfDisposed() {
    dropDown.dispose();
    dropDown.getSelectionIndex();
  }

  @Test( expected = SWTException.class )
  public void testSetSelectionIndex_ThrowsExceptionIfDisposed() {
    dropDown.dispose();
    dropDown.setSelectionIndex( 1 );
  }

  @Test
  public void testGetSelectionIndex_InitialValue() {
    assertEquals( -1, dropDown.getSelectionIndex() );
  }

  @Test
  public void testSetSelectionIndex_SetsSelection() {
    dropDown.setItems( FOUR_ITEMS );

    dropDown.setSelectionIndex( 2 );

    assertEquals( 2, dropDown.getSelectionIndex() );
  }

  @Test
  public void testSetSelectionIndex_RendersSelection() {
    dropDown.setItems( FOUR_ITEMS );

    dropDown.setSelectionIndex( 2 );

    verify( remoteObject ).set( "selectionIndex", 2 );
  }

  @Test
  public void testSetSelectionIndex_ResetsSelection() {
    dropDown.setItems( FOUR_ITEMS );
    dropDown.setSelectionIndex( 2 );

    dropDown.setSelectionIndex( -1 );

    assertEquals( -1, dropDown.getSelectionIndex() );
  }

  @Test
  public void testSetSelectionIndex_IgnoredForTooSmallValue() {
    dropDown.setItems( FOUR_ITEMS );
    dropDown.setSelectionIndex( 2 );

    dropDown.setSelectionIndex( -2 );

    assertEquals( 2, dropDown.getSelectionIndex() );
  }

  @Test
  public void testSetSelectionIndex_IgnoredForTooBigValue() {
    dropDown.setItems( FOUR_ITEMS );
    dropDown.setSelectionIndex( 2 );

    dropDown.setSelectionIndex( 4 );

    assertEquals( 2, dropDown.getSelectionIndex() );
  }

  @Test( expected = SWTException.class )
  public void testSetItems_ThrowsExceptionIfDisposed() {
    dropDown.dispose();
    dropDown.setItems( new String[]{ "a", "b", "c" } );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetItems_ThrowsExceptionForNullArgument() {
    dropDown.setItems( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetItems_ThrowsExceptionForNullItem() {
    dropDown.setItems( new String[]{ "a", null, "b" } );
  }

  @Test
  public void testGetItems_returnsItems() {
    String[] items = new String[]{ "a", "b", "c" };
    dropDown.setItems( items );

    assertTrue( Arrays.equals( items, dropDown.getItems() ) );
  }

  @Test
  public void testGetItems_returnsSaveCopy() {
    String[] items = new String[]{ "a", "b", "c" };
    dropDown.setItems( items );

    dropDown.getItems()[ 1 ] = "x";

    assertEquals( "b", dropDown.getItems()[ 1 ] );
  }

  @Test
  public void testSetItems_storesSaveCopy() {
    String[] items = new String[]{ "a", "b", "c" };
    dropDown.setItems( items );

    items[ 1 ] = "x";

    assertEquals( "b", dropDown.getItems()[ 1 ] );
  }

  @Test
  public void testSetItems_RenderItems() {
    dropDown.setItems( new String[]{ "a", "b", "c" } );

    JsonArray expected = JsonUtil.createJsonArray( new String[]{ "a", "b", "c" } );
    verify( remoteObject ).set( eq( "items" ), eq( expected ) );
  }

  @Test
  public void testSetItems_ResetsSelectionIndex() {
    handler.handleSet( new JsonObject().add( "selectionIndex", 7) );
    dropDown.setItems( new String[]{ "a" } );

    assertEquals( -1, dropDown.getSelectionIndex() );
  }

  @Test( expected = SWTException.class )
  public void testSetVisibleItemCount_ThrowsExceptionIfDisposed() {
    dropDown.dispose();
    dropDown.setVisibleItemCount( 7 );
  }

  @Test
  public void testSetVisibleItemCount_RendersVisibleItemCount() {
    dropDown.setVisibleItemCount( 7 );
    verify( remoteObject ).set( "visibleItemCount", 7 );
  }

  @Test
  public void testSetVisibleItemCount_DoesNotRenderVisibleItemCountIfUnchanged() {
    dropDown.setVisibleItemCount( 7 );
    dropDown.setVisibleItemCount( 7 );

    verify( remoteObject, times( 1 ) ).set( "visibleItemCount", 7 );
  }

  @Test( expected = SWTException.class )
  public void testGetVisibleItemCount_ThrowsExceptionIfDisposed() {
    dropDown.dispose();
    dropDown.getVisibleItemCount();
  }

  @Test
  public void testGetVisibleItemCount_ReturnInitialValue() {
    assertEquals( 5, dropDown.getVisibleItemCount() );
  }

  @Test
  public void testGetVisibleItemCount_ReturnUserValue() {
    dropDown.setVisibleItemCount( 23 );

    assertEquals( 23, dropDown.getVisibleItemCount() );
  }

  @Test( expected = SWTException.class )
  public void testSetData_ThrowsExceptionIfDiposed() {
    dropDown.dispose();
    dropDown.setData( "foo", "bar" );
  }

  @Test
  public void testSetData_RendersDataInWhiteList() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );

    WidgetUtil.registerDataKeys( "foo", "bar" );
    dropDown.setData( "foo", "bar" );

    verify( remoteObject ).set( eq( "data" ), eq( new JsonObject().add( "foo", "bar" ) ) );
  }

  @Test
  public void testSetData_RendersMarkupEnabled() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );

    dropDown.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );

    verify( remoteObject ).set( eq( "markupEnabled" ), eq( true ) );
  }

  @Test
  public void testSetData_RendersColumns() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );

    dropDown.setData( "columns", new int[]{ 10, 20 } );

    verify( remoteObject ).set( eq( "columns" ), eq( new JsonArray().add( 10 ).add( 20 ) ) );
  }

  @Test
  public void testSetData_RendersIncorrectTypeAsNull() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );

    dropDown.setData( "columns", Boolean.TRUE );

    verify( remoteObject ).set( eq( "columns" ), eq( JsonValue.NULL ) );
  }

  @Test
  public void testSetData_DoesNotRenderDataNotInWhiteList() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );

    WidgetUtil.registerDataKeys( "foo", "bar" );
    dropDown.setData( "fool", "bar" );

    verify( remoteObject, never() ).set( eq( "data" ), any( JsonObject.class ) );
  }

  @Test
  public void testAddListener_SelectionRenderListenTrue() {
    Listener listener = mock( Listener.class );

    dropDown.addListener( SWT.Selection, listener );

    verify( remoteObject ).listen( eq( "Selection" ), eq( true ) );
  }

  @Test
  public void testAddListener_DefaultSelectionRenderListenTrue() {
    Listener listener = mock( Listener.class );

    dropDown.addListener( SWT.DefaultSelection, listener );

    verify( remoteObject ).listen( eq( "DefaultSelection" ), eq( true ) );
  }

  @Test
  public void testAddListener_Selection_doesNotSendListenTwice() {
    dropDown.addListener( SWT.Selection, mock( Listener.class ) );
    dropDown.addListener( SWT.Selection, mock( Listener.class ) );

    verify( remoteObject, times( 1 ) ).listen( eq( "Selection" ), eq( true ) );
  }

  @Test
  public void testAddListener_Selection_doesNotSendListenForClientListener() {
    dropDown.addListener( SWT.Selection, new ClientListener( "foo" ) );

    verify( remoteObject, times( 0 ) ).listen( eq( "Selection" ), eq( true ) );
  }

  @Test
  public void testAddListener_rendersClientListeners() {
    ClientListener listener = new ClientListener( "" );
    String listenerId = ClientListenerUtil.getRemoteId( listener );

    dropDown.addListener( SWT.Show, listener );

    JsonObject expectedParameters
      = new JsonObject().add( "eventType", "Show" ).add( "listenerId", listenerId );
    verify( remoteObject ).call( eq( "addListener" ), eq( expectedParameters ) );
  }

  @Test
  public void testRemoveListener_SelectionRenderListenFalse() {
    Listener listener = mock( Listener.class );
    dropDown.addListener( SWT.Selection, listener );
    //Mockito.reset( remoteObject );
    dropDown.removeListener( SWT.Selection, listener );

    verify( remoteObject ).listen( eq( "Selection" ), eq( false ) );
  }

  @Test
  public void testRemoveListener_DefaultSelectionRenderListenFalse() {
    Listener listener = mock( Listener.class );
    dropDown.addListener( SWT.DefaultSelection, listener );
    dropDown.removeListener( SWT.DefaultSelection, listener );

    verify( remoteObject ).listen( eq( "DefaultSelection" ), eq( false ) );
  }

  @Test
  public void testRemoveListener_rendersClientListeners() {
    ClientListener listener = new ClientListener( "" );
    String listenerId = ClientListenerUtil.getRemoteId( listener );

    dropDown.removeListener( SWT.Show, listener );

    JsonObject expectedParameters
      = new JsonObject().add( "eventType", "Show" ).add( "listenerId", listenerId );
    verify( remoteObject ).call( eq( "removeListener" ), eq( expectedParameters ) );
  }

  @Test
  public void testProcessSetVisible_ValueIsTrue() {
    handler.handleSet( new JsonObject().add( "visible", true ) );

    assertTrue( dropDown.getVisible() );
  }

  @Test
  public void testProcessSetVisible_ValueIsFalse() {
    dropDown.setVisible( true );

    handler.handleSet( new JsonObject().add( "visible", false ) );

    assertFalse( dropDown.getVisible() );
  }

  @Test
  public void testProcessSetVisible_DoNotRenderToRemoteObject() {
    handler.handleSet( new JsonObject().add( "visible", true ) );

    verify( remoteObject, never() ).set( eq( "visible" ), anyBoolean() );
  }

  @Test
  public void testProcessSetSelectionIndex() {
    handler.handleSet( new JsonObject().add( "selectionIndex", 7) );

    assertEquals( 7, dropDown.getSelectionIndex() );
  }

  @Test
  public void testFireSelectionEvent() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final List<Event> log = new ArrayList<Event>();
    dropDown.setItems( FOUR_ITEMS );
    dropDown.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        log.add( event );
      }
    } );

    handler.handleNotify( "Selection", new JsonObject().add( "index", 2 ) );

    assertEquals( 1, log.size() );
    assertEquals( 2, log.get( 0 ).index );
    assertEquals( "c", log.get( 0 ).text );
  }

  @Test
  public void testFireDefaultSelectionEvent() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final List<Event> log = new ArrayList<Event>();
    dropDown.setItems( FOUR_ITEMS );
    dropDown.addListener( SWT.DefaultSelection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        log.add( event );
      }
    } );

    handler.handleNotify( "DefaultSelection", new JsonObject().add( "index", 2 ) );

    assertEquals( 1, log.size() );
    assertEquals( 2, log.get( 0 ).index );
    assertEquals( "c", log.get( 0 ).text );
  }

}
