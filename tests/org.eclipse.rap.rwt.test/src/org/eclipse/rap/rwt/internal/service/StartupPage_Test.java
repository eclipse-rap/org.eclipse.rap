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
package org.eclipse.rap.rwt.internal.service;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rap.rwt.internal.application.RWTFactory;
import org.eclipse.rap.rwt.internal.lifecycle.TestEntryPoint;
import org.eclipse.rap.rwt.internal.theme.QxImage;
import org.eclipse.rap.rwt.internal.theme.ThemeTestUtil;
import org.eclipse.rap.rwt.internal.theme.ThemeUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestResponse;


public class StartupPage_Test extends TestCase {

  private static final String CUSTOM_THEME_ID = "custom-theme-id";
  
  private StartupPage startupPage;
  private TestResponse response;

  public void testSetResponseHeaders() {
    TestResponse response = new TestResponse();
  
    StartupPage.setResponseHeaders( response );
  
    assertEquals( "text/html; charset=UTF-8", response.getHeader( "Content-Type" ) );
    assertTrue( response.getHeader( "Cache-Control" ).contains( "no-store" ) );
  }

  public void testSend() throws IOException {
    registerEntryPoint( null, null );

    startupPage.send( response );

    String content = response.getContent();
    assertTrue( content.startsWith( "<!DOCTYPE HTML" ) );
    assertTrue( content.endsWith( "</html>\n" ) );
  }
  
  public void testSuccessiveMarkup() throws IOException {
    registerEntryPoint( null, null );
    mockTemplate( "<some html>" );
    startupPage.send( response );
  
    TestResponse subsequentResponse = new TestResponse();
    startupPage.send( subsequentResponse );
  
    assertEquals( response.getContent(), subsequentResponse.getContent() );
  }
  
