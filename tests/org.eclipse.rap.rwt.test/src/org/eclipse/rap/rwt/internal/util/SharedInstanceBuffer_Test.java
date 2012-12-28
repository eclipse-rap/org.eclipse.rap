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
package org.eclipse.rap.rwt.internal.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.rwt.internal.util.SharedInstanceBuffer.IInstanceCreator;
import org.junit.Before;
import org.junit.Test;


public class SharedInstanceBuffer_Test {

  private SharedInstanceBuffer<Object,Object> keyValueStore;

  @Before
  public void setUp() {
    keyValueStore = new SharedInstanceBuffer<Object,Object>();
  }

  @Test
  public void testGetAndCreate() {
    Object key = new Object();
    final Object value = new Object();

    Object returnedValue = keyValueStore.get( key, new IInstanceCreator<Object>() {
      public Object createInstance() {
        return value;
      }
    } );

    assertSame( returnedValue, value );
  }

  @Test
  public void testGetAndCreateWithExistingKey() {
    final boolean[] createValueWasInvoked = { false };
    Object key = new Object();
    final Object value = new Object();
    keyValueStore.get( key, new TestInstanceCreator( value ) );

    Object returnedValue = keyValueStore.get( key, new IInstanceCreator<Object>() {
      public Object createInstance() {
        createValueWasInvoked[ 0 ] = true;
        return null;
      }
    } );

    assertSame( returnedValue, value );
    assertFalse( createValueWasInvoked[ 0 ] );
  }

  @Test
  public void testGetAndCreateWithNullKey() {
    IInstanceCreator<Object> valueCreator = new TestInstanceCreator( null );
    try {
      keyValueStore.get( null, valueCreator );
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testGetAndCreateWithNullValueCreator() {
    try {
      keyValueStore.get( new Object(), null );
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testGetAndCreateAfterRemove() {
    final boolean[] createValueWasInvoked = { false };
    Object key = new Object();
    final Object value = new Object();
    keyValueStore.get( key, new TestInstanceCreator( value ) );
    keyValueStore.remove( key );

    Object returnedValue = keyValueStore.get( key, new IInstanceCreator<Object>() {
      public Object createInstance() {
        createValueWasInvoked[ 0 ] = true;
        return value;
      }
    } );

    assertSame( returnedValue, value );
    assertTrue( createValueWasInvoked[ 0 ] );
  }

  @Test
  public void testRemoveNonExistingKey() {
    Object removedValue = keyValueStore.remove( new Object() );

    assertNull( removedValue );
  }

  @Test
  public void testRemoveExistingKey() {
    Object key = new Object();
    Object value = new Object();
    keyValueStore.get( key, new TestInstanceCreator( value ) );

    Object removed = keyValueStore.remove( key );

    assertSame( value, removed );
  }

  @Test
  public void testRemoveWithNullKey() {
    try {
      keyValueStore.remove( null );
    } catch( NullPointerException expected ) {
    }
  }

  private static class TestInstanceCreator implements IInstanceCreator<Object> {
    private final Object value;

    TestInstanceCreator( Object value ) {
      this.value = value;
    }

    public Object createInstance() {
      return value;
    }
  }

}
