/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.mockito.Mockito.mock;

import java.io.Serializable;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rap.rwt.internal.engine.RWTClusterSupport;
import org.eclipse.rap.rwt.internal.lifecycle.SimpleLifeCycle;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestSession;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.internal.widgets.WidgetAdapter;


public class DisplaySerialization_Test extends TestCase {

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

  private Display display;
  private ApplicationContextImpl applicationContext;

  public void testDisposeIsSerializable() throws Exception {
    Display deserializedDisplay = serializeAndDeserialize( display );

    assertFalse( deserializedDisplay.isDisposed() );
  }

  public void testDisplayAdapterIsSerializable() throws Exception {
    Display deserializedDisplay = serializeAndDeserialize( display );

    Object adapter = deserializedDisplay.getAdapter( IDisplayAdapter.class );
    IDisplayAdapter displayAdapter = ( IDisplayAdapter )adapter;

    assertNotNull( displayAdapter );
  }

  public void testWidgetAdapterIsSerializable() throws Exception {
    WidgetAdapter adapter = getWidgetAdapter( display );
    adapter.setInitialized( true );

    Display deserializedDisplay = serializeAndDeserialize( display );
    WidgetAdapter deserializedAdapter = getWidgetAdapter( deserializedDisplay );

    assertNotNull( deserializedAdapter );
    assertEquals( adapter.isInitialized(), deserializedAdapter.isInitialized() );
    assertEquals( adapter.getId(), deserializedAdapter.getId() );
  }

  public void testUISessionIsSerializable() throws Exception {
    Display deserializedDisplay = serializeAndDeserialize( display );

    UISession uiSession = getDisplayAdapter( deserializedDisplay ).getUISession();

    assertNotNull( uiSession );
  }

  public void testSynchronizer() throws Exception {
    Display deserializedDisplay = serializeAndDeserialize( display );

    assertSame( deserializedDisplay, deserializedDisplay.getSynchronizer().display );
  }

  public void testThreadIsNotSerializable() throws Exception {
    getDisplayAdapter( display ).attachThread();

    Display deserializedDisplay = Fixture.serializeAndDeserialize( display );

    assertNull( deserializedDisplay.getThread() );
  }

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

  public void testBoundsIsSerializable() throws Exception {
    getDisplayAdapter( display ).setBounds( new Rectangle( 1, 2, 3, 4 ) );
    Display deserializedDisplay = serializeAndDeserialize( display );

    assertEquals( new Rectangle( 1, 2, 3, 4 ), deserializedDisplay.getBounds() );
  }

  public void testCursorLocationIsSerializable() throws Exception {
    getDisplayAdapter( display ).setCursorLocation( 1, 2 );
    Display deserializedDisplay = serializeAndDeserialize( display );

    assertEquals( new Point( 1, 2 ), deserializedDisplay.getCursorLocation() );
  }

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

  public void testCloseListenerIsSerializable() throws Exception {
    display.addListener( SWT.Close, new SerializableListener() );
    Display deserializedDisplay = serializeAndDeserialize( display );

    deserializedDisplay.close();

    assertTrue( SerializableListener.wasInvoked );
  }

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

  public void testFiltersIsSerializable() throws Exception {
    display.addFilter( SWT.Skin, new SerializableListener() );

    Display deserializedDisplay = serializeAndDeserialize( display );

    assertTrue( deserializedDisplay.filters( SWT.Skin ) );
  }

  public void testDisposeExecRunnablesIsSerializable() throws Exception {
    display.disposeExec( new SerializableRunnable() );
    Display deserializedDisplay = serializeAndDeserialize( display );

    deserializedDisplay.dispose();

    assertTrue( SerializableRunnable.wasInvoked );
  }

  public void testAsyncExecIsSerializable() throws Exception {
    display.asyncExec( new SerializableRunnable() );

    Display deserializedDisplay = serializeAndDeserialize( display );
    deserializedDisplay.readAndDispatch();

    assertTrue( SerializableRunnable.wasInvoked );
  }

  public void testSyncExecIsSerializable() throws Exception {
    Thread thread = new Thread( new BackgroundRunnable( display ) );
    thread.setDaemon( true );
    thread.start();
    Thread.sleep( 50 );

    Display deserializedDisplay = serializeAndDeserialize( display );
    deserializedDisplay.readAndDispatch();

    assertTrue( SerializableRunnable.wasInvoked );
  }

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

  @Override
  protected void setUp() throws Exception {
    SerializableRunnable.wasInvoked = false;
    Fixture.createApplicationContext();
    Fixture.createServiceContext();
    Fixture.useDefaultResourceManager();
    applicationContext = ApplicationContextUtil.getInstance();
    applicationContext.getLifeCycleFactory().configure( SimpleLifeCycle.class );
    ApplicationContextUtil.set( ContextProvider.getUISession(), applicationContext );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
  }

  @Override
  protected void tearDown() throws Exception {
    display.dispose();
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfApplicationContext();
  }

  private static Display serializeAndDeserialize( Display display ) throws Exception {
    Display result = Fixture.serializeAndDeserialize( display );
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
    TestSession session = ( TestSession )ContextProvider.getRequest().getSession();
    ApplicationContextUtil.set( session.getServletContext(), applicationContext );
    UISessionImpl uiSession = ( UISessionImpl )getUISession( display );
    UISessionImpl.attachInstanceToSession( session, uiSession );
    uiSession.attachHttpSession( session );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  private static UISession getUISession( Display display ) {
    IDisplayAdapter displayAdapter = display.getAdapter( IDisplayAdapter.class );
    return displayAdapter.getUISession();
  }

  private static IDisplayAdapter getDisplayAdapter( Display display ) {
    return display.getAdapter( IDisplayAdapter.class );
  }

  private static WidgetAdapter getWidgetAdapter( Display display ) {
    return ( WidgetAdapter )display.getAdapter( IWidgetAdapter.class );
  }
}
