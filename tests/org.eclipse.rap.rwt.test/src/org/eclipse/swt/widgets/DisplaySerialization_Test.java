/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
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

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.TestSession;
import org.eclipse.rwt.internal.engine.*;
import org.eclipse.rwt.internal.lifecycle.SimpleLifeCycle;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.SessionStoreImpl;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;


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
  private ApplicationContext applicationContext;
  
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
    Display deserializedDisplay = serializeAndDeserialize( display );
    
    Object adapter = deserializedDisplay.getAdapter( IWidgetAdapter.class );
    IWidgetAdapter widgetAdapter = ( IWidgetAdapter )adapter;
    
    assertNotNull( widgetAdapter );
  }
  
  public void testSessionStoreIsSerializable() throws Exception {
    Display deserializedDisplay = serializeAndDeserialize( display );

    ISessionStore sessionStore = getDisplayAdapter( deserializedDisplay ).getSessionStore();
    
    assertNotNull( sessionStore );
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
    
    assertEquals( 1, getDisplayAdapter( deserializedDisplay ).getFilters().length );
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
    display.timerExec( 10, new SerializableRunnable() );
    
    Display deserializedDisplay = serializeAndDeserialize( display );
    display.dispose();
    ContextProvider.disposeContext();
    createServiceContext( deserializedDisplay );
    runClusterSupportFilter();
    Thread.sleep( 20 );
    deserializedDisplay.readAndDispatch();
    
    assertTrue( SerializableRunnable.wasInvoked );
  }
  
  protected void setUp() throws Exception {
    System.setProperty( "lifecycle", SimpleLifeCycle.class.getName() );
    SerializableRunnable.wasInvoked = false;
    Fixture.createApplicationContext();
    Fixture.createServiceContext();
    Fixture.useDefaultResourceManager();
    applicationContext = ApplicationContextUtil.getInstance();
    ApplicationContextUtil.set( ContextProvider.getSession(), applicationContext );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
  }

  protected void tearDown() throws Exception {
    display.dispose();
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfApplicationContext();
    System.getProperties().remove( "lifecycle" );
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
    SessionStoreImpl sessionStore = ( SessionStoreImpl )getSessionStore( display );
    SessionStoreImpl.attachInstanceToSession( session, sessionStore );
    sessionStore.attachHttpSession( session );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  private static ISessionStore getSessionStore( Display display ) {
    IDisplayAdapter displayAdapter = ( IDisplayAdapter )display.getAdapter( IDisplayAdapter.class );
    return displayAdapter.getSessionStore();
  }

  private static IDisplayAdapter getDisplayAdapter( Display display ) {
    return( ( IDisplayAdapter )display.getAdapter( IDisplayAdapter.class ) );
  }
}
