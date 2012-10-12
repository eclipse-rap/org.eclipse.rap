/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.events;

import static org.mockito.Mockito.mock;

import java.util.EventListener;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.SWTEventListener;


public class EventAdapter_Test extends TestCase {
  
  private static class SerializableListener implements SWTEventListener {
  }

  private IEventAdapter eventAdapter;

  public void testGetListenersForEventType() {
    EventListener[] listener = eventAdapter.getListener( SWT.Selection );
    
    assertEquals( 0, listener.length );
  }
  
  public void testGetListeners() {
    SWTEventListener listener = mock( SWTEventListener.class );
    eventAdapter.addListener( SWT.Selection, listener );

    EventListener[] listeners = eventAdapter.getListeners();

    assertEquals( 1, listeners.length );
    assertSame( listener, listeners[ 0 ] );
  }

  public void testAddListener() {
    SWTEventListener listener = mock( SWTEventListener.class );
    
    eventAdapter.addListener( SWT.Selection, listener );
    
    assertEquals( 1, eventAdapter.getListener( SWT.Selection ).length );
    assertSame( listener, eventAdapter.getListener( SWT.Selection )[ 0 ] );
  }
  
  public void testAddListenerWithBogusEventType() {
    int eventType = -12;
    SWTEventListener listener = mock( SWTEventListener.class );

    eventAdapter.addListener( eventType, listener );

    assertTrue( eventAdapter.hasListener( eventType ) );
    assertEquals( 1, eventAdapter.getListener( eventType ).length );
    assertSame( listener, eventAdapter.getListener( eventType )[ 0 ] );
  }
  
  public void testAddListenerWithNullListener() {
    try {
      eventAdapter.addListener( SWT.Selection, null );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testRemoveListener() {
    SWTEventListener listener = mock( SWTEventListener.class );
    eventAdapter.addListener( SWT.Selection, listener );
    
    eventAdapter.removeListener( SWT.Selection, listener );
    
    assertEquals( 0, eventAdapter.getListener( SWT.Selection ).length );
  }
  
  public void testHasListener() {
    eventAdapter.addListener( SWT.Selection, mock( SWTEventListener.class ) );
    
    boolean hasSelectionListener = eventAdapter.hasListener( SWT.Selection );
    boolean hasOtherListener = eventAdapter.hasListener( SWT.Resize );
    
    assertTrue( hasSelectionListener );
    assertFalse( hasOtherListener );
  }
  
  public void testIsSerializable() throws Exception {
    eventAdapter.addListener( SWT.Selection, new SerializableListener() );

    IEventAdapter deserialized = Fixture.serializeAndDeserialize( eventAdapter );
    
    assertEquals( 1, deserialized.getListener( SWT.Selection ).length );
    assertEquals( SerializableListener.class, deserialized.getListeners()[ 0 ].getClass() );
  }

  protected void setUp() throws Exception {
    eventAdapter = new EventAdapter();
  }
}
