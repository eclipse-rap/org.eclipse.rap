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
package org.eclipse.rwt.internal.lifecycle;

import junit.framework.TestCase;

import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.RWTFixture;

public class EntryPointManager_Test extends TestCase {

  public static String log = "";

  public static class TestEntryPointWithLog implements IEntryPoint {
    public int createUI() {
      log = "isRunning";
      return 123;
    }
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testRegister() {
    try {
      EntryPointManager.register( null, TestEntryPointWithLog.class );
      fail( "null-name not allowed" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      EntryPointManager.register( "xyz", null );
      fail( "null-class not allowed" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      EntryPointManager.register( "xyz", String.class );
      fail( "illegal entry point class" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      EntryPointManager.register( "xyz", TestEntryPointWithLog.class );
      EntryPointManager.register( "xyz", TestEntryPointWithLog.class );
      fail( "register duplicate names not allowed" );
    } catch( IllegalArgumentException e ) {
      EntryPointManager.deregister( "xyz" );
    }
  }

  public void testDeregister() {
    try {
      EntryPointManager.deregister( null );
      fail( "deregister( null ) not allowed" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      EntryPointManager.deregister( "does.not.exist.at.all" );
      fail( "deregister not allowed for unregistered entry points" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    EntryPointManager.register( "abc", TestEntryPointWithLog.class );
    EntryPointManager.deregister( "abc" );
    try {
      EntryPointManager.createUI( "abc" );
      fail( "deregistering entry point failed" );
    } catch( RuntimeException e ) {
    }
  }

  public void testCreateUI() {
    try {
      EntryPointManager.createUI( null );
      fail( "createUI must be given a non-null name" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      EntryPointManager.createUI( "does.not.exist" );
      fail( "cannot call createUI for non-existing entry point" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    EntryPointManager.register( EntryPointManager.DEFAULT,
                                TestEntryPointWithLog.class );
    int returnVal = EntryPointManager.createUI( EntryPointManager.DEFAULT );
    assertEquals( "isRunning", log );
    assertEquals( 123, returnVal );
    
    String currentEntryPoint = EntryPointManager.getCurrentEntryPoint();
    assertEquals( EntryPointManager.DEFAULT, currentEntryPoint );
    
    EntryPointManager.deregister( EntryPointManager.DEFAULT );
  }
}
