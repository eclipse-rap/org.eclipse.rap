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
package org.eclipse.rap.rwt.internal.client;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.client.service.BrowserHistoryEvent;
import org.eclipse.rap.rwt.client.service.BrowserHistoryListener;
import org.eclipse.rap.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.eclipse.swt.widgets.Display;
import org.json.JSONArray;
import org.json.JSONException;
import org.mockito.ArgumentCaptor;


public class BrowserHistoryImpl_Test extends TestCase {

  private static final String TYPE = "rwt.client.BrowserHistory";

  private Display display;
  private BrowserHistoryImpl history;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    history = new BrowserHistoryImpl();
    Fixture.fakeNewRequest( display );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testCreateEntry() {
    history.createEntry( "id", "text" );

    assertEquals( 1, history.getEntries().length );
    assertEquals( "id", history.getEntries()[ 0 ].id );
    assertEquals( "text", history.getEntries()[ 0 ].text );
  }

  public void testCreateEntryWithNullText() {
    history.createEntry( "id", null );

    assertEquals( 1, history.getEntries().length );
    assertEquals( "id", history.getEntries()[ 0 ].id );
    assertNull( history.getEntries()[ 0 ].text );
  }

  public void testCreateEntryWithEmptyId() {
    try {
      history.createEntry( "", "name" );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testCreateEntryWithNullId() {
    try {
      history.createEntry( null, "name" );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testAddBrowserHistoryListener() {
    try {
      history.addBrowserHistoryListener( null );
      fail( "BrowserHistory#addBrowserHistoryListener must not allow null" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testRemoveBrowserHistoryListener() {
    try {
      history.removeBrowserHistoryListener( null );
      fail( "BrowserHistory#removeBrowserHistoryListener must not allow null" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testFireNavigationEvent() {
    BrowserHistoryListener listener = mock( BrowserHistoryListener.class );
    history.addBrowserHistoryListener( listener );

    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( "entryId", "foo" );
    Fixture.fakeNotifyOperation( TYPE, "Navigation", parameters  );
    Fixture.executeLifeCycleFromServerThread();

    ArgumentCaptor<BrowserHistoryEvent> captor
      = ArgumentCaptor.forClass( BrowserHistoryEvent.class );
    verify( listener, times( 1 ) ).navigated( captor.capture() );
    BrowserHistoryEvent event = captor.getValue();
    assertEquals( "foo", event.entryId );
  }

  public void testRenderAddNavigationListener() {
    Fixture.fakePhase( PhaseId.READ_DATA );
    ProcessActionRunner.add( new Runnable() {
      public void run() {
        history.addBrowserHistoryListener( new BrowserHistoryListener() {
          public void navigated( BrowserHistoryEvent event ) {
          }
        } );
      }
    } );

    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( TYPE, "Navigation" ) );
  }

  public void testRenderRemoveNavigationListener() {
    final BrowserHistoryListener listener = new BrowserHistoryListener() {
      public void navigated( BrowserHistoryEvent event ) {
      }
    };
    history.addBrowserHistoryListener( listener );
    Fixture.fakePhase( PhaseId.READ_DATA );
    ProcessActionRunner.add( new Runnable() {
      public void run() {
        history.removeBrowserHistoryListener( listener );
      }
    } );

    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( TYPE, "Navigation" ) );
  }

  public void testRenderNavigationListenerUnchanged() {
    history.addBrowserHistoryListener( new BrowserHistoryListener() {
      public void navigated( BrowserHistoryEvent event ) {
      }
    } );

    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( TYPE, "navigation" ) );
  }

  public void testRenderAdd() throws JSONException {
    history.createEntry( "testId", "testText" );

    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    CallOperation operation = message.findCallOperation( TYPE, "add" );
    JSONArray entries = ( JSONArray )operation.getProperty( "entries" );
    JSONArray actual1 = entries.getJSONArray( 0 );
    assertTrue( ProtocolTestUtil.jsonEquals( "[\"testId\",\"testText\"]", actual1 ) );
  }

  public void testRenderAdd_NoEntries() {
    history.createEntry( "testId", "testText" );

    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeNewRequest( display );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findCallOperation( TYPE, "add" ) );
  }

  public void testRenderAddOrder() throws JSONException {
    history.createEntry( "testId1", "testText1" );
    history.createEntry( "testId2", "testText2" );

    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    CallOperation operation = message.findCallOperation( TYPE, "add" );
    JSONArray entries = ( JSONArray )operation.getProperty( "entries" );
    JSONArray actual1 = entries.getJSONArray( 0 );
    assertTrue( ProtocolTestUtil.jsonEquals( "[\"testId1\",\"testText1\"]", actual1 ) );
    JSONArray actual2 = entries.getJSONArray( 1 );
    assertTrue( ProtocolTestUtil.jsonEquals( "[\"testId2\",\"testText2\"]", actual2 ) );
  }
}
