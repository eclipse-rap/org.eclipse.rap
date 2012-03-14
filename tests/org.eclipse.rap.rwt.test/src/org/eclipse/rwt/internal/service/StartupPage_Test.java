/*******************************************************************************
 * Copyright (c) 2008, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestResponse;
import org.eclipse.rwt.internal.application.RWTFactory;


public class StartupPage_Test extends TestCase {

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testSend() throws IOException {
    RWTFactory.getStartupPage().send();

    String content = getResponseContent();
    assertTrue( content.startsWith( "<!DOCTYPE HTML" ) );
    assertTrue( content.endsWith( "</html>\n" ) );
  }

  public void testSuccessiveMarkup() throws IOException {
    RWTFactory.getStartupPage().send();
    String initialContent = getResponseContent();

    // second request
    Fixture.fakeResponseWriter();
    RWTFactory.getStartupPage().send();

    String successiveContent = getResponseContent();
    assertEquals( initialContent, successiveContent );
  }

  public void testHttpHeaders() throws IOException {
    RWTFactory.getStartupPage().send();

    TestResponse response = ( TestResponse )ContextProvider.getResponse();

    assertEquals( "text/html; charset=UTF-8", response.getHeader( "Content-Type" ) );
    assertTrue( response.getHeader( "Cache-Control" ).contains( "no-store" ) );
  }

  private static String getResponseContent() {
    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    return response.getContent();
  }
}
