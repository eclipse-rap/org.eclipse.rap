/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
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

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.lifecycle.RWTRequestVersionControl;
import org.eclipse.rap.rwt.testfixture.Fixture;


public class RWTRequestVersionControl_Test extends TestCase {

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeNewRequest();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testInitialRequestId() {
    Integer requestId = RWTRequestVersionControl.getInstance().nextRequestId();

    assertEquals( 0, requestId.intValue() );
  }

  public void testIsValidForInitialRequest() {
    boolean valid = RWTRequestVersionControl.getInstance().isValid();

    assertTrue( valid );
  }

  public void testIsValid() {
    Integer nextRequestId = RWTRequestVersionControl.getInstance().nextRequestId();
    Fixture.fakeHeadParameter( RWTRequestVersionControl.REQUEST_COUNTER, nextRequestId.toString() );

    boolean valid = RWTRequestVersionControl.getInstance().isValid();

    assertTrue( valid );
  }

  public void testIsValidWithUnknownRequestVersion() {
    RWTRequestVersionControl.getInstance().nextRequestId();
    Fixture.fakeHeadParameter( RWTRequestVersionControl.REQUEST_COUNTER, "4711" );

    boolean valid = RWTRequestVersionControl.getInstance().isValid();

    assertFalse( valid );
  }

  public void testIsValidWhenNoRequestVersionWasSent() {
    RWTRequestVersionControl.getInstance().nextRequestId();
    RWTRequestVersionControl.getInstance().nextRequestId();

    boolean valid = RWTRequestVersionControl.getInstance().isValid();

    assertTrue( valid );
  }

  public void testSerialization() throws Exception {
    Integer requestId = RWTRequestVersionControl.getInstance().nextRequestId();

    RWTRequestVersionControl deserialized
      = Fixture.serializeAndDeserialize( RWTRequestVersionControl.getInstance() );

    assertEquals( requestId, deserialized.getCurrentRequestId() );
  }

}
