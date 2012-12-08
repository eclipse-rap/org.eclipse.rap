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
package org.eclipse.rap.rwt.internal.uicallback;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;


public class CallBackActivationTracker_Test extends TestCase {
  
  private CallBackActivationTracker callBackActivationTracker;

  public void testInitialActiveState() {
    assertFalse( callBackActivationTracker.isActive() );
  }

  public void testActivate() {
    callBackActivationTracker.activate( "x" );
    assertTrue( callBackActivationTracker.isActive() );
  }

  public void testActivateTwice() {
    String id = "id";
    callBackActivationTracker.activate( id );
    callBackActivationTracker.activate( id );
    assertTrue( callBackActivationTracker.isActive() );
  }
  
  public void testIsActiveAfterActivate() {
    callBackActivationTracker.activate( "x" );
    assertTrue( callBackActivationTracker.isActive() );
  }
  
  public void testDeactivateWithNonExistingId() {
    callBackActivationTracker.deactivate( "does.not.exist" );
    assertFalse( callBackActivationTracker.isActive() );
  }
  
  public void testDeactivateExistingActivation() {
    String id = "id";
    callBackActivationTracker.activate( id );
    callBackActivationTracker.deactivate( id );
    assertFalse( callBackActivationTracker.isActive() );
  }
  
  public void testDeactivateSameActivationTwice() {
    String id = "id";
    callBackActivationTracker.activate( id );
    callBackActivationTracker.deactivate( id );
    callBackActivationTracker.deactivate( id );
    assertFalse( callBackActivationTracker.isActive() );
  }
  
  public void testActivateMultipleTimes() {
    callBackActivationTracker.activate( "id1" );
    callBackActivationTracker.activate( "id2" );
    assertTrue( callBackActivationTracker.isActive() );
  }
  
  public void testDeactivateOneFromMultipleActivations() {
    callBackActivationTracker.activate( "id1" );
    callBackActivationTracker.activate( "id2" );
    callBackActivationTracker.deactivate( "id2" );
    assertTrue( callBackActivationTracker.isActive() );
  }
  
  public void testSerializeWhenEmpty() throws Exception {
    byte[] bytes = Fixture.serialize( callBackActivationTracker );

    CallBackActivationTracker deserializedIdManager = deserialize( bytes );
    
    assertEquals( callBackActivationTracker.isActive(), deserializedIdManager.isActive() );
  }
  
  public void testSerializeWhenHoldingIds() throws Exception {
    String id = "id";
    callBackActivationTracker.activate( id );
    byte[] bytes = Fixture.serialize( callBackActivationTracker );
    
    CallBackActivationTracker deserializedIdManager = deserialize( bytes );
    
    assertEquals( callBackActivationTracker.isActive(), deserializedIdManager.isActive() );
    deserializedIdManager.deactivate( id );
    assertFalse( deserializedIdManager.isActive() );
  }

  protected void setUp() throws Exception {
    callBackActivationTracker = new CallBackActivationTracker();
  }

  private static CallBackActivationTracker deserialize( byte[] bytes ) throws Exception {
    return ( CallBackActivationTracker )Fixture.deserialize( bytes );
  }
}
