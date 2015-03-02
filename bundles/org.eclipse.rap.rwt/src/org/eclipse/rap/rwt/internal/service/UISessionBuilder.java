/*******************************************************************************
 * Copyright (c) 2012, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.internal.SingletonManager;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.client.ClientMessages;
import org.eclipse.rap.rwt.internal.textsize.MeasurementUtil;
import org.eclipse.rap.rwt.internal.theme.ThemeUtil;


public class UISessionBuilder {

  private final ServiceContext serviceContext;
  private final UISessionImpl uiSession;

  public UISessionBuilder( ServiceContext serviceContext ) {
    this.serviceContext = serviceContext;
    ApplicationContextImpl applicationContext = serviceContext.getApplicationContext();
    HttpServletRequest request = serviceContext.getRequest();
    HttpSession httpSession = request.getSession( true );
    String connectionId = request.getParameter( UrlParameters.PARAM_CONNECTION_ID );
    uiSession = new UISessionImpl( applicationContext, httpSession, connectionId );
  }

  public UISessionImpl buildUISession() {
    uiSession.attachToHttpSession();
    serviceContext.setUISession( uiSession );
    SingletonManager.install( uiSession );
    MeasurementUtil.installMeasurementOperator( uiSession );
    setCurrentTheme();
    selectClient();
    updateClientMessages();
    return uiSession;
  }

  private void setCurrentTheme() {
    String servletPath = serviceContext.getRequest().getServletPath();
    ThemeUtil.setCurrentThemeId( uiSession, ThemeUtil.getThemeIdFor( servletPath ) );
  }

  private void selectClient() {
    ApplicationContextImpl applicationContext = uiSession.getApplicationContext();
    applicationContext.getClientSelector().selectClient( serviceContext.getRequest(), uiSession );
  }

  private void updateClientMessages() {
    ClientMessages clientMessages = uiSession.getClient().getService( ClientMessages.class );
    if( clientMessages != null ) {
      clientMessages.update( uiSession.getLocale() );
    }
  }

}
