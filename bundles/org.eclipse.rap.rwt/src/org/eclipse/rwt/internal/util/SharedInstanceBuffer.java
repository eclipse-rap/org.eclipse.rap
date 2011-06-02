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
package org.eclipse.rwt.internal.util;

import java.io.Serializable;
import java.util.*;



public class SharedInstanceBuffer<K,I> implements Serializable {
  private static final long serialVersionUID = 1L;

  public interface IInstanceCreator<T> {
    T createInstance();
  }

  private final SerializableLock lock;
  private final Map<K,I> store;
  
  public SharedInstanceBuffer() {
    lock = new SerializableLock();
    store = new HashMap<K,I>();
  }
  
  public I get( K key, IInstanceCreator<I> instanceCreator ) {
    ParamCheck.notNull( instanceCreator, "valueCreator" );
    synchronized( lock ) {
      I result = store.get( key );
      if( result == null ) {
        result = instanceCreator.createInstance();
        store.put( key, result );
      }
      return result;
    }
  }
  
  public I remove( K key ) {
    synchronized( lock ) {
      return store.remove( key );
    }
  }
}
