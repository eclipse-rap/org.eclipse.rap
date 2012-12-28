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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.testfixture.TestSession;
import org.junit.Before;
import org.junit.Test;


public class PostDeserialization_Test {

  private UISession uiSession;
  private boolean wasExecuted;

  @Before
  public void setUp() {
    uiSession = createUISession();
    wasExecuted = false;
  }

  @Test
  public void testRunProcessor() {
    PostDeserialization.addProcessor( uiSession, new Runnable() {
      public void run() {
        wasExecuted = true;
      }
    } );

    PostDeserialization.runProcessors( uiSession );

    assertTrue( wasExecuted );
  }

  @Test
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

  private static UISessionImpl createUISession() {
    return new UISessionImpl( new TestSession() );
  }

}
