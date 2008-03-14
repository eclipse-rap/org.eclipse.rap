/*******************************************************************************
 * Copyright (c) 2002-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.service;

import java.util.regex.Pattern;

import javax.servlet.http.Cookie;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture.TestRequest;
import org.eclipse.rwt.Fixture.TestResponse;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.swt.RWTFixture;

/**
 * Tests for the class {@link SettingStoreManager}.
 */
public class SettingStoreManager_Test extends TestCase {
  
  public void testGetStoreTwoRequests() {
    ISettingStore store = SettingStoreManager.getStore();
    assertNotNull( store );
    
    // same session, new request -> same store
    RWTFixture.fakeNewRequest();
    ISettingStore sameStore = SettingStoreManager.getStore();
    assertSame( store, sameStore );
  }
  
  public void testGetStoreTwoSessions() {
    ISettingStore store = SettingStoreManager.getStore();
    assertNotNull( store );
    
    // new session -> new store
    fakeNewSession();
    ISettingStore newStore = SettingStoreManager.getStore();
    assertNotSame( store, newStore );
  }
  
  public void testGetStoreAfterLoad() throws Exception {
    ISettingStore store = SettingStoreManager.getStore();
    assertNotNull( store );

    // load storeById -> same store
    String randomId = String.valueOf( System.currentTimeMillis() );
    SettingStoreManager.getStore().loadById( randomId );
    assertSame( store, SettingStoreManager.getStore() );
  }
  
  public void testLoadById() throws Exception {
    String id = String.valueOf( System.currentTimeMillis() );

    ISettingStore store = SettingStoreManager.getStore();
    store.loadById( id );
    assertNull( store.getAttribute( "key" ) );
    store.setAttribute( "key", "value" );
    
    // new session -> new store
    fakeNewSession();
    ISettingStore newStore = SettingStoreManager.getStore();
    // no key in store, we haven't loaded yet
    assertNull( newStore.getAttribute( "key" ) );
    newStore.loadById( id );
    // key is in store
    assertEquals( "value", newStore.getAttribute( "key" ) );
  }
  
  public void testGetStoreSetsCookie() throws Exception {
    SettingStoreManager.getStore();
    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    Cookie cookie = response.getCookie( "settingStore" );
    assertTrue( cookie.getMaxAge() > 0 );
    assertTrue( Pattern.matches( "[0-9]*_[0-9]*", cookie.getValue() ) );
  }
  
  public void testGetStoreReadsCookie() throws Exception {
    String storeId = "myprecious";
    Cookie cookie = new Cookie( "settingStore", storeId );
    cookie.setMaxAge( 3600 );
    ( ( TestRequest )ContextProvider.getRequest() ).addCookie( cookie );
    
    ISettingStore store = SettingStoreManager.getStore();
    assertEquals( storeId, store.getId() );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }
  
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
  
  //////////////////
  // helping methods
  
  private void fakeNewSession() {
    ContextProvider.disposeContext();
    RWTFixture.fakeContext();
  }
  
}
