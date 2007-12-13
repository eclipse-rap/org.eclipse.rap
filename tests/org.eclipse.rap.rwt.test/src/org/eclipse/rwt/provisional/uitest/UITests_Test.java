/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.provisional.uitest;

import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.widgets.*;

import junit.framework.TestCase;


public class UITests_Test extends TestCase {

  public void testOverrideId() {
    Display display = new Display();
    Widget widget = new Shell( display );
    // Enable UI tests
    UITests.activate( display );
    // Test legal usage
    UITests.overrideId( widget, "customId" );
    assertEquals( "customId", WidgetUtil.getId( widget ) );
    // Test illagal usage
    String previousId = WidgetUtil.getId( widget );
    try {
      UITests.overrideId( widget, null );
      fail( "Must not allow null for widget id." );
    } catch( NullPointerException e ) {
      // ensure that null-value didn't get assigned
      assertEquals( previousId, WidgetUtil.getId( widget ) );
    }
    // Test with illegal id's
    try {
      UITests.overrideId( widget, "" );
      fail( "Must not allow illegal characers in widget id." );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      UITests.overrideId( widget, "1" );
      fail( "Must not allow illegal characers in widget id." );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      UITests.overrideId( widget, "$A" );
      fail( "Must not allow illegal characers in widget id." );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      UITests.overrideId( widget, "A$" );
      fail( "Must not allow illegal characers in widget id." );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      UITests.overrideId( widget, "A&B" );
      fail( "Must not allow illegal characers in widget id." );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      UITests.overrideId( widget, "A/8" );
      fail( "Must not allow illegal characers in widget id." );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }
  
  public void testIsActivated() {
    Display display = new Display();
    // Test that UI tests are deactivated by default
    assertFalse( UITests.isActivated() );
    // Test that isActivated returns true after activating UI tests
    UITests.activate( display );
    assertTrue( UITests.isActivated() );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
