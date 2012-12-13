/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.serverpush;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;


public class CallBackActivationTracker_Test extends TestCase {

  private ServerPushActivationTracker serverPushActivationTracker;

  public void testInitialActiveState() {
    assertFalse( serverPushActivationTracker.isActive() );
  }

  public void testActivate() {
    serverPushActivationTracker.activate( "x" );
    assertTrue( serverPushActivationTracker.isActive() );
  }

  public void testActivateTwice() {
    String id = "id";
    serverPushActivationTracker.activate( id );
    serverPushActivationTracker.activate( id );
    assertTrue( serverPushActivationTracker.isActive() );
  }

  public void testIsActiveAfterActivate() {
    serverPushActivationTracker.activate( "x" );
    assertTrue( serverPushActivationTracker.isActive() );
  }

  public void testDeactivateWithNonExistingId() {
    serverPushActivationTracker.deactivate( "does.not.exist" );
    assertFalse( serverPushActivationTracker.isActive() );
  }

  public void testDeactivateExistingActivation() {
    String id = "id";
    serverPushActivationTracker.activate( id );
    serverPushActivationTracker.deactivate( id );
    assertFalse( serverPushActivationTracker.isActive() );
  }

  public void testDeactivateSameActivationTwice() {
    String id = "id";
    serverPushActivationTracker.activate( id );
    serverPushActivationTracker.deactivate( id );
    serverPushActivationTracker.deactivate( id );
    assertFalse( serverPushActivationTracker.isActive() );
  }

  public void testActivateMultipleTimes() {
    serverPushActivationTracker.activate( "id1" );
    serverPushActivationTracker.activate( "id2" );
    assertTrue( serverPushActivationTracker.isActive() );
  }

  public void testDeactivateOneFromMultipleActivations() {
    serverPushActivationTracker.activate( "id1" );
    serverPushActivationTracker.activate( "id2" );
    serverPushActivationTracker.deactivate( "id2" );
    assertTrue( serverPushActivationTracker.isActive() );
  }

  public void testSerializeWhenEmpty() throws Exception {
    byte[] bytes = Fixture.serialize( serverPushActivationTracker );

    ServerPushActivationTracker deserializedIdManager = deserialize( bytes );

    assertEquals( serverPushActivationTracker.isActive(), deserializedIdManager.isActive() );
  }

  public void testSerializeWhenHoldingIds() throws Exception {
    String id = "id";
    serverPushActivationTracker.activate( id );
    byte[] bytes = Fixture.serialize( serverPushActivationTracker );

    ServerPushActivationTracker deserializedIdManager = deserialize( bytes );

    assertEquals( serverPushActivationTracker.isActive(), deserializedIdManager.isActive() );
    deserializedIdManager.deactivate( id );
    assertFalse( deserializedIdManager.isActive() );
  }

  @Override
  protected void setUp() throws Exception {
    serverPushActivationTracker = new ServerPushActivationTracker();
  }

  private static ServerPushActivationTracker deserialize( byte[] bytes ) throws Exception {
    return ( ServerPushActivationTracker )Fixture.deserialize( bytes );
  }
}
