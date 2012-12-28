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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class RequestCounter_Test {

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
  public void testInitialValueIsZero() {
    int requestId = RequestCounter.getInstance().currentRequestId();

    assertEquals( 0, requestId );
  }

  @Test
  public void testNextRequestId_returnsIncrementedValue() {
    int requestId = RequestCounter.getInstance().nextRequestId();

    assertEquals( 1, requestId );
  }

  @Test
  public void testNextCurrentRequestId_doesNotModifyValue() {
    RequestCounter.getInstance().currentRequestId();
    int requestId = RequestCounter.getInstance().currentRequestId();

    assertEquals( 0, requestId );
  }

  @Test
  public void testIsValid_trueWithValidParameter() {
    int nextRequestId = RequestCounter.getInstance().nextRequestId();
    Fixture.fakeHeadParameter( "requestCounter", Integer.toString( nextRequestId ) );

    boolean valid = RequestCounter.getInstance().isValid();

    assertTrue( valid );
  }

  @Test
  public void testIsValid_falseWithInvalidParameter() {
    RequestCounter.getInstance().nextRequestId();
    Fixture.fakeHeadParameter( "requestCounter", "23" );

    boolean valid = RequestCounter.getInstance().isValid();

    assertFalse( valid );
  }

  @Test
  public void testIsValid_falseWithIllegalParameterFormat() {
    RequestCounter.getInstance().nextRequestId();
    Fixture.fakeHeadParameter( "requestCounter", "not-a-number" );

    boolean valid = RequestCounter.getInstance().isValid();

    assertFalse( valid );
  }

  @Test
  public void testIsValid_toleratesMissingParameterInFirstRequest() {
    boolean valid = RequestCounter.getInstance().isValid();

    assertTrue( valid );
  }

  @Test
  public void testIsValid_falseWithMissingParameter() {
    RequestCounter.getInstance().nextRequestId();

    boolean valid = RequestCounter.getInstance().isValid();

    assertFalse( valid );
  }

  @Test
  public void testSerialization() throws Exception {
    RequestCounter instance = RequestCounter.getInstance();
    instance.nextRequestId(); // ensure counter differs from zero
    int currentRequestId = instance.nextRequestId();

    RequestCounter deserialized = Fixture.serializeAndDeserialize( instance );

    assertEquals( currentRequestId, deserialized.currentRequestId() );
  }

}
