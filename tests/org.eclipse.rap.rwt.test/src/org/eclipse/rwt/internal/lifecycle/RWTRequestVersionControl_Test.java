/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.lifecycle;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture.TestRequest;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.RWTFixture;


public class RWTRequestVersionControl_Test extends TestCase {
  
  private static final Integer VERSION_0 = new Integer( 0 );
  private static final Integer VERSION_1 = new Integer( 1 );
  private static final String ATTR_VERSION = RWTRequestVersionControl.VERSION;

  public void testDetermine() {
    RWTRequestVersionControl.determine();
    
    ISessionStore session = ContextProvider.getSession();
    Integer current = ( Integer )session.getAttribute( ATTR_VERSION );
    assertEquals( VERSION_0, current );
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    current = ( Integer )stateInfo.getAttribute( ATTR_VERSION );
    assertEquals( VERSION_0, current );
    
    session.setAttribute( ATTR_VERSION, VERSION_1 );
    RWTRequestVersionControl.determine();
    current = ( Integer )session.getAttribute( ATTR_VERSION );
    assertEquals( VERSION_1, current );
    current = ( Integer )stateInfo.getAttribute( ATTR_VERSION );
    assertEquals( VERSION_1, current );
  }
  
  public void testIncrease() {
    RWTRequestVersionControl.determine();
    
    ISessionStore session = ContextProvider.getSession();
    Integer current = ( Integer )session.getAttribute( ATTR_VERSION );
    assertEquals( VERSION_0, current );
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    current = ( Integer )stateInfo.getAttribute( ATTR_VERSION );
    assertEquals( VERSION_0, current );
    assertFalse( RWTRequestVersionControl.hasChanged() );
    
    RWTRequestVersionControl.increase();
    current = ( Integer )session.getAttribute( ATTR_VERSION );
    assertEquals( VERSION_1, current );
    current = ( Integer )stateInfo.getAttribute( ATTR_VERSION );
    assertEquals( VERSION_1, current );
    
    assertTrue( RWTRequestVersionControl.hasChanged() );
  }
  
  public void testCheck() {
    RWTRequestVersionControl.determine();
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.addParameter( RequestParams.REQUEST_COUNTER, "0" );
    
    assertTrue( RWTRequestVersionControl.check() );
    
    RWTRequestVersionControl.increase();
    assertFalse( RWTRequestVersionControl.check() );
  }
  
  public void testStore() {
    RWTRequestVersionControl.determine();

    ISessionStore session = ContextProvider.getSession();
    Integer current = ( Integer )session.getAttribute( ATTR_VERSION );
    assertEquals( VERSION_0, current );
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    current = ( Integer )stateInfo.getAttribute( ATTR_VERSION );
    assertEquals( VERSION_0, current );
    
    RWTRequestVersionControl.increase();
    current = ( Integer )session.getAttribute( ATTR_VERSION );
    assertEquals( VERSION_1, current );
    current = ( Integer )stateInfo.getAttribute( ATTR_VERSION );
    assertEquals( VERSION_1, current );

    session.setAttribute( ATTR_VERSION, null );
    
    RWTRequestVersionControl.store();
    current = ( Integer )session.getAttribute( ATTR_VERSION );
    assertEquals( VERSION_1, current );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }
  
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
