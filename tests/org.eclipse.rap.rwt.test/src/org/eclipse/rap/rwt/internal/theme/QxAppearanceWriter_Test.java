/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
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

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;


public class QxAppearanceWriter_Test {

  @Test
  public void testNoValues() {
    List<String> appearances = Collections.<String>emptyList();
    String code = QxAppearanceWriter.createQxAppearanceTheme( appearances );

    assertTrue( code.startsWith( "rwt.theme.AppearanceManager.getInstance().setCurrentTheme( {\n" ) );
    assertTrue( code.endsWith( "} );\n" ) );
  }

  @Test
  public void testTailAlreadyWritten() {
    List<String> appearances = new ArrayList<String>();
    appearances.add( "foo\nfoo" );
    appearances.add( "bar\nbar" );
    String code = QxAppearanceWriter.createQxAppearanceTheme( appearances );

    assertTrue( code.startsWith( "rwt.theme.AppearanceManager.getInstance().setCurrentTheme( {\n" ) );
    assertTrue( code.endsWith( "} );\n" ) );
    assertTrue( code.contains( "foo\nfoo,\nbar\nbar" ) );
  }

}
