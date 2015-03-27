/*******************************************************************************
 * Copyright (c) 2014, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.testfixture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.Client;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.internal.util.HTTP;
import org.eclipse.rap.rwt.remote.Connection;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.rap.rwt.testfixture.internal.TestHttpSession;
import org.eclipse.rap.rwt.testfixture.internal.TestRequest;
import org.eclipse.rap.rwt.testfixture.internal.TestResourceManager;
import org.eclipse.rap.rwt.testfixture.internal.TestResponse;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class TestContext_Test {

  @Rule
  public TestContext context = new TestContext();

  @Before
  public void setUp() {
    assertNotNull( context.getApplicationContext() );
    assertNotNull( context.getUISession() );
    context.getApplicationContext().setAttribute( "foo", "bar" );
    context.getUISession().setAttribute( "foo", "bar" );
  }

  @After
  public void tearDown() {
    assertNotNull( context.getApplicationContext() );
    assertNotNull( context.getUISession() );
  }

  @Test
  public void testStoresAreAvailable() {
    assertSame( RWT.getApplicationContext(), context.getApplicationContext() );
    assertSame( RWT.getUISession(), context.getUISession() );
  }

  @Test
  public void testStoresAreAccessible() {
    assertEquals( "bar", context.getApplicationContext().getAttribute( "foo" ) );
    assertEquals( "bar", context.getUISession().getAttribute( "foo" ) );
  }

  @Test
  public void testDisplayAndWidgetsCanBeCreated() {
    Display display = new Display();
    Shell shell = new Shell( display );

    shell.open();

    assertTrue( shell.isVisible() );
  }

  @Test
  public void testListenersAreNotified() {
    Listener listener = mock( Listener.class );
    Event event = new Event();
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.addListener( SWT.Resize, listener );

    shell.notifyListeners( SWT.Resize, event );

    verify( listener ).handleEvent( event );
  }

  @Test
  public void testLayoutCanBePerformed() {
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.setLayout( new GridLayout() );
    Button button = new Button(shell, SWT.PUSH );
    button.setText( "foo" );

    shell.layout();

    assertTrue( button.getSize().x > 0 );
  }

  @Test
  public void testResourceManager() {
    ResourceManager resourceManager = context.getApplicationContext().getResourceManager();

    assertTrue( resourceManager instanceof TestResourceManager );
  }

  public void testClientIsAvailable() {
    assertTrue( RWT.getClient() instanceof WebClient );
  }

  @Test
  public void testRequest_isAvailable() {
    assertTrue( RWT.getRequest() instanceof TestRequest );
  }

  @Test
  public void testRequest_hasHttpSession() {
    HttpServletRequest request = RWT.getRequest();

    assertNotNull( request.getSession() );
    assertTrue( request.getSession() instanceof TestHttpSession );
    assertSame( context.getUISession().getHttpSession(), request.getSession() );
  }

  @Test
  public void testRequest_hasCorrectParametersSet() {
    HttpServletRequest request = RWT.getRequest();

    assertEquals( HTTP.METHOD_POST, request.getMethod() );
    assertEquals( HTTP.CONTENT_TYPE_JSON, request.getContentType() );
    assertTrue( request.getContentLength() > 0 );
  }

  @Test
  public void testResponse_isAvailable() {
    assertTrue( RWT.getResponse() instanceof TestResponse );
  }

  @Test
  public void testReplaceConnection() {
    Connection connection = mock( Connection.class );

    context.replaceConnection( connection );

    assertSame( connection, context.getUISession().getConnection() );
  }

  @Test
  public void testReplaceClient() {
    Client client = mock( Client.class );

    context.replaceClient( client );

    assertSame( client, context.getUISession().getClient() );
  }

}
