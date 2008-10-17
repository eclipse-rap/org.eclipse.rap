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
package org.eclipse.rwt.internal.service;

import junit.framework.TestCase;


public class TemplateHolder_Test extends TestCase {
  private static final String SOME_TEXT_WITH_VARIABLE_DELIMITERS = "a$b{c}";
  private static final String NON_VARIABLE_TOKEN = "${nonVariable}";
  private static final String POSTFIX = "</template>";
  private static final String PREFIX = "<template>";
  private static final String TEMPLATE 
    =   PREFIX 
      + TemplateHolder.VAR_LIBRARIES
      + NON_VARIABLE_TOKEN
      + SOME_TEXT_WITH_VARIABLE_DELIMITERS
      + TemplateHolder.VAR_LIBRARIES
      + TemplateHolder.VAR_APPSCRIPT
      + POSTFIX;

  public void testTemplateParsing() {
    TemplateHolder template = new TemplateHolder( TEMPLATE  );
    String expected
      =   PREFIX 
        + "null"
        + NON_VARIABLE_TOKEN
        + SOME_TEXT_WITH_VARIABLE_DELIMITERS
        + "null"
        + "null"
        + POSTFIX;
    assertEquals( expected, getContent( template ) );
  }
  
  public void testVariableReplacement() {
    TemplateHolder template = new TemplateHolder( TEMPLATE  );
    String myLibraries = "myLibraries";
    template.replace( TemplateHolder.VAR_LIBRARIES, myLibraries );
    String expected
      =   PREFIX 
        + myLibraries
        + NON_VARIABLE_TOKEN
        + SOME_TEXT_WITH_VARIABLE_DELIMITERS
        + myLibraries
        + "null"
        + POSTFIX;
    assertEquals( expected, getContent( template ) );
    String myAppScript = "myAppScript";
    expected
      =   PREFIX 
        + myLibraries
        + NON_VARIABLE_TOKEN
        + SOME_TEXT_WITH_VARIABLE_DELIMITERS
        + myLibraries
        + myAppScript
        + POSTFIX;
    template.replace( TemplateHolder.VAR_APPSCRIPT, myAppScript );
    assertEquals( expected, getContent( template ) );
  }
  
  public void testVariableReset() {
    TemplateHolder template = new TemplateHolder( TEMPLATE  );
    String myLibraries = "myLibraries";
    template.replace( TemplateHolder.VAR_LIBRARIES, myLibraries );
    String myAppScript = "myAppScript";
    template.replace( TemplateHolder.VAR_APPSCRIPT, myAppScript );
    template.reset();
    
    String expected
      =   PREFIX 
        + "null"
        + NON_VARIABLE_TOKEN
        + SOME_TEXT_WITH_VARIABLE_DELIMITERS
        + "null"
        + "null"
        + POSTFIX;
  assertEquals( expected, getContent( template ) );
    
  }

  private String getContent( final TemplateHolder template ) {
    String[] tokens = template.getTokens();
    StringBuffer result = new StringBuffer();
    for( int i = 0; i < tokens.length; i++ ) {
      result.append( tokens[ i ] );
    }
    return result.toString();
  }
}
