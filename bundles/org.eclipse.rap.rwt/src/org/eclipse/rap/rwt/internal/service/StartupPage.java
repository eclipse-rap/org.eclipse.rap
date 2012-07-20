/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.internal.resources.ResourceRegistry;
import org.eclipse.rap.rwt.internal.theme.*;
import org.eclipse.rap.rwt.internal.util.*;
import org.eclipse.rwt.RWT;


/**
 * A helping class that delivers the initial HTML page in order to bootstrap the client side.
 */
public final class StartupPage {

  private final StartupPageConfigurer configurer;

  public StartupPage( ResourceRegistry resourceRegistry ) {
    configurer = new StartupPageConfigurer( resourceRegistry );
  }

  public void addJsLibrary( String location ) {
    configurer.addJsLibrary( location );
  }

  void send() throws IOException {
    HttpServletResponse response = ContextProvider.getResponse();
    setResponseHeaders( response );
    StartupPageTemplateHolder template = configurer.getTemplate();
    processTemplate( template );
    writeTemplate( response, template );
  }

  private static void setResponseHeaders( HttpServletResponse response ) {
    response.setContentType( HTTP.CONTENT_TYPE_HTML );
    response.setCharacterEncoding( HTTP.CHARSET_UTF_8 );
    // TODO [rh] this is a preliminary fix for a behavior that was easily
    //      reproducible in IE but also happened in FF: when restarting a
    //      web app (hit return in location bar), the browser used a cached
    //      version of the index.html *without* sending a request to ask
    //      whether the cached page can be used.
    //      fix for bug 220733: append no-store to the Cache-Control header
    response.addHeader( "Cache-Control", "max-age=0, no-cache, must-revalidate, no-store" );
  }

  private static void processTemplate( StartupPageTemplateHolder template ) {
    template.replace( StartupPageTemplateHolder.VAR_BACKGROUND_IMAGE, getBgImage() );
  }

  private static String getBgImage() {
    String result = "";
    QxType value = ThemeUtil.getCssValue( "Display", "background-image", SimpleSelector.DEFAULT );
    if( value instanceof QxImage ) {
      QxImage image = ( QxImage )value;
      // path is null if non-existing image was specified in css file
      String resourceName = image.getResourcePath();
      if( resourceName != null ) {
        result = RWT.getResourceManager().getLocation( resourceName );
      }
    }
    return result;
  }

  private static void writeTemplate( HttpServletResponse response,
                                     StartupPageTemplateHolder template ) throws IOException
  {
    PrintWriter writer = response.getWriter();
    for( String token : template.getTokens() ) {
      if( token != null ) {
        writer.write( token );
      }
    }
  }

}
