/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.lifecycle;

import junit.framework.TestCase;

import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public class WidgetUtil_Test extends TestCase {

  public void testFind() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Composite composite = new Composite( shell, SWT.NONE );
    Button button = new Button( composite, SWT.PUSH );
    String shellId = WidgetUtil.getId( shell );
    String compositeId = WidgetUtil.getId( composite );
    String buttonId = WidgetUtil.getId( button );

    assertSame( composite, WidgetUtil.find( composite, compositeId ) );
    assertSame( button, WidgetUtil.find( composite, buttonId ) );
    assertSame( composite, WidgetUtil.find( composite, compositeId ) );
    assertNull( WidgetUtil.find( composite, shellId ) );
  }

  public void testId() {
    Display display = new Display();
    Widget widget = new Shell( display );
    String customId = "customId";
    String generatedId = WidgetUtil.getId( widget );
    // ensure that generated id remains unchanged when UITests are disabled
    widget.setData( WidgetUtil.CUSTOM_WIDGET_ID, customId );
    assertEquals( generatedId, WidgetUtil.getId( widget ) );
    // ensure that custom id is taken into account when UITests are enabled
    System.setProperty( WidgetUtil.ENABLE_UI_TESTS, "true" );
    assertEquals( customId, WidgetUtil.getId( widget ) );
    // clean up
    System.getProperties().remove( WidgetUtil.ENABLE_UI_TESTS );
  }

  public void testGetVariant() throws Exception {
    Display display = new Display();
    Shell shell = new Shell( display );
    String valid = "Foo_Bar_23_42";
    shell.setData( WidgetUtil.CUSTOM_VARIANT, valid );
    assertEquals( valid, WidgetUtil.getVariant( shell ) );
    String withDash = "Foo-Bar-23-42";
    shell.setData( WidgetUtil.CUSTOM_VARIANT, withDash );
    assertEquals( withDash, WidgetUtil.getVariant( shell ) );
    String withLeadingDash = "-Foo-Bar-23-42";
    shell.setData( WidgetUtil.CUSTOM_VARIANT, withLeadingDash );
    assertEquals( withLeadingDash, WidgetUtil.getVariant( shell ) );
    String withNonAscii = "Foo-üäöæ-23-42";
    shell.setData( WidgetUtil.CUSTOM_VARIANT, withNonAscii );
    assertEquals( withNonAscii, WidgetUtil.getVariant( shell ) );
    String withSpaces = "Foo Bar 23 42 ";
    shell.setData( WidgetUtil.CUSTOM_VARIANT, withSpaces );
    try {
      WidgetUtil.getVariant( shell );
      fail( "IAE expected" );
    } catch( RuntimeException e ) {
      // expected
    }
    String withColon = "Foo:Bar";
    shell.setData( WidgetUtil.CUSTOM_VARIANT, withColon );
    try {
      WidgetUtil.getVariant( shell );
      fail( "IAE expected" );
    } catch( RuntimeException e ) {
      // expected
    }
    String withLeadingNumber = "1-Foo-Bar";
    shell.setData( WidgetUtil.CUSTOM_VARIANT, withLeadingNumber );
    try {
      WidgetUtil.getVariant( shell );
      fail( "IAE expected" );
    } catch( RuntimeException e ) {
      // expected
    }
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
