/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.resources;

import java.io.File;
import java.net.URL;
import java.util.*;

import org.eclipse.rwt.internal.engine.RWTConfiguration;


/**
 * A helping class that provides functionality for finding the URLs of the classes and libraries of
 * the web application.
 */
class ContextURLs {
  private final RWTConfiguration configuration;
  private final URL[] contextURLs;

  ContextURLs( RWTConfiguration configuration ) {
    this.configuration = configuration;
    this.contextURLs = getContextURLs();
  }
  
  /**
   * return the URLs of the classes and libraries of the web application./</p>
   */
  URL[] get() {
    return contextURLs;
  }
  private URL[] getContextURLs() {
    List buffer = new LinkedList();
    buffer.add( getClassDirectoryURL() );
    buffer.addAll( getLibraryPaths() );
    URL[] result = new URL[ buffer.size() ];
    buffer.toArray( result );
    return result;
  }
  
  private Collection getLibraryPaths() {
    List result = new LinkedList();
    List pathList = getLibraryPath();
    for( int i = 0; i < pathList.size(); i++ ) {
      File libraryFile = ( File )pathList.get( i );
      URL library = getLibraryURL( libraryFile );
      result.add( library );
    }
    return result;
  }

  private URL getLibraryURL( File libraryFile ) {
    URL library;
    try {
      library = libraryFile.toURI().toURL();
    } catch( RuntimeException rte ) {
      throw rte;
    } catch( Exception shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
    return library;
  }

  private URL getClassDirectoryURL() {
    File classDirectory = configuration.getClassDirectory();
    URL result = getLibraryURL( classDirectory );
    return result;
  }
  
  private List getLibraryPath() {
    List result = new LinkedList();
    File libDir = configuration.getLibraryDirectory();
    String[] libraryNames = libDir.list();
    if( libraryNames != null ) {
      for( int i = 0; i < libraryNames.length; i++ ) {
        if( libraryNames[ i ].endsWith( ".jar" ) ) {
          result.add( new File( libDir.toString()  + File.separator + libraryNames[ i ] ) );
        }
      }
    }
    return result;
  }
}