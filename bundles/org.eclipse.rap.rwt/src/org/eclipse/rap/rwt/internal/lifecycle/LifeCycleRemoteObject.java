/*******************************************************************************
* Copyright (c) 2011, 2013 EclipseSource and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.remote.RemoteObject;


/**
 * A remote object that writes directly to the message writer. Used for widgets and other objects
 * that are rendered by LCAs.
 */
public final class LifeCycleRemoteObject implements RemoteObject {

  private final String id;

  public LifeCycleRemoteObject( String id, String type ) {
    this.id = id;
    if( type != null ) {
      getWriter().appendCreate( id, type );
    }
  }

  public String getId() {
    return id;
  }

  public void destroy() {
    getWriter().appendDestroy( id );
  }

  public void set( String name, int value ) {
    getWriter().appendSet( id, name, value );
  }

  public void set( String name, double value ) {
    getWriter().appendSet( id, name, value );
  }

  public void set( String name, boolean value ) {
    getWriter().appendSet( id, name, value );
  }

  public void set( String name, String value ) {
    getWriter().appendSet( id, name, value );
  }

  public void set( String name, JsonValue value ) {
    getWriter().appendSet( id, name, value );
  }

  public void listen( String eventName, boolean listen ) {
    getWriter().appendListen( id, eventName, listen );
  }

  public void call( String method, JsonObject parameters ) {
    getWriter().appendCall( id, method, parameters );
  }

  public void setHandler( OperationHandler handler ) {
  }

  private static ProtocolMessageWriter getWriter() {
    return ContextProvider.getProtocolWriter();
  }

}
