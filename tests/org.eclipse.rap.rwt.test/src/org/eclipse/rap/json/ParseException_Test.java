/*******************************************************************************
 * Copyright (c) 2013 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package org.eclipse.rap.json;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class ParseException_Test {

  @Test
  public void position() {
    ParseException exception = new ParseException( "Foo", 23, 42 );

    assertEquals( 23, exception.getLine() );
    assertEquals( 42, exception.getColumn() );
  }

  @Test
  public void message() {
    ParseException exception = new ParseException( "Foo", 23, 42 );

    assertEquals( "Foo at 23:42", exception.getMessage() );
  }

}
