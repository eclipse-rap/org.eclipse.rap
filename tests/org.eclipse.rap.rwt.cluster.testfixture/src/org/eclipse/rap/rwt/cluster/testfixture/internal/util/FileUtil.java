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


public class FileUtil {
  
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
