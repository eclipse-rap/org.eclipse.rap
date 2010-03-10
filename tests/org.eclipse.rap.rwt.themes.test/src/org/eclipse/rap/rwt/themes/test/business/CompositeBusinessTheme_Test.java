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
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.theme.QxBorder;
import org.eclipse.rwt.internal.theme.QxColor;
import org.eclipse.rwt.internal.theme.QxImage;
import org.eclipse.rwt.internal.theme.QxType;
import org.eclipse.rwt.internal.theme.SimpleSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class CompositeBusinessTheme_Test extends TestCase {
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    ThemesTestUtil.activateTheme( ThemesTestUtil.BUSINESS_THEME_ID, 
                                   ThemesTestUtil.BUSINESS_PATH );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testCompositeBackgroundColor() {
    Composite comp = createComposite( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( comp, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testCompositeBackgroundImage() {
    Composite comp = createComposite( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( comp, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertTrue( image.none );
  }
  
  public void testCompositeBorder() {
    Composite comp = createComposite( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( comp, 
                                                   selector, 
                                                   "border" );
    QxBorder border = ( QxBorder ) cssValue;
    assertEquals( QxBorder.NONE, border );
  }
  
  public void testCompositeBackgroundColorBorder() {
    Composite comp = createComposite( SWT.NONE );
    SimpleSelector selector = new SimpleSelector( new String[] { "[BORDER" } );
    QxType cssValue = ThemesTestUtil.getCssValue( comp, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testCompositeBackgroundImageBorder() {
    Composite comp = createComposite( SWT.NONE );
    SimpleSelector selector = new SimpleSelector( new String[] { "[BORDER" } );
    QxType cssValue = ThemesTestUtil.getCssValue( comp, 
                                                   selector, 
                                                   "background-image" );
    QxImage image = ( QxImage ) cssValue;
    assertTrue( image.none );
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
