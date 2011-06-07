/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
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
package org.eclipse.rwt.internal.events;

import java.io.Serializable;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.internal.SWTEventListener;
import org.eclipse.swt.widgets.*;


public class EventAdapter_Test extends TestCase {
  
  private static class SerializableSelectionListener 
    extends SelectionAdapter 
    implements Serializable 
  {
    private static final long serialVersionUID = 1L;
  }

  private Widget widget;

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    widget = new Shell( display );
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testActionPerformed()  {
    IEventAdapter eventAdapter = ( IEventAdapter )widget.getAdapter( IEventAdapter.class );
    assertNotNull( eventAdapter );
    assertSame( eventAdapter, widget.getAdapter( IEventAdapter.class ) );
    assertFalse( eventAdapter.hasListener( SelectionListener.class ) );
    try {
      eventAdapter.hasListener( Object.class );
      fail();
    } catch( final IllegalArgumentException iae ) {
    }
    
    Object[] listener = eventAdapter.getListener( SelectionListener.class );
    assertEquals( 0, listener.length );
    SelectionListener actionListener = new SelectionAdapter() {
    }; 
    eventAdapter.addListener( SelectionListener.class, actionListener );
    assertTrue( eventAdapter.hasListener( SelectionListener.class ) );
    listener = eventAdapter.getListener( SelectionListener.class );
    assertEquals( 1, listener.length );
    assertSame( actionListener, listener[ 0 ] );
    eventAdapter.removeListener( SelectionListener.class, actionListener );
    assertFalse( eventAdapter.hasListener( SelectionListener.class ) );
  }
  
  public void testAddListenerWithIllegalArguments() {
    IEventAdapter eventAdapter = ( IEventAdapter )widget.getAdapter( IEventAdapter.class );
    try {
      eventAdapter.addListener( SelectionListener.class, new SWTEventListener() { } );
      fail();
    } catch( final IllegalArgumentException iae ) {
    }
    try {
      eventAdapter.addListener( SelectionListener.class, null );
      fail();
    } catch( final IllegalArgumentException iae ) {
    }
    try {
      SelectionListener validListener = new SelectionAdapter() {
      }; 
      eventAdapter.addListener( null, validListener );
      fail();
    } catch( final IllegalArgumentException iae ) {
    }
    Object[] listeners = eventAdapter.getListener( SelectionListener.class );
    assertEquals( 0, listeners.length );
  }
  
  public void testIsSerializable() throws Exception {
    IEventAdapter eventAdapter = ( IEventAdapter )widget.getAdapter( IEventAdapter.class );
    eventAdapter.addListener( SelectionListener.class, new SerializableSelectionListener() );

    IEventAdapter deserialized = Fixture.serializeAndDeserialize( eventAdapter );
    
    assertEquals( 1, deserialized.getListeners().length );
    assertEquals( SerializableSelectionListener.class, 
                  deserialized.getListeners()[ 0 ].getClass() );
  }
}
