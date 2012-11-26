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

import org.eclipse.rap.rwt.internal.util.ParamCheck;
import org.eclipse.swt.internal.widgets.IdGenerator;


/**
 * A factory used to create remote objects on the client.
 */
public class RemoteObjectFactory {

  /**
   * Creates a new remote object on the client with the given remote type. The returned
   * <code>RemoteObject</code> can be used to communicate with the remote object.
   *
   * @return a representation of the remote object that has been created
   */
  public static RemoteObject createRemoteObject( String remoteType ) {
    ParamCheck.notNullOrEmpty( remoteType, "type" );
    String id = IdGenerator.getInstance().newId( "r" );
    RemoteObjectImpl remoteObject = new RemoteObjectImpl( id, remoteType );
    RemoteObjectRegistry.getInstance().register( remoteObject );
    return remoteObject;
  }

}
