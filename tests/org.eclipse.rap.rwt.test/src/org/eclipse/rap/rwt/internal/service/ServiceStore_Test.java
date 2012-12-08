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


public class ServiceStore_Test extends TestCase {
  private static final String NAME = "name";
  private static final Object VALUE = new Object();

  private ServiceStore serviceStore;

  @Override
  protected void setUp() throws Exception {
    serviceStore = new ServiceStore();
  }

  public void testSetAttributeWithNullName() {
    try {
      serviceStore.setAttribute( null, VALUE );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testGetAttributeWithNullName() {
    try {
      serviceStore.getAttribute( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testGetAttribute() {
    serviceStore.setAttribute( NAME, VALUE );

    Object attribute = serviceStore.getAttribute( NAME );

    assertSame( VALUE, attribute );
  }

  public void testRemoveAttribute() {
    serviceStore.setAttribute( NAME, VALUE );

    serviceStore.removeAttribute( NAME );

    assertNull( serviceStore.getAttribute( NAME ) );
  }

  public void testRemoveAttributeWithNullArgument() {
    try {
      serviceStore.removeAttribute( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testClear() {
    serviceStore.setAttribute( "foo", "bar" );
    serviceStore.setAttribute( "abc", "def" );

    serviceStore.clear();

    assertNull( serviceStore.getAttribute( "foo" ) );
    assertNull( serviceStore.getAttribute( "abc" ) );
  }
}
