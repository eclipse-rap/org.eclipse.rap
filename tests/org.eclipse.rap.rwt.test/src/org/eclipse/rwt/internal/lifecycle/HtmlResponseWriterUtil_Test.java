/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.lifecycle;

import junit.framework.TestCase;


public class HtmlResponseWriterUtil_Test extends TestCase {
  
  public void testEmptyTags() throws Exception {
    String[] emptyTags = new String[] {
      "area",
      "br", 
      "base", 
      "basefont",
      "col",
      "frame",
      "hr",
      "img", 
      "input", 
      "isindex",
      "link",
      "meta",
      "param",
      "AREA",
      "BR", 
      "BASE", 
      "BASEFONT",
      "COL",
      "FRAME",
      "HR",
      "IMG", 
      "INPUT", 
      "ISINDEX",
      "LINK",
      "META",
      "PARAM"
    };
    boolean result = true;
    for( int i = 0; result && i < emptyTags.length; i++ ) {
      result = HtmlResponseWriterUtil.isEmptyTag( emptyTags[ i ] );
    }
    assertTrue( result );
    
    String[] nonEmptyTags = new String[] {
      "html",
      "body",
      "title",
      "head",
      "HTML",
      "BODY",
      "TITLE",
      "HEAD"
    };
    result = false;
    for( int i = 0; !result && i < nonEmptyTags.length; i++ ) {
      result = HtmlResponseWriterUtil.isEmptyTag( nonEmptyTags[ i ] );
    }
    assertFalse( result );
  }
}
