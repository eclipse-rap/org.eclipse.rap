/******************************************************************************* 
* Copyright (c) 2009, 2011 EclipseSource and others.
* All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.rwt.test;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import junit.framework.Test;
import junit.framework.TestSuite;


public class RWTAllTestSuite {
  private static final String JAR_EXTENSION = ".jar";
  private static final String CLASS_EXTENSION = ".class";

  public static Test suite() {
    return new RWTAllTestSuite().createSuite();
  }
  
  private final TestSuite suite;
  private final URLClassLoader classLoader;

  RWTAllTestSuite() {
    suite = new TestSuite( "RWT Test Suite" );
    classLoader = ( URLClassLoader )getClass().getClassLoader();
  }
  
  Test createSuite() {
    try {
      scanClasspath();
    } catch( IOException ioe ) {
      throw new RuntimeException( ioe );
    }
    return suite;
  }

  private void scanClasspath() throws IOException {
    URL[] urls = classLoader.getURLs();
    for( int i = 0; i < urls.length; i++ ) {
      File file = new File( urls[ i ].getFile() );
      if( file.exists() ) {
        if( file.getName().endsWith( JAR_EXTENSION ) ) {
          scanJar( file );
        } else {
          scanDirectory( file, "", file.getPath() );
        }
      }  
    }
  }

  private void scanJar( File file ) throws IOException {
    JarInputStream inputStream = new JarInputStream( new FileInputStream( file ), false );
    JarEntry jarEntry = inputStream.getNextJarEntry();
    while( jarEntry != null ) {
      if( isClassEntry( jarEntry ) ) {
        String className = toClassName( jarEntry );
        addToSuite( className );
      }
      jarEntry = inputStream.getNextJarEntry();
    }
    inputStream.close();
  }

  private void scanDirectory( File file, String initialPackagePath, String rootDirectory ) {
    if( file.isDirectory() ) {
      String packagePath = computePackagePath( file, initialPackagePath, rootDirectory );
      String[] files = file.list();
      for( int i = 0; i < files.length; i++ ) {
        File directory = new File( file, files[ i ] );
        scanDirectory( directory, packagePath, rootDirectory );
      }
    } else if( isClassFile( file ) ) {
      String className = toClassName( file, initialPackagePath );
      addToSuite( className );
    }
  }

  private static String computePackagePath( File file, String initialPackagePath, String rootDir ) {
    String result;
    if( file.getPath().equals( rootDir ) ) {
      result = "";
    } else if( initialPackagePath.length() == 0 ) {
      result = file.getName();
    } else {
      result = initialPackagePath + "." + file.getName();
    }
    return result;
  }

  private void addToSuite( String className ) {
    if( className.endsWith( "_Test" ) ) {
      try {
        suite.addTestSuite( classLoader.loadClass( className ) );
      } catch( ClassNotFoundException cnfe ) {
        throw new RuntimeException( cnfe );
      }
    }
  }

  private static String toClassName( JarEntry jarEntry ) {
    String result = removeClassExtension( jarEntry.getName() );
    result = result.replace( '/', '.' );
    return result;
  }

  private static String toClassName( File file, String packageName ) {
    String result = removeClassExtension( file.getName() );
    if( packageName.length() > 0 ) {
      result = packageName + "." + result;
    } 
    return result;
  }

  private static String removeClassExtension( String name ) {
    return name.substring( 0, name.lastIndexOf( CLASS_EXTENSION ) );
  }

  private static boolean isClassEntry( JarEntry jarEntry ) {
    return ( !jarEntry.isDirectory() ) && jarEntry.getName().endsWith( CLASS_EXTENSION );
  }

  private static boolean isClassFile( File file ) {
    return file.isFile() && file.getName().endsWith( CLASS_EXTENSION );
  }
}
