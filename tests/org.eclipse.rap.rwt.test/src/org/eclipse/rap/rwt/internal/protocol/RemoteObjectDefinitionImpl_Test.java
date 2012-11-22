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
package org.eclipse.rap.rwt.internal.protocol;

import static org.mockito.Mockito.mock;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.remote.Call;
import org.eclipse.rap.rwt.remote.Event;
import org.eclipse.rap.rwt.remote.Property;
import org.eclipse.rap.rwt.testfixture.Fixture;


public class RemoteObjectDefinitionImpl_Test extends TestCase {
  
  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testSavesType() {
    RemoteObjectDefinitionImpl<Object> definition = new RemoteObjectDefinitionImpl<Object>( Object.class );
    
    assertSame( Object.class, definition.getType() );
  }
  
  public void testFailsWithoutType() {
    try {
      new RemoteObjectDefinitionImpl<Object>( null );
      fail();
    } catch( IllegalArgumentException expected ) {}
  }
  
  @SuppressWarnings( "unchecked" )
  public void testHasProperty() {
    RemoteObjectDefinitionImpl<Object> definition = new RemoteObjectDefinitionImpl<Object>( Object.class );
    Property property = mock( Property.class );
    
    definition.addProperty( property );
    
    List<Property<Object>> properties = definition.getProperties();
    assertTrue( properties.contains( property ) );
    assertEquals( 1, properties.size() );
  }
  
  @SuppressWarnings( "unchecked" )
  public void testPropertiesIsSafeCopy() {
    RemoteObjectDefinitionImpl<Object> definition = new RemoteObjectDefinitionImpl<Object>( Object.class );
    definition.addProperty( mock( Property.class ) );
    
    List<Property<Object>> properties = definition.getProperties();

    assertNotSame( properties, definition.getProperties() );
  }
  
  public void testFailsWithNullProperty() {
    RemoteObjectDefinitionImpl<Object> definition = new RemoteObjectDefinitionImpl<Object>( Object.class );
    try {
      definition.addProperty( null );
      fail();
    } catch( IllegalArgumentException expected ) {}
  }
  
  @SuppressWarnings( "unchecked" )
  public void testHasEvent() {
    RemoteObjectDefinitionImpl<Object> definition = new RemoteObjectDefinitionImpl<Object>( Object.class );
    Event event = mock( Event.class );
    
    definition.addEvent( event );
    
    List<Event<Object>> events = definition.getEvents();
    assertTrue( events.contains( event ) );
    assertEquals( 1, events.size() );
  }
  
  @SuppressWarnings( "unchecked" )
  public void testEventsIsSafeCopy() {
    RemoteObjectDefinitionImpl<Object> definition = new RemoteObjectDefinitionImpl<Object>( Object.class );
    definition.addEvent( mock( Event.class ) );
    
    List<Event<Object>> events = definition.getEvents();
    
    assertNotSame( events, definition.getEvents() );
  }
  
  public void testFailsWithNullEvent() {
    RemoteObjectDefinitionImpl<Object> definition = new RemoteObjectDefinitionImpl<Object>( Object.class );
    try {
      definition.addEvent( null );
      fail();
    } catch( IllegalArgumentException expected ) {}
  }
  
  @SuppressWarnings( "unchecked" )
  public void testHasCall() {
    RemoteObjectDefinitionImpl<Object> definition = new RemoteObjectDefinitionImpl<Object>( Object.class );
    Call call = mock( Call.class );
    
    definition.addCall( call );
    
    List<Call<Object>> calls = definition.getCalls();
    assertTrue( calls.contains( call ) );
    assertEquals( 1, calls.size() );
  }
  
  @SuppressWarnings( "unchecked" )
  public void testCallsIsSafeCopy() {
    RemoteObjectDefinitionImpl<Object> definition = new RemoteObjectDefinitionImpl<Object>( Object.class );
    definition.addCall( mock( Call.class ) );
    
    List<Call<Object>> calls = definition.getCalls();
    
    assertNotSame( calls, definition.getCalls() );
  }
  
  public void testFailsWithNullCall() {
    RemoteObjectDefinitionImpl<Object> definition = new RemoteObjectDefinitionImpl<Object>( Object.class );
    try {
      definition.addCall( null );
      fail();
    } catch( IllegalArgumentException expected ) {}
  }
}
