/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.Fixture.TestResponse;
import org.eclipse.rwt.Fixture.TestServletOutputStream;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.util.HTML;
import org.eclipse.swt.RWTFixture;


public class UICallBackServiceHandler_Test extends TestCase {
  
  private static final String ID_1 = "id_1";
  private static final String ID_2 = "id_2";

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }
  
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
  
  public void testOnOffSwitch() throws InterruptedException {
    assertEquals( "", UICallBackServiceHandler.jsEnableUICallBack() );
    UICallBackServiceHandler.activateUICallBacksFor( ID_1 );
    UICallBackServiceHandler.jsEnableUICallBack();
    assertFalse( "".equals( UICallBackServiceHandler.jsEnableUICallBack() ) );
    // test that on/off switching is managed in session scope
    final String[] otherSession = new String[ 1 ];
    Thread thread = new Thread( new Runnable() {
      public void run() {
        RWTFixture.fakeContext();
        otherSession[ 0 ] = UICallBackServiceHandler.jsEnableUICallBack();
      } 
    } );
    thread.start();
    thread.join();
    assertEquals( "", otherSession[ 0 ] );
    UICallBackServiceHandler.deactivateUICallBacksFor( ID_1 );
    assertEquals( "", UICallBackServiceHandler.jsEnableUICallBack() );
    
    UICallBackServiceHandler.activateUICallBacksFor( ID_1 );
    UICallBackServiceHandler.activateUICallBacksFor( ID_2 );
    assertFalse( "".equals( UICallBackServiceHandler.jsEnableUICallBack() ) );
    UICallBackServiceHandler.deactivateUICallBacksFor( ID_1 );
    assertFalse( "".equals( UICallBackServiceHandler.jsEnableUICallBack() ) );
    UICallBackServiceHandler.deactivateUICallBacksFor( ID_2 );
    assertEquals( "", UICallBackServiceHandler.jsEnableUICallBack() );
  }
  
  public void testResponseContentType() throws IOException {
    Fixture.fakeResponseWriter();
    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    response.setOutputStream( new TestServletOutputStream() );
    UICallBackServiceHandler.writeResponse();
    assertEquals( HTML.CONTENT_TEXT_JAVASCRIPT_UTF_8, 
                  response.getHeader( "Content-Type" ) );
  }
}