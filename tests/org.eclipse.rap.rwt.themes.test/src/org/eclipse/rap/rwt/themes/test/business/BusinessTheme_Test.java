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

import java.io.IOException;

import org.eclipse.rap.rwt.themes.test.ThemeTestCase;
import org.eclipse.rap.rwt.themes.test.ThemesTestUtil;

public class BusinessTheme_Test extends ThemeTestCase {

  public void testBusinessTheme() throws IOException {
    ThemesTestUtil.createAndActivateTheme( ThemesTestUtil.BUSINESS_PATH,
                                           "test.business.id" );
    processCssTestFile( getClass(), "Menu.test.css" );
    processCssTestFile( getClass(), "Menu.CustomVariant.test.css" );
    processCssTestFile( getClass(), "Button.test.css" );
    processCssTestFile( getClass(), "Button.CustomVariant.test.css" );
    processCssTestFile( getClass(), "Shell.test.css" );
    processCssTestFile( getClass(), "Shell.CustomVariant.test.css" );
    processCssTestFile( getClass(), "Tree.test.css" );
    processCssTestFile( getClass(), "Table.test.css" );
    processCssTestFile( getClass(), "List.test.css" );
    processCssTestFile( getClass(), "Link.test.css" );
    processCssTestFile( getClass(), "Label.test.css" );
    processCssTestFile( getClass(), "ExpandBar.test.css" );
    processCssTestFile( getClass(), "Spinner.test.css" );
    processCssTestFile( getClass(), "DateTime.test.css" );
    processCssTestFile( getClass(), "Text.test.css" );
    processCssTestFile( getClass(), "Group.test.css" );
    processCssTestFile( getClass(), "ProgressBar.test.css" );
    processCssTestFile( getClass(), "Composite.test.css" );
    processCssTestFile( getClass(), "Composite.CustomVariant.test.css" );
    processCssTestFile( getClass(), "ToolTip.test.css" );
    processCssTestFile( getClass(), "Slider.test.css" );
    processCssTestFile( getClass(), "Display.test.css" );
    processCssTestFile( getClass(), "TabFolder.test.css" );
    processCssTestFile( getClass(), "ToolBar.test.css" );
    processCssTestFile( getClass(), "ToolBar.CustomVariant.test.css" );
    processCssTestFile( getClass(), "Combo.test.css" );
  }

}
