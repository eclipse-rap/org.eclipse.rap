/*******************************************************************************
 * Copyright (c) 2011, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.CONNECTION_ID;
import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.Serializable;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.engine.RWTClusterSupport;
import org.eclipse.rap.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.internal.lifecycle.SimpleLifeCycle;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil;
import org.eclipse.rap.rwt.testfixture.internal.TestHttpSession;
import org.eclipse.rap.rwt.testfixture.internal.TestRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.internal.widgets.WidgetRemoteAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class DisplaySerialization_Test {

  private Display display;
  private ApplicationContextImpl applicationContext;

  @Before
  public void setUp() {
    SerializableRunnable.wasInvoked = false;
    Fixture.createApplicationContext( true );
    Fixture.createServiceContext();
    applicationContext = getApplicationContext();
    applicationContext.getLifeCycleFactory().configure( SimpleLifeCycle.class );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
  }

  @After
  public void tearDown() {
    display.dispose();
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfApplicationContext();
  }

  @Test
  public void testDisposeIsSerializable() throws Exception {
    Display deserializedDisplay = serializeAndDeserialize( display );

    assertFalse( deserializedDisplay.isDisposed() );
  }

  @Test
  public void testDisplayAdapterIsSerializable() throws Exception {
    Display deserializedDisplay = serializeAndDeserialize( display );

    Object adapter = deserializedDisplay.getAdapter( IDisplayAdapter.class );
    IDisplayAdapter displayAdapter = ( IDisplayAdapter )adapter;

    assertNotNull( displayAdapter );
  }

  @Test
  public void testRemoteAdapterIsSerializable() throws Exception {
    WidgetRemoteAdapter adapter = getWidgetRemoteAdapter( display );
    adapter.setInitialized( true );

    Display deserializedDisplay = serializeAndDeserialize( display );
    WidgetRemoteAdapter deserializedAdapter = getWidgetRemoteAdapter( deserializedDisplay );

    assertNotNull( deserializedAdapter );
    assertTrue( adapter.isInitialized() == deserializedAdapter.isInitialized() );
    assertEquals( adapter.getId(), deserializedAdapter.getId() );
  }

  @Test
  public void testUISessionIsSerializable() throws Exception {
    Display deserializedDisplay = serializeAndDeserialize( display );

    UISession uiSession = getDisplayAdapter( deserializedDisplay ).getUISession();

    assertNotNull( uiSession );
  }

  @Test
  public void testSynchronizer() throws Exception {
    Display deserializedDisplay = serializeAndDeserialize( display );

    assertSame( deserializedDisplay, deserializedDisplay.getSynchronizer().display );
  }

  @Test
  public void testThreadIsNotSerializable() throws Exception {
    getDisplayAdapter( display ).attachThread();

    Display deserializedDisplay = SerializationTestUtil.serializeAndDeserialize( display );

    assertNull( deserializedDisplay.getThread() );
  }

  @Test
  public void testMonitorIsSerializable() throws Exception {
    Monitor monitor = display.getMonitors()[ 0 ];
    Display deserializedDisplay = serializeAndDeserialize( display );

    assertNotNull( deserializedDisplay.getPrimaryMonitor() );
    assertNotNull( deserializedDisplay.getMonitors() );
    assertNotNull( deserializedDisplay.getMonitors()[ 0 ] );
    Monitor deserializedMonitor = deserializedDisplay.getMonitors()[ 0 ];
    assertEquals( monitor.getBounds(), deserializedMonitor.getBounds() );
    assertEquals( monitor.getClientArea(), deserializedDisplay.getClientArea() );
  }

  @Test
  public void testBoundsIsSerializable() throws Exception {
    getDisplayAdapter( display ).setBounds( new Rectangle( 1, 2, 3, 4 ) );
    Display deserializedDisplay = serializeAndDeserialize( display );

    assertEquals( new Rectangle( 1, 2, 3, 4 ), deserializedDisplay.getBounds() );
  }

  @Test
  public void testCursorLocationIsSerializable() throws Exception {
    getDisplayAdapter( display ).setCursorLocation( 1, 2 );
    Display deserializedDisplay = serializeAndDeserialize( display );

    assertEquals( new Point( 1, 2 ), deserializedDisplay.getCursorLocation() );
  }

  @Test
  public void testDataIsSerializable() throws Exception {
    String data = "foo";
    String dataKey = "bar";
    String dataValue = "baz";
    display.setData( data );
    display.setData( dataKey, dataValue );
    Display deserializedDisplay = serializeAndDeserialize( display );

    assertEquals( data, deserializedDisplay.getData() );
    assertEquals( dataValue, deserializedDisplay.getData( dataKey ) );
  }

  @Test
  public void testCloseListenerIsSerializable() throws Exception {
    display.addListener( SWT.Close, new SerializableListener() );
    Display deserializedDisplay = serializeAndDeserialize( display );

    deserializedDisplay.close();

    assertTrue( SerializableListener.wasInvoked );
  }

  @Test
  public void testShellsAndActiveShellIsSerializable() throws Exception {
    String shellText = "shell";
    Shell shell = new Shell( display );
    shell.setText( shellText );
    shell.open();
    Display deserializedDisplay = serializeAndDeserialize( display );

    Shell[] shells = deserializedDisplay.getShells();

    assertEquals( 1, shells.length );
    assertEquals( shellText, shells[ 0 ].getText() );
    assertNotNull( deserializedDisplay.getActiveShell() );
    assertEquals( shellText, deserializedDisplay.getActiveShell().getText() );
  }

  @Test
  public void testFiltersIsSerializable() throws Exception {
    display.addFilter( SWT.Skin, new SerializableListener() );

    Display deserializedDisplay = serializeAndDeserialize( display );

    assertTrue( deserializedDisplay.filters( SWT.Skin ) );
  }

  @Test
  public void testDisposeExecRunnablesIsSerializable() throws Exception {
    display.disposeExec( new SerializableRunnable() );
    Display deserializedDisplay = serializeAndDeserialize( display );

    deserializedDisplay.dispose();

    assertTrue( SerializableRunnable.wasInvoked );
  }

  @Test
  public void testAsyncExecIsSerializable() throws Exception {
    display.asyncExec( new SerializableRunnable() );

    Display deserializedDisplay = serializeAndDeserialize( display );
    deserializedDisplay.readAndDispatch();

    assertTrue( SerializableRunnable.wasInvoked );
  }

  @Test
  public void testSyncExecIsSerializable() throws Exception {
    Thread thread = new Thread( new BackgroundRunnable( display ) );
    thread.setDaemon( true );
    thread.start();
    Thread.sleep( 50 );

    Display deserializedDisplay = serializeAndDeserialize( display );
    deserializedDisplay.readAndDispatch();

    assertTrue( SerializableRunnable.wasInvoked );
  }

  @Test
  public void testTimerExecIsSerializable() throws Exception {
    display.timerExec( 1, new SerializableRunnable() );

    Display deserializedDisplay = serializeAndDeserialize( display );
    display.dispose();
    ContextProvider.disposeContext();
    createServiceContext( deserializedDisplay );
    runClusterSupportFilter();
    Thread.sleep( 20 );
    deserializedDisplay.readAndDispatch();

    assertTrue( SerializableRunnable.wasInvoked );
  }

  private static Display serializeAndDeserialize( Display display ) throws Exception {
    Display result = SerializationTestUtil.serializeAndDeserialize( display );
    getDisplayAdapter( result ).attachThread();
    return result;
  }

  private void runClusterSupportFilter() throws Exception {
    HttpServletRequest request = ContextProvider.getRequest();
    HttpServletResponse response = ContextProvider.getResponse();
    FilterChain filterChain = mock( FilterChain.class );
    new RWTClusterSupport().doFilter( request, response, filterChain );
  }

  private void createServiceContext( Display display ) {
    Fixture.createServiceContext();
    UISessionImpl uiSession = ( UISessionImpl )getUISession( display );
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setParameter( CONNECTION_ID, uiSession.getConnectionId() );
    TestHttpSession session = ( TestHttpSession )request.getSession();
    uiSession.setHttpSession( session );
    uiSession.attachToHttpSession();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  private static UISession getUISession( Display display ) {
    return getDisplayAdapter( display ).getUISession();
  }

  private static IDisplayAdapter getDisplayAdapter( Display display ) {
    return display.getAdapter( IDisplayAdapter.class );
  }

  private static WidgetRemoteAdapter getWidgetRemoteAdapter( Display display ) {
    return ( WidgetRemoteAdapter )DisplayUtil.getAdapter( display );
  }

  private static class BackgroundRunnable implements Runnable {
    private final Display display;

    BackgroundRunnable( Display display ) {
      this.display = display;
    }

    public void run() {
      display.syncExec( new SerializableRunnable() );
    }
  }

  private static class SerializableRunnable implements Runnable, Serializable {
    static boolean wasInvoked;
    public void run() {
      wasInvoked = true;
    }
  }

  private static class SerializableListener implements Listener, Serializable {
    static boolean wasInvoked;
    public void handleEvent( Event event ) {
      wasInvoked = true;
    }
  }

}
