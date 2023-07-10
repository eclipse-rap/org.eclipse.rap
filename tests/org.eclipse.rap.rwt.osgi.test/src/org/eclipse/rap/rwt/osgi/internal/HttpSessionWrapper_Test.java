/*******************************************************************************
 * Copyright (c) 2011, 2023 Frank Appel and others.
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;


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
    wrapper.getSessionContext();
    wrapper.getValue( name );
    wrapper.getValueNames();
    wrapper.invalidate();
    wrapper.isNew();
    wrapper.putValue( name, object );
    wrapper.removeAttribute( name );
    wrapper.removeValue( name );
    wrapper.setAttribute( name, object );
    wrapper.setMaxInactiveInterval( 3 );

    verify( session, times( 2 ) ).getAttribute( "name" );
    verify( session, times( 2 ) ).getAttributeNames();
    verify( session ).getCreationTime();
    verify( session ).getId();
    verify( session ).getLastAccessedTime();
    verify( session ).getMaxInactiveInterval();
    verify( session ).getSessionContext();
    verify( session, never() ).getValue( name );
    verify( session, never() ).getValueNames();
    verify( session ).invalidate();
    verify( session ).isNew();
    verify( session, never() ).putValue( name, object );
    verify( session, times( 2 ) ).removeAttribute( name );
    verify( session, never() ).removeValue( name );
    verify( session, times( 2 ) ).setAttribute( name, object );
    verify( session ).setMaxInactiveInterval( 3 );
  }

  @Test
  public void testGetServletContext() {
    ServletContext found = wrapper.getServletContext();

    assertSame( servletContext, found );
  }

}
