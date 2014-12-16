/*******************************************************************************
 * Copyright (c) 2012, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.remote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class RemoteObjectRegistry_Test {

  private RemoteObjectRegistry registry;

  @Before
  public void setUp() {
    Fixture.setUp();
    registry = new RemoteObjectRegistry();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testReturnsSingletonInstance() {
    Object registry1 = RemoteObjectRegistry.getInstance();
    Object registry2 = RemoteObjectRegistry.getInstance();

    assertNotNull( registry1 );
    assertSame( registry1, registry2 );
  }

  @Test
  public void testCanRegisterRemoteObject() {
    RemoteObjectImpl remoteObject = mockRemoteObjectImpl( "id" );
    registry.register( remoteObject );

    RemoteObject result = registry.get( "id" );

    assertSame( remoteObject, result );
  }

  @Test
  public void testCanRemoveRegisteredObject() {
    RemoteObjectImpl remoteObject = mockRemoteObjectImpl( "id" );
    registry.register( remoteObject );

    registry.remove( remoteObject );

    assertNull( registry.get( "id" ) );
  }

  @Test
  public void testPreventsRegisterDuplicateIds() {
    registry.register( mockRemoteObjectImpl( "id" ) );

    try {
      registry.register( mockRemoteObjectImpl( "id" ) );
      fail();
    } catch( IllegalArgumentException exception ) {
      assertEquals( "Remote object already registered, id: id", exception.getMessage() );
    }
  }

  @Test
  public void testPreventsRemoveNonExisting() {
    try {
      registry.remove( mockRemoteObjectImpl( "id" ) );
      fail();
    } catch( IllegalArgumentException exception ) {
      assertEquals( "Remote object not found in registry, id: id", exception.getMessage() );
    }
  }

  @Test
  public void testGetRemoteObjects_returnsOrderedList() {
    for( int i = 0; i < 10; i++ ) {
      registry.register( new DeferredRemoteObject( "id" + i, "type" ) );
    }

    List<? extends RemoteObject> allObjects = registry.getRemoteObjects();

    assertEquals( "id0 id1 id2 id3 id4 id5 id6 id7 id8 id9", join( getIds( allObjects ), " " ) );
  }

  private static RemoteObjectImpl mockRemoteObjectImpl( String id ) {
    RemoteObjectImpl remoteObject = mock( RemoteObjectImpl.class );
    when( remoteObject.getId() ).thenReturn( id );
    return remoteObject;
  }

  private static List<String> getIds( List<? extends RemoteObject> objects ) {
    List<String> ids = new ArrayList<String>();
    for( RemoteObject object : objects ) {
      ids.add( object.getId() );
    }
    return ids;
  }

  private static String join( List<String> list, String glue ) {
    StringBuilder builder = new StringBuilder();
    for( String string : list ) {
      if( builder.length() > 0 ) {
        builder.append( glue );
      }
      builder.append( string );
    }
    return builder.toString();
  }

}
