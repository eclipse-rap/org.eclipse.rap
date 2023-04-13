/*******************************************************************************
 * Copyright (c) 2012, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.client;

import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serializeAndDeserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.BrowserNavigation;
import org.eclipse.rap.rwt.client.service.BrowserNavigationEvent;
import org.eclipse.rap.rwt.client.service.BrowserNavigationListener;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.internal.lifecycle.ProcessActionRunner;
import org.eclipse.rap.rwt.internal.protocol.Operation.CallOperation;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class BrowserNavigationImpl_Test {

  private static final String TYPE = "rwt.client.BrowserNavigation";

  private BrowserNavigationImpl navigation;

  @Before
  public void setUp() {
    Fixture.setUp();
    navigation = ( BrowserNavigationImpl )RWT.getClient().getService( BrowserNavigation.class );
    new Display();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCreateHistoryEntryWithEmptyId() {
    try {
      navigation.pushState( "", "name" );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testCreateHistoryEntryWithNullId() {
    try {
      navigation.pushState( null, "name" );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testAddBrowserNavigationListener_failsWithNull() {
    try {
      navigation.addBrowserNavigationListener( null );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testAddBrowserNavigationListener_addsListenerToList() {
    BrowserNavigationListener listener = mock( BrowserNavigationListener.class );

    navigation.addBrowserNavigationListener( listener );
    navigation.notifyListeners( mock( BrowserNavigationEvent.class ) );

    verify( listener ).navigated( any( BrowserNavigationEvent.class ) );
  }

  @Test
  public void testAddBrowserNavigationListener_doesNotAddListenerTwice() {
    BrowserNavigationListener listener = mock( BrowserNavigationListener.class );

    navigation.addBrowserNavigationListener( listener );
    navigation.addBrowserNavigationListener( listener );
    navigation.notifyListeners( mock( BrowserNavigationEvent.class ) );

    verify( listener, times( 1 ) ).navigated( any( BrowserNavigationEvent.class ) );
  }

  @Test
  public void testRemoveBrowserNavigationListener_failsWithNull() {
    try {
      navigation.removeBrowserNavigationListener( null );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testRemoveBrowserNavigationListener_removesListener() {
    BrowserNavigationListener listener = mock( BrowserNavigationListener.class );
    navigation.addBrowserNavigationListener( listener );

    navigation.removeBrowserNavigationListener( listener );
    navigation.notifyListeners( mock( BrowserNavigationEvent.class ) );

    verify( listener, times( 0 ) ).navigated( any( BrowserNavigationEvent.class ) );
  }

  @Test
  public void testFireNavigationEvent() {
    BrowserNavigationListener listener = mock( BrowserNavigationListener.class );
    navigation.addBrowserNavigationListener( listener );

    JsonObject parameters = new JsonObject().add( "state", "foo" );
    Fixture.fakeNotifyOperation( TYPE, "Navigation", parameters  );
    Fixture.executeLifeCycleFromServerThread();

    ArgumentCaptor<BrowserNavigationEvent> captor
      = ArgumentCaptor.forClass( BrowserNavigationEvent.class );
    verify( listener, times( 1 ) ).navigated( captor.capture() );
    BrowserNavigationEvent event = captor.getValue();
    assertEquals( "foo", event.getState() );
  }

  @Test
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

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( TYPE, "Navigation" ) );
  }

  @Test
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

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( TYPE, "Navigation" ) );
  }

  @Test
  public void testRenderNavigationListenerUnchanged() {
    navigation.addBrowserNavigationListener( new BrowserNavigationListener() {
      public void navigated( BrowserNavigationEvent event ) {
      }
    } );

    Fixture.executeLifeCycleFromServerThread();

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( TYPE, "navigation" ) );
  }

  @Test
  public void testRenderAddToHistory() {
    navigation.pushState( "testId", "testText" );

    Fixture.executeLifeCycleFromServerThread();

    TestMessage message = Fixture.getProtocolMessage();
    CallOperation operation = message.findCallOperation( TYPE, "addToHistory" );
    assertEquals( "testId", operation.getParameters().get( "state" ).asString() );
    assertEquals( "testText", operation.getParameters().get( "title" ).asString() );
  }

  @Test
  public void testRenderAddToHistory_NoEntries() {
    navigation.pushState( "testId", "testText" );

    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeNewRequest();
    Fixture.executeLifeCycleFromServerThread();

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findCallOperation( TYPE, "addToHistory" ) );
  }

  @Test
  public void testRenderAddToHistoryOrder() {
    navigation.pushState( "testId1", "testText1" );
    navigation.pushState( "testId2", "testText2" );

    Fixture.executeLifeCycleFromServerThread();

    TestMessage message = Fixture.getProtocolMessage();
    CallOperation callOperation1 = ( CallOperation )message.getOperation( 0 );
    CallOperation callOperation2 = ( CallOperation )message.getOperation( 1 );

    assertEquals( "addToHistory", callOperation1.getMethodName() );
    assertEquals( "testId1", callOperation1.getParameters().get( "state" ).asString() );
    assertEquals( "testText1", callOperation1.getParameters().get( "title" ).asString() );
    assertEquals( "addToHistory", callOperation2.getMethodName() );
    assertEquals( "testId2", callOperation2.getParameters().get( "state" ).asString() );
    assertEquals( "testText2", callOperation2.getParameters().get( "title" ).asString() );
  }

  @Test
  public void testIsSerializable() throws Exception {
    navigation.pushState( "testId1", "testText1" );

    serializeAndDeserialize( navigation );
  }

}
