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
package org.eclipse.rap.rwt.internal.events;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import junit.framework.TestCase;

import org.eclipse.rap.rwt.Adaptable;
import org.eclipse.swt.internal.SWTEventListener;
import org.eclipse.swt.internal.events.EventTable;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TypedListener;
import org.mockito.ArgumentCaptor;


public class RWTEvent_Test extends TestCase {
  
  private Adaptable widget;
  private EventTable eventTable;

  public void testAddListener() {
    SWTEventListener listener = mock( SWTEventListener.class );
    
    RWTEvent.addListener( widget, new int[] { 1, 2 }, listener );
    
    ArgumentCaptor<TypedListener> captor = ArgumentCaptor.forClass( TypedListener.class );
    verify( eventTable ).hook( eq( 1 ), captor.capture() );
    assertSame( listener, captor.getValue().getEventListener() );
    verify( eventTable ).hook( eq( 2 ), captor.capture() );
    assertSame( listener, captor.getValue().getEventListener() );
  }
  
  public void testAddListenerWithNullListener() {
    try {
      RWTEvent.addListener( widget, new int[ 0 ], null );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testRemoveListener() {
    SWTEventListener listener = mock( SWTEventListener.class );
    
    RWTEvent.removeListener( widget, new int[] { 1, 2 }, listener );
    
    verify( eventTable ).unhook( 1, listener );
    verify( eventTable ).unhook( 2, listener );
  }
  
  public void testRemoveListenerWithNullListener() {
    try {
      RWTEvent.removeListener( widget, new int[ 0 ], null );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testHasListenerWithoutAnyRegisteredListener() {
    boolean hasListener = RWTEvent.hasListener( widget, new int[] { 1, 2 } );
    
    assertFalse( hasListener );
  }
  
  public void testHasListenerWithMatchingRegisteredListener() {
    RWTEvent.addListener( widget, new int[] { 1 }, mock( SWTEventListener.class ) );
    
    boolean hasListener = RWTEvent.hasListener( widget, new int[] { 1, 2 } );
    
    assertTrue( hasListener );
  }
  
  public void testHasListenerWithoutMatchingRegisteredListener() {
    RWTEvent.addListener( widget, new int[] { 1 }, mock( SWTEventListener.class ) );
    
    boolean hasListener = RWTEvent.hasListener( widget, new int[] { 47 } );
    
    assertFalse( hasListener );
  }
  
  public void testGetListenerWithoutRegisteredListener() {
    Listener[] listener = RWTEvent.getListeners( widget, new int[]{ 1 } );
    
    assertEquals( 0, listener.length );
  }
  
  public void testGetListenerWithoutMatchingRegisteredListener() {
    RWTEvent.addListener( widget, new int[]{ 1 }, mock( SWTEventListener.class ) );
    
    Listener[] listener = RWTEvent.getListeners( widget, new int[]{ 2 } );
    
    assertEquals( 0, listener.length );
  }
  
  public void testGetListenerWithMatchingRegisteredListener() {
    SWTEventListener listener = mock( SWTEventListener.class );
    RWTEvent.addListener( widget, new int[]{ 1 }, listener );
    
    Listener[] listeners = RWTEvent.getListeners( widget, new int[]{ 1, 2 } );
    
    assertEquals( 1, listeners.length );
    assertTrue( listeners[ 0 ] instanceof TypedListener );
    TypedListener typedListener = ( TypedListener )listeners[ 0 ];
    assertSame( listener, typedListener.getEventListener() );
  }
  
  @Override
  protected void setUp() throws Exception {
    eventTable = spy( new EventTable() );
    widget = mock( Adaptable.class );
    when( widget.getAdapter( EventTable.class ) ).thenReturn( eventTable );
  }
}
