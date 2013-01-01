/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.browser;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.internal.events.EventTypes;
import org.eclipse.swt.internal.widgets.IBrowserAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;


public class Browser_Test {

  private Display display;
  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testInitialValues() {
    Browser browser = new Browser( shell, SWT.NONE );

    assertEquals( "", browser.getUrl() );
    assertEquals( "", getText( browser ) );
  }

  @Test
  public void testMozillaStyleFlags() {
    try {
      new Browser( shell, SWT.MOZILLA );
      fail( "SWT.MOZILLA not allowed" );
    } catch( SWTError error ) {
      assertEquals( SWT.ERROR_NO_HANDLES, error.code);
      assertEquals( "Unsupported Browser type", error.getMessage() );
    }
  }

  @Test
  public void testWebkitStyleFlag() {
    try {
      new Browser( shell, SWT.WEBKIT );
      fail( "SWT.WEBKIT not allowed" );
    } catch( SWTError error ) {
      assertEquals( SWT.ERROR_NO_HANDLES, error.code);
      assertEquals( "Unsupported Browser type", error.getMessage() );
    }
  }

  @Test
  public void testUrlAndText() {
    Browser browser = new Browser( shell, SWT.NONE );

    browser.setUrl( "http://eclipse.org/rap" );
    assertEquals( "", getText( browser ) );
    browser.setText( "<html></head>..." );
    assertEquals( "", browser.getUrl() );

    try {
      browser.setUrl( "oldValue" );
      browser.setUrl( null );
      fail( "Browser#setUrl: null not allowed" );
    } catch( IllegalArgumentException e ) {
      assertEquals( "oldValue", browser.getUrl() );
    }
    try {
      browser.setText( "oldValue" );
      browser.setText( null );
      fail( "Browser#setText: null not allowed" );
    } catch( IllegalArgumentException e ) {
      assertEquals( "oldValue", getText( browser ) );
    }
  }

  @Test
  public void testAddLocationListenerRegistersUntypedListeners() {
    Browser browser = new Browser( shell, SWT.NONE );

    browser.addLocationListener( mock( LocationListener.class ) );

    assertTrue( browser.isListening( EventTypes.LOCALTION_CHANGING ) );
    assertTrue( browser.isListening( EventTypes.LOCALTION_CHANGED ) );
  }

  @Test
  public void testRemoveLocationListenerRegistersUntypedListeners() {
    Browser browser = new Browser( shell, SWT.NONE );
    LocationListener locationListener = mock( LocationListener.class );
    browser.addLocationListener( locationListener );

    browser.removeLocationListener( locationListener );

    assertFalse( browser.isListening( EventTypes.LOCALTION_CHANGING ) );
    assertFalse( browser.isListening( EventTypes.LOCALTION_CHANGED ) );
  }

  @Test
  public void testAddProgressListenerRegistersUntypedListeners() {
    Browser browser = new Browser( shell, SWT.NONE );

    browser.addProgressListener( mock( ProgressListener.class ) );

    assertTrue( browser.isListening( EventTypes.PROGRESS_CHANGED ) );
    assertTrue( browser.isListening( EventTypes.PROGRESS_COMPLETED ) );
  }

  @Test
  public void testRemoveProgressListenerRegistersUntypedListeners() {
    Browser browser = new Browser( shell, SWT.NONE );
    ProgressListener progressListener = mock( ProgressListener.class );
    browser.addProgressListener( progressListener );

    browser.removeProgressListener( progressListener );

    assertFalse( browser.isListening( EventTypes.PROGRESS_CHANGED ) );
    assertFalse( browser.isListening( EventTypes.PROGRESS_COMPLETED ) );
  }

  @Test
  public void testSetTextWithNonVetoingLocationListener() {
    Browser browser = new Browser( shell, SWT.NONE );
    LocationListener listener = mock( LocationListener.class );
    browser.addLocationListener( listener );

    browser.setText( "text" );

    ArgumentCaptor<LocationEvent> changingCaptor = ArgumentCaptor.forClass( LocationEvent.class );
    verify( listener ).changing( changingCaptor.capture() );
    assertEquals( Browser.ABOUT_BLANK, changingCaptor.getValue().location );
    ArgumentCaptor<LocationEvent> changedCaptor = ArgumentCaptor.forClass( LocationEvent.class );
    verify( listener ).changed( changedCaptor.capture() );
    assertEquals( Browser.ABOUT_BLANK, changedCaptor.getValue().location );
  }

