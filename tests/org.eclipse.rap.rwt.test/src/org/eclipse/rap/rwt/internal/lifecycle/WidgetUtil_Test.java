/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
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
    shell.setData( RWT.CUSTOM_VARIANT, "foo" );

    assertEquals( "foo", WidgetUtil.getVariant( shell ) );
  }

  @Test
  public void testGetVariant_afterReset() {
    shell.setData( RWT.CUSTOM_VARIANT, "foo" );

    shell.setData( RWT.CUSTOM_VARIANT, null );

    assertNull( WidgetUtil.getVariant( shell ) );
  }

  @Test
  @SuppressWarnings( "deprecation" )
  public void testGetAdapter() {
    RemoteAdapter adapter = WidgetUtil.getAdapter( shell );

    assertNotNull( adapter );
    assertSame( shell.getAdapter( RemoteAdapter.class ), adapter );
  }

  @Test
  public void testRegisterDataKeys() {
    WidgetUtil.registerDataKeys( "a", "b", "c" );

    assertEquals( new HashSet<String>( Arrays.asList( "a", "b", "c" ) ),
                  WidgetDataUtil.getDataKeys() );
  }

  @Test( expected = NullPointerException.class )
  public void testRegisterDataKeys_withNullArgument() {
    WidgetDataUtil.registerDataKeys( ( String[] )null );
  }

}
