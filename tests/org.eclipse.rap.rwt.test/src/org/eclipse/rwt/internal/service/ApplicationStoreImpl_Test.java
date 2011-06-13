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


public class ApplicationStoreImpl_Test extends TestCase {
  private static final String VALUE = "value";
  private static final String KEY = "key";

  public void testGetAttribute() {
    ApplicationStoreImpl applicationStore = new ApplicationStoreImpl();
    applicationStore.setAttribute( KEY, VALUE );
    
    Object attribute = applicationStore.getAttribute( KEY );
    
    assertSame( VALUE, attribute );
  }

  public void testRemoveAttribute() {
    ApplicationStoreImpl applicationStore = new ApplicationStoreImpl();
    applicationStore.setAttribute( KEY, VALUE );
    
    applicationStore.removeAttribute( KEY );
    
    assertSame( null, applicationStore.getAttribute( KEY ) );
  }
  
  public void testReset() {
    ApplicationStoreImpl applicationStore = new ApplicationStoreImpl();
    applicationStore.setAttribute( KEY, VALUE );

    applicationStore.reset();
    
    assertSame( null, applicationStore.getAttribute( KEY ) );
  }
}
