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
package org.eclipse.rap.examples.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.application.Application;
import org.eclipse.rwt.application.Application.OperationMode;
import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.client.WebClient;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;


public class ExampleApplication implements ApplicationConfiguration {

  public void configure( Application application ) {
    Map<String, String> properties = new HashMap<String, String>();
    properties.put( WebClient.PAGE_TITLE, "RAP Examples" );
    properties.put( WebClient.BODY_HTML, readTextFromResource( "resources/body.html", "UTF-8" ) );
    properties.put( WebClient.FAVICON, "icons/favicon.png" );
    application.setOperationMode( OperationMode.SWT_COMPATIBILITY );
    application.addEntryPoint( "/examples", MainUi.class, properties );
    application.addStyleSheet( RWT.DEFAULT_THEME_ID, "theme/theme.css" );
    application.addResource( createResource( "icons/favicon.png" ) );
    application.addResource( createResource( "icons/loading.gif" ) );
  }

  private static IResource createResource( final String resourceName ) {
    return new IResource() {

      public boolean isJSLibrary() {
        return false;
      }

      public boolean isExternal() {
        return false;
      }

      public RegisterOptions getOptions() {
        return RegisterOptions.NONE;
      }

      public String getLocation() {
        return resourceName;
      }

      public ClassLoader getLoader() {
        return ExampleApplication.class.getClassLoader();
      }

      public String getCharset() {
        return null;
      }
    };
  }

  private static String readTextFromResource( String resourceName, String charset ) {
    String result;
    try {
      ClassLoader classLoader = ExampleApplication.class.getClassLoader();
      InputStream inputStream = classLoader.getResourceAsStream( resourceName );
      if( inputStream == null ) {
        throw new RuntimeException( "Resource not found: " + resourceName );
      }
      try {
        BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream, charset ) );
        StringBuilder stringBuilder = new StringBuilder();
        String line = reader.readLine();
        while( line != null ) {
          stringBuilder.append( line );
          stringBuilder.append( '\n' );
          line = reader.readLine();
        }
        result = stringBuilder.toString();
      } finally {
        inputStream.close();
      }
    } catch( IOException e ) {
      throw new RuntimeException( "Failed to read text from resource: " + resourceName );
    }
    return result;
  }
}
