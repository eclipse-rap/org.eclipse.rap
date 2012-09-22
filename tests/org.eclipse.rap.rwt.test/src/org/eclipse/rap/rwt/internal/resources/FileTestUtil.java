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
package org.eclipse.rap.rwt.internal.resources;

import java.io.File;

import org.eclipse.rap.rwt.internal.application.ApplicationContextUtil;


public class FileTestUtil {

  public static File createTempDir() {
    File globalTmpDir = new File( System.getProperty( "java.io.tmpdir" ) );
    String subDirName = "rap-test-" + Long.toHexString( System.currentTimeMillis() );
    File tmpDir = new File( globalTmpDir, subDirName );
    if( !tmpDir.mkdir() ) {
      String message = "Failed to create temp directory: " + tmpDir.getAbsolutePath();
      throw new IllegalStateException( message );
    }
    return tmpDir;
  }

  public static void delete( File toDelete ) {
    ApplicationContextUtil.delete( toDelete );
  }

}
