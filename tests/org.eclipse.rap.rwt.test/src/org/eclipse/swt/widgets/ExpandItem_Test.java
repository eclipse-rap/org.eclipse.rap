/*******************************************************************************
 * Copyright (c) 2008, 2016 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.eclipse.rap.rwt.testfixture.internal.TestUtil.createImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.theme.BoxDimensions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.widgets.MarkupValidator;
import org.eclipse.swt.internal.widgets.expanditemkit.ExpandItemLCA;
import org.eclipse.swt.layout.GridLayout;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class ExpandItem_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;
  private Shell shell;
  private ExpandBar expandBar;
  private ExpandItem expandItem;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display );
    expandBar = new ExpandBar( shell, SWT.NONE );
    expandItem = new ExpandItem( expandBar, SWT.NONE );
  }

  @Test
  public void testCreation_addsItemToParent() {
    assertEquals( 1, expandBar.getItemCount() );
    assertSame( expandItem, expandBar.getItem( 0 ) );
  }

  @Test
  public void testCreation_insertsItemBeforeFirstItem() {
    ExpandItem insertedItem = new ExpandItem( expandBar, SWT.NONE, 0 );

    assertEquals( 2, expandBar.getItemCount() );
    assertSame( insertedItem, expandBar.getItem( 0 ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testCreation_failsWithIndexOutOfBounds() {
    new ExpandItem( expandBar, SWT.NONE, expandBar.getItemCount() + 1 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testCreation_failsWithNullParent() {
    new ExpandItem( null, SWT.NONE );
  }

  @Test
  public void testGetParent() {
    assertSame( expandBar, expandItem.getParent() );
  }

  @Test
  public void testDispose_removesItemFromParent() {
    expandItem.dispose();

    assertEquals( 0, expandBar.getItemCount() );
  }

  @Test
  public void testText() throws IOException {
    assertEquals( "", expandItem.getText() );
    expandItem.setText( "abc" );
    assertEquals( "abc", expandItem.getText() );
    expandItem = new ExpandItem( expandBar, SWT.NONE );
    expandItem.setImage( createImage( display, Fixture.IMAGE1 ) );
    assertEquals( "", expandItem.getText() );
  }

  @Test
  public void testMarkupTextWithoutMarkupEnabled() {
    expandBar.setData( RWT.MARKUP_ENABLED, Boolean.FALSE );

    try {
      expandItem.setText( "invalid xhtml: <<&>>" );
    } catch( IllegalArgumentException notExpected ) {
      fail();
    }
  }

  @Test( expected = IllegalArgumentException.class )
  public void testMarkupTextWithMarkupEnabled() {
    expandBar.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );

    expandItem.setText( "invalid xhtml: <<&>>" );
  }

  @Test
  public void testMarkupTextWithMarkupEnabled_ValidationDisabled() {
    expandBar.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    expandBar.setData( MarkupValidator.MARKUP_VALIDATION_DISABLED, Boolean.TRUE );

    try {
      expandItem.setText( "invalid xhtml: <<&>>" );
    } catch( IllegalArgumentException notExpected ) {
      fail();
    }
  }

  @Test
  public void testImage() throws IOException {
    Image image = createImage( display, Fixture.IMAGE1 );
    ExpandItem expandItem = new ExpandItem( expandBar, SWT.NONE );
    assertEquals( null, expandItem.getImage() );
    expandItem.setImage( image );
    assertSame( image, expandItem.getImage() );
  }

  @Test
  public void testHeight() {
    Composite composite = new Composite( expandBar, SWT.NONE );
    composite.setLayout( new GridLayout() );
    new Button( composite, SWT.PUSH ).setText( "SWT.PUSH" );
    new Button( composite, SWT.RADIO ).setText( "SWT.RADIO" );
    new Button( composite, SWT.CHECK ).setText( "SWT.CHECK" );
    new Button( composite, SWT.TOGGLE ).setText( "SWT.TOGGLE" );
    expandItem.setText( "What is your favorite button?" );
    expandItem.setHeight( composite.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y );
    expandItem.setControl( composite );
    BoxDimensions itemBorder = expandItem.getItemBorder();
    assertEquals( composite.getSize().y + itemBorder.top + itemBorder.bottom, expandItem.getHeight() );
  }

  @Test
  public void testHeaderHeight() {
    expandItem.setText( "What is your favorite button?" );
    assertEquals( 24, expandItem.getHeaderHeight() );
    expandItem.setImage( display.getSystemImage( SWT.ICON_WARNING ) );
    assertEquals( 32, expandItem.getHeaderHeight() );
    Font font = new Font( display, "font", 30, SWT.BOLD );
    expandBar.setFont( font );
    assertEquals( 42, expandItem.getHeaderHeight() );
  }

  @Test
  public void testGetAdapter_LCA() {
    assertTrue( expandItem.getAdapter( WidgetLCA.class ) instanceof ExpandItemLCA );
    assertSame( expandItem.getAdapter( WidgetLCA.class ), expandItem.getAdapter( WidgetLCA.class ) );
  }

}
