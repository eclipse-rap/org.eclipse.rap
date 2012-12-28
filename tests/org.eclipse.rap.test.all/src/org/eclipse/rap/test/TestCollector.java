/*******************************************************************************
* Copyright (c) 2009, 2012 EclipseSource and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.rap.test;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.eclipse.rap.rwt.testfixture.Fixture;


public class TestCollector {

  private static final String JAR_EXTENSION = ".jar";
  private static final String CLASS_EXTENSION = ".class";

  private final URLClassLoader classLoader;
  private final List<Thread> threads;
  private final List<Throwable> problems;
  private final List<Class<?>> tests;

  TestCollector() {
    classLoader = ( URLClassLoader )getClass().getClassLoader();
    threads = new Vector<Thread>();
    problems = new Vector<Throwable>();
    tests = new Vector<Class<?>>();
  }

  Class<?>[] collectTests() {
    initializeFixture();
    try {
      scanClasspath();
    } catch( IOException ioe ) {
      throw new RuntimeException( ioe );
    }
    return sortTests();
  }

  protected boolean acceptPath( File file ) {
    return file.toString().contains( ".test" );
  }

  protected boolean acceptClassName( String className ) {
    return className.endsWith( "_Test" );
  }

  private void scanClasspath() throws IOException {
    URL[] urls = classLoader.getURLs();
    for( int i = 0; i < urls.length; i++ ) {
      File file = new File( URLDecoder.decode( urls[ i ].getFile(), "UTF-8" ) );
      if( file.exists() && acceptPath( file ) ) {
        runScan( file );
      }
    }
    waitForScan();
  }

  private void waitForScan() {
    while( !threads.isEmpty() ) {
      Thread thread = threads.remove( 0 );
      try {
        thread.join();
      } catch( InterruptedException ie ) {
        throw new RuntimeException( ie );
      }
    }
    handleProblems();
  }

  private void handleProblems() {
    if( !problems.isEmpty() ) {
      Iterator<Throwable> iterator = problems.iterator();
      while( iterator.hasNext() ) {
        Throwable exception = iterator.next();
        exception.printStackTrace();
      }
      throw new RuntimeException( "Unable to start test suite, see stacktraces." );
    }
  }

  private void runScan( final File file ) {
    Thread thread = new Thread( new Runnable() {
      public void run() {
        try {
          scan( file );
        } catch( Throwable thr ) {
          problems.add( thr );
        }
      }
    } );
    threads.add( thread );
    thread.setDaemon( true );
    thread.start();
  }

  private void scan( File file ) throws IOException {
    if( file.getName().endsWith( JAR_EXTENSION ) ) {
      scanJar( file );
    } else {
      scanDirectory( file, "", file.getPath() );
    }
  }

  private void scanJar( File file ) throws IOException {
    JarInputStream inputStream = new JarInputStream( new FileInputStream( file ), false );
    try {
      JarEntry jarEntry = inputStream.getNextJarEntry();
      while( jarEntry != null ) {
        if( isClassEntry( jarEntry ) ) {
          String className = toClassName( jarEntry );
          addClass( className );
        }
        jarEntry = inputStream.getNextJarEntry();
      }
    } finally {
      inputStream.close();
    }
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
      addClass( className );
    }
  }

  private void addClass( String className ) {
    if( acceptClassName( className ) ) {
      try {
        tests.add( classLoader.loadClass( className ) );
      } catch( ClassNotFoundException cnfe ) {
        throw new RuntimeException( cnfe );
      }
    }
  }

  private Class<?>[] sortTests() {
    Class<?>[] result = new Class[ tests.size() ];
    tests.toArray( result );
    Arrays.sort( result, new Comparator<Class<?>>() {
      public int compare( Class<?> test1, Class<?> test2 ) {
        return test1.getName().compareTo( test2.getName() );
      }
    } );
    return result;
  }

  private static void initializeFixture() {
    Fixture.setUp();
    Fixture.tearDown();
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
