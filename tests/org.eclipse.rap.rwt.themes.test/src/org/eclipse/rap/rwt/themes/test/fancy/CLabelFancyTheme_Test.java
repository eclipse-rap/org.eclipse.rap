/******************************************************************************* 
* Copyright (c) 2010 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.rwt.themes.test.fancy;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.themes.test.ThemesTestUtil;
import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.theme.QxColor;
import org.eclipse.rwt.internal.theme.QxType;
import org.eclipse.rwt.internal.theme.SimpleSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


public class CLabelFancyTheme_Test extends TestCase {
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    ThemesTestUtil.activateTheme( ThemesTestUtil.FANCY_THEME_ID, 
                                   ThemesTestUtil.FANCY_PATH );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testLabelColor() {
    CLabel label = createLabel( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( label, 
                                                   selector, 
                                                   "color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "4a4a4a", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testLabelBackgroundColor() {
    CLabel label = createLabel( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValue( label, 
                                                   selector, 
                                                   "background-color" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "ffffff", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
    
  /*
   * Little Helper Method to create CLabels
   */
  private CLabel createLabel( final int style ) {
    Display display = new Display();
    Shell shell = new Shell( display, style );
    CLabel label = new CLabel( shell, style );
    label.setText( "I'm a Clabel" );
    return label;
  }
  
}
