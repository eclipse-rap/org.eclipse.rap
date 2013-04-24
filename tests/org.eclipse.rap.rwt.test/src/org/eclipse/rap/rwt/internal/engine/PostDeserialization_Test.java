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
package org.eclipse.rap.rwt.internal.engine;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.testfixture.TestSession;
import org.junit.Before;
import org.junit.Test;


public class PostDeserialization_Test {

  private UISession uiSession;

  @Before
  public void setUp() {
    uiSession = createUISession();
  }

  @Test
  public void testRunProcessors_doesNotFailWithoutProcessorsAdded() {
    PostDeserialization.runProcessors( uiSession );
  }

  @Test
  public void testRunProcessors_runsAllAddedProcessors() {
    Runnable processor1 = mock( Runnable.class );
    Runnable processor2 = mock( Runnable.class );
    PostDeserialization.addProcessor( uiSession, processor1 );
    PostDeserialization.addProcessor( uiSession, processor2 );

    PostDeserialization.runProcessors( uiSession );

    verify( processor1 ).run();
    verify( processor2 ).run();
  }

  @Test
  public void testRunProcessors_withDifferentUISession() {
    Runnable processor = mock( Runnable.class );
    PostDeserialization.addProcessor( uiSession, processor );

    UISessionImpl differentUiSession = createUISession();
    PostDeserialization.runProcessors( differentUiSession );

    verifyZeroInteractions( processor );
  }

  private static UISessionImpl createUISession() {
    return new UISessionImpl( mock( ApplicationContextImpl.class ), new TestSession() );
  }

}
