/*******************************************************************************
 * Copyright (c) 2013, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.client;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.remote.ConnectionImpl;
import org.eclipse.rap.rwt.remote.RemoteObject;


public class ConnectionMessagesImpl implements ConnectionMessages {

  private static final String REMOTE_ID = "rwt.client.ConnectionMessages";
  private int waitHintTimeout = 1000;
  private RemoteObject remoteObject;


  public ConnectionMessagesImpl() {
    ConnectionImpl connection = ( ConnectionImpl )RWT.getUISession().getConnection();
    remoteObject = connection.createServiceObject( REMOTE_ID );
  }

  public int getWaitHintTimeout() {
    return waitHintTimeout;
  }

  @Override
  public void setWaitHintTimeout( int timeout ) {
    if( waitHintTimeout != timeout ) {
      remoteObject.set( "waitHintTimeout", timeout );
    }
    waitHintTimeout = timeout;
  }

}
