/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.branding.BrandingUtil;
import org.eclipse.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rwt.internal.lifecycle.HtmlResponseWriter;
import org.eclipse.rwt.internal.theme.*;
import org.eclipse.rwt.internal.util.*;


/**
 * <p>A helping class that loads a special html page in order to
 * bootstrap the client-side session.</p>
 */
public final class StartupPage {

  public interface IStartupPageConfigurer {
    StartupPageTemplateHolder getTemplate() throws IOException;
    boolean isModifiedSince();
  }

  public static IStartupPageConfigurer configurer
    = new RWTStartupPageConfigurer();

  static void send() throws IOException {
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

  private static String getBgImage() {
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

  private static void render() throws IOException {
    ContextProvider.getResponse().setContentType( HTML.CONTENT_TEXT_HTML );
    StartupPageTemplateHolder template = configurer.getTemplate();
    template.replace( StartupPageTemplateHolder.VAR_BACKGROUND_IMAGE, 
                      getBgImage() );
    // TODO [fappel]: check whether servletName has to be url encoded
    //                in case the client has switched of cookies
    template.replace( StartupPageTemplateHolder.VAR_SERVLET, 
                      URLHelper.getSerlvetName() );
    template.replace( StartupPageTemplateHolder.VAR_ENTRY_POINT,
                      EntitiesUtil.encodeHTMLEntities( getEntryPoint() ) );
    String[] tokens = template.getTokens();
    for( int i = 0; i < tokens.length; i++ ) {
      if( tokens[ i ] != null ) {
        getResponseWriter().append( tokens[ i ] );
      }
    }
  }

  private static String getEntryPoint() {
    HttpServletRequest request = ContextProvider.getRequest();
    String result = request.getParameter( RequestParams.STARTUP );
    if( result == null ) {
      result = EntryPointManager.DEFAULT;
    }
    return result;
  }

  private static HtmlResponseWriter getResponseWriter() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    return stateInfo.getResponseWriter();
  }
}