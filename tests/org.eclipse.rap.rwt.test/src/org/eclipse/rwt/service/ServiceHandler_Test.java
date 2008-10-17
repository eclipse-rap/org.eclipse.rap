/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.Fixture.TestResponse;
import org.eclipse.rwt.Fixture.TestServletOutputStream;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.service.LifeCycleServiceHandler.ILifeCycleServiceHandlerConfigurer;
import org.eclipse.rwt.internal.service.LifeCycleServiceHandler.LifeCycleServiceHandlerSync;
import org.eclipse.swt.RWTFixture;



public class ServiceHandler_Test extends TestCase {
  
  private final static String HANDLER_ID 
    = "org.eclipse.rwt.service.ServiceHandler_Test.CustomHandler";
  private final static String PROGRAMATIC_HANDLER_ID 
    = "programmaticServiceHandlerId";
  private static final String SERVICE_DONE = "service done";
  private static String log = "";
  
  public static class CustomHandler implements IServiceHandler {
    public void service() throws ServletException, IOException {
      log = SERVICE_DONE;
    }    
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
    Fixture.createContext( false );
  }
  
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
    Fixture.removeContext();
    log = "";
  }
  
  public void testCustomServiceHandler() throws Exception {
    initResponseOutputStream();
    Fixture.fakeRequestParam( IServiceHandler.REQUEST_PARAM, HANDLER_ID );
    ServiceManager.getHandler().service();
    assertEquals( SERVICE_DONE, log );
  }
  
  public void testProgramaticallyRegsiteredHandler() throws Exception {
    initResponseOutputStream();
    // Register
    RWT.getServiceManager().registerServiceHandler( PROGRAMATIC_HANDLER_ID, 
                                                    new CustomHandler() );
    Fixture.fakeRequestParam( IServiceHandler.REQUEST_PARAM, 
                              PROGRAMATIC_HANDLER_ID );
    ServiceManager.getHandler().service();
    assertEquals( SERVICE_DONE, log );
    // Unregister
    ILifeCycleServiceHandlerConfigurer bufferedConfigurer 
      = LifeCycleServiceHandler.configurer;
    LifeCycleServiceHandler.configurer 
      = new ILifeCycleServiceHandlerConfigurer()
    {
      public LifeCycleServiceHandlerSync getSynchronizationHandler() {
        return new LifeCycleServiceHandlerSync() {
          public void service() throws ServletException, IOException {
            doService();
          }
        };
      }
      public TemplateHolder getTemplateOfStartupPage() throws IOException {
        return new TemplateHolder( "Startup Page" );
      }
      public boolean isStartupPageModifiedSince() {
        return true;
      }
    };

    log = "";
    RWT.getServiceManager().unregisterServiceHandler( PROGRAMATIC_HANDLER_ID );
    ServiceManager.getHandler().service();
    assertEquals( "", log );
    LifeCycleServiceHandler.configurer = bufferedConfigurer; 
  }

  private void initResponseOutputStream() {
    HttpServletResponse response = ContextProvider.getResponse();
    TestResponse testResponse = ( TestResponse )response;
    testResponse.setOutputStream( new TestServletOutputStream() );
  }
}
