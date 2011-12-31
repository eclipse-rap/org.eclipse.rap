/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.util.*;

import org.eclipse.rwt.internal.util.ParamCheck;


public final class StartupPageTemplateHolder {
  private static final int[] EMPTY_INDICES = new int[ 0 ];

  private final static String TOKEN_BACKGROUND_IMAGE = "backgroundImage";
  private final static String TOKEN_LIBRARIES = "libraries";
  private final static String TOKEN_APPSCRIPT = "appScript";
  private final static String TOKEN_BODY = "body";
  private final static String TOKEN_TITLE = "title";
  private final static String TOKEN_HEADERS = "headers";
  private final static String TOKEN_NO_SCRIPT_MESSAGE = "noScriptMessage";

  public final static Variable VAR_BACKGROUND_IMAGE = new Variable( TOKEN_BACKGROUND_IMAGE );
  public final static Variable VAR_LIBRARIES = new Variable( TOKEN_LIBRARIES );
  public final static Variable VAR_APPSCRIPT = new Variable( TOKEN_APPSCRIPT );
  public final static Variable VAR_BODY = new Variable( TOKEN_BODY );
  public final static Variable VAR_TITLE = new Variable( TOKEN_TITLE );
  public final static Variable VAR_HEADERS = new Variable( TOKEN_HEADERS );
  public final static Variable VAR_NO_SCRIPT_MESSAGE = new Variable( TOKEN_NO_SCRIPT_MESSAGE );

  private final String[] tokens;
  private final Map<Variable,List<Integer>> replacementIndices;

  public static final class Variable {
    private final static Map<String,Variable> NAMES = new HashMap<String,Variable>();
    private final String name;

    private Variable( String varName ) {
      name = varName;
      NAMES.put( varName, this );
    }

    private static Variable lookup( String name ) {
      return NAMES.get( name );
    }

    public String toString() {
      return "${" + name + "}";
    }
  }

  public StartupPageTemplateHolder( String template ) {
    ParamCheck.notNull( template, "template" );
    replacementIndices = new HashMap<Variable,List<Integer>>();
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

  public String[] getTokens() {
    // no secure copy due to performance reasons...
    return tokens;
  }

  public void replace( Variable toReplace, String replacement ) {
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

  private boolean isVariableToken( String nextToken ) {
    return    nextToken.equals( TOKEN_BACKGROUND_IMAGE.toString() )
           || nextToken.equals( TOKEN_LIBRARIES.toString() )
           || nextToken.equals( TOKEN_APPSCRIPT.toString() )
           || nextToken.equals( TOKEN_BODY.toString() )
           || nextToken.equals( TOKEN_TITLE.toString() )
           || nextToken.equals( TOKEN_HEADERS.toString() )
           || nextToken.equals( TOKEN_NO_SCRIPT_MESSAGE.toString() );
  }

  private void addReplacementIndex( Variable variable, int index ) {
    List<Integer> indices = replacementIndices.get( variable );
    if( indices == null ) {
      indices = new ArrayList<Integer>();
      replacementIndices.put( variable, indices );
    }
    indices.add( new Integer( index ) );
  }

  private int[] getReplacementIndices( Variable variable ) {
    List<Integer> indices = replacementIndices.get( variable );
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
}