  @Test
  public void testSetUrlWithNonVetoingLocationListener() {
    Browser browser = new Browser( shell, SWT.NONE );
    LocationListener listener = mock( LocationListener.class );
    browser.addLocationListener( listener );

    String newUrl = "NEW_URL";
    browser.setUrl( newUrl );

    ArgumentCaptor<LocationEvent> changingCaptor = ArgumentCaptor.forClass( LocationEvent.class );
    verify( listener ).changing( changingCaptor.capture() );
    assertEquals( newUrl, changingCaptor.getValue().location );
    ArgumentCaptor<LocationEvent> changedCaptor = ArgumentCaptor.forClass( LocationEvent.class );
    verify( listener ).changed( changedCaptor.capture() );
    assertEquals( newUrl, changedCaptor.getValue().location );
  }

  @Test
  public void testLocationListenerOrderInSetText() {
    Browser browser = new Browser( shell, SWT.NONE );
    LocationListener listener = mock( LocationListener.class );
    browser.addLocationListener( listener );

    browser.setText( "text" );

    InOrder inOrder = inOrder( listener );
    inOrder.verify( listener ).changing( any( LocationEvent.class ) );
    inOrder.verify( listener ).changed( any( LocationEvent.class ) );
  }

  @Test
  public void testLocationEvent() {
    final StringBuilder log = new StringBuilder();
    final String[] expectedLocation = new String[ 1 ];
    final Browser browser = new Browser( shell, SWT.NONE );
    LocationListener listener = new LocationListener() {
      public void changing( LocationEvent event ) {
        log.append( "changing" + event.location + "|" );
        assertEquals( expectedLocation[ 0 ], event.location );
        assertFalse( event.top );
      }
      public void changed( LocationEvent event ) {
        log.append( "changed" + event.location );
        assertSame( browser, event.getSource() );
        assertTrue( event.doit );
        assertEquals( expectedLocation[ 0 ], event.location );
        assertTrue( event.top );
      }
    };

    // test basic event behaviour with setUrl
    browser.addLocationListener( listener );
    expectedLocation[ 0 ] = "NEW_URL";
    boolean success = browser.setUrl( expectedLocation[ 0 ] );
    assertTrue( success );
    assertEquals( "changingNEW_URL|changedNEW_URL", log.toString() );
    // setting the current url must also fire events
    log.setLength( 0 );
    success = browser.setUrl( expectedLocation[ 0 ] );
    assertTrue( success );
    assertEquals( "changingNEW_URL|changedNEW_URL", log.toString() );
    // clean up
    log.setLength( 0 );
    browser.removeLocationListener( listener );

    // test basic event behaviour with setText
    browser.addLocationListener( listener );
    expectedLocation[ 0 ] = "about:blank";
    success = browser.setText( "Some html" );
    assertTrue( success );
    assertEquals( "changingabout:blank|changedabout:blank", log.toString() );
    // setting the current url must also fire events
    log.setLength( 0 );
    success = browser.setText( "Some html" );
    assertTrue( success );
    assertEquals( "changingabout:blank|changedabout:blank", log.toString() );
  }

  @Test
  public void testSetUrlWithVetoingLocationListener() {
    String oldUrl = "OLD_URL";
    Browser browser = new Browser( shell, SWT.NONE );
    browser.setUrl( oldUrl );
    browser.addLocationListener( new VetoingLocationListener() );

    browser.setUrl( "NEW_URL" );

    assertEquals( oldUrl, browser.getUrl() );
  }

  @Test
  public void testSetTextWithVetoingLocationListener() {
    String oldText = "OLD_TEXT";
    Browser browser = new Browser( shell, SWT.NONE );
    browser.setText( oldText );
    browser.addLocationListener( new VetoingLocationListener() );

    browser.setUrl( "NEW_TEXT" );

    assertEquals( oldText, browser.getAdapter( IBrowserAdapter.class ).getText() );
  }

