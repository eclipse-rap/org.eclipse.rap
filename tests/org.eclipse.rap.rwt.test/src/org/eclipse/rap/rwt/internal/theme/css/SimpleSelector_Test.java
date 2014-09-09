/*******************************************************************************
 * Copyright (c) 2009, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme.css;

import static org.junit.Assert.assertEquals;

import org.eclipse.rap.rwt.internal.theme.CssColor;
import org.eclipse.rap.rwt.internal.theme.SimpleSelector;
import org.junit.Test;


public class SimpleSelector_Test {

  @Test
  public void testDummyMatcher() {
    // Get set of conditional results
    ConditionalValue value1
      = new ConditionalValue( new String[] { "[BORDER", ":selected" },
                              CssColor.create( 255, 0, 0 ) );
    ConditionalValue value2
      = new ConditionalValue( new String[] { ".special" },
                              CssColor.create( 0, 0, 255 ) );
    ConditionalValue value3
      = new ConditionalValue( new String[] {},
                              CssColor.create( 0, 255, 0 ) );
    ConditionalValue[] values
      = new ConditionalValue[] { value1, value2, value3 };

    SimpleSelector selector = SimpleSelector.DEFAULT;
    assertEquals( CssColor.create( 0, 255, 0 ), selector.select( values, null ) );
    selector = new SimpleSelector( new String[] { "[BORDER", ":selected" } );
    assertEquals( CssColor.create( 255, 0, 0 ), selector.select( values, null ) );
  }

}
