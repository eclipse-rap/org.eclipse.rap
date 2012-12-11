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
package org.eclipse.rap.rwt.internal.engine;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.testfixture.TestSession;


public class PostDeserialization_Test extends TestCase {

  private UISession uiSession;
  private boolean wasExecuted;

  public void testRunProcessor() {
    PostDeserialization.addProcessor( uiSession, new Runnable() {
      public void run() {
        wasExecuted = true;
      }
    } );

    PostDeserialization.runProcessors( uiSession );

    assertTrue( wasExecuted );
  }

  public void testRunProcessorFromDifferentUISession() {
    PostDeserialization.addProcessor( uiSession, new Runnable() {
      public void run() {
        wasExecuted = true;
      }
    } );

    UISessionImpl differentUiSession = createUISession();
    PostDeserialization.runProcessors( differentUiSession );

    assertFalse( wasExecuted );
  }

  @Override
  protected void setUp() throws Exception {
    uiSession = createUISession();
    wasExecuted = false;
  }

  private static UISessionImpl createUISession() {
    return new UISessionImpl( new TestSession() );
  }
}
