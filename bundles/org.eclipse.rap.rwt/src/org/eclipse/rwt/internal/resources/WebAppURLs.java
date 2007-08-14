/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.resources;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.eclipse.rwt.internal.IEngineConfig;


/** <p>A helping class that provides functionality for finding the URLs of
  * the classes and libraries of the web application./</p>
  */
public class WebAppURLs {
  
  /** finds the URLs of the classes and libraries of the web 
    * application./</p>*/
  public static List getWebAppURLs( final IEngineConfig config ) {
    List result = new Vector();
    // create the directory URL for classes
    try {
      File classesLib = config.getClassDir();
      result.add( classesLib.toURL() );
    } catch( Exception e ) {
      System.out.println( "Exception creating classes-directory URL in '" 
                        + WebAppURLs.class.getName() 
                        + ".getWebAppUrls( IEngineConfig )':\n" + e );
    }
    
    // create the path URLs for the librarys in the lib directory
    List pathList = getLibraryPath( config );
    for( int i = 0; i < pathList.size(); i++ ) {
      try {
        result.add( ( ( File )pathList.get( i ) ).toURL() );
      } catch( Exception e ) {
        System.out.println( "Exception creating URL in '" 
                          + WebAppURLs.class.getName() 
                          + ".getWebAppUrls( IEngineConfig )':\n" + e );
      }
    }    
    
    return result;
  }
  
  private static List getLibraryPath( final IEngineConfig config ) {
    List result = new Vector();
    File libDir = config.getLibDir();
    String[] libraryNames = libDir.list();
    if( libraryNames != null ) {
      for( int i = 0; i < libraryNames.length; i++ ) {
        if( libraryNames[ i ].endsWith( ".jar" ) ) {
          result.add( new File( libDir.toString() 
                              + File.separator
                              + libraryNames[ i ] ) );
        }
      }
    }
    return result;
  }
}