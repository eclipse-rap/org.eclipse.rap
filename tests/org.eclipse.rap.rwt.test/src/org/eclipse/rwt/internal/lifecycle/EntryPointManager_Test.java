/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import static org.mockito.Mockito.mock;
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

  @Override
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

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testRegisterEntryPointByPath_nullPath() {
    try {
      entryPointManager.registerByPath( null, TestEntryPoint.class );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testRegisterEntryPointByPath_nullClass() {
    try {
      entryPointManager.registerByPath( NAME, ( Class<? extends IEntryPoint> )null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testRegisterEntryPointByPath_duplicate() {
    entryPointManager.registerByPath( NAME, entryPointFactory );
    try {
      entryPointManager.registerByPath( NAME, entryPointFactory );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testRegisterEntryPointByPath() {
    entryPointManager.registerByPath( NAME, TestEntryPoint.class );

    IEntryPointFactory factory = entryPointManager.getFactoryByPath( NAME );

    assertEquals( DefaultEntryPointFactory.class, factory.getClass() );
    assertEquals( TestEntryPoint.class, factory.create().getClass() );
  }

  public void testRegisterFactoryByPath_nullPath() {
    try {
      entryPointManager.registerByPath( null, entryPointFactory );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testRegisterFactoryByPath_nullFactory() {
    try {
      entryPointManager.registerByPath( NAME, ( IEntryPointFactory )null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testRegisterFactoryByPath() {
    entryPointManager.registerByPath( NAME, entryPointFactory );

    IEntryPointFactory factory = entryPointManager.getFactoryByPath( NAME );

    assertSame( entryPointFactory, factory );
  }

  public void testGetServletPaths_initallyEmpty() {
    assertTrue( entryPointManager.getServletPaths().isEmpty() );
  }

  public void testGetServletPaths() {
    entryPointManager.registerByPath( "foo", entryPointFactory );
    entryPointManager.registerByPath( "bar", entryPointFactory );

    assertEquals( 2, entryPointManager.getServletPaths().size() );
    assertTrue( entryPointManager.getServletPaths().contains( "foo" ) );
    assertTrue( entryPointManager.getServletPaths().contains( "bar" ) );
  }

  public void testRegisterEntryPointByName_nullName() {
    try {
      entryPointManager.registerByName( null, TestEntryPoint.class );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testRegisterEntryPointByName_nullClass() {
    try {
      entryPointManager.registerByName( NAME, ( Class<? extends IEntryPoint> )null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testRegisterEntryPointByName_duplicate() {
    entryPointManager.registerByName( NAME, TestEntryPoint.class );
    try {
      entryPointManager.registerByName( NAME, TestEntryPoint.class );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testRegisterEntryPointByName() {
    entryPointManager.registerByName( NAME, TestEntryPoint.class );

    IEntryPointFactory factory = entryPointManager.getFactoryByName( NAME );

    assertEquals( DefaultEntryPointFactory.class, factory.getClass() );
    assertEquals( TestEntryPoint.class, factory.create().getClass() );
  }

  public void testRegisterFactoryByName_nullPath() {
    try {
      entryPointManager.registerByName( null, entryPointFactory );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testRegisterFactoryByName_nullFactory() {
    try {
      entryPointManager.registerByName( NAME, ( IEntryPointFactory )null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testRegisterFactoryByName() {
    entryPointManager.registerByName( NAME, entryPointFactory );

    IEntryPointFactory factory = entryPointManager.getFactoryByName( NAME );

    assertSame( entryPointFactory, factory );
  }

  public void testGetEntryPointNames_initallyEmpty() {
    assertTrue( entryPointManager.getEntryPointNames().isEmpty() );
  }

  public void testEntryPointNames() {
    entryPointManager.registerByName( "foo", entryPointFactory );
    entryPointManager.registerByName( "bar", entryPointFactory );

    assertEquals( 2, entryPointManager.getEntryPointNames().size() );
    assertTrue( entryPointManager.getEntryPointNames().contains( "foo" ) );
    assertTrue( entryPointManager.getEntryPointNames().contains( "bar" ) );
  }

  public void testDeregisterByName_nullName() {
    try {
      entryPointManager.deregisterByName( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testDeregisterByName_nonExistingName() {
    try {
      entryPointManager.deregisterByName( "does.not.exist" );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testDeregisterByName() {
    entryPointManager.registerByName( NAME, entryPointFactory );
    entryPointManager.deregisterByName( NAME );

    assertTrue( entryPointManager.getEntryPointNames().isEmpty() );
  }

  public void testDeregisterAll() {
    entryPointManager.registerByPath( NAME, entryPointFactory );
    entryPointManager.registerByName( NAME, TestEntryPoint.class );
    entryPointManager.deregisterAll();

    assertTrue( entryPointManager.getServletPaths().isEmpty() );
    assertTrue( entryPointManager.getEntryPointNames().isEmpty() );
  }
}
