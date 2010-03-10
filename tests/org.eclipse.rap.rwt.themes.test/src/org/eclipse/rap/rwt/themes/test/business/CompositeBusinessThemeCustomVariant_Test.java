/******************************************************************************* 
* Copyright (c) 2010 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.rwt.themes.test.business;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.themes.test.ThemesTestUtil;
import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.theme.QxBorder;
import org.eclipse.rwt.internal.theme.QxBoxDimensions;
import org.eclipse.rwt.internal.theme.QxColor;
import org.eclipse.rwt.internal.theme.QxType;
import org.eclipse.rwt.internal.theme.SimpleSelector;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class CompositeBusinessThemeCustomVariant_Test extends TestCase {
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    ThemesTestUtil.activateTheme( ThemesTestUtil.BUSINESS_THEME_ID, 
                                   ThemesTestUtil.BUSINESS_PATH );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testCompositeTabActiveBorder() {
    Composite comp = createComposite( SWT.NONE );
    comp.setData( WidgetUtil.CUSTOM_VARIANT, "tabActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".tabActive" } );
    QxType cssValue = ThemesTestUtil.getCssValue( comp, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( "solid", border.style );
    assertEquals( 1, border.width );
    assertEquals( "#00589f", border.color );
  }
  
  public void testCompositeTabActiveBorderRadius() {
    Composite comp = createComposite( SWT.NONE );
    comp.setData( WidgetUtil.CUSTOM_VARIANT, "tabActive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".tabActive" } );
    QxType cssValue = ThemesTestUtil.getCssValue( comp, 
                                                   selector, 
                                                   "border-radius" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 0, dim.top );
    assertEquals( 7, dim.right );
    assertEquals( 0, dim.bottom );
    assertEquals( 0, dim.left );
  }
  
  public void testCompositeTabInActiveBorder() {
    Composite comp = createComposite( SWT.NONE );
    comp.setData( WidgetUtil.CUSTOM_VARIANT, "tabInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".tabInactive" } );
    QxType cssValue = ThemesTestUtil.getCssValue( comp, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( "solid", border.style );
    assertEquals( 1, border.width );
    assertEquals( "#949494", border.color );
  }
  
  public void testCompositeTabInActiveBorderRadius() {
    Composite comp = createComposite( SWT.NONE );
    comp.setData( WidgetUtil.CUSTOM_VARIANT, "tabInactive" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".tabInactive" } );
    QxType cssValue = ThemesTestUtil.getCssValue( comp, 
                                                   selector, 
                                                   "border-radius" );
    QxBoxDimensions dim = ( QxBoxDimensions ) cssValue;
    assertEquals( 0, dim.top );
    assertEquals( 7, dim.right );
    assertEquals( 0, dim.bottom );
    assertEquals( 0, dim.left );
  }
  
  public void testCompositeInActiveButtonBorder() {
    Composite comp = createComposite( SWT.NONE );
    comp.setData( WidgetUtil.CUSTOM_VARIANT, "inactiveButton" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".inactiveButton" } );
    QxType cssValue = ThemesTestUtil.getCssValue( comp, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( QxBorder.NONE, border );
  }
  
  public void testCompositeInActiveButtonBackgroundColor() {
    Composite comp = createComposite( SWT.NONE );
    comp.setData( WidgetUtil.CUSTOM_VARIANT, "inactiveButton" );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ".inactiveButton" } );
    QxType cssValue = ThemesTestUtil.getCssValue( comp, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    assertTrue( color.transparent );
  }
  
  /*
   * Little Helper Method to create Composites
   */
  private Composite createComposite( final int style ) {
    Display display = new Display();
    Shell shell = new Shell( display, style );
    Composite comp = new Composite( shell, style );
    return comp;
  }
  
}
