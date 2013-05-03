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

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

public final class TestErrorReporter implements ErrorReporter {

  public void warning( String message,
                       String sourceName,
                       int line,
                       String lineSource,
                       int lineOffset )
  {
    System.out.println( getMessage( "WARNING", message ) );
  }

  public void error( String message,
                     String sourceName,
                     int line,
                     String lineSource,
                     int lineOffset )
  {
    System.out.println( getMessage( "ERROR", message ) );
  }

  public EvaluatorException runtimeError( String message,
                                          String sourceName,
                                          int line,
                                          String lineSource,
                                          int lineOffset )
  {
    error( message, sourceName, line, lineSource, lineOffset );
    return new EvaluatorException( message );
  }

  private String getMessage( String severity, String message ) {
    StringBuffer result = new StringBuffer();
    result.append( "\n[" );
    result.append( severity );
    result.append( "] " );
    result.append( message );
    return result.toString();
  }
}
