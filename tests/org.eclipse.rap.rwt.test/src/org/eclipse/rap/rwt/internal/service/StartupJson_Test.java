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
package org.eclipse.rap.rwt.internal.service;

import java.io.IOException;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.TestResponse;

import junit.framework.TestCase;


public class StartupJson_Test extends TestCase {

  @Override
  protected void setUp() {
    Fixture.setUp();
  }

  @Override
  protected void tearDown() {
    Fixture.tearDown();
  }

  public void testStartupJsonContent_Url() {
    String content = StartupJson.get();

    Message message = new Message( content );
    assertEquals( "rap", message.findHeadProperty( "url" ) );
  }

  public void testStartupJsonContent_CreateDisplay() {
    String content = StartupJson.get();

    Message message = new Message( content );
    assertNotNull( message.findCreateOperation( "w1" ) );
  }

  public void testSendStartupJson() throws IOException {
    Fixture.fakeNewGetRequest();

    StartupJson.send();

    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    assertEquals( "application/json; charset=UTF-8", response.getHeader( "Content-Type" ) );
    assertTrue( response.getContent().indexOf( "rwt.widgets.Display" ) != -1 );
  }

}
