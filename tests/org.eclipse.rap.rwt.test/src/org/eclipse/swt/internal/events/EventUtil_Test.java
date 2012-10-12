/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.events;

import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.internal.events.EventUtil;

import junit.framework.TestCase;


public class EventUtil_Test extends TestCase {
  
  public void testGetLastEventTimeInSameRequest() {
    int eventTime1 = EventUtil.getLastEventTime();
    int eventTime2 = EventUtil.getLastEventTime();
    
    assertEquals( eventTime1, eventTime2 - 1 );
  }
  
  public void testGetLastEventTimeInSubsequentRequests() throws InterruptedException {
    int eventTime1 = EventUtil.getLastEventTime();
    simulateNewRequest();
    Thread.sleep( 5 );
    int eventTime2 = EventUtil.getLastEventTime();
    
    assertTrue( eventTime1 < eventTime2 );
  }
  
  public void testGetLastEventTimeWithoutCurrentPhase() {
    Fixture.fakePhase( null );
    int eventTime = EventUtil.getLastEventTime();

    assertTrue( eventTime > 0 );
  }
  
  public void testGetLastEventTimeOutsideRequest() {
    ContextProvider.releaseContextHolder();
    int eventTime = EventUtil.getLastEventTime();
    
    assertTrue( eventTime > 0 );
  }
  
  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.READ_DATA );
  }

  @Override
  protected void tearDown() throws Exception {
    if( !ContextProvider.hasContext() ) {
      Fixture.createServiceContext();
    }
    Fixture.tearDown();
  }

  private void simulateNewRequest() {
    ContextProvider.releaseContextHolder();
    Fixture.createServiceContext();
    Fixture.fakePhase( PhaseId.READ_DATA );
  }

}
