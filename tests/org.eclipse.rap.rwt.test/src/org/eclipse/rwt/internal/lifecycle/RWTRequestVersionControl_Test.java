/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
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

import org.eclipse.rwt.Fixture;


public class RWTRequestVersionControl_Test extends TestCase {
  
  public void testInitialRequestId() {
    Integer requestId = RWTRequestVersionControl.getInstance().nextRequestId();
    
    assertEquals( 0, requestId.intValue() );
  }
  
  public void testIsValidForInitialRequest() {
    Fixture.fakeRequestParam( RWTRequestVersionControl.REQUEST_COUNTER, null );

    boolean valid = RWTRequestVersionControl.getInstance().isValid();
    
    assertTrue( valid );
  }
  
  public void testIsValid() {
    Integer nextRequestId = RWTRequestVersionControl.getInstance().nextRequestId();
    Fixture.fakeRequestParam( RWTRequestVersionControl.REQUEST_COUNTER, nextRequestId.toString() );
    
    boolean valid = RWTRequestVersionControl.getInstance().isValid();
    
    assertTrue( valid );
  }
  
  public void testIsValidWithUnknownRequestVersion() {
    RWTRequestVersionControl.getInstance().nextRequestId();
    Fixture.fakeRequestParam( RWTRequestVersionControl.REQUEST_COUNTER, "4711" );
    
    boolean valid = RWTRequestVersionControl.getInstance().isValid();
    
    assertFalse( valid );
  }
  
  public void testIsValidWhenNoRequestVersionWasSent() {
    RWTRequestVersionControl.getInstance().nextRequestId();
    RWTRequestVersionControl.getInstance().nextRequestId();
    Fixture.fakeRequestParam( RWTRequestVersionControl.REQUEST_COUNTER, null );
    
    boolean valid = RWTRequestVersionControl.getInstance().isValid();
    
    assertTrue( valid );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
