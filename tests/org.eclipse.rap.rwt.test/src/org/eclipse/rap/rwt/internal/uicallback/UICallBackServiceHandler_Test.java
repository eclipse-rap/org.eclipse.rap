/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.uicallback;

import static org.mockito.Mockito.mock;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.TestResponse;
import org.eclipse.swt.widgets.Display;


public class UICallBackServiceHandler_Test extends TestCase {

  private final static String UI_CALLBACK_ID = "rwt.client.UICallBack";
  private final static String METHOD_SEND_UI_REQUEST = "sendUIRequest";

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testResponseContentType() throws IOException {
    Fixture.fakeNewRequest();

    new UICallBackServiceHandler().service();

    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    assertEquals( "application/json; charset=UTF-8", response.getHeader( "Content-Type" ) );
  }

  public void testWritesUiRequestMessage() throws Throwable {
    new Display().asyncExec( mock( Runnable.class ) );

    new UICallBackServiceHandler().service();

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findCallOperation( UI_CALLBACK_ID, METHOD_SEND_UI_REQUEST ) );
  }

}
