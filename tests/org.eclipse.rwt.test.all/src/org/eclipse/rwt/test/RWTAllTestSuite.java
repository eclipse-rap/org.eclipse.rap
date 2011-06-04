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
import java.net.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import junit.framework.*;

import org.eclipse.rwt.Fixture;


public class RWTAllTestSuite {
  private static final String JAR_EXTENSION = ".jar";
  private static final String CLASS_EXTENSION = ".class";

  public static Test suite() {
    return new RWTAllTestSuite().createSuite();
  }
  
  private final URLClassLoader classLoader;
  private final List<Thread> threads;
  private final List<IOException> problems;
  private final List<Class<? extends TestCase>> tests;

  RWTAllTestSuite() {
    classLoader = ( URLClassLoader )getClass().getClassLoader();
    threads = new Vector<Thread>();
    problems = new Vector<IOException>();
    tests = new Vector<Class<? extends TestCase>>();
  }
  
  Test createSuite() {
    initializeFixture();
    try {
      scanClasspath();
    } catch( IOException ioe ) {
      throw new RuntimeException( ioe );
    }
    return createSortedSuite();
  }

  private void initializeFixture() {
    Fixture.setUp();
    Fixture.tearDown();
  }

  private TestSuite createSortedSuite() {
    Class<? extends TestCase>[] testCases = sortTests();
    return createSuite( testCases );
  }

  private TestSuite createSuite( Class<? extends TestCase>[] testCases ) {
    TestSuite result = new TestSuite( "RWT Test Suite" );
    for( Class<? extends TestCase> testClass : testCases ) {
      result.addTestSuite( testClass );
    }
    return result;
  }

  private Class<? extends TestCase>[] sortTests() {
    @SuppressWarnings("unchecked")
    Class<? extends TestCase>[] testCases = new Class[ tests.size() ];
    tests.toArray( testCases );
    Arrays.sort( testCases, new Comparator<Class<? extends TestCase>>() {
      public int compare( Class<? extends TestCase> o1, Class<? extends TestCase> o2 ) {
        return o1.getName().compareTo( o2.getName() );
      }
    } );
    return testCases;
  }

  private void scanClasspath() throws IOException {
    URL[] urls = classLoader.getURLs();
    for( int i = 0; i < urls.length; i++ ) {
      File file = new File( URLDecoder.decode( urls[ i ].getFile(), "UTF-8" ) );
      if( file.exists() && isInInclusionList( file ) ) {
        runScan( file );
      }  
    }
    waitTillScanIsFinished();
  }

  private boolean isInInclusionList( File file ) {
    String[] exclusionList = new String[] {
      ".test"
    };
    boolean result = false;
    for( int i = 0; !result && i < exclusionList.length; i++ ) {
      result = file.toString().contains( exclusionList[ i ] );
    }
    return result;
  }

  private void waitTillScanIsFinished() {
    if( !threads.isEmpty() ) {
      synchronized( tests ) {
        try {
          tests.wait();
        } catch( InterruptedException shouldNotHappen ) {
          throw new RuntimeException( shouldNotHappen );
        }
      }
    }
    handleProblems();
  }

  private void handleProblems() {
    if( !problems.isEmpty() ) {
      Iterator<IOException> iterator = problems.iterator();
      while( iterator.hasNext() ) {
        iterator.next().printStackTrace();
      }
      String msg = "Unable to start testsuite due to IO problems, see stacktraces.";
      throw new RuntimeException( msg );
    }
  }

  private void runScan( final File file ) {
    Thread thread = new Thread( new Runnable() {
      public void run() {
        try {
          scan( file );
        } catch( IOException ioe ) {
          problems.add( ioe );
        }
        notifyFileScanComplete();
      }
    } );
    threads.add( thread );
    thread.setDaemon( true );
    thread.start();
  }
  
  private void notifyFileScanComplete() {
    threads.remove( 0 );
    if( threads.isEmpty() ) {
      synchronized( tests ) {
        tests.notify();
      }
    }
  }

  private void scan( final File file ) throws IOException {
    if( file.getName().endsWith( JAR_EXTENSION ) ) {
      scanJar( file );
    } else {
      scanDirectory( file, "", file.getPath() );
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

  // TODO [fappel]: Had a failure marker and solved this using the cast below.
  //                But anyway we should think of supporting JUnit4 test cases
  //                as well.
  @SuppressWarnings("unchecked") 
  private void addToSuite( String className ) {
    if( className.endsWith( "_Test" ) ) {
      try {
        synchronized( tests ) {
          tests.add( ( Class<? extends TestCase> )classLoader.loadClass( className ) );
        }
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