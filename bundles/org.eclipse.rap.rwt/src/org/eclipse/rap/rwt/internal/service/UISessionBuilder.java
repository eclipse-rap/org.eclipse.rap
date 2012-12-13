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
package org.eclipse.rap.rwt.internal.service;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.internal.SingletonManager;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rap.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rap.rwt.internal.lifecycle.EntryPointRegistration;
import org.eclipse.rap.rwt.internal.theme.ThemeUtil;
import org.eclipse.rap.rwt.service.UISession;


public class UISessionBuilder {

  private final ApplicationContextImpl applicationContext;
  private final HttpServletRequest request;
  private final HttpSession httpSession;
  private final UISessionImpl uiSession;

  public UISessionBuilder( ApplicationContextImpl applicationContext, HttpServletRequest request ) {
    this.applicationContext = applicationContext;
    this.request = request;
    httpSession = request.getSession( true );
    uiSession = new UISessionImpl( httpSession );
  }

  public UISession buildUISession() {
    UISessionImpl.attachInstanceToSession( httpSession, uiSession );
    ApplicationContextUtil.set( uiSession, applicationContext );
    SingletonManager.install( uiSession );
    setCurrentTheme();
    selectClient();
    return uiSession;
  }

  private void setCurrentTheme() {
    Map<String, String> properties = getEntryPointProperties();
    String themeId = properties.get( WebClient.THEME_ID );
    if( themeId != null && themeId.length() > 0 ) {
      verifyThemeId( themeId );
      ThemeUtil.setCurrentThemeId( uiSession, themeId );
    } else {
      ThemeUtil.setCurrentThemeId( uiSession, RWT.DEFAULT_THEME_ID );
    }
  }

  private void selectClient() {
    applicationContext.getClientSelector().selectClient( request, uiSession );
  }

  private void verifyThemeId( String themeId ) {
    if( !applicationContext.getThemeManager().hasTheme( themeId ) ) {
      throw new IllegalArgumentException( "Illegal theme id: " + themeId );
    }
  }

  private Map<String, String> getEntryPointProperties() {
    Map<String, String> result = Collections.emptyMap();
    EntryPointManager entryPointManager = applicationContext.getEntryPointManager();
    // TODO [rh] silently ignore non-existing registration fow now, otherwise most tests would fail
    //      since they don't register an entry point
    String servletPath = request.getServletPath();
    EntryPointRegistration registration = entryPointManager.getRegistrationByPath( servletPath );
    if( registration != null ) {
      result = registration.getProperties();
    }
    return result;
  }

}
