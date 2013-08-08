/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Example for a utility that helps reading script file contents from the class path.
 */
public class ResourceLoaderUtil {

  private static final String CHARSET = "UTF-8";
  private static final ClassLoader CLASSLOADER = ResourceLoaderUtil.class.getClassLoader();

  private ResourceLoaderUtil() {
    // prevent instantiation
  }

  /**
   * Reads the content of a given text resource.
   *
   * @param resource the fully qualified name of the resource, without leading slash
   * @return the contents of the resource as string
   * @throws IllegalArgumentException if the resource could not be read
   */
  public static String readTextContent( String resource ) {
    try {
      return readTextContentChecked( resource );
    } catch( IOException e ) {
      throw new IllegalArgumentException( "Failed to read resource: " + resource );
    }
  }

  private static String readTextContentChecked( String resource ) throws IOException {
    InputStream stream = CLASSLOADER.getResourceAsStream( resource );
    if( stream == null ) {
      throw new IllegalArgumentException( "Resource not found: " + resource );
    }
    try {
      BufferedReader reader = new BufferedReader( new InputStreamReader( stream, CHARSET ) );
      return readLines( reader );
    } finally {
      stream.close();
    }
  }

  private static String readLines( BufferedReader reader ) throws IOException {
    StringBuilder builder = new StringBuilder();
    String line = reader.readLine();
    while( line != null ) {
      builder.append( line );
      builder.append( '\n' );
      line = reader.readLine();
    }
    return builder.toString();
  }

}
