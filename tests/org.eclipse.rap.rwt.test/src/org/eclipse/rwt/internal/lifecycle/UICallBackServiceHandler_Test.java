/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
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
package org.eclipse.rwt.internal.lifecycle;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.TestResponse;
import org.eclipse.rwt.internal.service.ContextProvider;


public class UICallBackServiceHandler_Test extends TestCase {
  
  protected void setUp() throws Exception {
    Fixture.setUp();
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testResponseContentType() throws IOException {
    Fixture.fakeResponseWriter();
    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    UICallBackServiceHandler.writeResponse();
    assertEquals( "text/javascript; charset=UTF-8", response.getHeader( "Content-Type" ) );
  }
}
