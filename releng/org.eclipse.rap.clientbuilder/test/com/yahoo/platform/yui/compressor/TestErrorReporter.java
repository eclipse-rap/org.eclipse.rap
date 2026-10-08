/*******************************************************************************
 * Copyright (c) 2010, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.yahoo.platform.yui.compressor;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;


public final class TestErrorReporter implements ErrorReporter {

  @Override
  public void warning( String message,
                       String sourceName,
                       int line,
                       String lineSource,
                       int lineOffset )
  {
    System.out.println( getMessage( "WARNING", message ) );
  }

  @Override
  public void error( String message,
                     String sourceName,
                     int line,
                     String lineSource,
                     int lineOffset )
  {
    System.out.println( getMessage( "ERROR", message ) );
  }

  @Override
  public EvaluatorException runtimeError( String message,
                                          String sourceName,
                                          int line,
                                          String lineSource,
                                          int lineOffset )
  {
    error( message, sourceName, line, lineSource, lineOffset );
    return new EvaluatorException( message );
  }

  private static String getMessage( String severity, String message ) {
    StringBuilder result = new StringBuilder();
    result.append( "\n[" );
    result.append( severity );
    result.append( "] " );
    result.append( message );
    return result.toString();
  }

}
