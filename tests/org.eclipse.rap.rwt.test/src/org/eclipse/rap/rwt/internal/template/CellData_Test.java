/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.template;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;


public class CellData_Test {

  @Test
  public void testAddAttributesData() {
    CellData data = new CellData();

    data.addAttribute( "foo", "bar" );

    assertEquals( "bar", data.getAttributes().get( "foo" ) );
  }

  @Test
  public void testDataIsSafeCopy() {
    CellData data = new CellData();
    Map<String, Object> attributes = data.getAttributes();

    data.addAttribute( "foo", "bar" );

    assertEquals( 0, attributes.size() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsToAddNullKeys() {
    CellData data = new CellData();

    data.addAttribute( null, "bar" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsToAddEmptyKeys() {
    CellData data = new CellData();

    data.addAttribute( "", "bar" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsToAddNullValues() {
    CellData data = new CellData();

    data.addAttribute( "foo", null );
  }
}
