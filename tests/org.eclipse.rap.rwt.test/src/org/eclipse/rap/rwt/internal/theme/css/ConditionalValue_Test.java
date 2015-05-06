/*******************************************************************************
 * Copyright (c) 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme.css;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.eclipse.rap.rwt.internal.theme.CssColor;
import org.eclipse.rap.rwt.internal.theme.CssValue;
import org.junit.Test;


public class ConditionalValue_Test {

  @Test
  public void testValue() {
    ConditionalValue conditionalValue = new ConditionalValue( CssColor.BLACK );

    assertSame( CssColor.BLACK, conditionalValue.value );
  }

  @Test
  public void testConstraints() {
    ConditionalValue conditionalValue = new ConditionalValue( CssColor.BLACK, "foo" );

    assertArrayEquals( new String[] { "foo" }, conditionalValue.constraints );
  }

  @Test
  public void testToString() {
    CssValue value = CssColor.BLACK;
    ConditionalValue conditionalValue = new ConditionalValue( value, "foo", "bar" );

    assertEquals( "ConditionalValue{ value: CssColor{ 0, 0, 0, 1.0 }, constraints: foo, bar }",
                  conditionalValue.toString() );
  }

}