  public void testSendWithUnknownToken() throws IOException {
    mockTemplate( variableFrom( "unknown" ) );
    
    try {
      startupPage.send( response );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testSendReplacesTitleToken() throws IOException {
    registerEntryPoint( WebClient.PAGE_TITLE, "title" );
    mockTemplate( variableFrom( StartupPageTemplate.TOKEN_TITLE ) );
    
    startupPage.send( response );
    
    assertEquals( "title", response.getContent() );
  }

  public void testSendReplacesTitleTokenWithoutTitleProperty() throws IOException {
    registerEntryPoint( WebClient.PAGE_TITLE, null );
    mockTemplate( variableFrom( StartupPageTemplate.TOKEN_TITLE ) );
    
    startupPage.send( response );
    
    verify( startupPage ).writeTitle( response.getWriter() );
    assertEquals( "", response.getContent() );
  }
  
  public void testSendReplacesHeaderTokenWithHeadHtml() throws IOException {
    registerEntryPoint( WebClient.HEAD_HTML, "<head />" );
    mockTemplate( variableFrom( StartupPageTemplate.TOKEN_HEADERS ) );
    
    startupPage.send( response );
    
    assertEquals( "<head />", response.getContent() );
  }
  
  public void testSendReplacesHeaderTokenWithFavIcon() throws IOException {
    String favIcon = "icon.png";
    registerEntryPoint( WebClient.FAVICON, favIcon );
    mockTemplate( variableFrom( StartupPageTemplate.TOKEN_HEADERS ) );
    
    startupPage.send( response );
    
    assertTrue( response.getContent().contains( favIcon ) );
  }
  
  public void testSendReplacesHeaderTokenWithHeadHtmlAndFavIcon() throws IOException {
    Map<String,String> properties = new HashMap<String,String>();
    String favIcon = "icon.png";
    String head = "<head />";
    properties.put( WebClient.HEAD_HTML, head );
    properties.put( WebClient.FAVICON, favIcon );
    RWTFactory.getEntryPointManager().registerByPath( "/rap", TestEntryPoint.class, properties );    
    mockTemplate( variableFrom( StartupPageTemplate.TOKEN_HEADERS ) );
    
    startupPage.send( response );
    
    int favIconIndex = response.getContent().indexOf( favIcon );
    int headIndex = response.getContent().indexOf( head );
    assertTrue( favIconIndex != -1 );
    assertTrue( favIconIndex < headIndex );
  }
  
  public void testSendReplacesBodyToken() throws IOException {
    registerEntryPoint( WebClient.BODY_HTML, "<body />" );
    mockTemplate( variableFrom( StartupPageTemplate.TOKEN_BODY ) );
    
    startupPage.send( response );
    
    assertEquals( "<body />", response.getContent() );
  }

  public void testSendReplacesBodyTokenWithoutBodyProperty() throws IOException {
    registerEntryPoint( WebClient.BODY_HTML, null );
    mockTemplate( variableFrom( StartupPageTemplate.TOKEN_BODY ) );
    
    startupPage.send( response );
    
    verify( startupPage ).writeBody( response.getWriter() );
    assertEquals( "", response.getContent() );
  }
  
  public void testSendReplacesLibraryToken() throws IOException {
    registerEntryPoint( null, null );
    startupPage.addJsLibrary( "library.js" );
    mockTemplate( variableFrom( StartupPageTemplate.TOKEN_LIBRARIES ) );
    
    startupPage.send( response );

    verify( startupPage ).writeLibraries( response.getWriter() );
    verify( startupPage ).writeScriptTag( response.getWriter(), "library.js" );
  }
  
  public void testSendReplacesBackgroundImageToken() throws IOException {
    registerEntryPoint( null, null );
    mockTemplate( variableFrom( StartupPageTemplate.TOKEN_BACKGROUND_IMAGE ) );
    
    startupPage.send( response );
    
    verify( startupPage ).writeBackgroundImage( response.getWriter() );
  }
  
  public void testSendReplacesNoScriptMessageToken() throws IOException {
    registerEntryPoint( null, null );
    mockTemplate( variableFrom( StartupPageTemplate.TOKEN_NO_SCRIPT_MESSAGE ) );
    
    startupPage.send( response );
    
    verify( startupPage ).writeNoScriptMessage( response.getWriter() );
  }

  public void testSendReplacesAppScriptToken() throws IOException {
    registerEntryPoint( null, null );
    mockTemplate( variableFrom( StartupPageTemplate.TOKEN_APP_SCRIPT ) );
    
    startupPage.send( response );
    
    verify( startupPage ).writeAppScript( response.getWriter() );
  }
  
  public void testSendSetsCurrentTheme() throws IOException {
    ThemeTestUtil.registerTheme( CUSTOM_THEME_ID, "", null );
    registerEntryPoint( WebClient.THEME_ID, CUSTOM_THEME_ID );
    
    startupPage.send( response );
    
    assertEquals( CUSTOM_THEME_ID, ThemeUtil.getCurrentThemeId() );
  }
  
  public void testGetBackgroundImageLocationWithNoneBackgroundImage() {
    doReturn( QxImage.NONE ).when( startupPage ).getBrackgroundImage();
    
    String backgroundImage = startupPage.getBackgroundImageLocation();
    
    assertEquals( "", backgroundImage );
  }
  
  public void testGetBackgroundImageLocationWithExistingBackgroundImage() {
    QxImage qxImage = mock( QxImage.class );
    doReturn( "image-location" ).when( qxImage ).getResourcePath();
    doReturn( qxImage ).when( startupPage ).getBrackgroundImage();
    
    String backgroundImage = startupPage.getBackgroundImageLocation();
    
    assertEquals( "rwt-resources/image-location", backgroundImage );
  }
  
  public void testGetBackgroundImageLocationWithNonExistingBackgroundImage() {
    doReturn( mock( QxImage.class ) ).when( startupPage ).getBrackgroundImage();
    
    String backgroundImage = startupPage.getBackgroundImageLocation();
    
    assertEquals( "", backgroundImage );
  }
  
  public void testWriteScriptTag() throws IOException {
    startupPage.writeScriptTag( response.getWriter(), "lib.js" );
    
    String tag = "<script type=\"text/javascript\" src=\"lib.js\" charset=\"UTF-8\"></script>";
    assertEquals( tag, response.getContent().trim() );
  }

  @Override
  protected void setUp() {
    Fixture.setUp();
    startupPage = spy( new StartupPage( ApplicationContextUtil.getInstance() ) );
    response = new TestResponse();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  private void mockTemplate( String template ) throws IOException {
    doReturn( new StartupPageTemplate( template ) ).when( startupPage ).getStartupPageTemplate();
  }

  private static String variableFrom( String variableName ) {
    return "${" + variableName + "}";
  }

  private void registerEntryPoint( String propertyName, String propertyValue ) {
    Map<String,String> properties = new HashMap<String,String>();
    if( propertyName != null ) {
      properties.put( propertyName, propertyValue );
    }
    RWTFactory.getEntryPointManager().registerByPath( "/rap", TestEntryPoint.class, properties );
  }

}
