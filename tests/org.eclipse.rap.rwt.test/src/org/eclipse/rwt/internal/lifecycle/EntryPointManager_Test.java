/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
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

  private EntryPointManager entryPointManager;

  protected void setUp() throws Exception {
    Fixture.setUp();
    entryPointManager = new EntryPointManager();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testRegisterWithNullName() {
    try {
      entryPointManager.register( null, TestEntryPointWithLog.class );
      fail( "null-name not allowed" );
    } catch( NullPointerException expected ) {
    }
  }

  public void testRegisterWithNullClass() {
    try {
      entryPointManager.register( "xyz", null );
      fail( "null-class not allowed" );
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testRegisterWithNonEntryPointClass() {
    try {
      entryPointManager.register( "xyz", String.class );
      fail( "illegal entry point class" );
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testRegisterDuplicateEntryPoint() {
    entryPointManager.register( "xyz", TestEntryPointWithLog.class );
    try {
      entryPointManager.register( "xyz", TestEntryPointWithLog.class );
      fail( "register duplicate names not allowed" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testDeregisterWithNullName() {
    try {
      entryPointManager.deregister( null );
      fail( "deregister( null ) not allowed" );
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testDeregisterNonExistingEntryPoint() {
    try {
      entryPointManager.deregister( "does.not.exist.at.all" );
      fail( "deregister not allowed for unregistered entry points" );
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testDeregister() {
    entryPointManager.register( "abc", TestEntryPointWithLog.class );
    entryPointManager.deregister( "abc" );
    try {
      entryPointManager.createUI( "abc" );
      fail( "deregistering entry point failed" );
    } catch( RuntimeException expected ) {
    }
  }

  public void testDeregisterAll() {
    entryPointManager.register( "abc", TestEntryPointWithLog.class );
    entryPointManager.deregisterAll();
    try {
      entryPointManager.createUI( "abc" );
      fail( "deregistering entry point failed" );
    } catch( RuntimeException expected ) {
    }
  }

  public void testCreateUIWithNullName() {
    try {
      entryPointManager.createUI( null );
      fail( "createUI must be given a non-null name" );
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testCreateUIWithNonExistingEntryPointName() {
    try {
      entryPointManager.createUI( "does.not.exist" );
      fail( "cannot call createUI for non-existing entry point" );
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testCreateUI() {
    entryPointManager.register( EntryPointManager.DEFAULT, TestEntryPointWithLog.class );
    int returnVal = entryPointManager.createUI( EntryPointManager.DEFAULT );
    assertEquals( "isRunning", log );
    assertEquals( 123, returnVal );
    assertEquals( EntryPointManager.DEFAULT, EntryPointManager.getCurrentEntryPoint() );
  }
}