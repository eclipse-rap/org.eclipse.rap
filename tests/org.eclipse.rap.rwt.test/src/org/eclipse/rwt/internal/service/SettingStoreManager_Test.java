/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.util.regex.Pattern;

import javax.servlet.http.Cookie;

import junit.framework.TestCase;

import org.eclipse.rwt.*;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.service.ISettingStore;
import org.eclipse.rwt.service.ISettingStoreFactory;


public class SettingStoreManager_Test extends TestCase {

  private static class TestSettingStoreFactory implements ISettingStoreFactory {
    public ISettingStore createSettingStore( String storeId ) {
      return null;
    }
  }

  public void testGetStoreTwoRequests() {
    ISettingStore store = RWTFactory.getSettingStoreManager().getStore();
    assertNotNull( store );

    // same session, new request -> same store
    Fixture.fakeNewRequest();
    ISettingStore sameStore = RWTFactory.getSettingStoreManager().getStore();
    assertSame( store, sameStore );
  }

  public void testGetStoreTwoSessions() {
    ISettingStore store = RWTFactory.getSettingStoreManager().getStore();
    assertNotNull( store );

    // new session -> new store
    fakeNewSession();
    ISettingStore newStore = RWTFactory.getSettingStoreManager().getStore();
    assertNotSame( store, newStore );
  }

  public void testGetStoreAfterLoad() throws Exception {
    ISettingStore store = RWTFactory.getSettingStoreManager().getStore();
    assertNotNull( store );

    // load storeById -> same store
    String randomId = String.valueOf( System.currentTimeMillis() );
    RWTFactory.getSettingStoreManager().getStore().loadById( randomId );
    assertSame( store, RWTFactory.getSettingStoreManager().getStore() );
  }

  public void testLoadById() throws Exception {
    String id = String.valueOf( System.currentTimeMillis() );

    ISettingStore store = RWTFactory.getSettingStoreManager().getStore();
    store.loadById( id );
    assertNull( store.getAttribute( "key" ) );
    store.setAttribute( "key", "value" );

    // new session -> new store
    fakeNewSession();
    ISettingStore newStore = RWTFactory.getSettingStoreManager().getStore();
    // no key in store, we haven't loaded yet
    assertNull( newStore.getAttribute( "key" ) );
    newStore.loadById( id );
    // key is in store
    assertEquals( "value", newStore.getAttribute( "key" ) );
  }

  public void testGetStoreSetsCookie() {
    RWTFactory.getSettingStoreManager().getStore();
    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    Cookie cookie = response.getCookie( "settingStore" );
    assertTrue( cookie.getMaxAge() > 0 );
    assertTrue( Pattern.matches( "[0-9]*_[0-9]*", cookie.getValue() ) );
  }

  public void testGetStoreReadsCookie() {
    String storeId = "123_456";
    Cookie cookie = new Cookie( "settingStore", storeId );
    cookie.setMaxAge( 3600 );
    ( ( TestRequest )ContextProvider.getRequest() ).addCookie( cookie );

    ISettingStore store = RWTFactory.getSettingStoreManager().getStore();
    assertEquals( storeId, store.getId() );
  }

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
  
  public void testRegister() {
    SettingStoreManager settingStoreManager = new SettingStoreManager();
    TestSettingStoreFactory factory = new TestSettingStoreFactory();
    settingStoreManager.register( factory );
    assertTrue( settingStoreManager.hasFactory() );
  }
  
  public void testRegisterTwice() {
    SettingStoreManager settingStoreManager = new SettingStoreManager();
    settingStoreManager.register( new TestSettingStoreFactory() );
    try {
      settingStoreManager.register( new TestSettingStoreFactory() );
    } catch( IllegalStateException expected ) {
    }
  }

  public void testRegisterWithNulArgument() {
    SettingStoreManager settingStoreManager = new SettingStoreManager();
    try {
      settingStoreManager.register( null );
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testDeregisterFactory() {
    SettingStoreManager settingStoreManager = new SettingStoreManager();
    settingStoreManager.register( new TestSettingStoreFactory() );
    
    settingStoreManager.deregisterFactory();
    
    assertFalse( settingStoreManager.hasFactory() );
  }
  
  public void testDeregisterFactoryIfNoFactoryHasBeenRegistered() {
    try {
      new SettingStoreManager().deregisterFactory();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  //////////////////
  // helping methods

  private void fakeNewSession() {
    ContextProvider.disposeContext();
    Fixture.createServiceContext();
  }

}
