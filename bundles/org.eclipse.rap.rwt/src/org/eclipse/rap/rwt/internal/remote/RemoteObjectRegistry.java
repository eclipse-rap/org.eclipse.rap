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
package org.eclipse.rap.rwt.internal.remote;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rap.rwt.SingletonUtil;


public class RemoteObjectRegistry implements Serializable {

  private final Map<String, RemoteObjectImpl> remoteObjects;

  RemoteObjectRegistry() {
    // TODO [rst] This is a linked list in order to provide an ordered list of remote objects to
    //            iterate in the render phase. Once messages are rendered directly, this doesn't
    //            have to be a linked list anymore.
    remoteObjects = new LinkedHashMap<String, RemoteObjectImpl>();
  }

  public static RemoteObjectRegistry getInstance() {
    return SingletonUtil.getSessionInstance( RemoteObjectRegistry.class );
  }

  public void register( RemoteObjectImpl object ) {
    String id = object.getId();
    if( remoteObjects.containsKey( id ) ) {
      throw new IllegalArgumentException( "Remote object already registered, id: " + id );
    }
    remoteObjects.put( id, object );
  }

  public void remove( RemoteObjectImpl object ) {
    String id = object.getId();
    if( !remoteObjects.containsKey( id ) ) {
      throw new IllegalArgumentException( "Remote object not found in registry, id: " + id );
    }
    remoteObjects.remove( id );
  }

  public RemoteObject get( String id ) {
    return remoteObjects.get( id );
  }

  public List<RemoteObjectImpl> getRemoteObjects() {
    return new ArrayList<RemoteObjectImpl>( remoteObjects.values() );
  }

}
