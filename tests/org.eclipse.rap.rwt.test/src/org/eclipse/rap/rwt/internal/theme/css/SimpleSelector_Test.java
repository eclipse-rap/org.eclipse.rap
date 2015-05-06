/*******************************************************************************
 * Copyright (c) 2009, 2015 EclipseSource and others.
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

  private static final CssColor RED = CssColor.create( 255, 0, 0 );
  private static final CssColor GREEN = CssColor.create( 0, 255, 0 );
  private static final CssColor BLUE = CssColor.create( 0, 0, 255 );

  @Test
  public void testDefault() {
    ConditionalValue value1 = new ConditionalValue( RED, "[BORDER", ":selected" );
    ConditionalValue value2 = new ConditionalValue( BLUE, ".special" );
    ConditionalValue value3 = new ConditionalValue( GREEN );
    ConditionalValue[] values = { value1, value2, value3 };

    SimpleSelector selector = SimpleSelector.DEFAULT;

    assertEquals( GREEN, selector.select( null, values ) );
  }

  @Test
  public void testSelect() {
    ConditionalValue value1 = new ConditionalValue( RED, "[BORDER", ":selected" );
    ConditionalValue value2 = new ConditionalValue( BLUE, ".special" );
    ConditionalValue value3 = new ConditionalValue( GREEN );
    ConditionalValue[] values = { value1, value2, value3 };

    SimpleSelector selector = new SimpleSelector( "[BORDER", ":selected" );

    assertEquals( RED, selector.select( null, values ) );
  }

}
