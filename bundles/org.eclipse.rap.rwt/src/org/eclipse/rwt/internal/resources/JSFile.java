/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.resources;

import java.io.*;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;


public class JSFile {

  private static final boolean PRESERVE_ALL_SEMICOLONS = false;
  private static final boolean DISABLE_OPTIMIZATIONS = false;
  private static final boolean VERBOSE = false;
  private static final ErrorReporter REPORTER = new SystemErrorReporter();
  
  private final JavaScriptCompressor compressor;
  private final TokenList tokens;

  public JSFile( String javaScript ) throws IOException {
    Reader inputReader = new StringReader( javaScript );
    try {
      compressor = new JavaScriptCompressor( inputReader, REPORTER );
    } finally {
      inputReader.close();
    }
    tokens = new TokenList( compressor.getTokens() );
  }

  public TokenList getTokens() {
    return tokens;
  }

  public String compress() throws IOException {
    cleanupCode( tokens );
    StringWriter stringWriter = new StringWriter();
    compressor.compress( stringWriter,
                         -1,
                         true,
                         VERBOSE,
                         PRESERVE_ALL_SEMICOLONS,
                         DISABLE_OPTIMIZATIONS );
    stringWriter.flush();
    String result = stringWriter.getBuffer().toString();
    stringWriter.close();
    return result;
  }

  private static void cleanupCode( TokenList tokens ) {
    QxCodeCleaner codeCleaner = new QxCodeCleaner( tokens );
    codeCleaner.cleanupQxCode();
  }

  private static final class SystemErrorReporter implements ErrorReporter {
  
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
}
