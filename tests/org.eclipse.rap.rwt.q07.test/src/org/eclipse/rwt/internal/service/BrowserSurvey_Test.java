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

import javax.servlet.ServletException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.resources.*;
import org.eclipse.rwt.internal.service.LifeCycleServiceHandler.ILifeCycleServiceHandlerConfigurer;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.swt.RWTFixture;


public class BrowserSurvey_Test extends TestCase {
  private ILifeCycleServiceHandlerConfigurer bufferedConfigurer;

  protected void setUp() throws Exception {
    RWTFixture.setUpWithoutResourceManager();
    Fixture.createContext( false );
    ResourceManager.register( new DefaultResourceManagerFactory() );
    ThemeManager.getInstance().initialize();
    bufferedConfigurer = LifeCycleServiceHandler.configurer; 
    LifeCycleServiceHandler.configurer
      = new LifeCycleServiceHandlerConfigurer();
    ResourceUtil.startJsConcatenation();
  }
  
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
    LifeCycleServiceHandler.configurer = bufferedConfigurer;
  }

  public void testSurveyGeneration() throws ServletException {
    Fixture.fakeResponseWriter();
//    long start = System.currentTimeMillis();
    BrowserSurvey.sendBrowserSurvey();
//    long end = System.currentTimeMillis();
//    long initialCreationTime = end - start;
    String initialMarkup = Fixture.getAllMarkup();

//    assertTrue( "The created index page is probably too small.",
//                initialCreationTime > 3000 );

    for( int i = 0; i < 10; i++ ) {
      Fixture.fakeResponseWriter();
//      start = System.currentTimeMillis();
      BrowserSurvey.sendBrowserSurvey();
//      end = System.currentTimeMillis();
//      long successiveCreationTime = end - start;

//      assertTrue( "There's probably a fault with the index template holder.",
//                  successiveCreationTime < 50 );
      
      String successiveMarkup = Fixture.getAllMarkup();
      assertEquals( initialMarkup, successiveMarkup );
    }
    
  }
}
