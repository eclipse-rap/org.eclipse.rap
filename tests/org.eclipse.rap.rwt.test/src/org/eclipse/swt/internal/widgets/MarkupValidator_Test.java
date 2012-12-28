/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;


public class MarkupValidator_Test {

  private MarkupValidator validator;

  @Before
  public void setUp() {
    validator = new MarkupValidator();
  }

  @Test
  public void testValidate() {
    String markup = "<b>foo</b><br/><span style=\"background-color: blue\">bar</span>";

    try {
      validator.validate( markup );
    } catch( Exception ex ) {
      ex.printStackTrace();
      fail( ex.getMessage() );
    }
  }

  @Test
  public void testValidate_NotWellFormedMarkup() {
    String markup = "<b>foo<br/><i>bar</i>";

    try {
      validator.validate( markup );
      fail( "validation should throw an exception" );
    } catch( Exception expected ) {
      assertTrue( expected instanceof IllegalArgumentException );
      assertEquals( "Failed to parse markup text", expected.getMessage() );
    }
  }

  @Test
  public void testValidate_UnsupportedElement() {
    String markup = "<ul>foo</ul><br/><i>bar</i>";

    try {
      validator.validate( markup );
      fail( "validation should throw an exception" );
    } catch( Exception expected ) {
      assertTrue( expected instanceof IllegalArgumentException );
      assertEquals( "Unsupported element in markup text: ul", expected.getMessage() );
    }
  }

  @Test
  public void testValidate_UnsupportedAttribute() {
    String markup = "<b>foo</b><br/><span href=\"abc\">bar</span>";

    try {
      validator.validate( markup );
      fail( "validation should throw an exception" );
    } catch( Exception expected ) {
      assertTrue( expected instanceof IllegalArgumentException );
      String expectedMessage = "Unsupported attribute \"href\" for element \"span\" in markup text";
      assertEquals( expectedMessage, expected.getMessage() );
    }
  }

  @Test
  public void testValidate_MissingMandatoryAttribute() {
    String markup = "<img src=\"image.png\" width=\"10\" />";
    try {
      validator.validate( markup );
      fail( "validation should throw an exception" );
    } catch( Exception expected ) {
      assertTrue( expected instanceof IllegalArgumentException );
      String expectedMessage
        = "Mandatory attribute \"height\" for element \"img\" is missing or not a valid integer";
      assertEquals( expectedMessage, expected.getMessage() );
    }
  }

  @Test
  public void testValidate_InvalidIntAttribute() {
    String markup = "<img src=\"image.png\" width=\"10\" height=\"abc\" />";
    try {
      validator.validate( markup );
      fail( "validation should throw an exception" );
    } catch( Exception expected ) {
      assertTrue( expected instanceof IllegalArgumentException );
      String expectedMessage
        = "Mandatory attribute \"height\" for element \"img\" is missing or not a valid integer";
      assertEquals( expectedMessage, expected.getMessage() );
    }
  }

}
