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
package org.eclipse.rwt.internal.service;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.branding.BrandingUtil;
import org.eclipse.rwt.internal.resources.ResourceRegistry;
import org.eclipse.rwt.internal.theme.*;
import org.eclipse.rwt.internal.util.*;


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
    if( configurer.isModifiedSince() ) {
      render();
    } else {
      AbstractBranding branding = BrandingUtil.determineBranding();
      if( branding.getThemeId() != null ) {
        ThemeUtil.setCurrentThemeId( branding.getThemeId() );
      }
    }
  }

  private void render() throws IOException {
    HttpServletResponse response = ContextProvider.getResponse();
    response.setContentType( HTTP.CONTENT_TYPE_HTML );
    response.setCharacterEncoding( HTTP.CHARSET_UTF_8 );
    StartupPageTemplateHolder template = configurer.getTemplate();
    template.replace( StartupPageTemplateHolder.VAR_BACKGROUND_IMAGE, getBgImage() );
    String[] tokens = template.getTokens();
    PrintWriter writer = response.getWriter();
    for( int i = 0; i < tokens.length; i++ ) {
      if( tokens[ i ] != null ) {
        writer.write( tokens[ i ] );
      }
    }
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

}
