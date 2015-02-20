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

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.eclipse.rap.rwt.internal.service.ContextProvider.getRequest;
import static org.eclipse.rap.rwt.internal.theme.ThemeUtil.getThemeIdFor;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.client.ClientMessages;
import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.internal.remote.DeferredRemoteObject;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectImpl;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.internal.textsize.MeasurementUtil;
import org.eclipse.rap.rwt.internal.theme.Theme;
import org.eclipse.rap.rwt.internal.theme.ThemeManager;
import org.eclipse.rap.rwt.internal.util.HTTP;


public class StartupJson {

  static final String PROPERTY_URL = "url";
  static final String DISPLAY_TYPE = "rwt.widgets.Display";
  static final String THEME_STORE_TYPE = "rwt.theme.ThemeStore";
  static final String METHOD_LOAD_FALLBACK_THEME = "loadFallbackTheme";
  static final String METHOD_LOAD_ACTIVE_THEME = "loadActiveTheme";

  private StartupJson() {
    // prevent instantiation
  }

  static void send( HttpServletResponse response ) throws IOException {
    setResponseHeaders( response );
    get().writeTo( response.getWriter() );
  }

  static JsonObject get() {
    ProtocolMessageWriter writer = new ProtocolMessageWriter();
    appendClientMessages( writer );
    appendLoadThemeDefinitions( writer );
    appendCreateDisplay( "w1", writer );
    MeasurementUtil.appendStartupTextSizeProbe( writer );
    return writer.createMessage().toJson();
  }

  private static void appendClientMessages( ProtocolMessageWriter writer ) {
    ClientMessages clientMessages = RWT.getClient().getService( ClientMessages.class );
    if( clientMessages != null ) {
      clientMessages.update( RWT.getLocale() );
      renderRemoteObjects( writer );
    }
  }

  /*
   * The StartupJson is requested in the initial GET request where there is no lifecycle
   */
  private static void renderRemoteObjects( ProtocolMessageWriter writer ) {
    RemoteObjectRegistry registry = RemoteObjectRegistry.getInstance();
    for( RemoteObjectImpl remoteObject : registry.getRemoteObjects() ) {
      if( remoteObject instanceof DeferredRemoteObject ) {
        ( ( DeferredRemoteObject )remoteObject ).render( writer );
      }
    }
  }

  private static void setResponseHeaders( HttpServletResponse response ) {
    response.setContentType( HTTP.CONTENT_TYPE_JSON );
    response.setCharacterEncoding( HTTP.CHARSET_UTF_8 );
    response.addHeader( "Cache-Control", "max-age=0, no-cache, must-revalidate, no-store" );
    response.setHeader( "Pragma", "no-cache" );
    response.setDateHeader( "Expires", 0 );
  }

  private static void appendCreateDisplay( String id, ProtocolMessageWriter writer ) {
    writer.appendCreate( id, DISPLAY_TYPE );
    writer.appendHead( PROPERTY_URL, JsonValue.valueOf( getUrl() ) );
  }

  private static void appendLoadThemeDefinitions( ProtocolMessageWriter writer ) {
    ThemeManager themeManager = getApplicationContext().getThemeManager();
    Theme fallbackTheme = themeManager.getTheme( ThemeManager.FALLBACK_THEME_ID );
    appendLoadTheme( writer, METHOD_LOAD_FALLBACK_THEME, fallbackTheme );
    // Get current theme from the entry point registration - see bug 396065
    String servletPath = getRequest().getServletPath();
    Theme currentTheme = themeManager.getTheme( getThemeIdFor( servletPath ) );
    appendLoadTheme( writer, METHOD_LOAD_ACTIVE_THEME, currentTheme );
  }

  private static void appendLoadTheme( ProtocolMessageWriter writer, String method, Theme theme ) {
    JsonObject parameters = new JsonObject().add( "url", theme.getRegisteredLocation() );
    writer.appendCall( THEME_STORE_TYPE, method, parameters );
  }

  private static String getUrl() {
    String servletPath = getRequest().getServletPath();
    String url = "".equals( servletPath ) ? "./" : servletPath.substring( 1 );
    return ContextProvider.getResponse().encodeURL( url );
  }

}
