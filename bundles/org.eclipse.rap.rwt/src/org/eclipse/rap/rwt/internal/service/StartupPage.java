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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.internal.RWTMessages;
import org.eclipse.rap.rwt.internal.application.ApplicationContext;
import org.eclipse.rap.rwt.internal.application.RWTFactory;
import org.eclipse.rap.rwt.internal.lifecycle.EntryPointUtil;
import org.eclipse.rap.rwt.internal.service.StartupPageTemplate.VariableWriter;
import org.eclipse.rap.rwt.internal.theme.QxImage;
import org.eclipse.rap.rwt.internal.theme.SimpleSelector;
import org.eclipse.rap.rwt.internal.theme.ThemeUtil;
import org.eclipse.rap.rwt.internal.util.HTTP;
import org.eclipse.rap.rwt.internal.util.ParamCheck;


public class StartupPage {
  private final ApplicationContext applicationContext;
  private final List<String> jsLibraries;
  private StartupPageTemplate startupPageTemplate;

  public StartupPage( ApplicationContext applicationContext ) {
    this.applicationContext = applicationContext;
    this.jsLibraries = new ArrayList<String>();
  }

  public void activate() {
    startupPageTemplate = createStartupPageTemplate();
  }

  public void deactivate() {
    startupPageTemplate = null;
  }

  public void addJsLibrary( String location ) {
    ParamCheck.notNull( location, "location" );
    jsLibraries.add( location );
  }

  void send( HttpServletResponse response ) throws IOException {
    setResponseHeaders( response );
    startupPageTemplate.writePage( response.getWriter(), new StartupPageValueProvider() );
  }

  static void setResponseHeaders( HttpServletResponse response ) {
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

  protected StartupPageTemplate createStartupPageTemplate() {
    return new StartupPageTemplate();
  }

  protected void writeTitle( PrintWriter printWriter ) {
    writeEntryPointproperty( printWriter, WebClient.PAGE_TITLE );
  }

  protected void writeBody( PrintWriter printWriter ) {
    writeEntryPointproperty( printWriter, WebClient.BODY_HTML );
  }

  protected void writeHead( PrintWriter printWriter ) {
    Map<String, String> properties = EntryPointUtil.getCurrentEntryPointProperties();
    String favIcon = properties.get( WebClient.FAVICON );
    if( favIcon != null && favIcon.length() > 0 ) {
      String pattern = "<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"%1$s\" />";
      String favIocnMarkup = String.format( pattern, getResourceLocation( favIcon ) );
      printWriter.write( favIocnMarkup );
    }
    writeEntryPointproperty( printWriter, WebClient.HEAD_HTML );
  }

  protected void writeLibraries( PrintWriter printWriter ) {
    for( String location : jsLibraries ) {
      writeScriptTag( printWriter, location );
    }
    writeScriptTag( printWriter, RWTFactory.getJSLibraryConcatenator().getLocation() );
  }

  protected void writeScriptTag( PrintWriter printWriter, String libraryLocation ) {
    if( libraryLocation != null ) {
      printWriter.write( "    <script type=\"text/javascript\" src=\"" );
      printWriter.write( libraryLocation );
      printWriter.write( "\" charset=\"" );
      printWriter.write( HTTP.CHARSET_UTF_8 );
      printWriter.write( "\"></script>\n" );
    }
  }

  protected void writeBackgroundImage( PrintWriter printWriter ) {
    printWriter.write( getBackgroundImageLocation() );
  }

  protected void writeNoScriptMessage( PrintWriter printWriter ) {
    String message = RWTMessages.getMessage( "RWT_NoScriptWarning" );
    printWriter.write( message );
  }

  protected void writeAppScript( PrintWriter printWriter ) {
    StringBuilder code = new StringBuilder();
    code.append( "rwt.protocol.MessageProcessor.processMessage( " );
    code.append( StartupJson.get() );
    code.append( ");/*EOM*/" );
    printWriter.write( code.toString() );
  }

  protected String getBackgroundImageLocation() {
    String result = "";
    QxImage image = getBrackgroundImage();
    String resourceName = image.getResourcePath();
    if( resourceName != null ) {
      result = getResourceLocation( resourceName );
    }
    return result;
  }

  protected QxImage getBrackgroundImage() {
    SimpleSelector defaultSelector = SimpleSelector.DEFAULT;
    return ( QxImage )ThemeUtil.getCssValue( "Display", "background-image", defaultSelector );
  }

  private String getResourceLocation( String resourceName ) {
    return applicationContext.getResourceManager().getLocation( resourceName );
  }

  private static void writeEntryPointproperty( PrintWriter printWriter, String property ) {
    Map<String, String> properties = EntryPointUtil.getCurrentEntryPointProperties();
    String title = properties.get( property );
    if( title != null ) {
      printWriter.write( title );
    }
  }

  private class StartupPageValueProvider implements VariableWriter {

    public void writeVariable( PrintWriter printWriter, String variableName ) {
      if( variableName.equals( StartupPageTemplate.TOKEN_LIBRARIES ) ) {
        writeLibraries( printWriter );
      } else if( variableName.equals( StartupPageTemplate.TOKEN_TITLE ) ) {
        writeTitle( printWriter );
      } else if( variableName.equals( StartupPageTemplate.TOKEN_BODY ) ) {
        writeBody( printWriter );
      } else if( variableName.equals( StartupPageTemplate.TOKEN_HEADERS ) ) {
        writeHead( printWriter );
      } else if( variableName.equals( StartupPageTemplate.TOKEN_BACKGROUND_IMAGE ) ) {
        writeBackgroundImage( printWriter );
      } else if( variableName.equals( StartupPageTemplate.TOKEN_NO_SCRIPT_MESSAGE ) ) {
        writeNoScriptMessage( printWriter );
      } else if( variableName.equals( StartupPageTemplate.TOKEN_APP_SCRIPT ) ) {
        writeAppScript( printWriter );
      } else {
        throw new IllegalArgumentException( "Unsupported variable: " + variableName );
      }
    }

  }

}
