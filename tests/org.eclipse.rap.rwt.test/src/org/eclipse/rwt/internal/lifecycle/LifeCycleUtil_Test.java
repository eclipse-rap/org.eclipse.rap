/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.swt.widgets.Display;


public class LifeCycleUtil_Test extends TestCase {
  
  public void testGetEntryPointWithStartupParameter() {
    String entryPoint = "foo";
    Fixture.fakeRequestParam( RequestParams.STARTUP, entryPoint );
    
    String returnedEntryPoint = LifeCycleUtil.getEntryPoint();
    
    assertEquals( entryPoint, returnedEntryPoint );
  }

  public void testGetEntryPointWithoutStartupParameter() {
    String entryPoint = LifeCycleUtil.getEntryPoint();
    assertEquals( EntryPointManager.DEFAULT, entryPoint );
  }
  
  public void testGetEntryPointWhenDisplayWasCreated() {
    new Display();
    String entryPoint = LifeCycleUtil.getEntryPoint();
    assertNull( entryPoint );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
