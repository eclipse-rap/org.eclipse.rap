/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.jstest.internal;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.theme.Theme;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.service.IServiceHandler;


@SuppressWarnings( "restriction" )
public class ClientResourcesServiceHandler implements IServiceHandler {

  public void service() throws IOException, ServletException {
    HttpServletResponse response = RWT.getResponse();
    response.setContentType( "text/javascript" );
    PrintWriter writer = response.getWriter();
    writer.write( "( function() {\n" );
    writeIncludeResource( writer, getClientLibraryLocation() );
    writeIncludeResource( writer, getThemeLocation() );
    writer.write( "} )();\n" );
  }

  private String getClientLibraryLocation() {
    IResourceManager resourceManager = RWTFactory.getResourceManager();
    return resourceManager.getLocation( "rap-client.js" );
  }

  private String getThemeLocation() {
    ThemeManager themeManager = RWTFactory.getThemeManager();
    Theme defaultTheme = themeManager.getTheme( ThemeManager.DEFAULT_THEME_ID );
    return defaultTheme.getRegisteredLocation();
  }

  private static void writeIncludeResource( PrintWriter writer, String resource ) {
    writer.write( "document.write( '<script src=\"" );
    writer.write( resource );
    writer.write( "?nocache=" );
    writer.write( Long.toString( System.currentTimeMillis() ) );
    writer.write( " type=\"text/javascript\"></script>' );\n" );
  }
}
