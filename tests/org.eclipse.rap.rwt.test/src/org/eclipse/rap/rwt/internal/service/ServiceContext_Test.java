/*******************************************************************************
 * Copyright (c) 2011, 2013 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ServiceContext_Test {

  private UISessionImpl uiSession;
  private ApplicationContextImpl applicationContext;

  @Before
  public void setUp() {
    ApplicationConfiguration applicationConfiguration = mock( ApplicationConfiguration.class );
    ServletContext servletContext = mock( ServletContext.class );
    when( servletContext.getRealPath( anyString() ) ).thenReturn( "" );
    applicationContext = new ApplicationContextImpl( applicationConfiguration, servletContext );
    Fixture.setSkipResourceRegistration( true );
    uiSession = new UISessionImpl( applicationContext, new TestSession() );
  }

  @After
  public void tearDown() {
    if( ContextProvider.hasContext() ) {
      Fixture.disposeOfServiceContext();
    }
    Fixture.resetSkipResourceRegistration();
  }

  @Test
  public void testGetApplicationContext_fromRealContext() {
    applicationContext.activate();
    ServiceContext context = new ServiceContext( mock( HttpServletRequest.class ),
                                                 mock( HttpServletResponse.class ),
                                                 applicationContext );

    ApplicationContextImpl found = context.getApplicationContext();

    assertSame( applicationContext, found );
  }

  @Test
  public void testGetApplicationContext_fromFakeContext() {
    applicationContext.activate();
    ServiceContext context = new ServiceContext( mock( HttpServletRequest.class ),
                                                 mock( HttpServletResponse.class ),
                                                 uiSession );

    ApplicationContextImpl found = context.getApplicationContext();

    assertSame( applicationContext, found );
  }

  @Test
  public void testGetApplicationContext_withDeactivatedApplicationContext() {
    ServiceContext context = new ServiceContext( mock( HttpServletRequest.class ),
                                                 mock( HttpServletResponse.class ),
                                                 applicationContext );

    ApplicationContextImpl found = context.getApplicationContext();

    assertNull( found );
  }

  @Test
  public void testGetApplicationContext_onDisposedServiceContext() {
    ServiceContext context = new ServiceContext( mock( HttpServletRequest.class ),
                                                 mock( HttpServletResponse.class ),
                                                 applicationContext );
    context.dispose();

    try {
      context.getApplicationContext();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

}
