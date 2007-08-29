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
package org.eclipse.rwt.internal.service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

import javax.servlet.ServletException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.Fixture.*;
import org.eclipse.rwt.internal.browser.Ie;
import org.eclipse.rwt.internal.service.LifeCycleServiceHandler.ILifeCycleServiceHandlerConfigurer;
import org.eclipse.rwt.internal.service.LifeCycleServiceHandler.LifeCycleServiceHandlerSync;
import org.eclipse.swt.RWTFixture;



public class Logger_Test extends TestCase {

  private static final String RESPONSE = "HELLO LOGGER";

  private static final class TestHandler extends Handler {
    
    private final List records = new ArrayList();

    public void close() throws SecurityException {
      // do nothing
    }
    
    public void flush() {
      // do nothing
    }
    
    public void reset() {
      records.clear();
    }

    public void publish( final LogRecord record ) {
      records.add( record );
    }
    
    public LogRecord[] getRecords() {
      return ( LogRecord[] )records.toArray( new LogRecord[ records.size() ] );
    }
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
    Fixture.createContext( false );
    LifeCycleServiceHandler.configurer 
      = new ILifeCycleServiceHandlerConfigurer()
    {
      public LifeCycleServiceHandlerSync getSynchronizationHandler() {
        return new LifeCycleServiceHandlerSync() {
          public void service() throws ServletException, IOException {
            doService();
          }
        };
      }
      public InputStream getTemplateOfStartupPage() throws IOException {
        return new ByteArrayInputStream( RESPONSE.getBytes() );
      }
      public boolean isStartupPageModifiedSince() {
        return true;
      }
      public void registerResources() throws IOException {
      }
    };
  }
  
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
    Fixture.removeContext();
    removeAllTestHandlers();
    LifeCycleServiceHandler.configurer = null; 
  }
  
  public void testRequestHeaderLogging() throws Exception {
    configureLogManager( "log-headers.properties" );
    TestHandler handler = installTestHandler();
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setHeader( "test-header", "test-value" );
    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    response.setOutputStream( new TestServletOutputStream() );
    ServiceManager.getHandler().service();
    LogRecord[] records = handler.getRecords();
    assertEquals( 1, records.length );
    assertTrue( records[ 0 ].getMessage().indexOf( "test-header" ) > -1 );
  }
  
  public void testRequestParameterLogging() throws Exception {
    configureLogManager( "log-params.properties" );
    TestHandler handler = installTestHandler();
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setParameter( "test-parameter", "test-value" );
    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    response.setOutputStream( new TestServletOutputStream() );
    ServiceManager.getHandler().service();
    LogRecord[] records = handler.getRecords();
    assertEquals( 1, records.length );
    assertTrue( records[ 0 ].getMessage().indexOf( "test-parameter" ) > -1 );
  }
  
  public void testResponseContentLogging() throws Exception {
    configureLogManager( "log-content.properties" );
    TestHandler handler = installTestHandler();
    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    response.setOutputStream( new TestServletOutputStream() );
    
    Fixture.fakeBrowser( new Ie( true, true ) );
    Fixture.fakeResponseWriter();
    ServiceManager.getHandler().service();
    LogRecord[] records = handler.getRecords();
    assertEquals( 1, records.length );
    assertEquals( LifeCycleServiceHandler.LOG_RESPONSE_CONTENT, 
                  records[ 0 ].getLoggerName() );
    assertEquals( RESPONSE, records[ 0 ].getMessage() );
  }
  
  private static TestHandler installTestHandler() {
    TestHandler result = new TestHandler();
    result.reset();
    result.setLevel( Level.ALL );
    Logger.getLogger( "" ).addHandler( result );
    return result;
  }

  private static void removeAllTestHandlers() {
    Logger logger = Logger.getLogger( "" );
    Handler[] handlers = logger.getHandlers();
    for( int i = 0; i < handlers.length; i++ ) {
      if( handlers[ i ] instanceof TestHandler ) {
        logger.removeHandler( handlers[ i ] );
      }
    }
  }

  private static void configureLogManager( final String resource ) 
    throws IOException 
  {
    InputStream stream = Logger_Test.class.getResourceAsStream( resource );
    try {
      LogManager.getLogManager().readConfiguration( stream );
    } finally {
      stream.close();
    }
  }
}
