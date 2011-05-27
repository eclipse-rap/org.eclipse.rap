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

import java.io.Serializable;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;


public class DisplaySerialization_Test extends TestCase {

  private static class SerializableRunnable implements Runnable, Serializable {
    private static final long serialVersionUID = 1L;
    static boolean wasInvoked;
    public void run() {
      wasInvoked = true;
    }
  }

  private static class SerializableListener implements Listener, Serializable {
    private static final long serialVersionUID = 1L;
    static boolean wasInvoked;
    public void handleEvent( Event event ) {
      wasInvoked = true;
    }
  }

  private Display display;
  
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
    byte[] bytes = Fixture.serialize( display );
    Display deserializedDisplay = ( Display )Fixture.deserialize( bytes );
    
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

// TODO [rh] uncomment as soon as widgets are serializable  
//  public void testShellsAndActiveShellIsSerializable() throws Exception {
//    String shellText = "shell";
//    Shell shell = new Shell( display );
//    shell.setText( shellText );
//    shell.open();
//    Display deserializedDisplay = serializeAndDeserialize( display );
//    
//    Shell[] shells = deserializedDisplay.getShells();
//    
//    assertEquals( 1, shells.length );
//    assertEquals( shellText, shells[ 0 ].getText() );
//    assertNotNull( deserializedDisplay.getActiveShell() );
//    assertEquals( shellText, deserializedDisplay.getActiveShell().getText() );
//  }
  
  public void testFiltersIsSerializable() throws Exception {
    display.addFilter( SWT.Skin, new SerializableListener() );
    
    Display deserializedDisplay = serializeAndDeserialize( display );
    
    assertEquals( 1, getDisplayAdapter( deserializedDisplay ).getFilters().length );
  }
  
  public void testDisposeExecRunnablesIsSerializable() throws Exception {
    SerializableRunnable.wasInvoked = false;
    display.disposeExec( new SerializableRunnable() );
    Display deserializedDisplay = serializeAndDeserialize( display );
    
    deserializedDisplay.dispose();
    
    assertTrue( SerializableRunnable.wasInvoked );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
  }

  protected void tearDown() throws Exception {
    display.dispose();
    Fixture.tearDown();
  }

  private static Display serializeAndDeserialize( Display display ) throws Exception {
    byte[] bytes = Fixture.serialize( display );
    Display result = ( Display )Fixture.deserialize( bytes );
    getDisplayAdapter( result ).attachThread();
    return result;
  }

  private static IDisplayAdapter getDisplayAdapter( Display display ) {
    return( ( IDisplayAdapter )display.getAdapter( IDisplayAdapter.class ) );
  }
}
