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


public class RequestCounter_Test extends TestCase {

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeNewRequest();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testInitialValueIsZero() {
    int requestId = RequestCounter.getInstance().currentRequestId();

    assertEquals( 0, requestId );
  }

  public void testNextRequestId_returnsIncrementedValue() {
    int requestId = RequestCounter.getInstance().nextRequestId();

    assertEquals( 1, requestId );
  }

  public void testNextCurrentRequestId_doesNotModifyValue() {
    RequestCounter.getInstance().currentRequestId();
    int requestId = RequestCounter.getInstance().currentRequestId();

    assertEquals( 0, requestId );
  }

  public void testIsValid_trueWithValidParameter() {
    int nextRequestId = RequestCounter.getInstance().nextRequestId();
    Fixture.fakeHeadParameter( "requestCounter", Integer.toString( nextRequestId ) );

    boolean valid = RequestCounter.getInstance().isValid();

    assertTrue( valid );
  }

  public void testIsValid_falseWithInvalidParameter() {
    RequestCounter.getInstance().nextRequestId();
    Fixture.fakeHeadParameter( "requestCounter", "23" );

    boolean valid = RequestCounter.getInstance().isValid();

    assertFalse( valid );
  }

  public void testIsValid_falseWithIllegalParameterFormat() {
    RequestCounter.getInstance().nextRequestId();
    Fixture.fakeHeadParameter( "requestCounter", "not-a-number" );

    boolean valid = RequestCounter.getInstance().isValid();

    assertFalse( valid );
  }

  public void testIsValid_toleratesMissingParameterInFirstRequest() {
    boolean valid = RequestCounter.getInstance().isValid();

    assertTrue( valid );
  }

  public void testIsValid_falseWithMissingParameter() {
    RequestCounter.getInstance().nextRequestId();

    boolean valid = RequestCounter.getInstance().isValid();

    assertFalse( valid );
  }

  public void testSerialization() throws Exception {
    RequestCounter instance = RequestCounter.getInstance();
    instance.nextRequestId(); // ensure counter differs from zero
    int currentRequestId = instance.nextRequestId();

    RequestCounter deserialized = Fixture.serializeAndDeserialize( instance );

    assertEquals( currentRequestId, deserialized.currentRequestId() );
  }

}
