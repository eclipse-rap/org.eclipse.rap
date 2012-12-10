/*******************************************************************************
 * Copyright (c) 2008, 2012 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.application.RWTFactory;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.theme.css.StyleSheet;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.engine.ThemeManagerHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;


public class AbstractThemeAdapter_Test extends TestCase {

  private Shell shell;

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeNewRequest();
    Display display = new Display();
    shell = new Shell( display );
  }

  protected void tearDown() throws Exception {
    RWTFactory.getThemeManager().deactivate();
    Fixture.tearDown();
  }

  public void testGetPrimaryElementForLabel() {
    Label label = new Label( shell, SWT.NONE );
    assertEquals( "Label", AbstractThemeAdapter.getPrimaryElement( label ) );
  }

  public void testGetPrimaryElementForButton() {
    Button button = new Button( shell, SWT.BORDER );
    assertEquals( "Button", AbstractThemeAdapter.getPrimaryElement( button ) );
  }

  public void testGetPrimaryElementForTree() {
    Tree tree = new Tree( shell, SWT.BORDER );
    assertEquals( "Tree", AbstractThemeAdapter.getPrimaryElement( tree ) );
  }

  public void testGetPrimaryElementForCompositeSubclass() {
    Composite customComposite = new Composite( shell, SWT.BORDER ) {
      // empty subclass
    };
    assertEquals( "Composite", AbstractThemeAdapter.getPrimaryElement( customComposite ) );
  }

  public void testGetCssValues() throws Exception {
    CustomWidget custom = new CustomWidget( shell, SWT.NONE );
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( "TestExample.css" );
    Theme theme = new Theme( "customId", "Custom Theme", styleSheet );
    ThemeManagerHelper.resetThemeManager();
    ThemeManager themeManager = RWTFactory.getThemeManager();
    themeManager.initialize();
    themeManager.registerTheme( theme );
    themeManager.activate();
    AbstractThemeAdapter adapter = new AbstractThemeAdapter() {
      protected void configureMatcher( WidgetMatcher matcher ) {
      }
    };
    // create theme adapter
    ThemeableWidget widget = new ThemeableWidget( CustomWidget.class, null );
    IThemeCssElement element = new ThemeCssElement( "CustomWidget" );
    widget.elements = new IThemeCssElement[] { element };
    // check default values
    Color defaultColor = adapter.getCssColor( "CustomWidget", "color", custom );
    assertNotNull( defaultColor );
    int defaultBorderWidth = adapter.getCssBorderWidth( "CustomWidget", "border", custom );
    // switch theme
    ThemeUtil.setCurrentThemeId( ContextProvider.getUISession(), "customId" );
    // color is redefined
    Color customColor = adapter.getCssColor( "CustomWidget", "color", custom );
    assertFalse( defaultColor.equals( customColor ) );
    // borderWidth is not
    int customBorderWidth = adapter.getCssBorderWidth( "CustomWidget", "border", custom );
    assertTrue( defaultBorderWidth == customBorderWidth );
    RWTFactory.getThemeManager().deactivate();
  }

}
