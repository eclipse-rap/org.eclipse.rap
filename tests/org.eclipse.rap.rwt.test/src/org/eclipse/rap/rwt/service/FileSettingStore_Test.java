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
package org.eclipse.rap.rwt.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Enumeration;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class FileSettingStore_Test {

  private final SettingStoreFactory factory = new FileSettingStoreFactory();
  private static int instanceCount = 0;
  private String storeId;
  private SettingStore store;

  @Before
  public void setUp() {
    Fixture.setUp();
    storeId = createUniqueId();
    store = getFactory().createSettingStore( storeId );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testGetAttributeNullKey() {
    try {
      assertNull( store.getAttribute( null ) );
      fail();
    } catch( NullPointerException npe ) {
      // expected
    }
  }

  @Test
  public void testGetAttributeUnknownKey() {
    assertNull( store.getAttribute( "key_does_not_exist" ) );
  }

  @Test
  public void testGetSetRemove() throws Exception {
    assertNull( store.getAttribute( "key" ) );
    store.setAttribute( "key", "value" );
    assertEquals( "value", store.getAttribute( "key" ) );
    store.setAttribute( "key", "value2" );
    assertEquals( "value2", store.getAttribute( "key" ) );
    store.removeAttribute( "key" );
    assertNull( store.getAttribute( "key" ) );
  }

  @Test
  public void testRemoveAttributeNullKey() throws Exception {
    try {
      store.removeAttribute( null );
      fail();
    } catch( NullPointerException npe ) {
      // expected
    }
  }

  @Test
  public void testRemoveAttributeUnknownKey() throws Exception {
    String key = "key_does_not_exist";
    store.removeAttribute( key );
    assertNull( store.getAttribute( key ) );
  }

  @Test
  public void testSetAttributeNullKey() throws Exception {
    try {
      store.setAttribute( null, "foo" );
      fail();
    } catch( NullPointerException npe ) {
      // expected
    }
  }

  @Test
  public void testSetAttributeNullValue() throws Exception {
    store.setAttribute( "key", "value" );
    assertNotNull( store.getAttribute( "key" ) );
    // set null value removes the key
    store.setAttribute( "key", null );
    assertNull( store.getAttribute( "key" ) );
  }

  @Test
  public void testGetAttributeNames() throws Exception {
    assertEquals( 0, countElements( store.getAttributeNames() ) );
    store.setAttribute( "key", "value" );
    assertEquals( 1, countElements( store.getAttributeNames() ) );
    assertEquals( "key", store.getAttributeNames().nextElement() );
    store.setAttribute( "key", "value" );
    assertEquals( 1, countElements( store.getAttributeNames() ) );
    store.setAttribute( "key2", "value2" );
    assertEquals( 2, countElements( store.getAttributeNames() ) );
    store.removeAttribute( "foo" );
    assertEquals( 2, countElements( store.getAttributeNames() ) );
    store.removeAttribute( "key2" );
    assertEquals( 1, countElements( store.getAttributeNames() ) );
  }

  @Test
  public void testLoadByIdNull() throws Exception {
    try {
      store.loadById( null );
      fail();
    } catch( NullPointerException npe ) {
      // expected
    }
  }

  @Test
  public void testLoadByIdEmpty() throws Exception {
    try {
      store.loadById( "" );
      fail();
    } catch( IllegalArgumentException iae ) {
      // expected
    }
    try {
      store.loadById( "  " );
      fail();
    } catch( IllegalArgumentException iae ) {
      // expected
    }
  }

  @Test
  public void testLoadByIdDoesClear() throws Exception {
    store.setAttribute( "key", "value" );
    assertEquals( 1, countElements( store.getAttributeNames() ) );
    store.loadById( createUniqueId() );
    assertEquals( 0, countElements( store.getAttributeNames() ) );
    store.setAttribute( "key", "value" );
    assertEquals( 1, countElements( store.getAttributeNames() ) );
  }

  @Test
  public void testLoadByIdDoesLoad() throws Exception {
    String currentId = store.getId();
    store.setAttribute( "key", "value" );
    // new store
    String newId = createUniqueId();
    SettingStore newStore = getFactory().createSettingStore( newId );
    assertNotSame( store, newStore );
    assertEquals( 0, countElements( newStore.getAttributeNames() ) );
    newStore.setAttribute( "key2", "value2" );
    // load currentId into new store
    newStore.loadById( currentId );
    assertEquals( 1, countElements( newStore.getAttributeNames() ) );
    assertEquals( "value", newStore.getAttribute( "key" ) );
    // load newId into new store
    newStore.loadById( newId );
    assertEquals( 1, countElements( newStore.getAttributeNames() ) );
    assertEquals( "value2", newStore.getAttribute( "key2" ) );
  }

  @Test
  public void testAddSettingStoreListenerNull() {
    try {
      store.addSettingStoreListener( null );
      fail();
    } catch( NullPointerException npe ) {
      // expected
    }
  }

  @Test
  public void testRemoveSettingStoreListenerNull() {
    try {
      store.removeSettingStoreListener( null );
      fail();
    } catch( NullPointerException npe ) {
      // expected
    }
  }

  @Test
  public void testAddRemoveSettingStoreListener() throws Exception {
    FTSettingStoreListener listener = new FTSettingStoreListener();
    store.addSettingStoreListener( listener );
    store.addSettingStoreListener( listener );
    assertEquals( 0, listener.getCount() );
    store.setAttribute( "key", "value" );
    assertEquals( 1, listener.getCount() );
    store.setAttribute( "key", "value2" );
    assertEquals( 2, listener.getCount() );
    store.setAttribute( "key", "value2" );
    assertEquals( 2, listener.getCount() );
    store.getAttribute( "key" );
    assertEquals( 2, listener.getCount() );
    store.getAttribute( "unknown_attribute" );
    assertEquals( 2, listener.getCount() );
    store.removeAttribute( "key" );
    assertEquals( 3, listener.getCount() );
    store.removeAttribute( "key" );
    assertEquals( 3, listener.getCount() );
    store.removeAttribute( "unknown_attribute" );
    assertEquals( 3, listener.getCount() );
    store.removeSettingStoreListener( listener );
    store.setAttribute( "key2", "value2" );
    assertEquals( 3, listener.getCount() );
  }

  @Test
  public void testSettingStoreListenerEvents() throws Exception {
    FTSettingStoreListener listener = new FTSettingStoreListener();
    store.addSettingStoreListener( listener );
    SettingStoreEvent event;
    store.setAttribute( "key", "value" );
    event = listener.getEvent();
    assertSame( store, event.getSource() );
    assertEquals( "key", event.getAttributeName() );
    assertNull( event.getOldValue() );
    assertEquals( "value", event.getNewValue() );
    store.setAttribute( "key", "value2" );
    event = listener.getEvent();
    assertSame( store, event.getSource() );
    assertEquals( "key", event.getAttributeName() );
    assertEquals( "value", event.getOldValue() );
    assertEquals( "value2", event.getNewValue() );
    store.removeAttribute( "foo" );
    assertSame( event, listener.getEvent() ); // no new event
    store.removeAttribute( "key" );
    event = listener.getEvent();
    assertSame( store, event.getSource() );
    assertEquals( "key", event.getAttributeName() );
    assertEquals( "value2", event.getOldValue() );
    assertNull( event.getNewValue() );
  }

  @Test
  public void testListenerEventsOnLoadForRemovedKeys() throws Exception {
    store.setAttribute( "key1", "value1" );
    store.setAttribute( "key2", "value2" );
    assertEquals( 2, countElements( store.getAttributeNames() ) );
    FTSettingStoreListener listener = new FTSettingStoreListener();
    store.addSettingStoreListener( listener );
    store.loadById( "newId" );
    assertEquals( 2, listener.getCount() );
    SettingStoreEvent lastEvent = listener.getEvent();
    assertNotNull( lastEvent.getAttributeName() );
    assertNotNull( lastEvent.getOldValue() );
    assertNull( lastEvent.getNewValue() );
  }

  @Test
  public void testListenerEventsOnLoadForLoadedKeys() throws Exception {
    store.setAttribute( "key1", "value1" );
    store.setAttribute( "key2", "value2" );
    store = getFactory().createSettingStore( "newId" );
    assertEquals( 0, countElements( store.getAttributeNames() ) );
    FTSettingStoreListener listener = new FTSettingStoreListener();
    store.addSettingStoreListener( listener );
    store.loadById( storeId );
    assertEquals( 2, countElements( store.getAttributeNames() ) );
    assertEquals( 2, listener.getCount() );
    SettingStoreEvent lastEvent = listener.getEvent();
    assertNotNull( lastEvent.getAttributeName() );
    assertNull( lastEvent.getOldValue() );
    assertNotNull( lastEvent.getNewValue() );
  }

  @Test
  public void testGetId() {
    assertNotNull( store.getId() );
    assertEquals( storeId, store.getId() );
  }

  private int countElements( Enumeration enu ) {
    int result = 0;
    while( enu.hasMoreElements() ) {
      result++ ;
      enu.nextElement();
    }
    return result;
  }

  private String createUniqueId() {
    return String.valueOf( System.currentTimeMillis() ) + "_" + ( ++instanceCount );
  }

  protected SettingStoreFactory getFactory() {
    return factory;
  }

}
