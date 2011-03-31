/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.resources;

import java.io.File;

import junit.framework.TestCase;


public class JSLibraryConcatenator_Test extends TestCase {
  
  public void testConcatenation() {
    char character = 'a';
    JSLibraryConcatenator concatenator = new JSLibraryConcatenator();

    concatenator.startJSConcatenation();
    concatenator.appendJSLibrary( new File( "library.js"), new int[] { character } );

    assertEquals( concatenator.getUncompressed()[ 0 ], character );
    assertEquals( concatenator.getUncompressed()[ 1 ], '\n' );
    assertEquals( 2, concatenator.getUncompressed().length );
    assertNotNull( concatenator.getHashCode() );
    assertNotNull( concatenator.getCompressed() );
  }

  public void testIgnoreConcatenation() {
    JSLibraryConcatenator concatenator = new JSLibraryConcatenator();

    concatenator.appendJSLibrary( new File( "library.js"), new int[] { 'a' } );
    
    assertEquals( 0, concatenator.getUncompressed().length );
  }
  
  public void testEmptyFileContent() {
    JSLibraryConcatenator concatenator = new JSLibraryConcatenator();
    
    concatenator.startJSConcatenation();
    concatenator.appendJSLibrary( new File( "library.js"), new int[ 0 ] );
    
    assertEquals( 0, concatenator.getUncompressed().length );
  }
  
  public void testIgnoreNonJSFiles() {
    JSLibraryConcatenator concatenator = new JSLibraryConcatenator();
    
    concatenator.startJSConcatenation();
    concatenator.appendJSLibrary( new File( "content.html"), new int[] { 'a' } );
    
    assertEquals( 0, concatenator.getUncompressed().length );    
  }
  
  public void testIgnoreAppendJSLibraryAfterGetHashCode() {
    JSLibraryConcatenator concatenator = new JSLibraryConcatenator();
    
    concatenator.startJSConcatenation();
    concatenator.getHashCode();
    concatenator.appendJSLibrary( new File( "content.js"), new int[] { 'a' } );
    
    assertEquals( 0, concatenator.getUncompressed().length );        
  }

  public void testIgnoreAppendJSLibraryAfterGetCompressed() {
    JSLibraryConcatenator concatenator = new JSLibraryConcatenator();
    
    concatenator.startJSConcatenation();
    concatenator.getCompressed();
    concatenator.appendJSLibrary( new File( "content.js"), new int[] { 'a' } );
    
    assertEquals( 0, concatenator.getUncompressed().length );        
  }
  
  public void testIgnoreAppendJSLibraryAfterGetUncompressed() {
    JSLibraryConcatenator concatenator = new JSLibraryConcatenator();
    
    concatenator.startJSConcatenation();
    concatenator.getUncompressed();
    concatenator.appendJSLibrary( new File( "content.js"), new int[] { 'a' } );
    
    assertEquals( 0, concatenator.getUncompressed().length );        
  }
}
