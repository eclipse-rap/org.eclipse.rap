/*******************************************************************************
* Copyright (c) 2010 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.rap.rwt.themes.test.rwtdefault;

import java.io.IOException;
import java.util.Random;

import org.eclipse.rap.rwt.themes.test.ThemeTestCase;
import org.eclipse.rap.rwt.themes.test.ThemesTestUtil;


public class DefaultTheme_Test extends ThemeTestCase {

  public void testDefaultTheme() throws IOException {
    createFakeTheme( "widgets/buttonkit/Button.default.css" );
    processCssTestFile( getClass(), "Button.test.css" );
    createFakeTheme( "widgets/shellkit/Shell.default.css" );
    processCssTestFile( getClass(), "Shell.test.css" );
    createFakeTheme( "widgets/toolbarkit/ToolBar.default.css" );
    processCssTestFile( getClass(), "Toolbar.test.css" );
    createFakeTheme( "widgets/treekit/Tree.default.css" );
    processCssTestFile( getClass(), "Tree.test.css" );
    createFakeTheme( "widgets/tablekit/Table.default.css" );
    processCssTestFile( getClass(), "Table.test.css" );
    createFakeTheme( "widgets/listkit/List.default.css" );
    processCssTestFile( getClass(), "List.test.css" );
    createFakeTheme( "widgets/linkkit/Link.default.css" );
    processCssTestFile( getClass(), "Link.test.css" );
    createFakeTheme( "widgets/menukit/Menu.default.css" );
    processCssTestFile( getClass(), "Menu.test.css" );
    createFakeTheme( "custom/clabelkit/CLabel.default.css" );
    processCssTestFile( getClass(), "CLabel.test.css" );
    createFakeTheme( "widgets/labelkit/Label.default.css" );
    processCssTestFile( getClass(), "Label.test.css" );
    createFakeTheme( "widgets/expandbarkit/ExpandBar.default.css" );
    processCssTestFile( getClass(), "ExpandBar.test.css" );
    createFakeTheme( "widgets/combokit/Combo.default.css" );
    processCssTestFile( getClass(), "Combo.test.css" );
    createFakeTheme( "custom/ccombokit/CCombo.default.css" );
    processCssTestFile( getClass(), "CCombo.test.css" );
    createFakeTheme( "widgets/spinnerkit/Spinner.default.css" );
    processCssTestFile( getClass(), "Spinner.test.css" );
    createFakeTheme( "widgets/datetimekit/DateTime.default.css" );
    processCssTestFile( getClass(), "DateTime.test.css" );
    createFakeTheme( "widgets/textkit/Text.default.css" );
    processCssTestFile( getClass(), "Text.test.css" );
    createFakeTheme( "widgets/groupkit/Group.default.css" );
    processCssTestFile( getClass(), "Group.test.css" );
    createFakeTheme( "widgets/progressbarkit/ProgressBar.default.css" );
    processCssTestFile( getClass(), "ProgressBar.test.css" );
    createFakeTheme( "widgets/compositekit/Composite.default.css" );
    processCssTestFile( getClass(), "Composite.test.css" );
    createFakeTheme( "widgets/sliderkit/Slider.default.css" );
    processCssTestFile( getClass(), "Slider.test.css" );
    createFakeTheme( "widgets/widgetkit/Widget.default.css" );
    processCssTestFile( getClass(), "Widget.test.css" );
    createFakeTheme( "widgets/tabfolderkit/TabFolder.default.css" );
    processCssTestFile( getClass(), "TabFolder.test.css" );
    createFakeTheme( "custom/ctabfolderkit/CTabFolder.default.css" );
    processCssTestFile( getClass(), "CTabFolder.test.css" );
    createFakeTheme( "widgets/scalekit/Scale.default.css" );
    processCssTestFile( getClass(), "Scale.test.css" );
    createFakeTheme( "widgets/sashkit/Sash.default.css" );
    processCssTestFile( getClass(), "Sash.test.css" );
  }


  private void createFakeTheme( final String filePath ) {
    String prefix = ThemesTestUtil.DEFAULT_PREFIX;
    Random random = new Random();
    ThemesTestUtil.createAndActivateTheme( prefix + filePath,
                                           "test." + random.nextInt() + ".id");
  }

}
