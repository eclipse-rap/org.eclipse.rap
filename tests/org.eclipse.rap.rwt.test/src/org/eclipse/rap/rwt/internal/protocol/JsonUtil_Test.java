/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
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
package org.eclipse.rap.rwt.internal.protocol;

import static org.junit.Assert.*;

import org.eclipse.rap.rwt.internal.protocol.JsonUtil;
import org.eclipse.swt.layout.GridLayout;
import org.junit.Test;


@SuppressWarnings( "deprecation" )
public class JsonUtil_Test {

  @Test
  public void testMessageWithIllegalParameterType() {
    GridLayout wrongParameter = new GridLayout();

    try {
      JsonUtil.createJsonValue( wrongParameter );
      fail();
    } catch( IllegalArgumentException exception ) {
      String expected = "Parameter object can not be converted to JSON value";
      assertTrue( exception.getMessage().startsWith( expected ) );
    }
  }

  @Test( expected = NullPointerException.class )
  public void createJsonArray_boolean_failsWithNull() {
    JsonUtil.createJsonArray( (boolean[])null );
  }

  @Test
  public void createJsonArray_boolean_empty() {
    assertTrue( JsonUtil.createJsonArray( new boolean[ 0 ] ).isEmpty() );
  }

  @Test( expected = NullPointerException.class )
  public void createJsonArray_int_failsWithNull() {
    JsonUtil.createJsonArray( (int[])null );
  }

  @Test
  public void createJsonArray_int_empty() {
    assertTrue( JsonUtil.createJsonArray( new int[ 0 ] ).isEmpty() );
  }

  @Test( expected = NullPointerException.class )
  public void createJsonArray_float_failsWithNull() {
    JsonUtil.createJsonArray( (float[])null );
  }

  @Test
  public void createJsonArray_float_empty() {
    assertTrue( JsonUtil.createJsonArray( new float[ 0 ] ).isEmpty() );
  }

  @Test( expected = NullPointerException.class )
  public void createJsonArray_string_failsWithNull() {
    JsonUtil.createJsonArray( (String[])null );
  }

  @Test
  public void createJsonArray_String_empty() {
    assertTrue( JsonUtil.createJsonArray( new String[ 0 ] ).isEmpty() );
  }

}
