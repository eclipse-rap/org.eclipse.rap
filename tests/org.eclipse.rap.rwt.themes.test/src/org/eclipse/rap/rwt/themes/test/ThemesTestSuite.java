/******************************************************************************* 
* Copyright (c) 2010 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.rwt.themes.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.rap.rwt.themes.test.business.BusinessTheme_Test;
import org.eclipse.rap.rwt.themes.test.fancy.FancyTheme_Test;


public class ThemesTestSuite {
  
  public static Test suite() {
    TestSuite cssSuite = new TestSuite( "Tests for RWT Themes" );
    // Add CSS Themes tests
    cssSuite.addTest( new TestSuite( BusinessTheme_Test.class, 
                                     "Business Theme" ) );
    cssSuite.addTest( new TestSuite( FancyTheme_Test.class, 
                                     "Fancy Theme" ) );   
    return cssSuite;
  }
  
}
