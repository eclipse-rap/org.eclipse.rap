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

import org.eclipse.rap.rwt.testfixture.Fixture;


public class RequestId_Test extends TestCase {

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
    Integer requestId = RequestId.getInstance().nextRequestId();

    assertEquals( 0, requestId.intValue() );
  }

  public void testIsValidForInitialRequest() {
    boolean valid = RequestId.getInstance().isValid();

    assertTrue( valid );
  }

  public void testIsValid() {
    Integer nextRequestId = RequestId.getInstance().nextRequestId();
    Fixture.fakeHeadParameter( RequestId.REQUEST_COUNTER, nextRequestId.toString() );

    boolean valid = RequestId.getInstance().isValid();

    assertTrue( valid );
  }

  public void testIsValidWithUnknownRequestVersion() {
    RequestId.getInstance().nextRequestId();
    Fixture.fakeHeadParameter( RequestId.REQUEST_COUNTER, "4711" );

    boolean valid = RequestId.getInstance().isValid();

    assertFalse( valid );
  }

  public void testIsValidWhenNoRequestVersionWasSent() {
    RequestId.getInstance().nextRequestId();
    RequestId.getInstance().nextRequestId();

    boolean valid = RequestId.getInstance().isValid();

    assertTrue( valid );
  }

  public void testSerialization() throws Exception {
    Integer requestId = RequestId.getInstance().nextRequestId();

    RequestId deserialized
      = Fixture.serializeAndDeserialize( RequestId.getInstance() );

    assertEquals( requestId, deserialized.getCurrentRequestId() );
  }

}
