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
package org.eclipse.rap.rwt.internal.util;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.rap.rwt.internal.util.SharedInstanceBuffer.InstanceCreator;
import org.junit.Before;
import org.junit.Test;


public class SharedInstanceBuffer_Test {

  private SharedInstanceBuffer<Object,Object> keyValueStore;
  private Object key;
  private Object value;

  @Before
  public void setUp() {
    key = new Object();
    value = new Object();
    keyValueStore = new SharedInstanceBuffer<Object,Object>();
  }

  @Test
  public void testGet_callsInstanceCreator() {
    InstanceCreator<Object, Object> instanceCreator = mockInstanceCreator( value );

    Object returnedValue = keyValueStore.get( key, instanceCreator );

    verify( instanceCreator ).createInstance( key );
    assertSame( returnedValue, value );
  }

  @Test
  public void testGet_cachesValue() {
    InstanceCreator<Object, Object> instanceCreator = mockInstanceCreator( value );

    Object returned1 = keyValueStore.get( key, instanceCreator );
    Object returned2 = keyValueStore.get( key, instanceCreator );

    verify( instanceCreator, times( 1 ) ).createInstance( key );
    assertSame( returned1, returned2 );
  }

  @Test
  public void testGet_acceptsNullKey() {
    Object returnedValue = keyValueStore.get( null, mockInstanceCreator( value ) );

    assertSame( value, returnedValue );
  }

  @Test( expected = NullPointerException.class )
  public void testGet_rejectsNullValueCreator() {
    keyValueStore.get( new Object(), null );
  }

  @Test
  public void testGet_acceptsRemovedKey() {
    InstanceCreator<Object, Object> instanceCreator = mockInstanceCreator( value );
    keyValueStore.get( key, instanceCreator );
    keyValueStore.remove( key );

    Object returnedValue = keyValueStore.get( key, instanceCreator );

    verify( instanceCreator, times( 2 ) ).createInstance( key );
    assertSame( returnedValue, value );
  }

  @Test
  public void testRemove_acceptsNonExistingKey() {
    Object removedValue = keyValueStore.remove( new Object() );

    assertNull( removedValue );
  }

  @Test
  public void testRemove_removesExistingKey() {
    keyValueStore.get( key, mockInstanceCreator( value ) );

    Object removed = keyValueStore.remove( key );

    assertSame( value, removed );
  }

  @Test
  public void testRemove_acceptsNullKey() {
    keyValueStore.get( null, mockInstanceCreator( value ) );

    Object removed = keyValueStore.remove( null );

    assertSame( value, removed );
  }

  @SuppressWarnings( "unchecked" )
  private static InstanceCreator<Object, Object> mockInstanceCreator( Object value ) {
    InstanceCreator mock = mock( InstanceCreator.class );
    when( mock.createInstance( any() ) ).thenReturn( value );
    return mock;
  }

}
