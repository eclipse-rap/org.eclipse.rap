/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 *******************************************************************************/

package org.eclipse.rap.ui.tests;

import java.io.File;

import junit.framework.TestCase;

public class Cleanup extends TestCase {
  
  public void testCleanup() {
    File workspace = new File( "workspace" );
    if( workspace.exists() ) {
      deleteDirectory( workspace);
    }
  }
  
  private boolean deleteDirectory( final File path ) {
    if( path.exists() ) {
      File[] files = path.listFiles();
      for( int i = 0; i < files.length; i++ ) {
         if( files[ i ].isDirectory() ) {
           deleteDirectory( files[ i ] );
         } else {
           files[ i ].delete();
         }
      }
    }
    return( path.delete() );
  }


}
