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
package org.eclipse.rap.rwt.internal.lifecycle;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.application.EntryPointFactory;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestRequest;


public class EntryPointManager_Test extends TestCase {

  private static final String PATH = "/entrypoint";
  private static final Integer RETURN_VALUE = Integer.valueOf( 123 );

  private EntryPointManager entryPointManager;
  private EntryPointFactory entryPointFactory;
  private EntryPoint entryPoint;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    mockEntryPoint();
    mockEntryPointFactory();
    entryPointManager = new EntryPointManager();
  }

  private void mockEntryPointFactory() {
    entryPointFactory = mock( EntryPointFactory.class );
    when( entryPointFactory.create() ).thenReturn( entryPoint );
  }

  private void mockEntryPoint() {
    entryPoint = mock( EntryPoint.class );
    when( Integer.valueOf( entryPoint.createUI() ) ).thenReturn( RETURN_VALUE );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testRegisterEntryPointByPath_nullPath() {
    try {
      entryPointManager.register( null, TestEntryPoint.class, null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testRegisterEntryPointByPath_nullClass() {
    try {
      entryPointManager.register( PATH, ( Class<? extends EntryPoint> )null, null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testRegisterEntryPointByPath_duplicate() {
    entryPointManager.register( PATH, TestEntryPoint.class, null );
    try {
      entryPointManager.register( PATH, TestEntryPoint.class, null );
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
    entryPointManager.register( PATH, TestEntryPoint.class, null );

    EntryPointFactory factory = entryPointManager.getRegistrationByPath( PATH ).getFactory();
    assertSame( DefaultEntryPointFactory.class, factory.getClass() );
    assertEquals( TestEntryPoint.class, factory.create().getClass() );
  }

  public void testRegisterEntryPointByPath_withProperties() {
    Map<String, String> map = new HashMap<String, String>();
    map.put( "foo", "bar" );

    entryPointManager.register( PATH, TestEntryPoint.class, map );

    assertEquals( map, entryPointManager.getRegistrationByPath( PATH ).getProperties() );
  }

  public void testRegisterFactoryByPath_nullPath() {
    try {
      entryPointManager.register( null, entryPointFactory, null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testRegisterFactoryByPath_duplicate() {
    entryPointManager.register( PATH, entryPointFactory, null );
    try {
      entryPointManager.register( PATH, entryPointFactory, null );
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
      entryPointManager.register( PATH, ( EntryPointFactory )null, null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testRegisterFactoryByPath() {
    entryPointManager.register( PATH, entryPointFactory, null );

    assertSame( entryPointFactory, entryPointManager.getRegistrationByPath( PATH ).getFactory() );
  }

  public void testRegisterFactoryByPath_withProperties() {
    Map<String, String> map = new HashMap<String, String>();
    map.put( "foo", "bar" );

    entryPointManager.register( PATH, entryPointFactory, map );

    assertEquals( map, entryPointManager.getRegistrationByPath( PATH ).getProperties() );
  }

  public void testGetRegistrationByPath_nonExisting() {
    assertNull( entryPointManager.getRegistrationByPath( PATH ) );
  }

  public void testGetRegistrationByPath_propertiesNotNull() {
    entryPointManager.register( PATH, entryPointFactory, null );

    assertNotNull( entryPointManager.getRegistrationByPath( PATH ).getProperties() );
    assertEquals( 0, entryPointManager.getRegistrationByPath( PATH ).getProperties().size() );
  }

  public void testGetRegistrationByPath_propertiesNotModifiable() {
    entryPointManager.register( PATH, entryPointFactory, new HashMap<String, String>() );
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
    entryPointManager.register( "/foo", entryPointFactory, null );
    entryPointManager.register( "/bar", entryPointFactory, null );

    assertEquals( 2, entryPointManager.getServletPaths().size() );
    assertTrue( entryPointManager.getServletPaths().contains( "/foo" ) );
    assertTrue( entryPointManager.getServletPaths().contains( "/bar" ) );
  }

  public void testDeregisterAll() {
    entryPointManager.register( PATH, entryPointFactory, null );

    entryPointManager.deregisterAll();

    assertTrue( entryPointManager.getServletPaths().isEmpty() );
  }

  public void testGetEntryPointRegistration() {
    HashMap<String, String> properties = new HashMap<String, String>();
    properties.put( "prop", "value" );
    entryPointManager.register( PATH, entryPointFactory, properties );
    TestRequest request = new TestRequest();
    request.setServletPath( PATH );

    EntryPointRegistration registration = entryPointManager.getEntryPointRegistration( request );

    assertNotNull( registration );
    assertEquals( "value", registration.getProperties().get( "prop" ) );
  }

  public void testGetEntryPointRegistrationForUnregisteredEntryPoint() {
    TestRequest request = new TestRequest();
    request.setServletPath( PATH );

    try {
      entryPointManager.getEntryPointRegistration( request );
      fail();
    } catch( Exception expected ) {
    }
  }

  private void assertRegisterByPathFails( String path, Class<? extends EntryPoint> type ) {
    try {
      entryPointManager.register( path, type, null );
      fail( "Exected to fail but succeeded" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  private void assertRegisterByPathFails( String path, EntryPointFactory factory ) {
    try {
      entryPointManager.register( path, factory, null );
      fail( "Exected to fail but succeeded" );
    } catch( IllegalArgumentException expected ) {
    }
  }

}
