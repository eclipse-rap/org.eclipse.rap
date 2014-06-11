/*******************************************************************************
 * Copyright (c) 2002, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.widgets;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;

import org.eclipse.rap.rwt.internal.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetDataUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings( "deprecation" )
public class WidgetUtil_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testGetId() {
    WidgetAdapter adapter = mock( WidgetAdapter.class );
    when( adapter.getId() ).thenReturn( "test.id" );
    Widget widget = mock( Widget.class );
    when( widget.getAdapter( WidgetAdapter.class ) ).thenReturn( adapter );

    String id = WidgetUtil.getId( widget );

    assertEquals( "test.id", id );
  }

  @Test
  public void testRegisterDataKeys() {
    WidgetUtil.registerDataKeys( "a", "b", "c" );

    assertEquals( new HashSet<String>( asList( "a", "b", "c" ) ), WidgetDataUtil.getDataKeys() );
  }

  @Test
  public void testRegisterDataKeys_twice() {
    WidgetUtil.registerDataKeys( "a", "b", "c" );
    WidgetUtil.registerDataKeys( "b", "c", "d" );

    assertEquals( new HashSet<String>( asList( "a", "b", "c", "d" ) ), WidgetDataUtil.getDataKeys() );
  }

  @Test( expected = NullPointerException.class )
  public void testRegisterDataKeys_withNullArgument() {
    WidgetDataUtil.registerDataKeys( ( String[] )null );
  }

}
