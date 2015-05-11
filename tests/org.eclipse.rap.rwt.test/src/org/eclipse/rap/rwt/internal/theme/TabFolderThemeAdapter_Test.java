/*******************************************************************************
 * Copyright (c) 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import static org.junit.Assert.assertEquals;

import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.rap.rwt.theme.BoxDimensions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.tabfolderkit.TabFolderThemeAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class TabFolderThemeAdapter_Test {

  @Rule
  public TestContext context = new TestContext();

  private Shell shell;
  private TabFolder folder;
  private TabItem firstItem;
  private TabItem item;
  private TabItem lastItem;

  @Before
  public void setUp() throws Exception {
    Display display = new Display();
    shell = new Shell( display );
    folder = new TabFolder( shell, SWT.NONE );
    firstItem = new TabItem( folder, SWT.NONE );
    item = new TabItem( folder, SWT.NONE );
    lastItem = new TabItem( folder, SWT.NONE );
    setCustomTheme();
  }

  @Test
  public void testGetItemPadding() {
    BoxDimensions actual = getThemeAdapter( folder ).getItemPadding( item );
    assertEquals( new BoxDimensions( 1, 1, 1, 1 ), actual );
  }

  @Test
  public void testGetItemPadding_onFirst() {
    BoxDimensions actual = getThemeAdapter( folder ).getItemPadding( firstItem );
    assertEquals( new BoxDimensions( 2, 2, 2, 2 ), actual );
  }

  @Test
  public void testGetItemPadding_onLast() {
    BoxDimensions actual = getThemeAdapter( folder ).getItemPadding( lastItem );
    assertEquals( new BoxDimensions( 3, 3, 3, 3 ), actual );
  }

  @Test
  public void testGetItemPadding_onSelected() {
    folder.setSelection( 1 );

    BoxDimensions actual = getThemeAdapter( folder ).getItemPadding( item );
    assertEquals( new BoxDimensions( 5, 5, 5, 5 ), actual );
  }

  @Test
  public void testGetItemPadding_onBottom() {
    folder = new TabFolder( shell, SWT.BOTTOM );
    firstItem = new TabItem( folder, SWT.NONE );
    item = new TabItem( folder, SWT.NONE );

    BoxDimensions actual = getThemeAdapter( folder ).getItemPadding( item );
    assertEquals( new BoxDimensions( 4, 4, 4, 4 ), actual );
  }

  @Test
  public void testGetItemSpacing() {
    BoxDimensions actual = getThemeAdapter( folder ).getItemMargin( item );
    assertEquals( new BoxDimensions( 1, 1, 1, 1 ), actual );
  }

  @Test
  public void testGetItemSpacing_onFirst() {
    BoxDimensions actual = getThemeAdapter( folder ).getItemMargin( firstItem );
    assertEquals( new BoxDimensions( 2, 2, 2, 2 ), actual );
  }

  @Test
  public void testGetItemSpacing_onLast() {
    BoxDimensions actual = getThemeAdapter( folder ).getItemMargin( lastItem );
    assertEquals( new BoxDimensions( 3, 3, 3, 3 ), actual );
  }

  @Test
  public void testGetItemBorder() {
    BoxDimensions actual = getThemeAdapter( folder ).getItemBorder( item );
    assertEquals( new BoxDimensions( 1, 1, 1, 1 ), actual );
  }

  @Test
  public void testGetItemBorder_onFirst() {
    BoxDimensions actual = getThemeAdapter( folder ).getItemBorder( firstItem );
    assertEquals( new BoxDimensions( 2, 2, 2, 2 ), actual );
  }

  @Test
  public void testGetItemBorder_onLast() {
    BoxDimensions actual = getThemeAdapter( folder ).getItemBorder( lastItem );
    assertEquals( new BoxDimensions( 3, 3, 3, 3 ), actual );
  }

  private static void setCustomTheme() throws Exception {
    StringBuilder css = new StringBuilder()
      .append( "TabItem { padding: 1px; margin: 1px; border: 1px solid black }" )
      .append( "TabItem:first { padding: 2px; margin: 2px; border: 2px solid black }" )
      .append( "TabItem:first:selected { padding: 2px; margin: 2px; border: 2px solid black }" )
      .append( "TabItem:last { padding: 3px; margin: 3px; border: 3px solid black }" )
      .append( "TabItem:bottom { padding: 4px; margin: 4px; border: 4px solid black }" )
      .append( "TabItem:selected { padding: 5px; margin: 5px; border: 5px solid black }" );
    ThemeTestUtil.registerTheme( "custom", css.toString(), null );
    ThemeTestUtil.setCurrentThemeId( "custom" );
  }

  private static TabFolderThemeAdapter getThemeAdapter( TabFolder folder ) {
    return ( TabFolderThemeAdapter )folder.getAdapter( ThemeAdapter.class );
  }

}
