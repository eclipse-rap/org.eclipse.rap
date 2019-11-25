/*******************************************************************************
 * Copyright (c) 2013, 2019 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.eclipse.rap.rwt.testfixture.internal.TestUtil.createImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.MarkupValidator;
import org.eclipse.swt.internal.widgets.tabitemkit.TabItemLCA;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class TabItem_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;
  private Shell shell;
  private TabFolder folder;
  private TabItem item;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display );
    folder = new TabFolder( shell, SWT.NONE );
    item = new TabItem( folder, SWT.NONE );
  }

  @Test
  public void testCreate() {
    assertSame( folder, item.getParent() );
    assertSame( display, item.getDisplay() );
  }

  @Test
  public void testCreateWithIndex() {
    TabItem secondItem = new TabItem( folder, SWT.NONE, 0 );

    assertSame( secondItem, folder.getItem( 0 ) );
    assertEquals( 0, folder.indexOf( secondItem ) );
    assertSame( item, folder.getItem( 1 ) );
    assertEquals( 1, folder.indexOf( item ) );
  }

  @Test
  public void testGetBounds_top() {
    folder.setSize( 400, 400 );
    item.setText( "TabItem 0" );
    createItems( folder, 2 );

    assertEquals( new Rectangle( 0, 0, 74, 32 ), folder.getItem( 0 ).getBounds() );
    assertEquals( new Rectangle( 74, 3, 74, 29 ), folder.getItem( 1 ).getBounds() );
    assertEquals( new Rectangle( 149, 3, 74, 29 ), folder.getItem( 2 ).getBounds() );
  }

  @Test
  public void testGetBounds_bottom() {
    folder = new TabFolder( shell, SWT.BOTTOM );
    folder.setSize( 400, 400 );
    createItems( folder, 3 );

    assertEquals( new Rectangle( 0, 368, 74, 32 ), folder.getItem( 0 ).getBounds() );
    assertEquals( new Rectangle( 74, 368, 74, 29 ), folder.getItem( 1 ).getBounds() );
    assertEquals( new Rectangle( 149, 368, 74, 29 ), folder.getItem( 2 ).getBounds() );
  }

  @Test
  public void testGetBounds_RTL() {
    folder.setOrientation( SWT.RIGHT_TO_LEFT );
    folder.setSize( 400, 400 );
    item.setText( "TabItem 0" );
    createItems( folder, 2 );

    assertEquals( new Rectangle( 0, 0, 74, 32 ), folder.getItem( 0 ).getBounds() );
    assertEquals( new Rectangle( 74, 3, 74, 29 ), folder.getItem( 1 ).getBounds() );
    assertEquals( new Rectangle( 149, 3, 74, 29 ), folder.getItem( 2 ).getBounds() );
  }

  @Test
  public void testItemDispose() {
    item.dispose();

    assertTrue( item.isDisposed() );
  }

  @Test
  public void testSetImage() throws IOException {
    Image image = createImage( display, Fixture.IMAGE1 );

    item.setImage( image );

    assertSame( image, item.getImage() );
  }

  @Test
  public void testSetText() {
    item.setText( "foo" );

    assertEquals( "foo", item.getText() );
  }

  @Test
  public void testToolTip() {
    item.setToolTipText( "foo" );

    assertEquals( "foo", item.getToolTipText() );
  }

  @Test
  public void testSetControl() {
    Control control = new Label( folder, SWT.NONE );

    item.setControl( control );

    assertSame( control, item.getControl() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetControl_withWrongParent() {
    item.setControl( shell );
  }

  @Test
  public void testSelectedControl() {
    Control control0 = new Button( folder, SWT.PUSH );
    item.setControl( control0 );
    assertTrue( control0.getVisible() );

    TabItem item1 = new TabItem( folder, SWT.NONE );
    Control control1 = new Button( folder, SWT.PUSH );
    item1.setControl( control1 );
    assertFalse( control1.getVisible() );

    folder.setSelection( item1 );
    assertTrue( control1.getVisible() );

    Control alternativeControl1 = new Button( folder, SWT.PUSH );
    item1.setControl( alternativeControl1 );
    assertFalse( control1.getVisible() );
    assertTrue( alternativeControl1.getVisible() );
  }

  @Test
  public void testSelectedControlVisibility_onItemDispose() {
    Control control0 = new Button( folder, SWT.PUSH );
    item.setControl( control0 );
    TabItem item1 = new TabItem( folder, SWT.NONE );
    Control control1 = new Button( folder, SWT.PUSH );
    item1.setControl( control1 );

    item.dispose();

    assertFalse( control0.getVisible() );
    assertTrue( control1.getVisible() );
  }

  @Test
  public void testMarkupToolTipTextWithoutMarkupEnabled() {
    item.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.FALSE );

    try {
      item.setToolTipText( "invalid xhtml: <<&>>" );
    } catch( IllegalArgumentException notExpected ) {
      fail();
    }
  }

  @Test( expected = IllegalArgumentException.class )
  public void testMarkupToolTipTextWithMarkupEnabled() {
    item.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.TRUE );

    item.setToolTipText( "invalid xhtml: <<&>>" );
  }

  @Test
  public void testMarkupTextWithMarkupEnabled_ValidationDisabled() {
    item.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.TRUE );
    item.setData( MarkupValidator.MARKUP_VALIDATION_DISABLED, Boolean.TRUE );

    try {
      item.setToolTipText( "invalid xhtml: <<&>>" );
    } catch( IllegalArgumentException notExpected ) {
      fail();
    }
  }

  @Test
  public void testSetToolTipMarkupEnabled_onDirtyWidget() {
    item.setToolTipText( "something" );

    try {
      item.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.TRUE );
      fail();
    } catch( SWTException expected ) {
      assertTrue( expected.throwable instanceof IllegalStateException );
    }
  }

  @Test
  public void testSetToolTipMarkupEnabled_onDirtyWidget_onceEnabledBefore() {
    item.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.TRUE );
    item.setToolTipText( "something" );

    item.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.TRUE );
  }

  @Test
  public void testDisableMarkupIsIgnored() {
    item.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.TRUE );

    item.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.FALSE );

    assertEquals( Boolean.TRUE, item.getData( RWT.TOOLTIP_MARKUP_ENABLED ) );
  }

  @Test
  public void testSetData() {
    item.setData( "foo", "bar" );

    assertEquals( "bar", item.getData( "foo" ) );
  }

  @Test
  public void testBadge() {
    item.setData( RWT.BADGE, "123" );

    assertEquals( "123", item.getData( RWT.BADGE ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testBadge_nonStringArgument() {
    item.setData( RWT.BADGE, Integer.valueOf( 3 ) );
  }

  @Test
  public void testBadge_resetWithNull() {
    item.setData( RWT.BADGE, "123" );

    item.setData( RWT.BADGE, null );

    assertNull( item.getData( RWT.BADGE ) );
  }

  private void createItems( TabFolder folder, int number ) {
    for( int i = 0; i < number; i++ ) {
      TabItem item = new TabItem( folder, SWT.NONE );
      item.setText( "TabItem " + i );
    }
  }

  @Test
  public void testGetAdapter_LCA() {
    assertTrue( item.getAdapter( WidgetLCA.class ) instanceof TabItemLCA );
    assertSame( item.getAdapter( WidgetLCA.class ), item.getAdapter( WidgetLCA.class ) );
  }

}
