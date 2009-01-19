/*******************************************************************************
 * Copyright (c) 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme.css;

import junit.framework.TestCase;

import org.eclipse.rwt.internal.theme.*;


public class SimpleSelector_Test extends TestCase {

  public void testDummyMatcher() {
    // Get set of conditional results
    ConditionalValue value1
      = new ConditionalValue( new String[] { "[BORDER", ":selected" },
                              QxColor.create( 255, 0, 0 ) );
    ConditionalValue value2
      = new ConditionalValue( new String[] { ".special" },
                              QxColor.create( 0, 0, 255 ) );
    ConditionalValue value3
      = new ConditionalValue( new String[] {},
                              QxColor.create( 0, 255, 0 ) );
    ConditionalValue[] values
      = new ConditionalValue[] { value1, value2, value3 };

    SimpleSelector selector;
    selector = SimpleSelector.DEFAULT;
    assertEquals( QxColor.create( 0, 255, 0 ),
                  selector.select( values, null ) );
    selector = new SimpleSelector( new String[] { "[BORDER", ":selected" } );
    assertEquals( QxColor.create( 255, 0, 0 ),
                  selector.select( values, null ) );
  }
}
