/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH and others.
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
import org.eclipse.rwt.internal.application.RWTFactory;


public class StartupPage_Test extends TestCase {

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testSend() throws IOException {
    RWTFactory.getStartupPage().send();

    String markup = Fixture.getAllMarkup();
    assertTrue( markup.startsWith( "<!DOCTYPE HTML" ) );
    assertTrue( markup.endsWith( "</html>\n" ) );
  }

  public void testSuccessiveMarkup() throws IOException {
    RWTFactory.getStartupPage().send();
    String initialMarkup = Fixture.getAllMarkup();
    Fixture.fakeResponseWriter();
    RWTFactory.getStartupPage().send();
    String successiveMarkup = Fixture.getAllMarkup();

    assertEquals( initialMarkup, successiveMarkup );
  }
}
