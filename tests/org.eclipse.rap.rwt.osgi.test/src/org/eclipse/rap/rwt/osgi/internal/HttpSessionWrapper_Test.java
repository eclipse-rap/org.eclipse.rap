/*******************************************************************************
 * Copyright (c) 2011, 2024 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.osgi.internal;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;


public class HttpSessionWrapper_Test {
  private HttpSession session;
  private ServletContext servletContext;
  private HttpSessionWrapper wrapper;

  @Before
  public void setUp() {
    session = mock( HttpSession.class );
    when( session.getAttributeNames() ).thenReturn( Collections.emptyEnumeration() );
    servletContext = mock( ServletContext.class );
    wrapper = new HttpSessionWrapper( session, servletContext );
  }

  @Test
  @SuppressWarnings( "deprecation" )
  public void testDelegation() {
    String name = "name";
    Object object = new Object();

    wrapper.getAttribute( name );
    wrapper.getAttributeNames();
    wrapper.getCreationTime();
    wrapper.getId();
    wrapper.getLastAccessedTime();
    wrapper.getMaxInactiveInterval();
    wrapper.invalidate();
    wrapper.isNew();
    wrapper.removeAttribute( name );
    wrapper.setAttribute( name, object );
    wrapper.setMaxInactiveInterval( 3 );

    verify( session ).getAttribute( "name" );
    verify( session ).getAttributeNames();
    verify( session ).getCreationTime();
    verify( session ).getId();
    verify( session ).getLastAccessedTime();
    verify( session ).getMaxInactiveInterval();
    verify( session ).invalidate();
    verify( session ).isNew();
    verify( session ).removeAttribute( name );
    verify( session ).setAttribute( name, object );
    verify( session ).setMaxInactiveInterval( 3 );
  }

  @Test
  public void testGetServletContext() {
    ServletContext found = wrapper.getServletContext();

    assertSame( servletContext, found );
  }

}
