/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 *******************************************************************************/
package org.eclipse.rap.rwt.themes.test.rwtdefault;

import java.io.IOException;

import org.eclipse.rap.rwt.themes.test.ThemeTestBase;
import org.eclipse.rap.rwt.themes.test.ThemesTestUtil;
import org.junit.Test;


public class DefaultTheme_Test extends ThemeTestBase {

  @Test
  public void testDefaultTheme() throws IOException {
    ThemesTestUtil.createAndActivateTheme( ThemesTestUtil.DEFAULT_PATH, "test.dafault.id" );
    processCssTestFile( getClass(), "Button.test.css" );
    processCssTestFile( getClass(), "Shell.test.css" );
    processCssTestFile( getClass(), "ToolBar.test.css" );
    processCssTestFile( getClass(), "Tree.test.css" );
    processCssTestFile( getClass(), "Table.test.css" );
    processCssTestFile( getClass(), "List.test.css" );
    processCssTestFile( getClass(), "Link.test.css" );
    processCssTestFile( getClass(), "Menu.test.css" );
    processCssTestFile( getClass(), "CLabel.test.css" );
    processCssTestFile( getClass(), "Label.test.css" );
    processCssTestFile( getClass(), "ExpandBar.test.css" );
    processCssTestFile( getClass(), "Combo.test.css" );
    processCssTestFile( getClass(), "Spinner.test.css" );
    processCssTestFile( getClass(), "DateTime.test.css" );
    processCssTestFile( getClass(), "Text.test.css" );
    processCssTestFile( getClass(), "Group.test.css" );
    processCssTestFile( getClass(), "ProgressBar.test.css" );
    processCssTestFile( getClass(), "Composite.test.css" );
    processCssTestFile( getClass(), "Slider.test.css" );
    processCssTestFile( getClass(), "ScrollBar.test.css" );
    processCssTestFile( getClass(), "Widget.test.css" );
    processCssTestFile( getClass(), "TabFolder.test.css" );
    processCssTestFile( getClass(), "CTabFolder.test.css" );
    processCssTestFile( getClass(), "Scale.test.css" );
    processCssTestFile( getClass(), "Sash.test.css" );
    processCssTestFile( getClass(), "ToolTip.test.css" );
    processCssTestFile( getClass(), "FileUpload.test.css" );
  }

}
