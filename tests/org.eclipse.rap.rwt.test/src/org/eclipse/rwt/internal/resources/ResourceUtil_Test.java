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
package org.eclipse.rwt.internal.resources;

import junit.framework.TestCase;


public class ResourceUtil_Test extends TestCase {
  
  public void testCompress() {
    // tests for removeOneLineComments
    StringBuffer javaScript = new StringBuffer( "" );
    ResourceUtil.compress( javaScript );
    assertEquals( "", javaScript.toString() );
    
    javaScript = new StringBuffer( "// the one and only" );
    ResourceUtil.compress( javaScript );
    assertEquals( "", javaScript.toString() );
    
    javaScript = new StringBuffer( "// the one and only\nfunction xy(){}" ) ;
    ResourceUtil.compress( javaScript );
    assertEquals( "\nfunction xy(){}", javaScript.toString() );

    javaScript = new StringBuffer( "// the one and only\n\rfunction xy(){}" ) ;
    ResourceUtil.compress( javaScript );
    assertEquals( "\n\rfunction xy(){}", javaScript.toString() );
    
    javaScript = new StringBuffer( "// the one and only\rfunction xy(){}" ) ;
    ResourceUtil.compress( javaScript );
    assertEquals( "\rfunction xy(){}", javaScript.toString() );
    
    javaScript = new StringBuffer( "////" ) ;
    ResourceUtil.compress( javaScript );
    assertEquals( "", javaScript.toString() );
    
    javaScript = new StringBuffer( "//// \nfunction xyz(){}" ) ;
    ResourceUtil.compress( javaScript );
    assertEquals( "\nfunction xyz(){}", javaScript.toString() );
    
    javaScript = new StringBuffer( "// line1\n//line 2\nfunction xyz(){}" ) ;
    ResourceUtil.compress( javaScript );
    assertEquals( "\n\nfunction xyz(){}", javaScript.toString() );
    
    // tests for removeMultiLineComments
    javaScript = new StringBuffer( "/**/" ) ;
    ResourceUtil.compress( javaScript );
    assertEquals( "", javaScript.toString() );
    
    javaScript = new StringBuffer( "/**/noMoreComment" ) ;
    ResourceUtil.compress( javaScript );
    assertEquals( "noMoreComment", javaScript.toString() );
    
    javaScript 
      = new StringBuffer( "/*a\ncomment\nwith\nlineBreaks*/noMoreComment" ) ;
    ResourceUtil.compress( javaScript );
    assertEquals( "noMoreComment", javaScript.toString() );
    
    javaScript 
      = new StringBuffer( "/** JavaDoc-like comment */noMoreComment" ) ;
    ResourceUtil.compress( javaScript );
    assertEquals( "noMoreComment", javaScript.toString() );
    
    javaScript 
      = new StringBuffer( "/* */noMoreComment" ) ;
    ResourceUtil.compress( javaScript );
    assertEquals( "noMoreComment", javaScript.toString() );
    
    // tests for removeMultipleBlanks
    javaScript = new StringBuffer( "  " ) ;
    ResourceUtil.compress( javaScript );
    assertEquals( " ", javaScript.toString() );
    
    javaScript = new StringBuffer( "   " ) ;
    ResourceUtil.compress( javaScript );
    assertEquals( " ", javaScript.toString() );
    
    javaScript 
      = new StringBuffer( "  some  Text  with        loads of    blanks" ) ;
    ResourceUtil.compress( javaScript );
    assertEquals( " some Text with loads of blanks", javaScript.toString() );
  }
}
