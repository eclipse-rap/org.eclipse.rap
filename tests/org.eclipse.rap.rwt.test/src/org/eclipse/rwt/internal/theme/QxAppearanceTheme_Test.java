/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import junit.framework.TestCase;


public class QxAppearanceTheme_Test extends TestCase {

  public void testNoValues() {
    QxAppearanceWriter theme = new QxAppearanceWriter();
    String code = theme.getJsCode();
    assertTrue( code.contains( "qx.theme.manager.Appearance.getInstance().setCurrentTheme( {\n" ) );
    assertTrue( code.endsWith( "} );\n" ) );
  }

  public void testTailAlreadyWritten() {
    QxAppearanceWriter theme = new QxAppearanceWriter();
    theme.appendAppearances( "foo" );
    theme.getJsCode();
    theme.getJsCode(); // calling getJsCode twice is ok

    try {
      theme.appendAppearances( "bar" );
      fail( "ISE expected" );
    } catch( final IllegalStateException e ) {
      // expected
    }
  }

}
