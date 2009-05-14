/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import javax.servlet.http.Cookie;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.service.*;


/**
 * An SettingStoreManager manages ISettingStores with the help of a
 * registered {@link ISettingStoreFactory}.
 */
public final class SettingStoreManager {

  private static final String COOKIE_NAME = "settingStore";
  private static final int COOKIE_MAX_AGE_SEC = 3600 * 24 * 90; // 3 months

  private static long last = System.currentTimeMillis();
  private static int instanceCount;

  private static ISettingStoreFactory factory = null;

  public synchronized static ISettingStore getStore() {
    ISessionStore session = ContextProvider.getSession();
    String storeId = getStoreId();
    ISettingStore result
      = ( ISettingStore )session.getAttribute( storeId );
    if( result == null ) {
        result = factory.createSettingStore( storeId );
        session.setAttribute( storeId, result );
    }
    return result;
  }

  public synchronized static void register(
    final ISettingStoreFactory factory )
  {
    ParamCheck.notNull( factory, "factory" );
    if( factory != null && SettingStoreManager.factory != null ) {
      String msg = "There is already an ISettingStoreFactory registered.";
      throw new IllegalStateException( msg );
    }
    SettingStoreManager.factory = factory;
  }

  public synchronized static boolean hasFactory() {
    return SettingStoreManager.factory != null;
  }

  //////////////////
  // helping methods

  private static synchronized String createUniqueStoreId() {
    long now = System.currentTimeMillis();
    if( last == now ) {
      instanceCount++;
    } else {
      last = now;
      instanceCount = 0;
    }
    return String.valueOf( now ) + "_" + String.valueOf( instanceCount );
  }

  private static String getStoreId() {
    ISessionStore session = ContextProvider.getSession();
    // 1. storeId stored in session? (implies cookie exists)
    String result = ( String )session.getAttribute( COOKIE_NAME );
    if( result == null ) {
      // 2. storeId stored in cookie?
      result = getStoreIdFromCookie();
      if( result == null ) {
        // 3. create new storeId
        result = createUniqueStoreId();
      }
      // (2+3) do refresh cookie, to ensure it expires in COOKIE_MAX_AGE_SEC
      Cookie cookie = new Cookie( COOKIE_NAME, result );
      cookie.setSecure( RWT.getRequest().isSecure() );
      cookie.setMaxAge( COOKIE_MAX_AGE_SEC );
      ContextProvider.getResponse().addCookie( cookie );
      // (2+3) update storeId stored in session
      // Note: This attribute must be checked for validity to prevent attacks
      // like http://www.owasp.org/index.php/Cross-User_Defacement
      session.setAttribute( COOKIE_NAME, result );
    }
    return result;
  }

  private static String getStoreIdFromCookie() {
    String result = null;
    Cookie[] cookies = ContextProvider.getRequest().getCookies();
    if( cookies != null ) {
      for( int i = 0; result == null && i < cookies.length; i++ ) {
        Cookie cookie = cookies[ i ];
        if( COOKIE_NAME.equals( cookie.getName() ) ) {
          String value = cookie.getValue();
          // Validate cookies to prevent cookie manipulation and related attacks
          // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=275380
          if( isValidCookieValue( value ) ) {
            result = value;
          }
        }
      }
    }
    return result;
  }

  static boolean isValidCookieValue( final String value ) {
    boolean result = false;
    try {
      int index = value.indexOf( '_' );
      if( index != -1 ) {
        Long.parseLong( value.substring( 0, index ) );
        Integer.parseInt( value.substring( index + 1 ) );
        result = true;
      }
    } catch( Exception e ) {
      // Cookie format is invalid
    }
    return result;
  }
}
