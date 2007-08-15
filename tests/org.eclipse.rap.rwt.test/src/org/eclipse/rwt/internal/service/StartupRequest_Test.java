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
import java.util.*;

import javax.servlet.ServletException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.Fixture.*;
import org.eclipse.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rwt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.rwt.internal.service.LifeCycleServiceHandler.ILifeCycleServiceHandlerConfigurer;
import org.eclipse.rwt.internal.service.LifeCycleServiceHandler.LifeCycleSerivceHandlerSync;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.widgets.Display;



public class StartupRequest_Test extends TestCase {
  
  public static final class DefaultEntryPoint implements IEntryPoint {
    public Display createUI() {
      return new Display();
    }
  }

  public void testAdditionalParameters() throws Exception {
    TestRequest original = new TestRequest();
    String p0 = "p0";
    String v0 = "v0";
    original.setParameter( p0, v0 );
    String p1 = "p1";
    String v1a = "v1a";
    String v1b = "v1b";
    original.addParameter( p1, v1a );
    original.addParameter( p1, v1b );
    
    Map paramMap = new HashMap();
    String p2 = "p2";
    String v2 = "v2";
    String p3 = "p3";
    String v3a = "v3a";
    String v3b = "v3b";
    paramMap.put( p2, new String[] { v2 } );    
    paramMap.put( p3, new String[] { v3a, v3b } );    
    StartupRequest wrapper = new StartupRequest( original, paramMap );
    
    assertEquals( v0, wrapper.getParameter( p0 ) );
    assertEquals( v1a, wrapper.getParameter( p1 ) );
    assertEquals( v2, wrapper.getParameter( p2 ) );
    assertEquals( v3a, wrapper.getParameter( p3 ) );
    
    Enumeration parameterNames = wrapper.getParameterNames();
    Set names = new HashSet();
    while( parameterNames.hasMoreElements() ) {
      names.add(  parameterNames.nextElement() );
    }
    assertTrue( names.contains( p0 ) );
    assertTrue( names.contains( p1 ) );
    assertTrue( names.contains( p2 ) );
    assertTrue( names.contains( p3 ) );
    
    assertEquals( v0, wrapper.getParameterValues( p0 )[ 0 ] );
    assertEquals( v1a, wrapper.getParameterValues( p1 )[ 0 ] );
    assertEquals( v1b, wrapper.getParameterValues( p1 )[ 1 ] );
    assertEquals( v2, wrapper.getParameterValues( p2 )[ 0 ] );
    assertEquals( v3a, wrapper.getParameterValues( p3 )[ 0 ] );
    assertEquals( v3b, wrapper.getParameterValues( p3 )[ 1 ] );
    
    Map parameterMap = wrapper.getParameterMap();
    assertEquals( v0, ( ( String[] )parameterMap.get( p0 ) )[ 0 ] );
    assertEquals( v1a, ( ( String[] )parameterMap.get( p1 ) )[ 0 ] );
    assertEquals( v1b, ( ( String[] )parameterMap.get( p1 ) )[ 1 ] );
    assertEquals( v2, ( ( String[] )parameterMap.get( p2 ) )[ 0 ] );
    assertEquals( v3a, ( ( String[] )parameterMap.get( p3 ) )[ 0 ] );
    assertEquals( v3b, ( ( String[] )parameterMap.get( p3 ) )[ 1 ] );

    try {
      parameterMap.clear();
      fail();
    } catch( UnsupportedOperationException usoe ) {
    }
  }
  
  public void testStartupRequestWithParameter() throws Exception {
    System.setProperty( "lifecycle", RWTLifeCycle.class.getName() );
    LifeCycleServiceHandler.configurer
     = new ILifeCycleServiceHandlerConfigurer()
    {
      public LifeCycleSerivceHandlerSync getSynchronizationHandler() {
        return new LifeCycleSerivceHandlerSync() {
          public void service() throws ServletException, IOException {
            doService();
          }
        };
      }
      public InputStream getTemplateOfStartupPage() throws IOException {
        return new ByteArrayInputStream( "Startup Page".getBytes() );
      }
      public boolean isStartupPageModifiedSince() {
        return true;
      }
      public void registerResources() throws IOException {
      }
    };

    String p1 = "p1";
    String v1 = "v1";
    Fixture.fakeRequestParam( p1, v1 );
    ServiceManager.getHandler().service();
    String allMarkup = Fixture.getAllMarkup();
    assertTrue( allMarkup.indexOf( "Startup Page" ) != -1 );
    
    EntryPointManager.register( EntryPointManager.DEFAULT, 
                                DefaultEntryPoint.class );
    Fixture.fakeRequestParam( p1, null );
    Fixture.fakeRequestParam( "w4t_startup", EntryPointManager.DEFAULT );
    Fixture.fakeRequestParam( "w4t_scriptEnabled", "true" );
    Fixture.fakeRequestParam( "w4t_ajaxEnabled", "true" );
    Fixture.fakeUserAgent( "myAgent" );
    ServiceManager.getHandler().service();

    assertEquals( v1, ContextProvider.getRequest().getParameter( p1 ) );
    LifeCycleServiceHandler.configurer = null;
    System.getProperties().remove( "lifecycle" );
    EntryPointManager.deregister( EntryPointManager.DEFAULT );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
    Fixture.createContext( false );
    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    response.setOutputStream( new TestServletOutputStream() );
  }
  
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
    Fixture.removeContext();
  }
}
