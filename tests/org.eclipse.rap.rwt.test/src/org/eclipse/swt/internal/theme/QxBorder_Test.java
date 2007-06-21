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

public class QxBorder_Test extends TestCase {
  
  public void testConstructor() throws Exception {
    try {
      new QxBorder( null );
      fail( "null arguement should throw NPE" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      new QxBorder( "-1" );
      fail( "negative width should throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      new QxBorder( "1 solid red 2" );
      fail( "one too many argument should throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }
  
  public void testDefaults() throws Exception {
    QxBorder border;
    border = new QxBorder( "" );
    assertEquals( 1, border.width );
    assertEquals( "solid", border.style );
    assertNull( border.color );
    border = new QxBorder( "none" );
    assertEquals( 0, border.width );
    assertEquals( "solid", border.style );
    border = new QxBorder( "2" );
    assertEquals( 2, border.width );
    assertEquals( "solid", border.style );
    border = new QxBorder( "black" );
    assertEquals( 1, border.width );
    assertEquals( "solid", border.style );
    assertEquals( "black", border.color );
    border = new QxBorder( "1 dashed red" );
    assertEquals( 1, border.width );
    assertEquals( "dashed", border.style );
    assertEquals( "red", border.color );
  }
  
  public void testQxColors() throws Exception {
    QxBorder border;
    // none
    border = new QxBorder( "none" );
    assertNull( border.getQxColors() );
    // thin inset
    border = new QxBorder( "1 inset" );
    assertEquals( "solid", border.getQxStyle() );
    assertEquals( "[ \"widget.shadow\", \"widget.highlight\", \"widget.highlight\", \"widget.shadow\" ]",
                  border.getQxColors() );
    // thin outset
    border = new QxBorder( "1 outset" );
    assertEquals( "solid", border.getQxStyle() );
    assertEquals( "[ \"widget.highlight\", \"widget.shadow\", \"widget.shadow\", \"widget.highlight\" ]",
                  border.getQxColors() );
    // inset
    border = new QxBorder( "2 inset" );
    assertEquals( "solid", border.getQxStyle() );
    assertEquals( "[ \"widget.shadow\", \"widget.highlight\", \"widget.highlight\", \"widget.shadow\" ]", 
                  border.getQxColors() );
    assertEquals( "[ \"widget.darkshadow\", \"widget.lightshadow\", \"widget.lightshadow\", \"widget.darkshadow\" ]", 
                  border.getQxInnerColors() );
    // outset
    border = new QxBorder( "2 outset" );
    assertEquals( "solid", border.getQxStyle() );
    assertEquals( "[ \"widget.lightshadow\", \"widget.darkshadow\", \"widget.darkshadow\", \"widget.lightshadow\" ]", 
                  border.getQxColors() );
    assertEquals( "[ \"widget.highlight\", \"widget.shadow\", \"widget.shadow\", \"widget.highlight\" ]", 
                  border.getQxInnerColors() );
    // groove
    border = new QxBorder( "2 groove" );
    assertEquals( "solid", border.getQxStyle() );
    assertEquals( "[ \"widget.shadow\", \"widget.highlight\", \"widget.highlight\", \"widget.shadow\" ]", 
                  border.getQxColors() );
    assertEquals( "[ \"widget.highlight\", \"widget.shadow\", \"widget.shadow\", \"widget.highlight\" ]", 
                  border.getQxInnerColors() );
    // ridge
    border = new QxBorder( "2 ridge" );
    assertEquals( "solid", border.getQxStyle() );
    assertEquals( "[ \"widget.highlight\", \"widget.shadow\", \"widget.shadow\", \"widget.highlight\" ]", 
                  border.getQxColors() );
    assertEquals( "[ \"widget.shadow\", \"widget.highlight\", \"widget.highlight\", \"widget.shadow\" ]", 
                  border.getQxInnerColors() );
  }
}
