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

package org.eclipse.swt.externalbrowser;

import junit.framework.TestCase;

import org.eclipse.swt.RWTFixture;


public class ExternalBrowser_Test extends TestCase {
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testOpen() {
    // Test illegal arguments
    try {
      ExternalBrowser.open( null, "http://nowhere.org", 0 );
      fail( "ExternalBrowser#open must not allow id == null" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      ExternalBrowser.open( "", "http://nowhere.org", 0 );
      fail( "ExternalBrowser#open must not allow id == empty string" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      ExternalBrowser.open( "myId", null, 0 );
      fail( "ExternalBrowser#open must not allow url == null" );
    } catch( NullPointerException e ) {
      // expected
    }
  }
  
  public void testClose() {
    // Test illegal arguments
    try {
      ExternalBrowser.close( null );
      fail( "ExternalBrowser#close must not allow id == null" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      ExternalBrowser.close( "" );
      fail( "ExternalBrowser#close must not allow id == empty string" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }
  
  public void testEscapeId() {
    String escapedId = ExternalBrowser.escapeId( "my.id" );
    assertEquals( -1, escapedId.indexOf( "." ) );
    
    String escapedId1 = ExternalBrowser.escapeId( "my_id" );
    String escapedId2 = ExternalBrowser.escapeId( "my.id" );
    assertFalse( escapedId1.equals( escapedId2 ) );
    
    escapedId1 = ExternalBrowser.escapeId( "my_id_0" );
    escapedId2 = ExternalBrowser.escapeId( "my.id_0" );
    assertFalse( escapedId1.equals( escapedId2 ) );
  }
}
