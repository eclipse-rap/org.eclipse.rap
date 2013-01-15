/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
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

import org.eclipse.rap.rwt.internal.util.ParamCheck;
import org.eclipse.rap.rwt.remote.Connection;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.swt.internal.widgets.IdGeneratorProvider;


public class ConnectionImpl implements Connection, Serializable {

  public RemoteObject createRemoteObject( String remoteType ) {
    ParamCheck.notNullOrEmpty( remoteType, "type" );
    String id = IdGeneratorProvider.getIdGenerator().createId( "r" );
    RemoteObjectImpl remoteObject = new RemoteObjectImpl( id, remoteType );
    RemoteObjectRegistry.getInstance().register( remoteObject );
    return remoteObject;
  }

  /**
   * Creates an instance of RemoteObject for a given id that is agreed with the client, but does not
   * create the remote object on the client. The returned <code>RemoteObject</code> can be used to
   * receive messages from the client and to communicate with the remote object, provided that the
   * client knows the id.
   *
   * @return a representation of the remote object with the given id
   */
  // TODO [rst] Before this API is published, we should rethink the concept of "service" objects,
  //            i.e. remote objects that aren't created in the protocol, but used by agreed ids.
  public RemoteObject createServiceObject( String id ) {
    ParamCheck.notNullOrEmpty( id, "id" );
    RemoteObjectImpl remoteObject = new RemoteObjectImpl( id, null );
    RemoteObjectRegistry.getInstance().register( remoteObject );
    return remoteObject;
  }

}
