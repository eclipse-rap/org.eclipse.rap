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
import org.eclipse.rwt.internal.theme.QxColor;
import org.eclipse.rwt.internal.theme.QxType;
import org.eclipse.rwt.internal.theme.SimpleSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;


public class LinkBusinessTheme_Test extends TestCase {
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    ThemesTestUtil.activateTheme( ThemesTestUtil.BUSINESS_THEME_ID, 
                                   ThemesTestUtil.BUSINESS_PATH );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testLinkColor() {
    Link link = createLink( SWT.NONE );
    SimpleSelector selector = SimpleSelector.DEFAULT;
    QxType cssValue = ThemesTestUtil.getCssValueForElement( link, 
                                                             selector, 
                                                             "color",
                                                             "Link-Hyperlink" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "00589f", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
  
  public void testLinkColorDisabled() {
    Link link = createLink( SWT.NONE );
    SimpleSelector selector 
      = new SimpleSelector( new String[] { ":disabled" } );
    QxType cssValue = ThemesTestUtil.getCssValueForElement( link, 
                                                             selector, 
                                                             "color",
                                                             "Link-Hyperlink" );
    QxColor color = ( QxColor ) cssValue;
    Color swtColor = Graphics.getColor( color.red, color.green, color.blue );
    assertEquals( "959595", ThemesTestUtil.getHexStringFromColor( swtColor ) );
  }
    
  /*
   * Little Helper Method to create Links
   */
  private Link createLink( final int style ) {
    Display display = new Display();
    Shell shell = new Shell( display, style );
    Link link = new Link( shell, style );
    link.setText( "<a href='www.eclipse.org'>a link</a>" );
    return link;
  }
  
}
