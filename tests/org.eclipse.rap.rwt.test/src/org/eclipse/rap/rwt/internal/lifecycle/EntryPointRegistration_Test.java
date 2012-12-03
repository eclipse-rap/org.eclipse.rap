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

import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.application.EntryPointFactory;


public class EntryPointRegistration_Test extends TestCase {
  
  private EntryPointFactory factory;
  private Map<String, String> properties;

  public void testConstructor() {
    EntryPointRegistration registration = new EntryPointRegistration( factory, properties );
    
    assertEquals( factory, registration.getFactory() );
    assertNotSame( properties, registration.getProperties() );
    assertEquals( properties, registration.getProperties() );
  }
  
  public void testGetPropertiesReturnsUnmodifiableMap() {
    EntryPointRegistration registration = new EntryPointRegistration( factory, properties );

    try {
      registration.getProperties().remove( "foo" );
      fail();
    } catch( UnsupportedOperationException expected ) {
    }
  }
  
  @Override
  protected void setUp() throws Exception {
    factory = mock( EntryPointFactory.class );
    properties = new HashMap<String,String>();
  }
}
