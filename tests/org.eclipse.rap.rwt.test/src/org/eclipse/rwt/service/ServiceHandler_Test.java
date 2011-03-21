/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
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

package org.eclipse.rwt.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.eclipse.rwt.*;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.ServiceManager;
import org.eclipse.rwt.internal.util.ClassInstantiationException;


public class ServiceHandler_Test extends TestCase {
  private final static String HANDLER_ID 
    = "org.eclipse.rwt.service.ServiceHandler_Test.CustomHandler";
  private final static String PROGRAMATIC_HANDLER_ID 
    = "programmaticServiceHandlerId";
  private static final String SERVICE_DONE = "service done";
  private static String log = "";
  private static boolean throwException = false;
  
  
  public static class CustomHandler implements IServiceHandler {

    public CustomHandler() {
      if( throwException ) {
        throw new IllegalStateException();
      }
    }
    
    public void service() throws ServletException, IOException {
      log = SERVICE_DONE;
    }    
  }

  public void testCustomServiceHandler() throws Exception {
    Fixture.fakeRequestParam( IServiceHandler.REQUEST_PARAM, HANDLER_ID );
   
    ServiceManager.getHandler().service();
    
    assertEquals( SERVICE_DONE, log );
  }
  
  public void testProgramaticallyRegsiteredHandler() throws Exception {
    String id = PROGRAMATIC_HANDLER_ID;
    RWT.getServiceManager().registerServiceHandler( id, new CustomHandler() );
    Fixture.fakeRequestParam( IServiceHandler.REQUEST_PARAM, id );

    ServiceManager.getHandler().service();
    
    assertEquals( SERVICE_DONE, log );
  }
    
  public void testProgramaticallyUnRegsiteredHandler() throws Exception {
    String id = PROGRAMATIC_HANDLER_ID;
    RWT.getServiceManager().registerServiceHandler( id, new CustomHandler() );
    Fixture.fakeRequestParam( IServiceHandler.REQUEST_PARAM, id );
    RWT.getServiceManager().unregisterServiceHandler( id );

    ServiceManager.getHandler().service();

    assertEquals( "", log );
  }

  public void testCustomServiceHandlerCausingProblem() {
    prepareCustomHandlerThatThrowsExceptionInConstructor();

    try {
      registerCustomHandler();
      fail();
    } catch( ClassInstantiationException expected ) {
      InvocationTargetException ite = ( InvocationTargetException )expected.getCause();
      assertTrue( ite.getCause() instanceof IllegalStateException );
      doTestEnvironmentCleanup();
    }
/////////////////////////////////////
//    TODO [fappel]: Replace catch clause with this code once patch of bug #340482 is applied,
//                   or remove this comment in case patch is declined.    
//  } catch( IllegalStateException expected ) {
//    doTestEnvironmentCleanup();
//  }
  }
  
  protected void setUp() throws Exception {
    throwException = false;
    registerCustomHandler();
    initResponseOutputStream();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
    log = "";
  }
  
  private void initResponseOutputStream() {
    HttpServletResponse response = ContextProvider.getResponse();
    TestResponse testResponse = ( TestResponse )response;
    testResponse.setOutputStream( new TestServletOutputStream() );
  }

  private void registerCustomHandler() {
    Fixture.setUp();
  }

  private void prepareCustomHandlerThatThrowsExceptionInConstructor() {
    Fixture.tearDown();
    throwException = true;
  }

  private void doTestEnvironmentCleanup() {
    throwException = false;
    registerCustomHandler();
  }  
}