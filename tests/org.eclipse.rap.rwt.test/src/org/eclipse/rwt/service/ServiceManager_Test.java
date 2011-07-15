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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.service.ServiceManager;


public class ServiceManager_Test extends TestCase {
  private static final String SERVICE_HANDLER_ID = "serviceHandlerId";

  private IServiceHandler lifeCycleServiceHandler;
  private ServiceManager serviceManager;
  
  public void testRegsiterServiceHandler() throws Exception {
    IServiceHandler serviceHandler = mock( IServiceHandler.class );
    serviceManager.registerServiceHandler( SERVICE_HANDLER_ID, serviceHandler );

    Fixture.fakeRequestParam( IServiceHandler.REQUEST_PARAM, SERVICE_HANDLER_ID );
    serviceManager.getHandler().service();

    verify( serviceHandler ).service();
  }

  public void testRegsiterServiceHandlerTwice() throws Exception {
    IServiceHandler firstHandler = mock( IServiceHandler.class );
    serviceManager.registerServiceHandler( SERVICE_HANDLER_ID, firstHandler );
    IServiceHandler secondHandler = mock( IServiceHandler.class );
    serviceManager.registerServiceHandler( SERVICE_HANDLER_ID, secondHandler );

    Fixture.fakeRequestParam( IServiceHandler.REQUEST_PARAM, SERVICE_HANDLER_ID );
    serviceManager.getHandler().service();
    
    verifyZeroInteractions( firstHandler );
    verify( secondHandler ).service();
  }
  
  public void testUnregsiterServiceHandler() throws Exception {
    IServiceHandler serviceHandler = mock( IServiceHandler.class );
    serviceManager.registerServiceHandler( SERVICE_HANDLER_ID, serviceHandler );

    serviceManager.unregisterServiceHandler( SERVICE_HANDLER_ID );

    Fixture.fakeRequestParam( IServiceHandler.REQUEST_PARAM, SERVICE_HANDLER_ID );
    serviceManager.getHandler().service();
    verifyZeroInteractions( serviceHandler );
  }
  
  public void testClear() throws Exception {
    IServiceHandler serviceHandler = mock( IServiceHandler.class );
    serviceManager.registerServiceHandler( SERVICE_HANDLER_ID, serviceHandler );

    serviceManager.clear();

    Fixture.fakeRequestParam( IServiceHandler.REQUEST_PARAM, SERVICE_HANDLER_ID );
    serviceManager.getHandler().service();
    verifyZeroInteractions( serviceHandler );
  }
  
  public void testLifeCycleServiceHandler() throws Exception {
    serviceManager.getHandler().service();
    
    verify( lifeCycleServiceHandler ).service();
  }

  protected void setUp() {
    Fixture.setUp();
    lifeCycleServiceHandler = mock( IServiceHandler.class );
    serviceManager = new ServiceManager( lifeCycleServiceHandler );
  }

  protected void tearDown() {
    Fixture.tearDown();
  }
}