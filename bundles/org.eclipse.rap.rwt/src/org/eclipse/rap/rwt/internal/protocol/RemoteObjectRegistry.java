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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.rap.rwt.SingletonUtil;
import org.eclipse.rap.rwt.remote.RemoteObject;


public class RemoteObjectRegistry {
  
  private final List<RemoteObject> remoteObjects;

  public static RemoteObjectRegistry getInstance() {
    return SingletonUtil.getSessionInstance( RemoteObjectRegistry.class );
  }

  private RemoteObjectRegistry() {
    remoteObjects = Collections.synchronizedList( new ArrayList<RemoteObject>() );
  }
  
  public void register( RemoteObject remoteObject ) {
    remoteObjects.add( remoteObject );
  }
  
  void remove( RemoteObject remoteObject ) {
    remoteObjects.remove( remoteObject );
  }
  
  public List<RemoteObject> getRemoteObjects() {
    return new ArrayList<RemoteObject>( remoteObjects );
  }

}
