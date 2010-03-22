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
    processCssTestFile( getClass(), "Button.test.css" );
    processCssTestFile( getClass(), "ToolBar.test.css" );
  }

}
