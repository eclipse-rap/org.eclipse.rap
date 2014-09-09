/*******************************************************************************
 * Copyright (c) 2008, 2013 Innoopract Informationssysteme GmbH and others.
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
package org.eclipse.rap.rwt.internal.service;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rap.rwt.internal.lifecycle.TestEntryPoint;
import org.eclipse.rap.rwt.internal.theme.CssImage;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;


public class StartupPage_Test {

  private StartupPage startupPage;
  private TestResponse response;
  private ApplicationContextImpl applicationContext;

  @Before
  public void setUp() {
    Fixture.setUp();
    applicationContext = getApplicationContext();
    startupPage = spy( new StartupPage( applicationContext ) );
    response = new TestResponse();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testSetResponseHeaders() {
    startupPage.activate();

    StartupPage.setResponseHeaders( response );

    assertEquals( "text/html; charset=UTF-8", response.getHeader( "Content-Type" ) );
    assertTrue( response.getHeader( "Cache-Control" ).contains( "no-store" ) );
  }

  @Test
  public void testSend() throws IOException {
    startupPage.activate();
    registerEntryPoint( null, null );

    startupPage.send( response );

    String content = response.getContent();
    assertTrue( content.startsWith( "<!DOCTYPE HTML" ) );
    assertTrue( content.endsWith( "</html>\n" ) );
  }

  @Test
  public void testSuccessiveMarkup() throws IOException {
    startupPage.activate();
    mockTemplate( "<some html>" );
    startupPage.send( response );

    TestResponse subsequentResponse = new TestResponse();
    startupPage.send( subsequentResponse );

    assertEquals( response.getContent(), subsequentResponse.getContent() );
  }

  @Test
  public void testSendWithUnknownToken() throws IOException {
    startupPage.activate();
    mockTemplate( variableFrom( "unknown" ) );

    try {
      startupPage.send( response );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testSendReplacesTitleToken() throws IOException {
    registerEntryPoint( WebClient.PAGE_TITLE, "title" );
    startupPage.activate();
    mockTemplate( variableFrom( StartupPageTemplate.TOKEN_TITLE ) );

    startupPage.send( response );

    assertEquals( "title", response.getContent() );
  }

  @Test
  public void testSendReplacesTitleTokenWithoutTitleProperty() throws IOException {
    registerEntryPoint( WebClient.PAGE_TITLE, null );
    startupPage.activate();
    mockTemplate( variableFrom( StartupPageTemplate.TOKEN_TITLE ) );

    startupPage.send( response );

    verify( startupPage ).writeTitle( response.getWriter() );
    assertEquals( "", response.getContent() );
  }

  @Test
  public void testSendReplacesHeaderTokenWithHeadHtml() throws IOException {
    registerEntryPoint( WebClient.HEAD_HTML, "<head />" );
    startupPage.activate();
    mockTemplate( variableFrom( StartupPageTemplate.TOKEN_HEADERS ) );

    startupPage.send( response );

    assertEquals( "<head />", response.getContent() );
  }

  @Test
  public void testSendReplacesHeaderTokenWithFavIcon() throws IOException {
    String favIcon = "icon.png";
    registerEntryPoint( WebClient.FAVICON, favIcon );
    startupPage.activate();
    mockTemplate( variableFrom( StartupPageTemplate.TOKEN_HEADERS ) );

    startupPage.send( response );

    assertTrue( response.getContent().contains( favIcon ) );
  }

  @Test
  public void testSendReplacesHeaderTokenWithHeadHtmlAndFavIcon() throws IOException {
    Map<String,String> properties = new HashMap<String,String>();
    String favIcon = "icon.png";
    String head = "<head />";
    properties.put( WebClient.HEAD_HTML, head );
    properties.put( WebClient.FAVICON, favIcon );
    EntryPointManager entryPointManager = getApplicationContext().getEntryPointManager();
    entryPointManager.register( "/rap", TestEntryPoint.class, properties );
    startupPage.activate();
    mockTemplate( variableFrom( StartupPageTemplate.TOKEN_HEADERS ) );

    startupPage.send( response );

    int favIconIndex = response.getContent().indexOf( favIcon );
    int headIndex = response.getContent().indexOf( head );
    assertTrue( favIconIndex != -1 );
    assertTrue( favIconIndex < headIndex );
  }

  @Test
  public void testSendReplacesBodyToken() throws IOException {
    registerEntryPoint( WebClient.BODY_HTML, "<body />" );
    startupPage.activate();
    mockTemplate( variableFrom( StartupPageTemplate.TOKEN_BODY ) );

    startupPage.send( response );

    assertEquals( "<body />", response.getContent() );
  }

  @Test
  public void testSendReplacesBodyTokenWithoutBodyProperty() throws IOException {
    registerEntryPoint( WebClient.BODY_HTML, null );
    startupPage.activate();
    mockTemplate( variableFrom( StartupPageTemplate.TOKEN_BODY ) );

    startupPage.send( response );

    verify( startupPage ).writeBody( response.getWriter() );
    assertEquals( "", response.getContent() );
  }

  @Test
  public void testSendReplacesLibraryToken() throws IOException {
    startupPage.setClientJsLibrary( "client.js" );
    startupPage.activate();
    mockTemplate( variableFrom( StartupPageTemplate.TOKEN_LIBRARIES ) );

    startupPage.send( response );

    verify( startupPage ).writeScriptTag( response.getWriter(), "client.js" );
  }

  @Test
  public void testAppendsJsLibrariesAfterClientLibrary() throws IOException {
    startupPage.addJsLibrary( "library.js" );
    startupPage.setClientJsLibrary( "client.js" );
    startupPage.activate();
    mockTemplate( variableFrom( StartupPageTemplate.TOKEN_LIBRARIES ) );

    startupPage.send( response );

    InOrder order = inOrder( startupPage );
    order.verify( startupPage ).writeScriptTag( response.getWriter(), "client.js" );
    order.verify( startupPage ).writeScriptTag( response.getWriter(), "library.js" );
  }

  @Test
  public void testSendReplacesBackgroundImageToken() throws IOException {
    startupPage.activate();
    mockTemplate( variableFrom( StartupPageTemplate.TOKEN_BACKGROUND_IMAGE ) );

    startupPage.send( response );

    verify( startupPage ).writeBackgroundImage( response.getWriter() );
  }

  @Test
  public void testSendReplacesNoScriptMessageToken() throws IOException {
    startupPage.activate();
    mockTemplate( variableFrom( StartupPageTemplate.TOKEN_NO_SCRIPT_MESSAGE ) );

    startupPage.send( response );

    verify( startupPage ).writeNoScriptMessage( response.getWriter() );
  }

  @Test
  public void testSendReplacesAppScriptToken() throws IOException {
    startupPage.activate();
    mockTemplate( variableFrom( StartupPageTemplate.TOKEN_APP_SCRIPT ) );

    startupPage.send( response );

    verify( startupPage ).writeAppScript( response.getWriter() );
  }

  @Test
  public void testGetBackgroundImageLocationWithNoneBackgroundImage() {
    doReturn( CssImage.NONE ).when( startupPage ).getBrackgroundImage();

    String backgroundImage = startupPage.getBackgroundImageLocation();

    assertEquals( "", backgroundImage );
  }

  @Test
  public void testGetBackgroundImageLocationWithExistingBackgroundImage() {
    CssImage qxImage = mock( CssImage.class );
    doReturn( "image-location" ).when( qxImage ).getResourcePath( applicationContext );
    doReturn( qxImage ).when( startupPage ).getBrackgroundImage();

    String backgroundImage = startupPage.getBackgroundImageLocation();

    assertEquals( "rwt-resources/image-location", backgroundImage );
  }

  @Test
  public void testGetBackgroundImageLocationWithNonExistingBackgroundImage() {
    doReturn( mock( CssImage.class ) ).when( startupPage ).getBrackgroundImage();

    String backgroundImage = startupPage.getBackgroundImageLocation();

    assertEquals( "", backgroundImage );
  }

  @Test
  public void testWriteScriptTag() throws IOException {
    startupPage.writeScriptTag( response.getWriter(), "lib.js" );

    String tag = "<script type=\"text/javascript\" src=\"lib.js\" charset=\"UTF-8\"></script>";
    assertEquals( tag, response.getContent().trim() );
  }

  private void mockTemplate( String template ) {
    startupPage.startupPageTemplate = new StartupPageTemplate( template );
  }

  private static String variableFrom( String variableName ) {
    return "${" + variableName + "}";
  }

  private void registerEntryPoint( String propertyName, String propertyValue ) {
    Map<String,String> properties = new HashMap<String,String>();
    if( propertyName != null ) {
      properties.put( propertyName, propertyValue );
    }
    getApplicationContext().getEntryPointManager().register( "/rap", TestEntryPoint.class, properties );
  }

}