  @Test
  public void testSetTextWithProgressListener() {
    Browser browser = new Browser( shell, SWT.NONE );
    ProgressListener listener = mock( ProgressListener.class );
    browser.addProgressListener( listener );

    browser.setText( "test" );

    verify( listener ).changed( any( ProgressEvent.class ) );
    verify( listener, never() ).completed( any( ProgressEvent.class ) );
  }

  @Test
  public void testVetoedSetTextWithProgressListener() {
    Browser browser = new Browser( shell, SWT.NONE );
    ProgressListener listener = mock( ProgressListener.class );
    browser.addProgressListener( listener );
    browser.addLocationListener( new VetoingLocationListener() );

    browser.setText( "test" );

    verify( listener, never() ).changed( any( ProgressEvent.class ) );
    verify( listener, never() ).completed( any( ProgressEvent.class ) );
  }

  @Test
  public void testSetUrlWithProgressListener() {
    Browser browser = new Browser( shell, SWT.NONE );
    ProgressListener listener = mock( ProgressListener.class );
    browser.addProgressListener( listener );

    browser.setUrl( "http://eclipse.org/rap" );

    verify( listener ).changed( any( ProgressEvent.class ) );
    verify( listener, never() ).completed( any( ProgressEvent.class ) );
  }

  @Test
  public void testVetoedSetUrlWithProgressListener() {
    Browser browser = new Browser( shell, SWT.NONE );
    ProgressListener listener = mock( ProgressListener.class );
    browser.addProgressListener( listener );
    browser.addLocationListener( new VetoingLocationListener() );

    browser.setUrl( "http://eclipse.org/rap" );

    verify( listener, never() ).changed( any( ProgressEvent.class ) );
    verify( listener, never() ).completed( any( ProgressEvent.class ) );
  }

  @Test
  public void testGetWebBrowser() {
    Browser browser = new Browser( shell, SWT.NONE );
    assertNull( browser.getWebBrowser() );
  }

  @Test
  public void testIsSerializable() throws Exception {
    Browser browser = new Browser( shell, SWT.NONE );
    browser.setUrl( "http://eclipse.org/rap" );

    Browser deserializedBrowser = Fixture.serializeAndDeserialize( browser );

    assertEquals( browser.getUrl(), deserializedBrowser.getUrl() );
  }

  @Test
  public void testExecuteReturnsAfterDispose() {
    final Browser browser = new Browser( shell, SWT.NONE );
    display.asyncExec( new Runnable() {
      public void run() {
        browser.dispose();
      }
    } );

    boolean result = browser.execute( "var x = 2;" );

    assertFalse( result );
  }

  @Test
  public void testExecute_JEE_COMPATIBILITY() {
    // Activate SimpleLifeCycle
    getApplicationContext().getLifeCycleFactory().deactivate();
    getApplicationContext().getLifeCycleFactory().activate();
    Browser browser = new Browser( shell, SWT.NONE );

    try {
      browser.execute( "var x = 2;" );
      fail();
    } catch( UnsupportedOperationException expected ) {
      assertEquals( "Method not supported in JEE_COMPATIBILITY mode.", expected.getMessage() );
    }
  }

  @Test
  public void testEvaluate_JEE_COMPATIBILITY() {
    // Activate SimpleLifeCycle
    getApplicationContext().getLifeCycleFactory().deactivate();
    getApplicationContext().getLifeCycleFactory().activate();
    Browser browser = new Browser( shell, SWT.NONE );

    try {
      browser.evaluate( "var x = 2;" );
      fail();
    } catch( UnsupportedOperationException expected ) {
      assertEquals( "Method not supported in JEE_COMPATIBILITY mode.", expected.getMessage() );
    }
  }

  private static String getText( Browser browser ) {
    Object adapter = browser.getAdapter( IBrowserAdapter.class );
    IBrowserAdapter browserAdapter = ( IBrowserAdapter )adapter;
    return browserAdapter.getText();
  }

  private static class VetoingLocationListener implements LocationListener {
    public void changing( LocationEvent event ) {
      event.doit = false;
    }

    public void changed( LocationEvent event ) {
    }
  }

}
