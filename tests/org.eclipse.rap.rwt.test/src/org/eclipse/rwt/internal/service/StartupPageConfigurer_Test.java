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
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestResponse;
import org.eclipse.rwt.client.WebClient;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.branding.TestBranding;
import org.eclipse.rwt.internal.lifecycle.TestEntryPoint;
import org.eclipse.rwt.internal.resources.ResourceRegistry;
import org.eclipse.rwt.internal.theme.ThemeTestUtil;
import org.eclipse.rwt.internal.theme.ThemeUtil;


public class StartupPageConfigurer_Test extends TestCase {

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testReplacePlaceholder() {
    RWTFactory.getEntryPointManager().registerByPath( "/rap", TestEntryPoint.class, null );
    String templateString = StartupPageTemplateHolder.VAR_BODY.toString();
    StartupPageTemplateHolder template = new StartupPageTemplateHolder( templateString );

    StartupPageConfigurer.replacePlaceholder( template,
                                              StartupPageTemplateHolder.VAR_BODY,
                                              "replacement" );
    assertEquals( "replacement", getContent( template ) );

    template.reset();
    StartupPageConfigurer.replacePlaceholder( template,
                                              StartupPageTemplateHolder.VAR_BODY,
                                              null );
    assertEquals( "", getContent( template ) );
  }

  // bug 373156
  public void testRenderJsLibraries_NullSource() throws IOException {
    RWTFactory.getEntryPointManager().registerByPath( "/rap", TestEntryPoint.class, null );
    StartupPage page = new StartupPage( new ResourceRegistry() );

    page.send();

    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    String content = response.getContent();
    assertTrue( content.indexOf( "src=\"null\"" ) == -1 );
  }

  public void testThemeFromProperties() throws IOException {
    ThemeTestUtil.registerTheme( "foo.theme", "", null );
    Map<String, String> properties = new HashMap<String, String>();
    properties.put( WebClient.THEME_ID, "foo.theme" );
    RWTFactory.getEntryPointManager().registerByPath( "/rap", TestEntryPoint.class, properties );

    new StartupPageConfigurer( new ResourceRegistry() ).getTemplate();

    assertEquals( "foo.theme", ThemeUtil.getCurrentThemeId() );
  }

  public void testHeadHtmlFromProperties() throws IOException {
    Map<String, String> properties = new HashMap<String, String>();
    properties.put( WebClient.HEAD_HTML, "<meta test>" );
    RWTFactory.getEntryPointManager().registerByPath( "/rap", TestEntryPoint.class, properties );

    String page = getContent( new StartupPageConfigurer( new ResourceRegistry() ).getTemplate() );

    assertTrue( page.contains( "<meta test>" ) );
    assertTrue( page.indexOf( "<head" ) < page.indexOf( "<meta test>" ) );
    assertTrue( page.indexOf( "<meta test>" ) < page.indexOf( "</head>" ) );
  }

  public void testBodyHtmlFromProperties() throws IOException {
    Map<String, String> properties = new HashMap<String, String>();
    properties.put( WebClient.BODY_HTML, "<b>custom stuff</b>" );
    RWTFactory.getEntryPointManager().registerByPath( "/rap", TestEntryPoint.class, properties );

    String page = getContent( new StartupPageConfigurer( new ResourceRegistry() ).getTemplate() );

    assertTrue( page.contains( "<b>custom stuff</b>" ) );
    assertTrue( page.indexOf( "<body" ) < page.indexOf( "<b>custom stuff</b>" ) );
    assertTrue( page.indexOf( "<b>custom stuff</b>" ) < page.indexOf( "</body>" ) );
  }

  public void testPageTitleFromProperties() throws IOException {
    Map<String, String> properties = new HashMap<String, String>();
    properties.put( WebClient.PAGE_TITLE, "custom title" );
    RWTFactory.getEntryPointManager().registerByPath( "/rap", TestEntryPoint.class, properties );

    String page = getContent( new StartupPageConfigurer( new ResourceRegistry() ).getTemplate() );

    assertTrue( page.contains( "<title>custom title</title>" ) );
  }

  public void testFavIconFromProperties() throws IOException {
    Map<String, String> properties = new HashMap<String, String>();
    properties.put( WebClient.FAVICON, "images/site-icon.png" );
    RWTFactory.getEntryPointManager().registerByPath( "/rap", TestEntryPoint.class, properties );

    String page = getContent( new StartupPageConfigurer( new ResourceRegistry() ).getTemplate() );

    assertTrue( page.contains( "<link rel=\"shortcut icon\" type=\"image/x-icon\""
                               + " href=\"rwt-resources/images/site-icon.png\" />" ) );
  }

  public void testFavIconFromBranding() throws IOException {
    Map<String, String> properties = new HashMap<String, String>();
    RWTFactory.getEntryPointManager().registerByPath( "/rap", TestEntryPoint.class, properties );
    TestBranding branding = new TestBranding( "rap", null, null );
    branding.setFavIcon( "site-icon.png" );
    RWTFactory.getBrandingManager().register( branding );

    String page = getContent( new StartupPageConfigurer( new ResourceRegistry() ).getTemplate() );

    assertTrue( page.contains( "<link rel=\"shortcut icon\" type=\"image/x-icon\""
                               + " href=\"rwt-resources/site-icon.png\" />" ) );
  }

  private static String getContent( StartupPageTemplateHolder template ) {
    StringBuilder builder = new StringBuilder();
    for( String token : template.getTokens() ) {
      builder.append( token );
    }
    return builder.toString();
  }

}
