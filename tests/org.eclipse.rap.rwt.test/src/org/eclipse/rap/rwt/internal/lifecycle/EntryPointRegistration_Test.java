/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.application.EntryPointFactory;
import org.junit.Before;
import org.junit.Test;


public class EntryPointRegistration_Test {

  private EntryPointFactory factory;
  private Map<String, String> properties;

  @Before
  public void setUp() {
    factory = mock( EntryPointFactory.class );
    properties = new HashMap<String,String>();
  }

  @Test
  public void testConstructor() {
    EntryPointRegistration registration = new EntryPointRegistration( factory, properties );

    assertEquals( factory, registration.getFactory() );
    assertNotSame( properties, registration.getProperties() );
    assertEquals( properties, registration.getProperties() );
  }

  @Test
  public void testGetPropertiesReturnsUnmodifiableMap() {
    EntryPointRegistration registration = new EntryPointRegistration( factory, properties );

    try {
      registration.getProperties().remove( "foo" );
      fail();
    } catch( UnsupportedOperationException expected ) {
    }
  }

}
