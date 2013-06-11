/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.regex.Pattern;

import javax.servlet.http.Cookie;

import org.eclipse.rap.rwt.service.SettingStore;
import org.eclipse.rap.rwt.service.SettingStoreFactory;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.eclipse.rap.rwt.testfixture.TestResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class SettingStoreManager_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testGetStoreTwoRequests() {
    SettingStore store = getSettingStoreManager().getStore();
    assertNotNull( store );

    // same session, new request -> same store
    Fixture.fakeNewRequest();
    SettingStore sameStore = getSettingStoreManager().getStore();
    assertSame( store, sameStore );
  }

  @Test
  public void testGetStoreTwoSessions() {
    SettingStore store = getSettingStoreManager().getStore();
    assertNotNull( store );

    // new session -> new store
    fakeNewSession();
    SettingStore newStore = getSettingStoreManager().getStore();
    assertNotSame( store, newStore );
  }

  @Test
  public void testGetStoreAfterLoad() throws Exception {
    SettingStore store = getSettingStoreManager().getStore();
    assertNotNull( store );

    // load storeById -> same store
    String randomId = String.valueOf( System.currentTimeMillis() );
    getSettingStoreManager().getStore().loadById( randomId );
    assertSame( store, getSettingStoreManager().getStore() );
  }

  @Test
  public void testLoadById() throws Exception {
    String id = String.valueOf( System.currentTimeMillis() );

    SettingStore store = getSettingStoreManager().getStore();
    store.loadById( id );
    assertNull( store.getAttribute( "key" ) );
    store.setAttribute( "key", "value" );

    // new session -> new store
    fakeNewSession();
    SettingStore newStore = getSettingStoreManager().getStore();
    // no key in store, we haven't loaded yet
    assertNull( newStore.getAttribute( "key" ) );
    newStore.loadById( id );
    // key is in store
    assertEquals( "value", newStore.getAttribute( "key" ) );
  }

  @Test
  public void testGetStoreSetsCookie() {
    getSettingStoreManager().getStore();
    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    Cookie cookie = response.getCookie( "settingStore" );
    assertTrue( cookie.getMaxAge() > 0 );
    assertTrue( Pattern.matches( "[0-9]*_[0-9]*", cookie.getValue() ) );
  }

  @Test
  public void testGetStoreReadsCookie() {
    String storeId = "123_456";
    Cookie cookie = new Cookie( "settingStore", storeId );
    cookie.setMaxAge( 3600 );
    ( ( TestRequest )ContextProvider.getRequest() ).addCookie( cookie );

    SettingStore store = getSettingStoreManager().getStore();
    assertEquals( storeId, store.getId() );
  }

  @Test
  public void testValidateCookieValue() {
    assertFalse( SettingStoreManager.isValidCookieValue( "" ) );
    assertFalse( SettingStoreManager.isValidCookieValue( "_" ) );
    assertFalse( SettingStoreManager.isValidCookieValue( "ABC_DEF" ) );
    assertTrue( SettingStoreManager.isValidCookieValue( "123_456" ) );
    String maxLong = String.valueOf( Long.MAX_VALUE );
    String maxInt = String.valueOf( Integer.MAX_VALUE );
    String value = maxLong + "_" + maxInt;
    assertTrue( SettingStoreManager.isValidCookieValue( value ) );
  }

  @Test
  public void testRegister() {
    SettingStoreManager settingStoreManager = new SettingStoreManager();
    TestSettingStoreFactory factory = new TestSettingStoreFactory();
    settingStoreManager.register( factory );
    assertTrue( settingStoreManager.hasFactory() );
  }

  @Test
  public void testRegisterTwice() {
    SettingStoreManager settingStoreManager = new SettingStoreManager();
    settingStoreManager.register( new TestSettingStoreFactory() );
    try {
      settingStoreManager.register( new TestSettingStoreFactory() );
    } catch( IllegalStateException expected ) {
    }
  }

  @Test
  public void testRegisterWithNulArgument() {
    SettingStoreManager settingStoreManager = new SettingStoreManager();
    try {
      settingStoreManager.register( null );
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testDeregisterFactory() {
    SettingStoreManager settingStoreManager = new SettingStoreManager();
    settingStoreManager.register( new TestSettingStoreFactory() );

    settingStoreManager.deregisterFactory();

    assertFalse( settingStoreManager.hasFactory() );
  }

  @Test
  public void testDeregisterFactoryIfNoFactoryHasBeenRegistered() {
    try {
      new SettingStoreManager().deregisterFactory();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  private void fakeNewSession() {
    ContextProvider.disposeContext();
    Fixture.createServiceContext();
  }

  private static SettingStoreManager getSettingStoreManager() {
    return getApplicationContext().getSettingStoreManager();
  }

  private static class TestSettingStoreFactory implements SettingStoreFactory {
    public SettingStore createSettingStore( String storeId ) {
      return null;
    }
  }

}
