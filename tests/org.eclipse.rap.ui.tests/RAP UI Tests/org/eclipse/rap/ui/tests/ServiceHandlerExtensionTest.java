/*******************************************************************************
 * Copyright (c) 2010 EclipseSource Corporation and others.
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

import org.eclipse.rap.ui.tests.impl.ServiceHandler1;
import org.eclipse.rap.ui.tests.impl.ServiceHandler2;
import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.service.ServiceManager;
import org.eclipse.rwt.service.IServiceHandler;

public class ServiceHandlerExtensionTest extends TestCase {
  
  public static String log = "";
  
  public void testServiceHandler1Registration() throws IOException, ServletException {
    Fixture.fakeRequestParam( IServiceHandler.REQUEST_PARAM, "myHandler1" );
    
    ServiceManager.getHandler().service();

    assertEquals( log, ServiceHandler1.class.getName() );
  }

  public void testServiceHandler2Registration() throws IOException, ServletException {
    Fixture.fakeRequestParam( IServiceHandler.REQUEST_PARAM, "myHandler2" );
    
    ServiceManager.getHandler().service();

    assertEquals( log, ServiceHandler2.class.getName() );
  }

  protected void setUp() {
    Fixture.fakeNewRequest();
    log = "";
  }
  
  protected void tearDown() {
    Fixture.fakeRequestParam( IServiceHandler.REQUEST_PARAM, null );
    log = "";
  }
}
