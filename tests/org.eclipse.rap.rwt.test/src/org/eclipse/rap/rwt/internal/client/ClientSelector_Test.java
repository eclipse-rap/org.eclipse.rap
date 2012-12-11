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
package org.eclipse.rap.rwt.internal.client;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.client.Client;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestSession;


public class ClientSelector_Test extends TestCase {

  private ClientSelector clientSelector;
  private UISession uiSession;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    uiSession = new UISessionImpl( new TestSession() );
    clientSelector = new ClientSelector();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testNoClientSelectedByDefault() {
    assertNull( clientSelector.getSelectedClient( uiSession ) );
  }

  public void testSelectsMatchingClient() {
    Client client = mock( Client.class );
    clientSelector.addClientProvider( mockClientProvider( true, client ) );

    clientSelector.selectClient( mockRequest(), uiSession );

    assertSame( client, clientSelector.getSelectedClient( uiSession ) );
  }

  public void testSelectFailsWithoutClientProviders() {
    try {
      clientSelector.selectClient( mockRequest(), uiSession );
      fail();
    } catch( IllegalStateException exception ) {
      assertEquals( "No client provider found for request", exception.getMessage() );
    }
  }

  public void testSelectFailsWithoutMatchingClientProviders() {
    clientSelector.addClientProvider( mockClientProvider( false, null ) );

    try {
      clientSelector.selectClient( mockRequest(), uiSession );
      fail();
    } catch( Exception exception ) {
      assertEquals( "No client provider found for request", exception.getMessage() );
    }
  }

  public void testSelectsFirstMatchingClient() {
    Client expected = mock( Client.class );
    clientSelector.addClientProvider( mockClientProvider( false, null ) );
    clientSelector.addClientProvider( mockClientProvider( true, expected ) );
    clientSelector.addClientProvider( mockClientProvider( true, mock( Client.class ) ) );

    clientSelector.selectClient( mockRequest(), uiSession );

    assertSame( expected, clientSelector.getSelectedClient( uiSession ) );
  }

  public void testActivateInstallsWebClientProvider() {
    clientSelector.activate();

    clientSelector.selectClient( mockRequest(), uiSession );

    assertTrue( clientSelector.getSelectedClient( uiSession ) instanceof WebClient );
  }

  public void testWebClientDoesNotOverrideOthers() {
    Client client = mock( Client.class );
    clientSelector.addClientProvider( mockClientProvider( true, client ) );
    clientSelector.activate();

    clientSelector.selectClient( mockRequest(), uiSession );

    assertSame( client, clientSelector.getSelectedClient( uiSession ) );
  }

  public void testFallbackToWebClient() {
    clientSelector.addClientProvider( mockClientProvider( false, null ) );
    clientSelector.activate();

    clientSelector.selectClient( mockRequest(), uiSession );

    assertTrue( clientSelector.getSelectedClient( uiSession ) instanceof WebClient );
  }

  public void testCannotActivateTwice() {
    clientSelector.activate();
    try {
      clientSelector.activate();
      fail();
    } catch( IllegalStateException exception ) {
      assertEquals( "ClientSelector already activated", exception.getMessage() );
    }
  }

  public void testCannotAddClientProviderAfterActivation() {
    clientSelector.activate();
    try {
      clientSelector.addClientProvider( mock( ClientProvider.class ) );
      fail();
    } catch( IllegalStateException exception ) {
      assertEquals( "ClientSelector already activated", exception.getMessage() );
    }
  }

  private static HttpServletRequest mockRequest() {
    return mock( HttpServletRequest.class );
  }

  private static ClientProvider mockClientProvider( boolean accept, Client client ) {
    ClientProvider provider = mock( ClientProvider.class );
    HttpServletRequest anyReq = any( HttpServletRequest.class );
    when( Boolean.valueOf( provider.accept( anyReq ) ) ).thenReturn( Boolean.valueOf( accept ) );
    when( provider.getClient() ).thenReturn( client );
    return provider;
  }

}
