/*******************************************************************************
 * Copyright (c) 2009, 2015 EclipseSource and others.
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
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.internal.widgets.MarkupValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ToolItem_Test {

  private Display display;
  private Shell shell;
  private ToolBar toolbar;
  private ToolItem item;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display , SWT.NONE );
    toolbar = new ToolBar( shell, SWT.NONE );
    item = new ToolItem( toolbar, SWT.NONE );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testText() {
    ToolItem separator = new ToolItem( toolbar, SWT.SEPARATOR );
    String text0 = "text0";
    String text1 = "text1";

    // Test 'normal' tool item
    item.setText( text0 );
    assertEquals( text0, item.getText() );
    item.setText( text1 );
    assertEquals( text1, item.getText() );
    // Test separator tool item
    assertEquals( "", separator.getText() );
    separator.setText( text1 );
    assertEquals( "", separator.getText() );
  }

  @Test
  public void testImage() {
    item.setImage( null );
    assertEquals( null, item.getImage() );
  }

  @Test
  public void testEnabled() {
    ToolItem separator = new ToolItem( toolbar, SWT.SEPARATOR );
    separator.setControl( new Text( toolbar, SWT.NONE ) );

    // ToolItem must be enabled initially
    assertTrue( item.getEnabled() );

    // Test enabled ToolItem on disabled ToolBar
    toolbar.setEnabled( false );
    item.setEnabled( true );
    assertTrue( item.getEnabled() );
    assertFalse( item.isEnabled() );

    // Test disabled ToolItem on disabled ToolBar
    toolbar.setEnabled( false );
    item.setEnabled( false );
    assertFalse( item.getEnabled() );
    assertFalse( item.isEnabled() );

    // Test SEPARATOR ToolItem
    separator.setEnabled( false );
    assertTrue( separator.getControl().getEnabled() );
  }

  @Test
  public void testSeparatorWithControl() {
    ToolItem separator = new ToolItem( toolbar, SWT.SEPARATOR );
    separator.setControl( new Text( toolbar, SWT.NONE ) );

    // Using control property on ToolItem without SEPARATOR style has no effect
    item.setControl( new Text( toolbar, SWT.NONE ) );
    assertEquals( null, item.getControl() );

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
    item.addSelectionListener( mock( SelectionListener.class ) );

    assertTrue( item.isListening( SWT.Selection ) );
    assertTrue( item.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testRemoveSelectionListener() {
    SelectionListener listener = mock( SelectionListener.class );
    item.addSelectionListener( listener );

    item.removeSelectionListener( listener );

    assertFalse( item.isListening( SWT.Selection ) );
    assertFalse( item.isListening( SWT.DefaultSelection ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddSelectionListenerWithNullArgument() {
    item.addSelectionListener( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemoveSelectionListenerWithNullArgument() {
    item.removeSelectionListener( null );
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
  public void testBadge_isSetForPush() {
    item = new ToolItem( toolbar, SWT.PUSH );

    item.setData( RWT.BADGE, "11" );

    assertEquals( "11", item.getData( RWT.BADGE ) );
  }

  @Test
  public void testBadge_isNotSetForCheck() {
    item = new ToolItem( toolbar, SWT.CHECK );

    item.setData( RWT.BADGE, "11" );

    assertNull( item.getData( RWT.BADGE ) );
  }

  @Test
  public void testBadge_isNotSetForRadio() {
    item = new ToolItem( toolbar, SWT.RADIO );

    item.setData( RWT.BADGE, "11" );

    assertNull( item.getData( RWT.BADGE ) );
  }

  @Test
  public void testBadge_isNotSetForSeparator() {
    item = new ToolItem( toolbar, SWT.SEPARATOR );

    item.setData( RWT.BADGE, "11" );

    assertNull( item.getData( RWT.BADGE ) );
  }

  @Test
  public void testBadge_isNotSetForDropDown() {
    item = new ToolItem( toolbar, SWT.DROP_DOWN );

    item.setData( RWT.BADGE, "11" );

    assertNull( item.getData( RWT.BADGE ) );
  }

}
