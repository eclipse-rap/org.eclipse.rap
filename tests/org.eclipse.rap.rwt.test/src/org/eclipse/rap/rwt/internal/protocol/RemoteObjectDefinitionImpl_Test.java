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
import junit.framework.TestCase;

import org.eclipse.rap.rwt.remote.EventHandler;
import org.eclipse.rap.rwt.remote.MethodHandler;
import org.eclipse.rap.rwt.remote.PropertyHandler;
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
    } catch( NullPointerException expected ) {}
  }
  
  @SuppressWarnings( "unchecked" )
  public void testHasProperty() {
    RemoteObjectDefinitionImpl<Object> definition = new RemoteObjectDefinitionImpl<Object>( Object.class );
    PropertyHandler property = mock( PropertyHandler.class );
    
    definition.addProperty( "foo", property );
    
    assertNotNull( definition.getProperty( "foo" ) );
  }
  
  public void testFailsWithNullProperty() {
    RemoteObjectDefinitionImpl<Object> definition = new RemoteObjectDefinitionImpl<Object>( Object.class );
    try {
      definition.addProperty( "foo", null );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  @SuppressWarnings( "unchecked" )
  public void testFailsWithNullPropertyName() {
    RemoteObjectDefinitionImpl<Object> definition = new RemoteObjectDefinitionImpl<Object>( Object.class );
    try {
      definition.addProperty( null, mock( PropertyHandler.class ) );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  @SuppressWarnings( "unchecked" )
  public void testFailsWithEmptyPropertyName() {
    RemoteObjectDefinitionImpl<Object> definition = new RemoteObjectDefinitionImpl<Object>( Object.class );
    try {
      definition.addProperty( "", mock( PropertyHandler.class ) );
      fail();
    } catch( IllegalArgumentException expected ) {}
  }
  
  @SuppressWarnings( "unchecked" )
  public void testHasEvent() {
    RemoteObjectDefinitionImpl<Object> definition = new RemoteObjectDefinitionImpl<Object>( Object.class );
    EventHandler event = mock( EventHandler.class );
    
    definition.addEventHandler( "foo", event );
    
    assertNotNull( definition.getEventHandler( "foo" ) );
  }
  
  public void testFailsWithNullEvent() {
    RemoteObjectDefinitionImpl<Object> definition = new RemoteObjectDefinitionImpl<Object>( Object.class );
    try {
      definition.addEventHandler( "foo", null );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  @SuppressWarnings( "unchecked" )
  public void testFailsWithNullEventName() {
    RemoteObjectDefinitionImpl<Object> definition = new RemoteObjectDefinitionImpl<Object>( Object.class );
    try {
      definition.addEventHandler( null, mock( EventHandler.class ) );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  @SuppressWarnings( "unchecked" )
  public void testFailsWithEmptyEventName() {
    RemoteObjectDefinitionImpl<Object> definition = new RemoteObjectDefinitionImpl<Object>( Object.class );
    try {
      definition.addEventHandler( "", mock( EventHandler.class ) );
      fail();
    } catch( IllegalArgumentException expected ) {}
  }
  
  @SuppressWarnings( "unchecked" )
  public void testHasCall() {
    RemoteObjectDefinitionImpl<Object> definition = new RemoteObjectDefinitionImpl<Object>( Object.class );
    MethodHandler method = mock( MethodHandler.class );
    
    definition.addMethod( "foo", method );
    
    assertNotNull( definition.getMethod( "foo" ) );
  }
  
  public void testFailsWithNullCall() {
    RemoteObjectDefinitionImpl<Object> definition = new RemoteObjectDefinitionImpl<Object>( Object.class );
    try {
      definition.addMethod( "foo", null );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  @SuppressWarnings( "unchecked" )
  public void testFailsWithNullMethodName() {
    RemoteObjectDefinitionImpl<Object> definition = new RemoteObjectDefinitionImpl<Object>( Object.class );
    try {
      definition.addMethod( null, mock( MethodHandler.class ) );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  @SuppressWarnings( "unchecked" )
  public void testFailsWithEmptyMethodName() {
    RemoteObjectDefinitionImpl<Object> definition = new RemoteObjectDefinitionImpl<Object>( Object.class );
    try {
      definition.addMethod( "", mock( MethodHandler.class ) );
      fail();
    } catch( IllegalArgumentException expected ) {}
  }
}
