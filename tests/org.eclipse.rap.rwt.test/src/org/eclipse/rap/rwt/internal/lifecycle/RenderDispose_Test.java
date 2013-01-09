/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;

import org.eclipse.rap.rwt.lifecycle.PhaseEvent;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.PhaseListener;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class RenderDispose_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testDisposeNotYetInitialized() {
    // set up the test widget hierarchy
    Display display = new Display();
    final Composite shell = new Shell( display , SWT.NONE );
    // render initial message that creates the remote widgets (display and shell)
    Fixture.executeLifeCycleFromServerThread( );
    // create a button and dispose of it immediately
    Fixture.fakeNewRequest();
    LifeCycle lifeCycle = getApplicationContext().getLifeCycleFactory().getLifeCycle();
    lifeCycle.addPhaseListener( new PhaseListener() {
      public PhaseId getPhaseId() {
        return PhaseId.RENDER;
      }
      public void beforePhase( PhaseEvent event ) {
        Button button = new Button( shell, SWT.PUSH );
        button.dispose();
      }
      public void afterPhase( PhaseEvent event ) {
      }
    } );

    Fixture.executeLifeCycleFromServerThread( );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 2, message.getRequestCounter() );
    assertEquals( 0, message.getOperationCount() );
  }

}
