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

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.lifecycle.DefaultEntryPointFactory;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.IEntryPointFactory;


public class EntryPointManager_Test extends TestCase {

  private static final String NAME = "entryPointName";
  private static final String PATH = "/entrypoint";
  private static final Integer RETURN_VALUE = Integer.valueOf( 123 );

  private EntryPointManager entryPointManager;
  private IEntryPointFactory entryPointFactory;
  private IEntryPoint entryPoint;

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
      entryPointManager.registerByPath( null, TestEntryPoint.class, null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testRegisterEntryPointByPath_nullClass() {
    try {
      entryPointManager.registerByPath( PATH, ( Class<? extends IEntryPoint> )null, null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testRegisterEntryPointByPath_duplicate() {
    entryPointManager.registerByPath( PATH, TestEntryPoint.class, null );
    try {
      entryPointManager.registerByPath( PATH, TestEntryPoint.class, null );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testRegisterEntryPointByPath_illegalPath() {
    Class<TestEntryPoint> entryPointClass = TestEntryPoint.class;
    assertRegisterByPathFails( "", entryPointClass );
    assertRegisterByPathFails( "/", entryPointClass );
    assertRegisterByPathFails( "foo", entryPointClass );
    assertRegisterByPathFails( "/foo/", entryPointClass );
    assertRegisterByPathFails( "/foo/bar", entryPointClass );
  }

  public void testRegisterEntryPointByPath() {
    entryPointManager.registerByPath( PATH, TestEntryPoint.class, null );

    IEntryPointFactory factory = entryPointManager.getRegistrationByPath( PATH ).getFactory();
    assertSame( DefaultEntryPointFactory.class, factory.getClass() );
    assertEquals( TestEntryPoint.class, factory.create().getClass() );
  }

  public void testRegisterEntryPointByPath_withProperties() {
    Map<String, String> map = new HashMap<String, String>();
    map.put( "foo", "bar" );

    entryPointManager.registerByPath( PATH, TestEntryPoint.class, map );

    assertEquals( map, entryPointManager.getRegistrationByPath( PATH ).getProperties() );
  }

  public void testRegisterFactoryByPath_nullPath() {
    try {
      entryPointManager.registerByPath( null, entryPointFactory, null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testRegisterFactoryByPath_duplicate() {
    entryPointManager.registerByPath( PATH, entryPointFactory, null );
    try {
      entryPointManager.registerByPath( PATH, entryPointFactory, null );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testRegisterFactoryByPath_illegalPath() {
    assertRegisterByPathFails( "", entryPointFactory );
    assertRegisterByPathFails( "/", entryPointFactory );
    assertRegisterByPathFails( "foo", entryPointFactory );
    assertRegisterByPathFails( "/foo/", entryPointFactory );
    assertRegisterByPathFails( "/foo/bar", entryPointFactory );
  }

  public void testRegisterFactoryByPath_nullFactory() {
    try {
      entryPointManager.registerByPath( PATH, ( IEntryPointFactory )null, null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testRegisterFactoryByPath() {
    entryPointManager.registerByPath( PATH, entryPointFactory, null );

    assertSame( entryPointFactory, entryPointManager.getRegistrationByPath( PATH ).getFactory() );
  }

  public void testRegisterFactoryByPath_withProperties() {
    Map<String, String> map = new HashMap<String, String>();
    map.put( "foo", "bar" );

    entryPointManager.registerByPath( PATH, entryPointFactory, map );

    assertEquals( map, entryPointManager.getRegistrationByPath( PATH ).getProperties() );
  }

  public void testGetRegistrationByPath_nonExisting() {
    assertNull( entryPointManager.getRegistrationByPath( PATH ) );
  }

  public void testGetRegistrationByPath_propertiesNotNull() {
    entryPointManager.registerByPath( PATH, entryPointFactory, null );

    assertNotNull( entryPointManager.getRegistrationByPath( PATH ).getProperties() );
    assertEquals( 0, entryPointManager.getRegistrationByPath( PATH ).getProperties().size() );
  }

  public void testGetRegistrationByPath_propertiesNotModifiable() {
    entryPointManager.registerByPath( PATH, entryPointFactory, new HashMap<String, String>() );
    Map<String, String> properties = entryPointManager.getRegistrationByPath( PATH ).getProperties();

    try {
      properties.put( "foo", "bar" );
      fail();
    } catch( Exception e ) {
      // expected
    }
  }

  public void testGetServletPaths_initallyEmpty() {
    assertTrue( entryPointManager.getServletPaths().isEmpty() );
  }

  public void testGetServletPaths() {
    entryPointManager.registerByPath( "/foo", entryPointFactory, null );
    entryPointManager.registerByPath( "/bar", entryPointFactory, null );

    assertEquals( 2, entryPointManager.getServletPaths().size() );
    assertTrue( entryPointManager.getServletPaths().contains( "/foo" ) );
    assertTrue( entryPointManager.getServletPaths().contains( "/bar" ) );
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

  public void testGetFactoryByName_nonExisting() {
    assertNull( entryPointManager.getFactoryByName( NAME ) );
  }

  public void testDeregisterAll() {
    entryPointManager.registerByPath( PATH, entryPointFactory, null );
    entryPointManager.registerByName( NAME, TestEntryPoint.class );

    entryPointManager.deregisterAll();

    assertTrue( entryPointManager.getServletPaths().isEmpty() );
    assertNull( entryPointManager.getRegistrationByPath( NAME ) );
    assertNull( entryPointManager.getFactoryByName( NAME ) );
  }

  private void assertRegisterByPathFails( String path, Class<? extends IEntryPoint> type ) {
    try {
      entryPointManager.registerByPath( path, type, null );
      fail( "Exected to fail but succeeded" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  private void assertRegisterByPathFails( String path, IEntryPointFactory factory ) {
    try {
      entryPointManager.registerByPath( path, factory, null );
      fail( "Exected to fail but succeeded" );
    } catch( IllegalArgumentException expected ) {
    }
  }
}
