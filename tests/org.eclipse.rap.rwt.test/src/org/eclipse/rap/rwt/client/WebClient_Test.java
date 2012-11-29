/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.client;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.client.service.BrowserHistory;
import org.eclipse.rap.rwt.client.service.ClientService;
import org.eclipse.rap.rwt.client.service.ExitConfirmation;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.rap.rwt.internal.client.ExitConfirmationImpl;
import org.eclipse.rap.rwt.internal.resources.JavaScriptModuleLoader;
import org.eclipse.rap.rwt.internal.resources.JavaScriptModuleLoaderImpl;
import org.eclipse.rap.rwt.internal.widgets.BrowserHistoryImpl;
import org.eclipse.rap.rwt.internal.widgets.JavaScriptExecutorImpl;
import org.eclipse.rap.rwt.testfixture.Fixture;


public class WebClient_Test extends TestCase {

  private WebClient client;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    client = new WebClient();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testGetInvalidService() {
    assertNull( client.getService( UnsupportedService.class ) );
  }

  public void testGetJavaScriptExecutorService() {
    ClientService service = client.getService( JavaScriptExecutor.class );
    assertTrue( service instanceof JavaScriptExecutorImpl );
  }

  public void testGetServiveTwice() {
    ClientService service1 = client.getService( JavaScriptExecutor.class );
    ClientService service2 = client.getService( JavaScriptExecutor.class );

    assertSame( service1, service2 );
  }

  public void testGetBrowserHistoryService() {
    ClientService service = client.getService( BrowserHistory.class );
    assertTrue( service instanceof BrowserHistoryImpl );
  }

  public void testGetExitConfirmationService() {
    ClientService service = client.getService( ExitConfirmation.class );
    assertTrue( service instanceof ExitConfirmationImpl );
  }

  public void testGetJavaScriptLoaderService() {
    ClientService service = client.getService( JavaScriptModuleLoader.class );
    assertTrue( service instanceof JavaScriptModuleLoaderImpl );
  }

  //////////////////
  // Helping classes

  private class UnsupportedService implements ClientService {
  }
}
