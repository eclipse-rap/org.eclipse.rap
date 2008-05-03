/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.theme;

import org.eclipse.rwt.internal.theme.css.CssFileReader_Test;
import org.eclipse.rwt.internal.theme.css.PropertyResolver_Test;

import junit.framework.Test;
import junit.framework.TestSuite;


public class AllThemeTests {

  public static Test suite() {
    TestSuite suite = new TestSuite( "Test for org.eclipse.rwt.internal.theme" );
    //$JUnit-BEGIN$
    suite.addTestSuite( QxBorder_Test.class );
    suite.addTestSuite( QxBoolean_Test.class );
    suite.addTestSuite( ThemeManager_Test.class );
    suite.addTestSuite( QxTheme_Test.class );
    suite.addTestSuite( ThemeUtil_Test.class );
    suite.addTestSuite( QxBoxDimensions_Test.class );
    suite.addTestSuite( QxColor_Test.class );
    suite.addTestSuite( Theme_Test.class );
    suite.addTestSuite( QxImage_Test.class );
    suite.addTestSuite( QxDimension_Test.class );
    suite.addTestSuite( ThemeDefinitionReader_Test.class );
    suite.addTestSuite( QxFont_Test.class );
    suite.addTestSuite( CssFileReader_Test.class );
    suite.addTestSuite( PropertyResolver_Test.class );
    //$JUnit-END$
    return suite;
  }
}
