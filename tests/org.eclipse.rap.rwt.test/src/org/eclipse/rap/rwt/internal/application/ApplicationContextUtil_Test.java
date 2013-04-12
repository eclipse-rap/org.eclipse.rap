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
package org.eclipse.rap.rwt.internal.application;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import javax.servlet.ServletContext;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.Before;
import org.junit.Test;


public class ApplicationContextUtil_Test {

  private ApplicationContextImpl applicationContext;

  @Before
  public void setUp() {
    applicationContext = new ApplicationContextImpl( null, null );
  }

  @Test
  public void testSetToServletContext() {
    ServletContext servletContext = Fixture.createServletContext();

    ApplicationContextUtil.set( servletContext, applicationContext );

    assertSame( applicationContext, ApplicationContextUtil.get( servletContext ) );
  }

  @Test
  public void testRemoveFromServletContext() {
    ServletContext servletContext = Fixture.createServletContext();
    ApplicationContextUtil.set( servletContext, applicationContext );

    ApplicationContextUtil.remove( servletContext );

    assertNull( ApplicationContextUtil.get( servletContext ) );
  }

}
