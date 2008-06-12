/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.util;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.browser.Mozilla1_6;
import org.eclipse.rwt.internal.lifecycle.HtmlResponseWriter;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.swt.RWTFixture;

/**
 * <p>Unit tests for HTMLUtil</p>
 */
public class HTMLUtil_Test extends TestCase {
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }
  
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
  
  public void testHiddenInput() throws IOException {
    StringBuffer buffer = new StringBuffer();
    HTMLUtil.hiddenInput( buffer, "name", "value" );
    String expected;
    expected = "<input type=\"hidden\" "
               + "id=\"name\" name=\"name\" value=\"value\" />";
    assertEquals( expected, buffer.toString() );
    // document that no null-check of arguments is done - this may change
    buffer.setLength( 0 );
    HTMLUtil.hiddenInput( buffer, null, null );
    expected = "<input type=\"hidden\" "
             + "id=\"null\" name=\"null\" value=\"null\" />";
    assertEquals( expected, buffer.toString() );
    // NPE when buffer is null
    try {
      HTMLUtil.hiddenInput( ( StringBuffer )null, null, null );
      fail( "expected NPE" );
    } catch( NullPointerException e ) {
      // as expected
    }
    // to encode or not to encode
    Fixture.fakeBrowser( new Mozilla1_6( true, false ) );
    Fixture.fakeResponseWriter();
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter writer = stateInfo.getResponseWriter();
    HTMLUtil.hiddenInput( writer, "name", "Umlaut-values: הצüß" );
    expected 
      = "<input type=\"hidden\" id=\"name\" name=\"name\" " 
      + "value=\"Umlaut-values: &auml;&ouml;&uuml;&szlig;\" />";
    assertEquals( expected, Fixture.getAllMarkup() );
    buffer = new StringBuffer();
    HTMLUtil.hiddenInput( buffer, "name", "Umlaut-values: הצüß" );
    expected 
      = "<input type=\"hidden\" id=\"name\" name=\"name\" " 
      + "value=\"Umlaut-values: &auml;&ouml;&uuml;&szlig;\" />";
    assertEquals( expected, buffer.toString() );
  }
  
  public void testAttribute() {
    // don't render attributes with empty value
    StringBuffer buffer = new StringBuffer();
    HTMLUtil.attribute( buffer, "some-name", "" );
    assertEquals( " some-name=\"\"", buffer.toString() );
    // ... but those with 
    buffer.setLength( 0 );
    HTMLUtil.attribute( buffer, "some-name", "my-value" );
    assertEquals( " some-name=\"my-value\"", buffer.toString() );
    // document that no null-check of arguments is done - this may change
    buffer.setLength( 0 );
    HTMLUtil.attribute( buffer, "some-name", null );
    assertEquals( " some-name=\"null\"", buffer.toString() );
    // ensure value is encoded
    buffer.setLength( 0 );
    HTMLUtil.attribute( buffer, "name",  "--&הצü<>\"" );
    assertEquals( " name=\"&#045;&#045;&amp;&auml;&ouml;&uuml;&lt;&gt;&quot;\"", 
                  buffer.toString() );
  }
}
