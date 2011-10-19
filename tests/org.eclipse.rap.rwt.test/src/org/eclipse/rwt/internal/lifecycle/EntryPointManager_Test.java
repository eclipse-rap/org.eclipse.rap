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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.lifecycle.DefaultEntryPointFactory;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.IEntryPointFactory;

public class EntryPointManager_Test extends TestCase {

  private static final String NAME = "entryPointName";
  private static final Integer RETURN_VALUE = Integer.valueOf( 123 );
  
  private EntryPointManager entryPointManager;
  private IEntryPointFactory entryPointFactory;
  private IEntryPoint entryPoint;
  
  public static class TestEntryPoint implements IEntryPoint {
    public int createUI() {
      return 0;
    }
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    mockEntryPoint();
    mockEntryPointFactory();
    entryPointManager = new EntryPointManager();
  }

  private void mockEntryPointFactory() {
    entryPointFactory = mock( IEntryPointFactory.class );
    when( entryPointFactory.create() ).thenReturn( entryPoint );
  }

  private void mockEntryPoint() {
    entryPoint = mock( IEntryPoint.class );
    when( Integer.valueOf( entryPoint.createUI() ) ).thenReturn( RETURN_VALUE );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testRegisterEntryPointWithNullParam() {
    try {
      entryPointManager.register( NAME, ( Class<? extends IEntryPoint> )null );
      fail( "null-entrypoint not allowed" );
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testDelegationOfEntryPointRegistration() {
    EntryPointManager entryPointManagerSpy = spy( entryPointManager );
    
    entryPointManagerSpy.register( NAME, TestEntryPoint.class );
    
    verify( entryPointManagerSpy ).register( eq( NAME ), any( DefaultEntryPointFactory.class ) );
  }
  
  public void testRegisterWithNullName() {
    try {
      entryPointManager.register( null, entryPointFactory );
      fail( "null-name not allowed" );
    } catch( NullPointerException expected ) {
    }
  }

  public void testRegisterWithNullFactory() {
    try {
      entryPointManager.register( NAME, ( IEntryPointFactory )null );
      fail( "null-factory not allowed" );
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testRegisterDuplicateEntryPoint() {
    entryPointManager.register( NAME, entryPointFactory );
    try {
      entryPointManager.register( NAME, entryPointFactory );
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
    entryPointManager.register( NAME, entryPointFactory );
    entryPointManager.deregister( NAME );
    try {
      entryPointManager.createUI( NAME );
      fail( "deregistering entry point failed" );
    } catch( RuntimeException expected ) {
    }
  }

  public void testDeregisterAll() {
    entryPointManager.register( NAME, entryPointFactory );
    entryPointManager.deregisterAll();
    try {
      entryPointManager.createUI( NAME );
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
    entryPointManager.register( EntryPointManager.DEFAULT, entryPointFactory );
    
    int returnVal = entryPointManager.createUI( EntryPointManager.DEFAULT );
    
    verify( entryPointFactory ).create();
    verify( entryPoint ).createUI();
    assertEquals( RETURN_VALUE.intValue(), returnVal );
    assertEquals( EntryPointManager.DEFAULT, EntryPointManager.getCurrentEntryPoint() );
  }
}