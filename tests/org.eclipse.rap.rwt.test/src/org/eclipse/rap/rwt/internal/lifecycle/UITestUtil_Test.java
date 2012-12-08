/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
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

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;


public class UITestUtil_Test extends TestCase {

  private Display display;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
    UITestUtil.enabled = false;
  }

  public void testIsValidId() {
    // Test with legal id
    assertTrue( UITestUtil.isValidId( "customId" ) );
    assertTrue( UITestUtil.isValidId( "custom-id" ) );
    assertTrue( UITestUtil.isValidId( "custom_id" ) );
    assertTrue( UITestUtil.isValidId( "custom:id" ) );
    assertTrue( UITestUtil.isValidId( "custom.id" ) );
    assertTrue( UITestUtil.isValidId( "custom123" ) );
    // Test with illegal id's
    assertFalse( UITestUtil.isValidId( null ) );
    assertFalse( UITestUtil.isValidId( "" ) );
    assertFalse( UITestUtil.isValidId( "1" ) );
    assertFalse( UITestUtil.isValidId( "$A" ) );
    assertFalse( UITestUtil.isValidId( "A$" ) );
    assertFalse( UITestUtil.isValidId( "A&B" ) );
    assertFalse( UITestUtil.isValidId( "A/8" ) );
  }

  public void testOverrideId() {
    Widget widget = new Shell( display );
    String customId = "customId";
    String generatedId = WidgetUtil.getId( widget );
    // ensure that generated id remains unchanged when UITests are disabled
    widget.setData( WidgetUtil.CUSTOM_WIDGET_ID, customId );
    assertEquals( generatedId, WidgetUtil.getId( widget ) );
    // ensure that custom id is taken into account when UITests are enabled
    UITestUtil.enabled = true;
    assertEquals( customId, WidgetUtil.getId( widget ) );
  }

  public void testInvalidCustomId() {
    UITestUtil.enabled = true;
    Shell shell = new Shell( display, SWT.NONE );

    try {
      shell.setData( WidgetUtil.CUSTOM_WIDGET_ID, "a/8" );
      fail( "widget id contains illegal characters" );
    } catch( IllegalArgumentException iae ) {
    }
  }

  public void testGetIdAfterDispose() {
    // set up test scenario
    UITestUtil.enabled = true;
    Shell shell = new Shell( display, SWT.NONE );
    // ensure that the overridden id is available after the widget was disposed
    // of - needed by render phase
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    shell.setData( WidgetUtil.CUSTOM_WIDGET_ID, "customId" );
    assertEquals( "customId", WidgetUtil.getId( shell ) );
    shell.dispose();
    assertEquals( "customId", WidgetUtil.getId( shell ) );
  }

}
