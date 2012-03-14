/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
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
import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.internal.service.ServiceManager;


public class ServiceManager_Test extends TestCase {
  private static final String SERVICE_HANDLER_ID = "serviceHandlerId";

  private IServiceHandler lifeCycleServiceHandler;
  private ServiceManager serviceManager;

  @Override
  protected void setUp() {
    Fixture.setUp();
    lifeCycleServiceHandler = mock( IServiceHandler.class );
    serviceManager = new ServiceManager( lifeCycleServiceHandler );
  }

  @Override
  protected void tearDown() {
    Fixture.tearDown();
  }

  public void testRegisterServiceHandler() throws Exception {
    IServiceHandler serviceHandler = mock( IServiceHandler.class );

    serviceManager.registerServiceHandler( SERVICE_HANDLER_ID, serviceHandler );

    assertSame( serviceHandler, serviceManager.getServiceHandler( SERVICE_HANDLER_ID ) );
  }

  public void testRegisterServiceHandlerTwice() throws Exception {
    IServiceHandler firstHandler = mock( IServiceHandler.class );
    IServiceHandler secondHandler = mock( IServiceHandler.class );

    serviceManager.registerServiceHandler( SERVICE_HANDLER_ID, firstHandler );
    serviceManager.registerServiceHandler( SERVICE_HANDLER_ID, secondHandler );

    assertSame( secondHandler, serviceManager.getServiceHandler( SERVICE_HANDLER_ID ) );
  }

  public void testUnregisterServiceHandler() throws Exception {
    IServiceHandler serviceHandler = mock( IServiceHandler.class );
    serviceManager.registerServiceHandler( SERVICE_HANDLER_ID, serviceHandler );

    serviceManager.unregisterServiceHandler( SERVICE_HANDLER_ID );

    assertNull( serviceManager.getServiceHandler( SERVICE_HANDLER_ID ) );
  }

  public void testClear() throws Exception {
    IServiceHandler serviceHandler = mock( IServiceHandler.class );
    serviceManager.registerServiceHandler( SERVICE_HANDLER_ID, serviceHandler );

    serviceManager.clear();

    assertNull( serviceManager.getServiceHandler( SERVICE_HANDLER_ID ) );
  }

  public void testGetHandler_default() throws Exception {
    assertSame( lifeCycleServiceHandler, serviceManager.getHandler() );
  }

  public void testGetHandler_custom() throws Exception {
    IServiceHandler serviceHandler = mock( IServiceHandler.class );
    serviceManager.registerServiceHandler( SERVICE_HANDLER_ID, serviceHandler );

    Fixture.fakeRequestParam( IServiceHandler.REQUEST_PARAM, SERVICE_HANDLER_ID );
    IServiceHandler handler = serviceManager.getHandler();

    assertSame( serviceHandler, handler );
  }

  public void testGetHandler_unknownId() throws Exception {
    Fixture.fakeRequestParam( IServiceHandler.REQUEST_PARAM, SERVICE_HANDLER_ID );

    try {
      serviceManager.getHandler();
      fail();
    } catch( IllegalArgumentException expected ) {
      assertTrue( expected.getMessage().contains( SERVICE_HANDLER_ID ) );
    }
  }
}
