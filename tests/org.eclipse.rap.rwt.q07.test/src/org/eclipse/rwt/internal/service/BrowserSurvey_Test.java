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

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;


public class BrowserSurvey_Test extends TestCase {
  private BrowserSurvey.IStartupPageConfigurer bufferedConfigurer;

  protected void setUp() throws Exception {
    Fixture.setUp();
    bufferedConfigurer = BrowserSurvey.configurer; 
    BrowserSurvey.configurer = new RWTStartupPageConfigurer();
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
    BrowserSurvey.configurer = bufferedConfigurer;
  }

  public void testSurveyGeneration() throws IOException {
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
