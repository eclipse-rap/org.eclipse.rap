/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.branding.BrandingUtil;
import org.eclipse.rwt.internal.engine.ApplicationContext;
import org.eclipse.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rwt.internal.lifecycle.JavaScriptResponseWriter;
import org.eclipse.rwt.internal.theme.*;
import org.eclipse.rwt.internal.util.*;


/**
 * A helping class that delivers the initial HTML page in order to bootstrap the client side.
 */
public final class StartupPage {
  private IStartupPageConfigurer configurer;

  public interface IStartupPageConfigurer {
    StartupPageTemplateHolder getTemplate() throws IOException;
    boolean isModifiedSince();
  }

  static void send() throws IOException {
    getInstance().doSend();
  }

  private static StartupPage getInstance() {
    return ( StartupPage )ApplicationContext.getSingleton( StartupPage.class );
  }

  void setConfigurer( IStartupPageConfigurer configurer ) {
    this.configurer = configurer;
  }

  private void doSend() throws IOException {
    ensureConfigurer();
    if( configurer.isModifiedSince() ) {
      // send out the survey
      render();
    } else {
      AbstractBranding branding = BrandingUtil.findBranding();
      if( branding.getThemeId() != null ) {
        ThemeUtil.setCurrentThemeId( branding.getThemeId() );
      }
    }
  }

  private void ensureConfigurer() {
    if( configurer == null ) {
      Class instanceType = RWTStartupPageConfigurer.class;
      configurer = ( IStartupPageConfigurer )ApplicationContext.getSingleton( instanceType );
    }
  }

  private void render() throws IOException {
    HttpServletResponse response = ContextProvider.getResponse();
    response.setContentType( HTTP.CONTENT_TEXT_HTML );
    response.setCharacterEncoding( HTTP.CHARSET_UTF_8 );
    StartupPageTemplateHolder template = configurer.getTemplate();
    template.replace( StartupPageTemplateHolder.VAR_BACKGROUND_IMAGE,
                      getBgImage() );
    // TODO [fappel]: check whether servletName has to be url encoded
    //                in case the client has switched of cookies
    template.replace( StartupPageTemplateHolder.VAR_SERVLET,
                      URLHelper.getServletName() );
    template.replace( StartupPageTemplateHolder.VAR_ENTRY_POINT,
                      EncodingUtil.encodeHTMLEntities( getEntryPoint() ) );
    String[] tokens = template.getTokens();
    JavaScriptResponseWriter responseWriter = getResponseWriter();
    for( int i = 0; i < tokens.length; i++ ) {
      if( tokens[ i ] != null ) {
        responseWriter.write( tokens[ i ] );
      }
    }
  }

  private String getBgImage() {
    String result = "";
    QxType cssValue = ThemeUtil.getCssValue( "Display",
                                             "background-image",
                                             SimpleSelector.DEFAULT );
    if( cssValue instanceof QxImage ) {
      QxImage image = ( QxImage )cssValue;
      // path is null if non-existing image was specified in css file
      String resourceName = image.getResourceName();
      if( resourceName != null ) {
        result = RWT.getResourceManager().getLocation( resourceName );
      }
    }
    return result;
  }

  private String getEntryPoint() {
    HttpServletRequest request = ContextProvider.getRequest();
    String result = request.getParameter( RequestParams.STARTUP );
    if( result == null ) {
      result = EntryPointManager.DEFAULT;
    }
    return result;
  }

  private JavaScriptResponseWriter getResponseWriter() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    return stateInfo.getResponseWriter();
  }

  private StartupPage() {
    // prevent instance creation
  }
}
