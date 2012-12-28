/*******************************************************************************
 * Copyright (c) 2011, 2012 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.osgi.internal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;


public class ApplicationReferencesContainer_Test {

  private ApplicationReferencesContainer container;
  private ApplicationReferenceImpl applicationReference;

  @Test
  public void testAdd() {
    container.add( applicationReference );

    assertEquals( 1, container.getAll().length );
  }

  @Test
  public void testRemove() {
    container.add( applicationReference );

    container.remove( applicationReference );

    assertEquals( 0, container.getAll().length );
  }

  @Test
  public void testClear() {
    container.add( applicationReference );

    container.clear();

    assertEquals( 0, container.getAll().length );
  }


  @Before
  public void setUp() {
    container = new ApplicationReferencesContainer();
    applicationReference = mockApplicationReference();
  }

  private ApplicationReferenceImpl mockApplicationReference() {
    return mock( ApplicationReferenceImpl.class );
  }
}