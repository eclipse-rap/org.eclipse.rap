/*******************************************************************************
 * Copyright (c) 2008, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.util.*;

import org.eclipse.rwt.internal.util.ParamCheck;


public final class StartupPageTemplateHolder {
  
  private static final int[] EMPTY_INDICES = new int[ 0 ];
  private final static String TOKEN_BACKGROUND_IMAGE = "backgroundImage";
  private final static String TOKEN_LIBRARIES = "libraries";
  private final static String TOKEN_APPSCRIPT = "appScript";
  private final static String TOKEN_SERVLET = "servlet";
  private final static String TOKEN_ENTRY_POINT = "entrypoint";
  private final static String TOKEN_BODY = "body";
  private final static String TOKEN_TITLE = "title";
  private final static String TOKEN_HEADERS = "headers";
  private final static String TOKEN_STARTUP = "startup";
  private final static String TOKEN_EXIT_CONFIRMATION = "exitConfirmation";
  private final static String TOKEN_NO_SCRIPT_MESSAGE = "noScriptMessage";

  public final static Variable VAR_BACKGROUND_IMAGE
    = new Variable( TOKEN_BACKGROUND_IMAGE );
  public final static Variable VAR_LIBRARIES
    = new Variable( TOKEN_LIBRARIES );
  public final static Variable VAR_APPSCRIPT
    = new Variable( TOKEN_APPSCRIPT );
  public final static Variable VAR_SERVLET
    = new Variable( TOKEN_SERVLET );
  public final static Variable VAR_ENTRY_POINT
    = new Variable( TOKEN_ENTRY_POINT );
  public final static Variable VAR_BODY
    = new Variable( TOKEN_BODY );
  public final static Variable VAR_TITLE
    = new Variable( TOKEN_TITLE );
  public final static Variable VAR_HEADERS
    = new Variable( TOKEN_HEADERS );
  public final static Variable VAR_STARTUP
    = new Variable( TOKEN_STARTUP );
  public final static Variable VAR_EXIT_CONFIRMATION
    = new Variable( TOKEN_EXIT_CONFIRMATION );
  public final static Variable VAR_NO_SCRIPT_MESSAGE
    = new Variable( TOKEN_NO_SCRIPT_MESSAGE );


  public static final class Variable {
    
    private final static Map NAMES = new HashMap();
    private final String name;

    private Variable( final String varName ) {
      this.name = varName;
      NAMES.put( varName, this );
    }

    private static Variable lookup( final String name ) {
      return ( Variable )NAMES.get( name );
    }

    public String toString() {
      return "${" + name + "}";
    }
  }


  private final String[] tokens;
  private final Map replacementIndices;


  public StartupPageTemplateHolder( final String template ) {
    ParamCheck.notNull( template, "template" );
    replacementIndices = new HashMap();
    StringTokenizer tokenizer = new StringTokenizer( template, "${}", true );
    int countTokens = tokenizer.countTokens();
    tokens = new String[ countTokens ];
    boolean ignoreNextToken = false;
    for( int i = 0; i < tokens.length; i++ ) {
      String nextToken = tokenizer.nextToken();
      if( ignoreNextToken ) {
        ignoreNextToken = false;
      } else if( !isVariableToken( nextToken ) ) {
        tokens[ i ] = nextToken;
      } else {
        Variable variable = Variable.lookup( nextToken );
        addReplacementIndex( variable, i );
        tokens[ i - 1 ] = "";
        tokens[ i - 2 ] = "";
        tokens[ i + 1 ] = "";
        ignoreNextToken = true;
      }
    }
  }

  private boolean isVariableToken( final String nextToken ) {
    return    nextToken.equals( TOKEN_BACKGROUND_IMAGE.toString() )
           || nextToken.equals( TOKEN_LIBRARIES.toString() )
           || nextToken.equals( TOKEN_APPSCRIPT.toString() )
           || nextToken.equals( TOKEN_SERVLET.toString() )
           || nextToken.equals( TOKEN_ENTRY_POINT.toString() )
           || nextToken.equals( TOKEN_BODY.toString() )
           || nextToken.equals( TOKEN_TITLE.toString() )
           || nextToken.equals( TOKEN_HEADERS.toString() )
           || nextToken.equals( TOKEN_STARTUP.toString() )
           || nextToken.equals( TOKEN_EXIT_CONFIRMATION.toString() )
           || nextToken.equals( TOKEN_NO_SCRIPT_MESSAGE.toString() );
  }

  private void addReplacementIndex( final Variable variable, final int index ) {
    List indices = ( List )replacementIndices.get( variable );
    if( indices == null ) {
      indices = new ArrayList();
      replacementIndices.put( variable, indices );
    }
    indices.add( new Integer( index ) );
  }

  private int[] getReplacementIndices( final Variable variable ) {
    List indices = ( List )replacementIndices.get( variable );
    int[] result = null;
    if( indices == null ) {
      result = EMPTY_INDICES;
    } else {
      Object[] buffer = indices.toArray();
      result = new int[ buffer.length ];
      for( int i = 0; i < result.length; i++ ) {
        result[ i ] = ( ( Integer )buffer[ i ] ).intValue();
      }
    }
    return result;
  }
  public String[] getTokens() {
    // no secure copy due to performance reasons...
    return tokens;
  }

  public void replace( final Variable toReplace, final String replacement ) {
    int[] indices = getReplacementIndices( toReplace );
    for( int i = 0; i < indices.length; i++ ) {
      tokens[ indices[ i ] ] = replacement;
    }
  }

  public void reset() {
    Iterator iterator = Variable.NAMES.values().iterator();
    while( iterator.hasNext() ) {
      Variable variable = ( Variable )iterator.next();
      int[] indices = getReplacementIndices( variable );
      for( int i = 0; i < indices.length; i++ ) {
        tokens[ indices[ i ] ] = null;
      }
    }
  }
}