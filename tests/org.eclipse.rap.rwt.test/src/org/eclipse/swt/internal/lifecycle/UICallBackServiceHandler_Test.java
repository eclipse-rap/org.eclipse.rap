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

package org.eclipse.swt.internal.lifecycle;

import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import com.w4t.Fixture.*;


public class UICallBackServiceHandler_Test extends TestCase {
  
  private static final String ID_1 = "id_1";
  private static final String ID_2 = "id_2";

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }
  
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
  
  public void testOnOffSwitch() throws InterruptedException {
    assertEquals( "", UICallBackServiceHandler.jsEnableUICallBack() );
    UICallBackServiceHandler.activateUICallBacksFor( ID_1 );
    UICallBackServiceHandler.jsEnableUICallBack();
    assertFalse( "".equals( UICallBackServiceHandler.jsEnableUICallBack() ) );
    // test that on/off switching is managed in session scope
    final String[] otherSession = new String[ 1 ];
    Thread thread = new Thread( new Runnable() {
      public void run() {
        RWTFixture.fakeContext();
        otherSession[ 0 ] = UICallBackServiceHandler.jsEnableUICallBack();
      } 
    } );
    thread.start();
    thread.join();
    assertEquals( "", otherSession[ 0 ] );
    UICallBackServiceHandler.deactivateUICallBacksFor( ID_1 );
    assertEquals( "", UICallBackServiceHandler.jsEnableUICallBack() );
    
    UICallBackServiceHandler.activateUICallBacksFor( ID_1 );
    UICallBackServiceHandler.activateUICallBacksFor( ID_2 );
    assertFalse( "".equals( UICallBackServiceHandler.jsEnableUICallBack() ) );
    UICallBackServiceHandler.deactivateUICallBacksFor( ID_1 );
    assertFalse( "".equals( UICallBackServiceHandler.jsEnableUICallBack() ) );
    UICallBackServiceHandler.deactivateUICallBacksFor( ID_2 );
    assertEquals( "", UICallBackServiceHandler.jsEnableUICallBack() );
  }
}