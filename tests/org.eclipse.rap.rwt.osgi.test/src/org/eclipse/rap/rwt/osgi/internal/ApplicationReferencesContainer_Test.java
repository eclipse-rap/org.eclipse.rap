/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.osgi.internal;

import static org.mockito.Mockito.mock;
import junit.framework.TestCase;


public class ApplicationReferencesContainer_Test extends TestCase {
  
  private ApplicationReferencesContainer container;
  private ApplicationReferenceImpl applicationReference;

  public void testAdd() {
    container.add( applicationReference );
    
    assertEquals( 1, container.getAll().length );
  }
  
  public void testRemove() {
    container.add( applicationReference );
    
    container.remove( applicationReference );
    
    assertEquals( 0, container.getAll().length );
  }
  
  public void testClear() {
    container.add( applicationReference );

    container.clear();
    
    assertEquals( 0, container.getAll().length );
  }
  

  protected void setUp() {
    container = new ApplicationReferencesContainer();
    applicationReference = mockApplicationReference();
  }
  
  private ApplicationReferenceImpl mockApplicationReference() {
    return mock( ApplicationReferenceImpl.class );
  }
}