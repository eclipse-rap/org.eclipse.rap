/*******************************************************************************
 * Copyright (c) 2013 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package org.eclipse.rap.json;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ParseException_Test {

  @Test
  public void position() {
    ParseException exception = new ParseException( "Foo", 17, 23, 42 );

    assertEquals( 17, exception.getOffset() );
    assertEquals( 23, exception.getLine() );
    assertEquals( 42, exception.getColumn() );
  }

  @Test
  public void message() {
    ParseException exception = new ParseException( "Foo", 17, 23, 42 );

    assertEquals( "Foo at 23:42", exception.getMessage() );
  }

}
