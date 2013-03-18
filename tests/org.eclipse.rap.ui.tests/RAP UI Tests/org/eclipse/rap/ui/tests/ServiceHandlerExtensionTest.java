/*******************************************************************************
 * Copyright (c) 2010, 2013 EclipseSource Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.ui.tests;

import java.io.IOException;

import javax.servlet.ServletException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.ServiceManagerImpl;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.eclipse.rap.ui.tests.impl.ServiceHandler1;
import org.eclipse.rap.ui.tests.impl.ServiceHandler2;

public class ServiceHandlerExtensionTest extends TestCase {

  public static String log = "";

  public void testServiceHandler1Registration() throws IOException, ServletException {
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setParameter( ServiceManagerImpl.REQUEST_PARAM, "myHandler1" );

    ServiceManagerImpl serviceManagerImpl = ( ServiceManagerImpl )RWT.getServiceManager();
    serviceManagerImpl.getHandler().service( request, RWT.getResponse() );

    assertEquals( log, ServiceHandler1.class.getName() );
  }

  public void testServiceHandler2Registration() throws IOException, ServletException {
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setParameter( ServiceManagerImpl.REQUEST_PARAM, "myHandler2" );

    ServiceManagerImpl serviceManagerImpl = ( ServiceManagerImpl )RWT.getServiceManager();
    serviceManagerImpl.getHandler().service( RWT.getRequest(), RWT.getResponse() );

    assertEquals( log, ServiceHandler2.class.getName() );
  }

  protected void setUp() {
    Fixture.fakeNewRequest();
    log = "";
  }

  protected void tearDown() {
    log = "";
  }
}
