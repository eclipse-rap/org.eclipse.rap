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

import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.util.ParamCheck;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.swt.internal.widgets.IdGenerator;


/**
 * A factory used to create remote objects on the client.
 */
public class RemoteObjectFactory implements Serializable {

  private static final String SESSION_STORE_KEY
    = RemoteObjectFactory.class.getName() + "#instance";

  public static RemoteObjectFactory getInstance() {
    UISession uiSession = ContextProvider.getUISession();
    Object result = uiSession.getAttribute( SESSION_STORE_KEY );
    if( result == null ) {
      result = new RemoteObjectFactory();
      uiSession.setAttribute( SESSION_STORE_KEY, result );
    }
    return ( RemoteObjectFactory )result;
  }

  /**
   * Creates a new remote object on the client with the given remote type. The returned
   * <code>RemoteObject</code> can be used to communicate with the remote object.
   *
   * @return a representation of the remote object that has been created
   */
  public RemoteObject createRemoteObject( String remoteType ) {
    ParamCheck.notNullOrEmpty( remoteType, "type" );
    String id = IdGenerator.getInstance().newId( "r" );
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

  private RemoteObjectFactory() {
    // prevent instantiation
  }

}
