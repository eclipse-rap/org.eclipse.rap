/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import junit.framework.TestCase;


public class ServiceStateInfo_Test extends TestCase {
  private static final String NAME = "name";
  private static final Object VALUE = new Object();
  
  private ServiceStateInfo serviceStateInfo;

  public void testSetAttributeWithNullName() {
    try {
      serviceStateInfo.setAttribute( null, VALUE );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testGetAttributeWithNullName() {
    try {
      serviceStateInfo.getAttribute( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testGetAttribute() {
    serviceStateInfo.setAttribute( NAME, VALUE );
    
    Object attribute = serviceStateInfo.getAttribute( NAME );
    
    assertSame( VALUE, attribute );
  }

  public void testRemoveAttribute() {
    serviceStateInfo.setAttribute( NAME, VALUE );
    
    serviceStateInfo.removeAttribute( NAME );
    
    assertNull( serviceStateInfo.getAttribute( NAME ) );
  }
  
  public void testRemoveAttributeWithNullArgument() {
    try {
      serviceStateInfo.removeAttribute( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  @Override
  protected void setUp() throws Exception {
    serviceStateInfo = new ServiceStateInfo();
  }
}
