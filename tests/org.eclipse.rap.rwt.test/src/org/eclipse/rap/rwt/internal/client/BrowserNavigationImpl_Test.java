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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.client.service.BrowserNavigationEvent;
import org.eclipse.rap.rwt.client.service.BrowserNavigationListener;
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


public class BrowserNavigationImpl_Test extends TestCase {

  private static final String TYPE = "rwt.client.BrowserNavigation";

  private Display display;
  private BrowserNavigationImpl navigation;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    navigation = new BrowserNavigationImpl();
    Fixture.fakeNewRequest( display );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testCreateHistoryEntry() {
    navigation.pushState( "state", "title" );

    assertEquals( 1, navigation.getEntries().length );
    assertEquals( "state", navigation.getEntries()[ 0 ].state );
    assertEquals( "title", navigation.getEntries()[ 0 ].title );
  }

  public void testCreateHistoryEntryWithNullText() {
    navigation.pushState( "state", null );

    assertEquals( 1, navigation.getEntries().length );
    assertEquals( "state", navigation.getEntries()[ 0 ].state );
    assertNull( navigation.getEntries()[ 0 ].title );
  }

  public void testCreateHistoryEntryWithEmptyId() {
    try {
      navigation.pushState( "", "name" );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testCreateHistoryEntryWithNullId() {
    try {
      navigation.pushState( null, "name" );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testAddBrowserNavigationListener_failsWithNull() {
    try {
      navigation.addBrowserNavigationListener( null );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testAddBrowserNavigationListener_addsListenerToList() {
    BrowserNavigationListener listener = mock( BrowserNavigationListener.class );

    navigation.addBrowserNavigationListener( listener );
    navigation.notifyListeners( mock( BrowserNavigationEvent.class ) );

    verify( listener ).navigated( any( BrowserNavigationEvent.class ) );
  }

  public void testAddBrowserNavigationListener_doesNotAddListenerTwice() {
    BrowserNavigationListener listener = mock( BrowserNavigationListener.class );

    navigation.addBrowserNavigationListener( listener );
    navigation.addBrowserNavigationListener( listener );
    navigation.notifyListeners( mock( BrowserNavigationEvent.class ) );

    verify( listener, times( 1 ) ).navigated( any( BrowserNavigationEvent.class ) );
  }

  public void testRemoveBrowserNavigationListener_failsWithNull() {
    try {
      navigation.removeBrowserNavigationListener( null );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testRemoveBrowserNavigationListener_removesListener() {
    BrowserNavigationListener listener = mock( BrowserNavigationListener.class );
    navigation.addBrowserNavigationListener( listener );

    navigation.removeBrowserNavigationListener( listener );
    navigation.notifyListeners( mock( BrowserNavigationEvent.class ) );

    verify( listener, times( 0 ) ).navigated( any( BrowserNavigationEvent.class ) );
  }

  public void testFireNavigationEvent() {
    BrowserNavigationListener listener = mock( BrowserNavigationListener.class );
    navigation.addBrowserNavigationListener( listener );

    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( "state", "foo" );
    Fixture.fakeNotifyOperation( TYPE, "Navigation", parameters  );
    Fixture.executeLifeCycleFromServerThread();

    ArgumentCaptor<BrowserNavigationEvent> captor
      = ArgumentCaptor.forClass( BrowserNavigationEvent.class );
    verify( listener, times( 1 ) ).navigated( captor.capture() );
    BrowserNavigationEvent event = captor.getValue();
    assertEquals( "foo", event.getState() );
  }

  public void testRenderAddNavigationListener() {
    Fixture.fakePhase( PhaseId.READ_DATA );
    ProcessActionRunner.add( new Runnable() {
      public void run() {
        navigation.addBrowserNavigationListener( new BrowserNavigationListener() {
          public void navigated( BrowserNavigationEvent event ) {
          }
        } );
      }
    } );

    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( TYPE, "Navigation" ) );
  }

  public void testRenderRemoveNavigationListener() {
    final BrowserNavigationListener listener = new BrowserNavigationListener() {
      public void navigated( BrowserNavigationEvent event ) {
      }
    };
    navigation.addBrowserNavigationListener( listener );
    Fixture.fakePhase( PhaseId.READ_DATA );
    ProcessActionRunner.add( new Runnable() {
      public void run() {
        navigation.removeBrowserNavigationListener( listener );
      }
    } );

    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( TYPE, "Navigation" ) );
  }

  public void testRenderNavigationListenerUnchanged() {
    navigation.addBrowserNavigationListener( new BrowserNavigationListener() {
      public void navigated( BrowserNavigationEvent event ) {
      }
    } );

    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( TYPE, "navigation" ) );
  }

  public void testRenderAddToHistory() throws JSONException {
    navigation.pushState( "testId", "testText" );

    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    CallOperation operation = message.findCallOperation( TYPE, "addToHistory" );
    JSONArray entries = ( JSONArray )operation.getProperty( "entries" );
    JSONArray actual1 = entries.getJSONArray( 0 );
    assertTrue( ProtocolTestUtil.jsonEquals( "[\"testId\",\"testText\"]", actual1 ) );
  }

  public void testRenderAddToHistory_NoEntries() {
    navigation.pushState( "testId", "testText" );

    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeNewRequest( display );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findCallOperation( TYPE, "addToHistory" ) );
  }

  public void testRenderAddToHistoryOrder() throws JSONException {
    navigation.pushState( "testId1", "testText1" );
    navigation.pushState( "testId2", "testText2" );

    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    CallOperation operation = message.findCallOperation( TYPE, "addToHistory" );
    JSONArray entries = ( JSONArray )operation.getProperty( "entries" );
    JSONArray actual1 = entries.getJSONArray( 0 );
    assertTrue( ProtocolTestUtil.jsonEquals( "[\"testId1\",\"testText1\"]", actual1 ) );
    JSONArray actual2 = entries.getJSONArray( 1 );
    assertTrue( ProtocolTestUtil.jsonEquals( "[\"testId2\",\"testText2\"]", actual2 ) );
  }
}
