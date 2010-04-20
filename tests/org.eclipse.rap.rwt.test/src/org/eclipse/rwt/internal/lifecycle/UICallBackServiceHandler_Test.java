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

import org.eclipse.rwt.*;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.ServiceStateInfo;
import org.eclipse.rwt.internal.util.HTML;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.widgets.Display;


public class UICallBackServiceHandler_Test extends TestCase {
  
  private static final String ID_1 = "id_1";
  private static final String ID_2 = "id_2";

  protected void setUp() throws Exception {
    Fixture.setUp();
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testOnOffSwitchFromDifferentSession() throws Exception {
    final UICallBackActivator uiCallBackActivator
      = new UICallBackActivator( ContextProvider.getSession() );
    // test that on/off switching is managed in session scope
    final String[] otherSession = new String[ 1 ];
    Thread thread = new Thread( new Runnable() {
      public void run() {
        Fixture.fakeContext();
        ContextProvider.getContext().setStateInfo( new ServiceStateInfo() );
        Fixture.fakeResponseWriter();
        uiCallBackActivator.writeActivationJS();
        otherSession[ 0 ] = Fixture.getAllMarkup();
      } 
    } );
    thread.start();
    thread.join();
    assertEquals( "", otherSession[ 0 ] );
  }
  
  public void testOnOffSwitch() {
    UICallBackActivator uiCallBackActivator
      = new UICallBackActivator( ContextProvider.getSession() );
    Fixture.fakeResponseWriter();
    uiCallBackActivator.writeActivationJS();
    assertEquals( "", Fixture.getAllMarkup() );

    Fixture.fakeResponseWriter();
    UICallBackServiceHandler.activateUICallBacksFor( ID_1 );
    uiCallBackActivator.writeActivationJS();
    uiCallBackActivator.writeActivationJS();
    assertFalse( "".equals( Fixture.getAllMarkup() ) );

    Fixture.fakeResponseWriter();
    UICallBackServiceHandler.deactivateUICallBacksFor( ID_1 );
    uiCallBackActivator.writeActivationJS();
    assertEquals( "", Fixture.getAllMarkup() );
    
    Fixture.fakeResponseWriter();
    UICallBackServiceHandler.activateUICallBacksFor( ID_1 );
    UICallBackServiceHandler.activateUICallBacksFor( ID_2 );
    uiCallBackActivator.writeActivationJS();
    assertFalse( "".equals( Fixture.getAllMarkup() ) );
    
    Fixture.fakeResponseWriter();
    UICallBackServiceHandler.deactivateUICallBacksFor( ID_1 );
    uiCallBackActivator.writeActivationJS();
    assertFalse( "".equals( Fixture.getAllMarkup() ) );
    
    Fixture.fakeResponseWriter();
    UICallBackServiceHandler.deactivateUICallBacksFor( ID_2 );
    uiCallBackActivator.writeActivationJS();
    assertEquals( "", Fixture.getAllMarkup() );
  }
  
  public void testResponseContentType() throws IOException {
    Fixture.fakeResponseWriter();
    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    response.setOutputStream( new TestServletOutputStream() );
    UICallBackServiceHandler.writeResponse();
    assertEquals( HTML.CONTENT_TEXT_JAVASCRIPT_UTF_8, 
                  response.getHeader( "Content-Type" ) );
  }
  
  public void testActivateDeactivateWithPendingRunnables() throws Exception {
    UICallBackServiceHandler.activateUICallBacksFor( ID_1 );
    UICallBackServiceHandler.deactivateUICallBacksFor( ID_1 );
    UICallBackManager.getInstance().addAsync( new Display(), new Runnable() {
      public void run() {
      }
    } );
    Fixture.fakeResponseWriter();
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.afterPhaseExecution( PhaseId.RENDER );
    assertEquals( "org.eclipse.swt.Request.getInstance().enableUICallBack();", 
                  Fixture.getAllMarkup() );
  }
}