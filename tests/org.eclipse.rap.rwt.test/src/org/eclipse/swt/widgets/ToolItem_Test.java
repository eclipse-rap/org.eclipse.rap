/*******************************************************************************
 * Copyright (c) 2009, 2019 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.internal.widgets.MarkupValidator;
import org.eclipse.swt.internal.widgets.toolitemkit.ToolItemLCA;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class ToolItem_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;
  private Shell shell;
  private ToolBar toolbar;
  private ToolItem toolItem;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display , SWT.NONE );
    toolbar = new ToolBar( shell, SWT.NONE );
    toolItem = new ToolItem( toolbar, SWT.NONE );
  }

  @Test
  public void testText() {
    ToolItem separator = new ToolItem( toolbar, SWT.SEPARATOR );
    String text0 = "text0";
    String text1 = "text1";

    // Test 'normal' tool item
    toolItem.setText( text0 );
    assertEquals( text0, toolItem.getText() );
    toolItem.setText( text1 );
    assertEquals( text1, toolItem.getText() );
    // Test separator tool item
    assertEquals( "", separator.getText() );
    separator.setText( text1 );
    assertEquals( "", separator.getText() );
  }

  @Test
  public void testImage() {
    toolItem.setImage( null );
    assertEquals( null, toolItem.getImage() );
  }

  @Test
  public void testEnabled() {
    ToolItem separator = new ToolItem( toolbar, SWT.SEPARATOR );
    separator.setControl( new Text( toolbar, SWT.NONE ) );

    // ToolItem must be enabled initially
    assertTrue( toolItem.getEnabled() );

    // Test enabled ToolItem on disabled ToolBar
    toolbar.setEnabled( false );
    toolItem.setEnabled( true );
    assertTrue( toolItem.getEnabled() );
    assertFalse( toolItem.isEnabled() );

    // Test disabled ToolItem on disabled ToolBar
    toolbar.setEnabled( false );
    toolItem.setEnabled( false );
    assertFalse( toolItem.getEnabled() );
    assertFalse( toolItem.isEnabled() );

    // Test SEPARATOR ToolItem
    separator.setEnabled( false );
    assertTrue( separator.getControl().getEnabled() );
  }

  @Test
  public void testSeparatorWithControl() {
    ToolItem separator = new ToolItem( toolbar, SWT.SEPARATOR );
    separator.setControl( new Text( toolbar, SWT.NONE ) );

    // Using control property on ToolItem without SEPARATOR style has no effect
    toolItem.setControl( new Text( toolbar, SWT.NONE ) );
    assertEquals( null, toolItem.getControl() );

    // Setting a valid control on a SEPARATOR ToolItem
    Control control = new Text( toolbar, SWT.NONE );
    separator.setControl( control );
    assertSame( control, separator.getControl() );
    separator.setControl( null );
    assertEquals( null, separator.getControl() );

    // Illegal values for setControl
    Control currentControl = new Text( toolbar, SWT.NONE );
    separator.setControl( currentControl );
    Control diposedControl = new Text( toolbar, SWT.NONE );
    diposedControl.dispose();
    try {
      separator.setControl( diposedControl );
      fail( "Must not allow to set diposed control in setControl" );
    } catch( IllegalArgumentException e ) {
      assertSame( currentControl, separator.getControl() );
    }
    separator.setControl( currentControl );
    Control shellControl = new Text( shell, SWT.NONE );
    shellControl.dispose();
    try {
      separator.setControl( shellControl );
      fail( "Must not allow to set control with other parent than ToolItem" );
    } catch( IllegalArgumentException e ) {
      assertSame( currentControl, separator.getControl() );
    }

    // Ensure visibility of control is adjusted in the right way
    currentControl.setVisible( false );
    separator.setControl( currentControl );
    assertTrue( currentControl.getVisible() );
    separator.setControl( null );
    assertFalse( currentControl.getVisible() );

    // Dispose of control that is currently set on the SEPARATOR
    Control tempControl = new Text( toolbar, SWT.NONE );
    separator.setControl( tempControl );
    tempControl.dispose();
    assertEquals( null, separator.getControl() );
  }

  @Test
  public void testSeparatorWithControlBounds() {
    ToolItem push = new ToolItem( toolbar, SWT.PUSH );
    ToolItem separator = new ToolItem( toolbar, SWT.SEPARATOR );
    separator.setWidth( 60 );
    Text text = new Text( toolbar, SWT.BORDER );
    separator.setControl( text );
    push.setImage( display.getSystemImage( SWT.ICON_ERROR ) );
    toolbar.pack();
    assertEquals( separator.getBounds(), text.getBounds() );
  }

  @Test
  public void testSeparatorWidthHorizontal() {
    ToolItem push = new ToolItem( toolbar, SWT.PUSH );
    ToolItem separator = new ToolItem( toolbar, SWT.SEPARATOR );
    push.setImage( display.getSystemImage( SWT.ICON_ERROR ) );
    toolbar.pack();
    int initalWidth = separator.getSeparatorWidth();
    assertEquals( initalWidth, separator.getWidth() );
    separator.setWidth( 60 );
    toolbar.pack();
    assertEquals( 60, separator.getWidth() );
    separator.setWidth( 60 );
  }

  @Test
  public void testSeparatorWidthVertical() {
    toolbar = new ToolBar( shell, SWT.VERTICAL );
    ToolItem push = new ToolItem( toolbar, SWT.PUSH );
    ToolItem separator = new ToolItem( toolbar, SWT.SEPARATOR );
    push.setImage( display.getSystemImage( SWT.ICON_ERROR ) );
    toolbar.pack();
    int initalWidth = push.getWidth();
    assertEquals( initalWidth, separator.getWidth() );
    separator.setWidth( 60 );
    toolbar.pack();
    assertEquals( 60, separator.getWidth() );
    separator.setWidth( 60 );
  }

  @Test
  public void testPreferredHeight() {
    toolbar = new ToolBar( shell, SWT.VERTICAL );
    ToolItem push = new ToolItem( toolbar, SWT.PUSH );
    assertEquals( 22, push.getPreferredHeight() );
    push.setText( "x" );
    assertEquals( 30, push.getPreferredHeight() );
    push.setImage( display.getSystemImage( SWT.ICON_ERROR ) );
    assertEquals( 66, push.getPreferredHeight() );
  }

  @Test
  public void testPreferredWidth() {
    toolbar = new ToolBar( shell, SWT.VERTICAL );
    ToolItem push = new ToolItem( toolbar, SWT.PUSH );
    assertEquals( 16, push.getPreferredWidth() );
    push.setImage( display.getSystemImage( SWT.ICON_ERROR ) );
    assertEquals( 48, push.getPreferredWidth() );
    push.setText( "x" );
    assertEquals( 48, push.getPreferredWidth() );
    push.setText( "Hello Hello Hello" );
    assertTrue( push.getPreferredWidth() > 60 );
  }

  @Test
  public void testPreferredHeightWithStyleRight() {
    toolbar = new ToolBar( shell, SWT.VERTICAL | SWT.RIGHT );
    ToolItem push = new ToolItem( toolbar, SWT.PUSH );
    assertEquals( 22, push.getPreferredHeight() );
    push.setText( "Hello" );
    assertEquals( 30, push.getPreferredHeight() );
    push.setImage( display.getSystemImage( SWT.ICON_ERROR ) );
    assertEquals( 48, push.getPreferredHeight() );
  }

  @Test
  public void testPreferredWidthWithStyleRight() {
    toolbar = new ToolBar( shell, SWT.VERTICAL | SWT.RIGHT );
    ToolItem push = new ToolItem( toolbar, SWT.PUSH );
    assertEquals( 16, push.getPreferredWidth() );
    push.setImage( display.getSystemImage( SWT.ICON_ERROR ) );
    assertEquals( 48, push.getPreferredWidth() );
    push.setText( "x" );
    assertTrue( push.getPreferredWidth() > 44 );
  }

  @Test
  public void testDropDownPreferredWidth() {
    toolbar = new ToolBar( shell, SWT.VERTICAL );
    ToolItem push = new ToolItem( toolbar, SWT.DROP_DOWN );
    assertEquals( 32, push.getPreferredWidth() );
    push.setImage(  display.getSystemImage( SWT.ICON_ERROR ) );
    assertEquals( 64, push.getPreferredWidth() );
    push.setText( "x" );
    assertTrue( push.getPreferredWidth() > 60 );
  }

  @Test
  public void testAddSelectionListener() {
    toolItem.addSelectionListener( mock( SelectionListener.class ) );

    assertTrue( toolItem.isListening( SWT.Selection ) );
    assertTrue( toolItem.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testRemoveSelectionListener() {
    SelectionListener listener = mock( SelectionListener.class );
    toolItem.addSelectionListener( listener );

    toolItem.removeSelectionListener( listener );

    assertFalse( toolItem.isListening( SWT.Selection ) );
    assertFalse( toolItem.isListening( SWT.DefaultSelection ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddSelectionListenerWithNullArgument() {
    toolItem.addSelectionListener( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemoveSelectionListenerWithNullArgument() {
    toolItem.removeSelectionListener( null );
  }

  @Test
  public void testMarkupToolTipTextWithoutMarkupEnabled() {
    toolItem.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.FALSE );

    try {
      toolItem.setToolTipText( "invalid xhtml: <<&>>" );
    } catch( IllegalArgumentException notExpected ) {
      fail();
    }
  }

  @Test( expected = IllegalArgumentException.class )
  public void testMarkupToolTipTextWithMarkupEnabled() {
    toolItem.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.TRUE );

    toolItem.setToolTipText( "invalid xhtml: <<&>>" );
  }

  @Test
  public void testMarkupTextWithMarkupEnabled_ValidationDisabled() {
    toolItem.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.TRUE );
    toolItem.setData( MarkupValidator.MARKUP_VALIDATION_DISABLED, Boolean.TRUE );

    try {
      toolItem.setToolTipText( "invalid xhtml: <<&>>" );
    } catch( IllegalArgumentException notExpected ) {
      fail();
    }
  }

  @Test
  public void testSetToolTipMarkupEnabled_onDirtyWidget() {
    toolItem.setToolTipText( "something" );

    try {
      toolItem.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.TRUE );
      fail();
    } catch( SWTException expected ) {
      assertTrue( expected.throwable instanceof IllegalStateException );
    }
  }

  @Test
  public void testSetToolTipMarkupEnabled_onDirtyWidget_onceEnabledBefore() {
    toolItem.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.TRUE );
    toolItem.setToolTipText( "something" );

    toolItem.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.TRUE );
  }

  @Test
  public void testDisableMarkupIsIgnored() {
    toolItem.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.TRUE );

    toolItem.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.FALSE );

    assertEquals( Boolean.TRUE, toolItem.getData( RWT.TOOLTIP_MARKUP_ENABLED ) );
  }

  @Test
  public void testSetData() {
    toolItem.setData( "foo", "bar" );

    assertEquals( "bar", toolItem.getData( "foo" ) );
  }

  @Test
  public void testBadge_isSetForPush() {
    toolItem = new ToolItem( toolbar, SWT.PUSH );

    toolItem.setData( RWT.BADGE, "11" );

    assertEquals( "11", toolItem.getData( RWT.BADGE ) );
  }

  @Test
  public void testBadge_isNotSetForCheck() {
    toolItem = new ToolItem( toolbar, SWT.CHECK );

    toolItem.setData( RWT.BADGE, "11" );

    assertNull( toolItem.getData( RWT.BADGE ) );
  }

  @Test
  public void testBadge_isNotSetForRadio() {
    toolItem = new ToolItem( toolbar, SWT.RADIO );

    toolItem.setData( RWT.BADGE, "11" );

    assertNull( toolItem.getData( RWT.BADGE ) );
  }

  @Test
  public void testBadge_isNotSetForSeparator() {
    toolItem = new ToolItem( toolbar, SWT.SEPARATOR );

    toolItem.setData( RWT.BADGE, "11" );

    assertNull( toolItem.getData( RWT.BADGE ) );
  }

  @Test
  public void testBadge_isNotSetForDropDown() {
    toolItem = new ToolItem( toolbar, SWT.DROP_DOWN );

    toolItem.setData( RWT.BADGE, "11" );

    assertNull( toolItem.getData( RWT.BADGE ) );
  }

  @Test
  public void testGetAdapter_LCA() {
    assertTrue( toolItem.getAdapter( WidgetLCA.class ) instanceof ToolItemLCA );
    assertSame( toolItem.getAdapter( WidgetLCA.class ), toolItem.getAdapter( WidgetLCA.class ) );
  }

}
