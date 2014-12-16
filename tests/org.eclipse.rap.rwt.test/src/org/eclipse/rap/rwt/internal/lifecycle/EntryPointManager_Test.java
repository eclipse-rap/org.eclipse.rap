/*******************************************************************************
 * Copyright (c) 2002, 2014 Innoopract Informationssysteme GmbH and others.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.application.EntryPointFactory;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class EntryPointManager_Test {

  private EntryPointManager entryPointManager;
  private EntryPointFactory entryPointFactory;
  private EntryPoint entryPoint;

  @Before
  public void setUp() {
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
    when( Integer.valueOf( entryPoint.createUI() ) ).thenReturn( Integer.valueOf( 23 ) );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test( expected = NullPointerException.class )
  public void testRegister_entryPoint_nullPath() {
    entryPointManager.register( null, TestEntryPoint.class, null );
  }

  @Test( expected = NullPointerException.class )
  public void testRegister_entryPoint_nullClass() {
    entryPointManager.register( "/foo", ( Class<? extends EntryPoint> )null, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRegister_entryPoint_duplicate() {
    entryPointManager.register( "/foo", TestEntryPoint.class, null );
    entryPointManager.register( "/foo", TestEntryPoint.class, null );
  }

  @Test
  public void testRegister_entryPoint_illegalPath() {
    Class<TestEntryPoint> entryPointClass = TestEntryPoint.class;
    assertRegisterFails( "", entryPointClass );
    assertRegisterFails( "foo", entryPointClass );
    assertRegisterFails( "/foo/", entryPointClass );
    assertRegisterFails( "/foo/bar", entryPointClass );
  }

  @Test
  public void testRegister_entryPoint() {
    entryPointManager.register( "/foo", TestEntryPoint.class, null );

    EntryPointFactory factory = entryPointManager.getRegistrationByPath( "/foo" ).getFactory();

    assertSame( DefaultEntryPointFactory.class, factory.getClass() );
    assertEquals( TestEntryPoint.class, factory.create().getClass() );
  }

  @Test
  public void testRegister_entryPoint_withRootPath() {
    entryPointManager.register( "/", TestEntryPoint.class, null );

    EntryPointFactory factory = entryPointManager.getRegistrationByPath( "/" ).getFactory();

    assertSame( DefaultEntryPointFactory.class, factory.getClass() );
    assertEquals( TestEntryPoint.class, factory.create().getClass() );
  }

  @Test
  public void testRegister_entryPoint_withProperties() {
    Map<String, String> map = new HashMap<String, String>();
    map.put( "foo", "bar" );

    entryPointManager.register( "/foo", TestEntryPoint.class, map );

    assertEquals( map, entryPointManager.getRegistrationByPath( "/foo" ).getProperties() );
  }

  @Test( expected = NullPointerException.class )
  public void testRegister_factory_nullPath() {
    entryPointManager.register( null, entryPointFactory, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRegister_factory_duplicate() {
    entryPointManager.register( "/foo", entryPointFactory, null );
    entryPointManager.register( "/foo", entryPointFactory, null );
  }

  @Test
  public void testRegister_factory_illegalPath() {
    assertRegisterFails( "", entryPointFactory );
    assertRegisterFails( "foo", entryPointFactory );
    assertRegisterFails( "/foo/", entryPointFactory );
    assertRegisterFails( "/foo/bar", entryPointFactory );
  }

  @Test( expected = NullPointerException.class )
  public void testRegister_factory_nullFactory() {
    entryPointManager.register( "/foo", ( EntryPointFactory )null, null );
  }

  @Test
  public void testRegister_factory() {
    entryPointManager.register( "/foo", entryPointFactory, null );

    assertSame( entryPointFactory, entryPointManager.getRegistrationByPath( "/foo" ).getFactory() );
  }

  @Test
  public void testRegister_factory_withRootPath() {
    entryPointManager.register( "/", entryPointFactory, null );

    assertSame( entryPointFactory, entryPointManager.getRegistrationByPath( "/" ).getFactory() );
  }

  @Test
  public void testRegister_factory_withProperties() {
    Map<String, String> map = new HashMap<String, String>();
    map.put( "foo", "bar" );

    entryPointManager.register( "/foo", entryPointFactory, map );

    assertEquals( map, entryPointManager.getRegistrationByPath( "/foo" ).getProperties() );
  }

  @Test
  public void testGetRegistrationByPath_nonExisting() {
    assertNull( entryPointManager.getRegistrationByPath( "/foo" ) );
  }

  @Test
  public void testGetRegistrationByPath_propertiesNotNull() {
    entryPointManager.register( "/foo", entryPointFactory, null );

    EntryPointRegistration registration = entryPointManager.getRegistrationByPath( "/foo" );

    assertNotNull( registration.getProperties() );
    assertEquals( 0, registration.getProperties().size() );
  }

  @Test
  public void testGetRegistrationByPath_withRootPath() {
    // getServletPath returns "" for servlets registered at the root path
    entryPointManager.register( "/", entryPointFactory, null );

    EntryPointRegistration registration = entryPointManager.getRegistrationByPath( "" );

    assertSame( entryPointFactory, registration.getFactory() );
  }

  @Test( expected = UnsupportedOperationException.class )
  public void testGetRegistrationByPath_propertiesNotModifiable() {
    entryPointManager.register( "/foo", entryPointFactory, new HashMap<String, String>() );

    EntryPointRegistration registration = entryPointManager.getRegistrationByPath( "/foo" );

    registration.getProperties().put( "foo", "bar" );
  }

  @Test
  public void testGetServletPaths_initallyEmpty() {
    assertTrue( entryPointManager.getServletPaths().isEmpty() );
  }

  @Test
  public void testGetServletPaths() {
    entryPointManager.register( "/foo", entryPointFactory, null );
    entryPointManager.register( "/bar", entryPointFactory, null );

    assertEquals( 2, entryPointManager.getServletPaths().size() );
    assertTrue( entryPointManager.getServletPaths().contains( "/foo" ) );
    assertTrue( entryPointManager.getServletPaths().contains( "/bar" ) );
  }

  @Test
  public void testDeregisterAll() {
    entryPointManager.register( "/foo", entryPointFactory, null );

    entryPointManager.deregisterAll();

    assertTrue( entryPointManager.getServletPaths().isEmpty() );
  }

  @Test
  public void testGetEntryPointRegistration() {
    HashMap<String, String> properties = new HashMap<String, String>();
    properties.put( "prop", "value" );
    entryPointManager.register( "/foo", entryPointFactory, properties );
    TestRequest request = new TestRequest();
    request.setServletPath( "/foo" );

    EntryPointRegistration registration = entryPointManager.getEntryPointRegistration( request );

    assertNotNull( registration );
    assertEquals( "value", registration.getProperties().get( "prop" ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testGetEntryPointRegistration_forUnregisteredEntryPoint() {
    TestRequest request = new TestRequest();
    request.setServletPath( "/foo" );

    entryPointManager.getEntryPointRegistration( request );
  }

  private void assertRegisterFails( String path, Class<? extends EntryPoint> type ) {
    try {
      entryPointManager.register( path, type, null );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  private void assertRegisterFails( String path, EntryPointFactory factory ) {
    try {
      entryPointManager.register( path, factory, null );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

}
