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

import junit.framework.TestCase;


public class JSLibraryConcatenator_Test extends TestCase {
  private static final char CHARACTER = 'a';

  private JSLibraryConcatenator concatenator;

  public void testConcatenation() {
    appendAndActivate( new byte[] { ( byte )CHARACTER } );

    assertEquals( concatenator.getContent()[ 0 ], CHARACTER );
    assertEquals( concatenator.getContent()[ 1 ], '\n' );
    assertEquals( 2, concatenator.getContent().length );
    assertNotNull( concatenator.getHashCode() );
  }

  public void testActivate() {
    appendAndActivate( new byte[] { ( byte )CHARACTER } );
    concatenator.deactivate();

    assertNull( concatenator.getContent() );
    assertNull( concatenator.getHashCode() );
  }

  public void testIgnoreConcatenation() {
    concatenator.appendJSLibrary( new byte[] { CHARACTER } );
    concatenator.activate();

    assertEquals( 0, concatenator.getContent().length );
  }

  public void testEmptyFileContent() {
    appendAndActivate( new byte[ 0 ] );

    assertEquals( 0, concatenator.getContent().length );
  }

  public void testIgnoreAppendJSLibraryAfterFinishJSConcatenation() {
    concatenator.startJSConcatenation();
    concatenator.activate();
    concatenator.appendJSLibrary( new byte[] { 'a' } );

    assertEquals( 0, concatenator.getContent().length );
  }

  protected void setUp() {
    concatenator = new JSLibraryConcatenator();
  }

  private void appendAndActivate( byte[] content ) {
    concatenator.startJSConcatenation();
    concatenator.appendJSLibrary( content );
    concatenator.activate();
  }
}