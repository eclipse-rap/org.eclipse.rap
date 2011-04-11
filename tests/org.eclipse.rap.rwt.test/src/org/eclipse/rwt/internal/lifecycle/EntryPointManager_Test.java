/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
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

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.lifecycle.IEntryPoint;

public class EntryPointManager_Test extends TestCase {

  public static String log = "";

  public static class TestEntryPointWithLog implements IEntryPoint {
    public int createUI() {
      log = "isRunning";
      return 123;
    }
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testRegisterWithNullName() {
    EntryPointManager entryPointManager = new EntryPointManager();
    try {
      entryPointManager.register( null, TestEntryPointWithLog.class );
      fail( "null-name not allowed" );
    } catch( NullPointerException expected ) {
    }
  }

  public void testRegisterWithNullClass() {
    EntryPointManager entryPointManager = new EntryPointManager();
    try {
      entryPointManager.register( "xyz", null );
      fail( "null-class not allowed" );
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testRegisterWithNonEntryPointClass() {
    EntryPointManager entryPointManager = new EntryPointManager();
    try {
      entryPointManager.register( "xyz", String.class );
      fail( "illegal entry point class" );
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testRegisterDuplicateEntryPoint() {
    EntryPointManager entryPointManager = new EntryPointManager();
    entryPointManager.register( "xyz", TestEntryPointWithLog.class );
    try {
      entryPointManager.register( "xyz", TestEntryPointWithLog.class );
      fail( "register duplicate names not allowed" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testDeregisterWithNullName() {
    EntryPointManager entryPointManager = new EntryPointManager();
    try {
      entryPointManager.deregister( null );
      fail( "deregister( null ) not allowed" );
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testDeregisterNonExistingEntryPoint() {
    EntryPointManager entryPointManager = new EntryPointManager();
    try {
      entryPointManager.deregister( "does.not.exist.at.all" );
      fail( "deregister not allowed for unregistered entry points" );
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testDeregister() {
    EntryPointManager entryPointManager = new EntryPointManager();
    entryPointManager.register( "abc", TestEntryPointWithLog.class );
    entryPointManager.deregister( "abc" );
    try {
      entryPointManager.createUI( "abc" );
      fail( "deregistering entry point failed" );
    } catch( RuntimeException expected ) {
    }
  }

  public void testCreateUIWithNullName() {
    EntryPointManager entryPointManager = new EntryPointManager();
    try {
      entryPointManager.createUI( null );
      fail( "createUI must be given a non-null name" );
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testCreateUIWithNonExistingEntryPointName() {
    EntryPointManager entryPointManager = new EntryPointManager();
    try {
      entryPointManager.createUI( "does.not.exist" );
      fail( "cannot call createUI for non-existing entry point" );
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testCreateUI() {
    EntryPointManager entryPointManager = new EntryPointManager();
    entryPointManager.register( EntryPointManager.DEFAULT, TestEntryPointWithLog.class );
    int returnVal = entryPointManager.createUI( EntryPointManager.DEFAULT );
    assertEquals( "isRunning", log );
    assertEquals( 123, returnVal );
    assertEquals( EntryPointManager.DEFAULT, EntryPointManager.getCurrentEntryPoint() );
  }
}
