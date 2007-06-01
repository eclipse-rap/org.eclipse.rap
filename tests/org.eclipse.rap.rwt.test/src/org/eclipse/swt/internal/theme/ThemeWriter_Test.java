/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.theme;

import junit.framework.TestCase;

public class ThemeWriter_Test extends TestCase {

  public void testAColor() throws Exception {
    ThemeWriter writer = new ThemeWriter( "foo", "a", ThemeWriter.COLOR );
    writer.writeColor( "default.background", new QxColor( "#ffce67" ) );
    writer.writeColor( "default.foreground", new QxColor( "#ffce67" ) );
    writer.writeColor( "another.background", new QxColor( "#ffce67" ) );
    String generatedCode = writer.getGeneratedCode();
//    System.out.println( generatedCode );
//    System.out.println( "---" );
    String defStr = "qx.Theme.define( \"org.eclipse.swt.theme.AColors\",";
    assertTrue( generatedCode.indexOf( defStr ) != 0 );
    assertTrue( generatedCode.indexOf( "[ 255, 206, 103 ]" ) != -1 );
    assertTrue( generatedCode.endsWith( "} );\n" ) );
  }
  
  public void testDefaultBorder() throws Exception {
    ThemeWriter writer = new ThemeWriter( "", "Default", ThemeWriter.BORDER );
    writer.writeBorder( "default.border",
                        new QxBorder( 1, "solid", new QxColor( "#121212" ) ) );
    writer.writeBorder( "another.border",
                        new QxBorder( 2, "outset", new QxColor( "#ffffff" ) ) );
    String generatedCode = writer.getGeneratedCode();
//    System.out.println( generatedCode );
//    System.out.println( "---" );
    String defStr = "qx.Theme.define( \"org.eclipse.swt.theme.DefaultBorders\",";
    assertTrue( generatedCode.indexOf( defStr ) != 0 );
//    TODO [rst] Test color representation
//    assertTrue( generatedCode.indexOf( "[ 255, 206, 103 ]" ) != -1 );
    assertTrue( generatedCode.endsWith( "} );\n" ) );
  }

}
