/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;


public class StartupPageConfigurer_Test extends TestCase {

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testReplacePlaceholder() {
    String templateString = StartupPageTemplateHolder.VAR_BODY.toString();
    StartupPageTemplateHolder template = new StartupPageTemplateHolder( templateString );

    StartupPageConfigurer.replacePlaceholder( template,
                                              StartupPageTemplateHolder.VAR_BODY,
                                              "replacement" );
    assertEquals( "replacement", getTemplateContent( template ).toString() );

    template.reset();
    StartupPageConfigurer.replacePlaceholder( template,
                                              StartupPageTemplateHolder.VAR_BODY,
                                              null );
    assertEquals( "", getTemplateContent( template ).toString() );
  }

  private StringBuilder getTemplateContent( StartupPageTemplateHolder template ) {
    String[] tokens = template.getTokens();
    StringBuilder result = new StringBuilder();
    for( int i = 0; i < tokens.length; i++ ) {
      result.append( tokens[ i ] );
    }
    return result;
  }

}
