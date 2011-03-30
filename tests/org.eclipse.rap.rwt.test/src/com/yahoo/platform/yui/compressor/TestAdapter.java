/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package com.yahoo.platform.yui.compressor;

import java.io.*;
import java.util.ArrayList;

import org.mozilla.javascript.EvaluatorException;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 * Adapter to access package private fields and methods of the YUI Compressor.
 */
public final class TestAdapter {

  public static ArrayList parseString( String input )
    throws EvaluatorException, IOException
  {
    Reader inputReader = new StringReader( input );
    TestErrorReporter reporter = new TestErrorReporter();
    ArrayList tokens = JavaScriptCompressor.parse( inputReader, reporter );
    return tokens;
  }

  public static String getLiteralString( int type ) {
    return ( String )JavaScriptCompressor.literals.get( new Integer( type ) );
  }
}
