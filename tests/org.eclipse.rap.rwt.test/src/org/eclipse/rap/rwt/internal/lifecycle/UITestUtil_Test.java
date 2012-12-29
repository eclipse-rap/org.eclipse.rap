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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class UITestUtil_Test {

  private Display display;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
    UITestUtil.enabled = false;
  }

  @Test
  public void testIsValidId_trueForLegalIds() {
    assertTrue( UITestUtil.isValidId( "customId" ) );
    assertTrue( UITestUtil.isValidId( "custom-id" ) );
    assertTrue( UITestUtil.isValidId( "custom_id" ) );
    assertTrue( UITestUtil.isValidId( "custom:id" ) );
    assertTrue( UITestUtil.isValidId( "custom.id" ) );
    assertTrue( UITestUtil.isValidId( "custom123" ) );
  }

  @Test
  public void testIsValidId_falseForIllegalIds() {
    assertFalse( UITestUtil.isValidId( null ) );
    assertFalse( UITestUtil.isValidId( "" ) );
    assertFalse( UITestUtil.isValidId( "1" ) );
    assertFalse( UITestUtil.isValidId( "$A" ) );
    assertFalse( UITestUtil.isValidId( "A$" ) );
    assertFalse( UITestUtil.isValidId( "A&B" ) );
    assertFalse( UITestUtil.isValidId( "A/8" ) );
  }

  @Test
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
