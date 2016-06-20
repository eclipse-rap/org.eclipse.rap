/*******************************************************************************
 * Copyright (c) 2009, 2016 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import static org.junit.Assert.assertEquals;

import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.rap.rwt.theme.BoxDimensions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.toolbarkit.ToolBarThemeAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class ToolBarThemeAdapter_Test {

  @Rule
  public TestContext context = new TestContext();

  private Shell shell;
  private ToolBar toolBar;
  private ToolItem firstItem;
  private ToolItem item;
  private ToolItem lastItem;

  @Before
  public void setUp() throws Exception {
    Display display = new Display();
    shell = new Shell( display );
    toolBar = new ToolBar( shell, SWT.NONE );
    firstItem = new ToolItem( toolBar, SWT.NONE );
    item = new ToolItem( toolBar, SWT.NONE );
    lastItem = new ToolItem( toolBar, SWT.NONE );
    setCustomTheme();
  }

  @Test
  public void testGetItemPadding() {
    BoxDimensions actual = getThemeAdapter( toolBar ).getItemPadding( item );
    assertEquals( new BoxDimensions( 1, 1, 1, 1 ), actual );
  }

  @Test
  public void testGetItemPadding_onFirst() {
    BoxDimensions actual = getThemeAdapter( toolBar ).getItemPadding( firstItem );
    assertEquals( new BoxDimensions( 2, 2, 2, 2 ), actual );
  }

  @Test
  public void testGetItemPadding_onLast() {
    BoxDimensions actual = getThemeAdapter( toolBar ).getItemPadding( lastItem );
    assertEquals( new BoxDimensions( 3, 3, 3, 3 ), actual );
  }

  @Test
  public void testGetItemSpacing() {
    assertEquals( 1, getThemeAdapter( toolBar ).getItemSpacing( item ) );
  }

  @Test
  public void testGetItemSpacing_onFirst() {
    assertEquals( 2, getThemeAdapter( toolBar ).getItemSpacing( firstItem ) );
  }

  @Test
  public void testGetItemSpacing_onLast() {
    assertEquals( 3, getThemeAdapter( toolBar ).getItemSpacing( lastItem ) );
  }

  @Test
  public void testGetItemBorder() {
    BoxDimensions actual = getThemeAdapter( toolBar ).getItemBorder( item );
    assertEquals( new BoxDimensions( 1, 1, 1, 1 ), actual );
  }

  @Test
  public void testGetItemBorder_onFirst() {
    BoxDimensions actual = getThemeAdapter( toolBar ).getItemBorder( firstItem );
    assertEquals( new BoxDimensions( 2, 2, 2, 2 ), actual );
  }

  @Test
  public void testGetItemBorder_onLast() {
    BoxDimensions actual = getThemeAdapter( toolBar ).getItemBorder( lastItem );
    assertEquals( new BoxDimensions( 3, 3, 3, 3 ), actual );
  }

  @Test
  public void testGetItemBorder_onDisabled() {
    item.setEnabled( false );
    BoxDimensions actual = getThemeAdapter( toolBar ).getItemBorder( item );
    assertEquals( new BoxDimensions( 4, 4, 4, 4 ), actual );
  }

  private static void setCustomTheme() throws Exception {
    StringBuilder css = new StringBuilder()
      .append( "ToolItem { padding: 1px; spacing: 1px; border: 1px solid black }" )
      .append( "ToolItem:first { padding: 2px; spacing: 2px; border: 2px solid black }" )
      .append( "ToolItem:last { padding: 3px; spacing: 3px; border: 3px solid black }" )
      .append( "ToolItem:disabled { padding: 4px; spacing: 4px; border: 4px solid black }" );
    ThemeTestUtil.registerTheme( "custom", css.toString(), null );
    ThemeTestUtil.setCurrentThemeId( "custom" );
  }

  private static ToolBarThemeAdapter getThemeAdapter( ToolBar toolBar ) {
    return ( ToolBarThemeAdapter )toolBar.getAdapter( ThemeAdapter.class );
  }

}
