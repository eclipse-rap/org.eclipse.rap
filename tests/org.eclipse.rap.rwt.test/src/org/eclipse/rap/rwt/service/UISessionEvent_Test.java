/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.service;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.testfixture.TestSession;


public class UISessionEvent_Test extends TestCase {

  private UISessionImpl uiSession;

  @Override
  protected void setUp() throws Exception {
    uiSession = new UISessionImpl( new TestSession() );
  }

  public void testConstructorWithNullArgument() {
    try {
      new UISessionEvent( null );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testGetSource() {
    UISessionEvent event = new UISessionEvent( uiSession );

    Object source = event.getSource();

    assertSame( uiSession, source );
  }

  public void testGetUISession() {
    UISessionEvent event = new UISessionEvent( uiSession );

    UISession result = event.getUISession();

    assertSame( uiSession, result );
  }

}
