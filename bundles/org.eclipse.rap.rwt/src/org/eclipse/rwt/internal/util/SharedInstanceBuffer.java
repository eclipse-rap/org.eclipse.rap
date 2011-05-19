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

import java.util.*;



public class SharedInstanceBuffer {
  
  public interface IInstanceCreator {
    Object createInstance();
  }

  private final Object lock;
  private final Map store;
  
  public SharedInstanceBuffer() {
    lock = new Object();
    store = new HashMap();
  }
  
  public Object get( Object key, IInstanceCreator instanceCreator ) {
    ParamCheck.notNull( instanceCreator, "valueCreator" );
    synchronized( lock ) {
      Object result = store.get( key );
      if( result == null ) {
        result = instanceCreator.createInstance();
        store.put( key, result );
      }
      return result;
    }
  }
  
  public Object remove( Object key ) {
    synchronized( lock ) {
      return store.remove( key );
    }
  }
}
