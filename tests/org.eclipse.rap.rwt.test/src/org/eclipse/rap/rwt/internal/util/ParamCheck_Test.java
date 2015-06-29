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
package org.eclipse.rap.rwt.internal.util;

import static org.junit.Assert.*;

import org.junit.Test;


public class ParamCheck_Test {

  @Test
  public void testNotNull() {
    try {
      ParamCheck.notNull( null, "foo" );
    } catch( NullPointerException exception ) {
      assertEquals( "The parameter 'foo' must not be null.", exception.getMessage() );
    }
  }

  @Test
  public void testNotNullOrEmpty_null() {
    try {
      ParamCheck.notNullOrEmpty( null, "foo" );
    } catch( NullPointerException exception ) {
      assertEquals( "The parameter 'foo' must not be null.", exception.getMessage() );
    }
  }

  @Test
  public void testNotNullOrEmpty_empty() {
    try {
      ParamCheck.notNullOrEmpty( "", "foo" );
    } catch( IllegalArgumentException exception ) {
      assertEquals( "The parameter 'foo' must not be empty.", exception.getMessage() );
    }
  }

}
