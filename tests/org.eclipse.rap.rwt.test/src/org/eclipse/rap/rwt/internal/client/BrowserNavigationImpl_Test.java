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
  private BrowserNavigationImpl history;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    history = new BrowserNavigationImpl();
    Fixture.fakeNewRequest( display );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testCreateHistoryEntry() {
    history.createHistoryEntry( "id", "text" );

    assertEquals( 1, history.getEntries().length );
    assertEquals( "id", history.getEntries()[ 0 ].id );
    assertEquals( "text", history.getEntries()[ 0 ].text );
  }

  public void testCreateHistoryEntryWithNullText() {
    history.createHistoryEntry( "id", null );

    assertEquals( 1, history.getEntries().length );
    assertEquals( "id", history.getEntries()[ 0 ].id );
    assertNull( history.getEntries()[ 0 ].text );
  }

  public void testCreateHistoryEntryWithEmptyId() {
    try {
      history.createHistoryEntry( "", "name" );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testCreateHistoryEntryWithNullId() {
    try {
      history.createHistoryEntry( null, "name" );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testAddBrowserNavigationListener() {
    try {
      history.addBrowserNavigationListener( null );
      fail( "BrowserNavigation#addBrowserNavigationListener must not allow null" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testRemoveBrowserNavigationListener() {
    try {
      history.removeBrowserNavigationListener( null );
      fail( "BrowserNavigation#removeBrowserNavigationListener must not allow null" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testFireNavigationEvent() {
    BrowserNavigationListener listener = mock( BrowserNavigationListener.class );
    history.addBrowserNavigationListener( listener );

    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( "entryId", "foo" );
    Fixture.fakeNotifyOperation( TYPE, "Navigation", parameters  );
    Fixture.executeLifeCycleFromServerThread();

    ArgumentCaptor<BrowserNavigationEvent> captor
      = ArgumentCaptor.forClass( BrowserNavigationEvent.class );
    verify( listener, times( 1 ) ).navigated( captor.capture() );
    BrowserNavigationEvent event = captor.getValue();
    assertEquals( "foo", event.entryId );
  }

  public void testRenderAddNavigationListener() {
    Fixture.fakePhase( PhaseId.READ_DATA );
    ProcessActionRunner.add( new Runnable() {
      public void run() {
        history.addBrowserNavigationListener( new BrowserNavigationListener() {
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
    history.addBrowserNavigationListener( listener );
    Fixture.fakePhase( PhaseId.READ_DATA );
    ProcessActionRunner.add( new Runnable() {
      public void run() {
        history.removeBrowserNavigationListener( listener );
      }
    } );

    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( TYPE, "Navigation" ) );
  }

  public void testRenderNavigationListenerUnchanged() {
    history.addBrowserNavigationListener( new BrowserNavigationListener() {
      public void navigated( BrowserNavigationEvent event ) {
      }
    } );

    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( TYPE, "navigation" ) );
  }

  public void testRenderAddToHistory() throws JSONException {
    history.createHistoryEntry( "testId", "testText" );

    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    CallOperation operation = message.findCallOperation( TYPE, "addToHistory" );
    JSONArray entries = ( JSONArray )operation.getProperty( "entries" );
    JSONArray actual1 = entries.getJSONArray( 0 );
    assertTrue( ProtocolTestUtil.jsonEquals( "[\"testId\",\"testText\"]", actual1 ) );
  }

  public void testRenderAddToHistory_NoEntries() {
    history.createHistoryEntry( "testId", "testText" );

    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeNewRequest( display );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findCallOperation( TYPE, "addToHistory" ) );
  }

  public void testRenderAddToHistoryOrder() throws JSONException {
    history.createHistoryEntry( "testId1", "testText1" );
    history.createHistoryEntry( "testId2", "testText2" );

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
