/*******************************************************************************
 * Copyright (c) 2011, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class CssIdentifier_Test {

  @Test
  public void testCreate() {
    CssIdentifier identifier = new CssIdentifier( "line-through" );
    assertEquals( "line-through", identifier.value  );
  }

  @Test
  public void testDefaultString() {
    CssIdentifier identifier = new CssIdentifier( "line-through" );
    assertEquals( "line-through", identifier.toDefaultString() );
  }

  @Test
  public void testEquals() {
    CssIdentifier identifier1 = new CssIdentifier( "line-through" );
    CssIdentifier identifier2 = new CssIdentifier( "line-through" );
    assertEquals( identifier1, identifier2 );
  }

  @Test
  public void testHashCode() {
    CssIdentifier identifier1 = new CssIdentifier( "line-through" );
    CssIdentifier identifier2 = new CssIdentifier( "line-through" );
    assertEquals( identifier1.hashCode(), identifier2.hashCode() );
  }

}
