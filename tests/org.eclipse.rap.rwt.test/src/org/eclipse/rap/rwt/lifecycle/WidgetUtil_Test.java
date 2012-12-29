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
package org.eclipse.rap.rwt.lifecycle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class WidgetUtil_Test {

  private Display display;
  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testFind() {
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

  @Test
  public void testGetVariant() {
    String valid = "Foo_Bar_23_42";
    shell.setData( RWT.CUSTOM_VARIANT, valid );
    assertEquals( valid, WidgetUtil.getVariant( shell ) );
    String withDash = "Foo-Bar-23-42";
    shell.setData( RWT.CUSTOM_VARIANT, withDash );
    assertEquals( withDash, WidgetUtil.getVariant( shell ) );
    String withLeadingDash = "-Foo-Bar-23-42";
    shell.setData( RWT.CUSTOM_VARIANT, withLeadingDash );
    assertEquals( withLeadingDash, WidgetUtil.getVariant( shell ) );
    String withNonAscii = "Foo-üäöæ-23-42";
    shell.setData( RWT.CUSTOM_VARIANT, withNonAscii );
    assertEquals( withNonAscii, WidgetUtil.getVariant( shell ) );
    String withSpaces = "Foo Bar 23 42 ";
    shell.setData( RWT.CUSTOM_VARIANT, withSpaces );
    try {
      WidgetUtil.getVariant( shell );
      fail( "IAE expected" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    String withColon = "Foo:Bar";
    shell.setData( RWT.CUSTOM_VARIANT, withColon );
    try {
      WidgetUtil.getVariant( shell );
      fail( "IAE expected" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    String withLeadingNumber = "1-Foo-Bar";
    shell.setData( RWT.CUSTOM_VARIANT, withLeadingNumber );
    try {
      WidgetUtil.getVariant( shell );
      fail( "IAE expected" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testInitializedForShell() {
    Fixture.fakeNewRequest();
    Fixture.fakeResponseWriter();
    Composite shell = new Shell( display, SWT.NONE );

    WidgetAdapter adapter = WidgetUtil.getAdapter( shell );

    assertSame( shell.getAdapter( WidgetAdapter.class ), adapter );
  }

}
