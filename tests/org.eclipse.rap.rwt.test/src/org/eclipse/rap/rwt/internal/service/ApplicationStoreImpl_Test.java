/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import junit.framework.TestCase;


public class ApplicationStoreImpl_Test extends TestCase {
  private static final String VALUE = "value";
  private static final String KEY = "key";

  private ApplicationStoreImpl applicationStore;

  @Override
  protected void setUp() throws Exception {
    applicationStore = new ApplicationStoreImpl();
  }

  public void testSetAttribute_failsWithNullName() {
    try {
      applicationStore.setAttribute( null, new Object() );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testSetAttribute_succeedsWithNullValue() {
    applicationStore.setAttribute( "name", null );

    assertNull( applicationStore.getAttribute( "name" ) );
  }

  public void testGetAttribute() {
    applicationStore.setAttribute( KEY, VALUE );

    Object attribute = applicationStore.getAttribute( KEY );

    assertSame( VALUE, attribute );
  }

  public void testGetAttribute_failsWithNullName() {
    try {
      applicationStore.getAttribute( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testRemoveAttribute() {
    applicationStore.setAttribute( KEY, VALUE );

    applicationStore.removeAttribute( KEY );

    assertSame( null, applicationStore.getAttribute( KEY ) );
  }

  public void testRemoveAttribute_failsWithNullName() {
    try {
      applicationStore.removeAttribute( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testReset() {
    applicationStore.setAttribute( KEY, VALUE );

    applicationStore.reset();

    assertSame( null, applicationStore.getAttribute( KEY ) );
  }

}
