/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.client;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rap.rwt.client.service.ClientInfo;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectFactory;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.remote.OperationHandler;


public class ClientInfoImpl implements ClientInfo, Serializable {

  private Integer timezoneOffset;
  private Locale[] locales;

  public ClientInfoImpl() {
    initialize();
  }

  private void initialize() {
    RemoteObjectFactory factory = RemoteObjectFactory.getInstance();
    RemoteObject remoteObject = factory.createServiceObject( "rwt.client.ClientInfo" );
    remoteObject.setHandler( new InfoOperationHandler() );
    HttpServletRequest request = ContextProvider.getRequest();
    if( request.getHeader( "Accept-Language" ) != null ) {
      Enumeration<Locale> locales = request.getLocales();
      this.locales = Collections.list( locales ).toArray( new Locale[ 1 ] );
    }
  }

  public int getTimezoneOffset() {
    if( timezoneOffset == null ) {
      throw new IllegalStateException( "timezoneOffset is not set" );
    }
    return timezoneOffset.intValue();
  }

  public Locale getLocale() {
    return locales == null ? null : locales[ 0 ];
  }

  public Locale[] getLocales() {
    return locales == null ? new Locale[ 0 ] : locales.clone();
  }

  private final class InfoOperationHandler extends OperationHandler {
    @Override
    public void handleSet( Map<String, Object> properties ) {
      if( properties.containsKey( "timezoneOffset" ) ) {
        timezoneOffset = ( Integer )properties.get( "timezoneOffset" );
      }
    }
  }

}
