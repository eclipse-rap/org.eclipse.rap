/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.service;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.testfixture.TestSession;
import org.junit.Before;
import org.junit.Test;


public class UISessionEvent_Test {

  private UISessionImpl uiSession;

  @Before
  public void setUp() {
    uiSession = new UISessionImpl( mock( ApplicationContextImpl.class ), new TestSession() );
  }

  @Test
  public void testConstructorWithNullArgument() {
    try {
      new UISessionEvent( null );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testGetSource() {
    UISessionEvent event = new UISessionEvent( uiSession );

    Object source = event.getSource();

    assertSame( uiSession, source );
  }

  @Test
  public void testGetUISession() {
    UISessionEvent event = new UISessionEvent( uiSession );

    UISession result = event.getUISession();

    assertSame( uiSession, result );
  }

}
