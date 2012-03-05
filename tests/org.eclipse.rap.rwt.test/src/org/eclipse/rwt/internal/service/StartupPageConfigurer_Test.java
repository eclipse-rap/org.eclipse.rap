/*******************************************************************************
 * Copyright (c) 2008, 2012 Innoopract Informationssysteme GmbH and others.
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

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestResponse;
import org.eclipse.rwt.internal.resources.ResourceRegistry;


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

  // bug 373156
  public void testRenderJsLibraries_NullSource() throws IOException {
    StartupPage page = new StartupPage( new ResourceRegistry() );

    page.send();

    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    String content = response.getContent();
    assertTrue( content.indexOf( "src=\"null\"" ) == -1 );
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
