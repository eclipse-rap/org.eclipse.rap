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
package org.eclipse.rap.rwt.cluster.testfixture.internal.util;

import java.io.File;
import java.io.IOException;


public class FileUtil {
  
  public static File getTempDir( String prefix ) {
    String baseTempDir = System.getProperty( "java.io.tmpdir" );
    File tempDir = new File( baseTempDir, prefix + "-temp" );
    try {
      return tempDir.getCanonicalFile();
    } catch( IOException ioe ) {
      throw new RuntimeException( "Failed to obtain temp directory.", ioe );
    }
  }

  public static void deleteDirectory( File file ) {
    if( file.isDirectory() ) {
      File[] files = file.listFiles();
      for( int i = 0; i < files.length; i++ ) {
        deleteDirectory( files[ i ] );
      }
    }
    if( !file.delete() ) {
      throw new RuntimeException( "Failed to delete file: " + file.getAbsolutePath() );
    }
  }

  private FileUtil() {
    // prevent instantiation
  }
}
