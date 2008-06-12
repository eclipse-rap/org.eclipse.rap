/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.browser;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.service.RequestParams;

/** 
 * <p> BrowserLoader_Test is the testcase for browser class loading for a 
 * given userAgent string. the userAgent is set as a fake in the Fixture. 
 * </p>
 */
public class BrowserLoader_Test extends TestCase {
  
  private final static String USERAGENT_STRANGE = "Mozilla/4.0 (compatible;)";
  
  private final static String USERAGENT_DEFAULT 
    = "DEFAULT/0.8.15 (SPIN i686; de)";
  private final static String USERAGENT_IE_2_0_WIN
    = "Mozilla/1.22 (compatible; MSIE 2.0; Windows 95)";
  private final static String USERAGENT_IE_5_0_WIN
    = "Mozilla/4.0 (compatible; MSIE 5.0; Windows 3.1)";
  private final static String USERAGENT_IE_5_5_WIN 
    = "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; .NET CLR 1.1.4322)";
  private final static String USERAGENT_IE_6_0_WIN 
    = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)";
  private final static String USERAGENT_IE_7_0_WIN 
    = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.0)";
  private final static String USERAGENT_NETSCAPE_6_2_WIN 
    = "Mozilla/5.0 (Windows; U; Windows NT 5.0; en-US; rv:0.9.4) " 
    + "Gecko/20011128 Netscape6/6.2.1";
  private final static String USERAGENT_NETSCAPE_7_1_WIN 
    = "Mozilla/5.0 (Windows; U; Windows NT 5.0; en-US; rv:1.4) "
    + "Gecko/20030624 Netscape/7.1 (ax)";
  private final static String USERAGENT_NETSCAPE_8_WIN 
    = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.5) " 
    + "Gecko/20050512 Netscape/8.0";
  private final static String USERAGENT_NETSCAPE_4_7_WIN 
    = "Mozilla/4.7 [de] (WinNT; U)";
  private final static String USERAGENT_OPERA_6
    = "Mozilla/4.0 (compatible; MSIE 5.0; Windows 2000) Opera 6.0 [en]";
  private final static String USERAGENT_OPERA_7_23_WIN_2K 
    = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0) Opera 7.23  [de]";
  private final static String USERAGENT_OPERA_8_WIN 
    = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; en) Opera 8.0";
  private final static String USERAGENT_OPERA_9_WIN 
    = "Opera/9.00 (Windows NT 5.1; U; en)";
  private final static String USERAGENT_KONQUEROR_3_1 
    = "Mozilla/5.0 (compatible; Konqueror/3.1; Linux)";
  private final static String USERAGENT_KONQUEROR_3_2
    = "Mozilla/5.0 (compatible; Konqueror/3.2; Linux) (KHTML, like Gecko)";
  private final static String USERAGENT_KONQUEROR_3_5
    = "Mozilla/5.0 (compatible; Konqueror/3.5; Linux) KHTML/3.5.4 (like Gecko) " 
    + "(Debian)";
  private final static String USERAGENT_MOZILLA_1_6 
    = "Mozilla/5.0 (X11; U; Linux i686; de-AT; rv:1.6) Gecko/20040119";
  private final static String USERAGENT_FIREFOX_0_8_LINUX
    = "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.6) "
    + "Gecko/20040427 Firefox/0.8";
  private final static String USERAGENT_FIREFOX_1_0_WIN
    = "Mozilla/5.0 (Windows; U; Windows NT 5.1; de-DE; rv:1.7.8) "
    + "Gecko/20050511 Firefox/1.0.4"; 
  private static final String USERAGENT_FIREFOX_1_5 
    = "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.0.4) " 
    + "Gecko/20060602 Firefox/1.5.0.4";
  private static final String USERAGENT_FIREFOX_1_6
    = "Mozilla/5.0 (Windows; U; Windows NT 5.0; en-US; rv:1.9a1) " 
    + "Gecko/20051102 Firefox/1.6a1";
  private static final String USERAGENT_FIREFOX_2_0_ALPHA 
    = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8)" 
    + "Gecko/20060319 Firefox/2.0a1"; 
  private static final String USERAGENT_SAFARI_1_3
    = "Mozilla/5.0 (Macintosh; U; PPC Mac OS X; de-de) " 
    + "AppleWebKit/312.5.1 (KHTML, like Gecko) Safari/312.3.1";
  private final static String USERAGENT_SAFARI_2_0
    = "Mozilla/5.0 (Macintosh; U; PPC Mac OS X; de-de) "
    + "AppleWebKit/417.9 (KHTML, like Gecko) Safari/417.8";
  private static final String USERAGENT_CAMINO
    = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X; en-US; rv:1.8.0.1) " 
    + "Gecko/20060118 Camino/1.0b2+";
  private static final String USERAGENT_EPIPHANY_1_8_2 
    = "Mozilla/5.0 (X11; U; Linux i686; cs-CZ; rv:1.7.13) " 
    + "Gecko/20060418 Epiphany/1.8.2 (Ubuntu) (Ubuntu package 1.0.8)";
  private static final String USERAGENT_EPIPHANY_1_8_5
    = "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.8.0.1) " 
    + "Gecko/Debian-1.8.0.1-5 Epiphany/1.8.5";

  private PrintStream bufferedSystemOut;
  private ByteArrayOutputStream capturedSystemOut;

  
  public void testLoadClassForName() {
    String userAgent = "unknown browser v0.43";
    Fixture.fakeUserAgent( userAgent );
    Fixture.fakeRequestParam( RequestParams.SCRIPT, "true" );
    Fixture.fakeRequestParam( RequestParams.AJAX_ENABLED, "true" );
    //
    Browser browser;
    browser = BrowserLoader.loadClassForName( userAgent, false, false );
    assertEquals( Default.class, browser.getClass() );
    assertEquals( false, browser.isScriptEnabled() );
    assertEquals( false, browser.isAjaxEnabled() );
    //
    Fixture.fakeUserAgent( userAgent );
    Fixture.fakeRequestParam( RequestParams.SCRIPT, "true" );
    Fixture.fakeRequestParam( RequestParams.AJAX_ENABLED, "true" );
    Mozilla1_6 fallback = new Mozilla1_6( false, false );
    browser = BrowserLoader.loadClassForName( userAgent, fallback );
    assertEquals( Default.class, browser.getClass() );
    assertEquals( false, browser.isScriptEnabled() );
    assertEquals( false, browser.isAjaxEnabled() );
  }

  public void testStrange() {
    Fixture.fakeUserAgent( USERAGENT_STRANGE );
    Fixture.fakeRequestParam( RequestParams.SCRIPT, "true" );
    Fixture.fakeRequestParam( RequestParams.AJAX_ENABLED, "true" );
    Browser browser = BrowserLoader.load();
    assertEquals( Default.class, browser.getClass() );
    assertEquals( false, browser.isScriptEnabled() );
    assertEquals( false, browser.isAjaxEnabled() );
    assertNoSystemOut();
  }
  
  public void testDefault() {
    Fixture.fakeUserAgent( USERAGENT_DEFAULT );
    Fixture.fakeRequestParam( RequestParams.SCRIPT, "true" );
    Fixture.fakeRequestParam( RequestParams.AJAX_ENABLED, "true" );
    Browser browser = BrowserLoader.load();
    assertEquals( Default.class, browser.getClass() );
    assertEquals( false, browser.isScriptEnabled() );
    assertEquals( false, browser.isAjaxEnabled() );
    assertNoSystemOut();
  }
  
  public void testSafari() {   
    Browser browser;
    Fixture.fakeUserAgent( USERAGENT_SAFARI_1_3 );
    browser = BrowserLoader.load();
    assertEquals( org.eclipse.rwt.internal.browser.Safari2.class, browser.getClass() );   
    Fixture.fakeUserAgent( USERAGENT_SAFARI_2_0 );
    browser = BrowserLoader.load();
    assertEquals( org.eclipse.rwt.internal.browser.Safari2.class, browser.getClass() );
    assertNoSystemOut();
  }
  
  public void testNetscapeNavigator() {
    Browser browser;
    Fixture.fakeUserAgent( USERAGENT_NETSCAPE_4_7_WIN );
    browser = BrowserLoader.load();  
    assertEquals( Default.class, browser.getClass() ); 
    assertNoSystemOut();
  }
  
  public void testNetscapeMozilla() {
    Browser browser;
    Fixture.fakeUserAgent( USERAGENT_NETSCAPE_6_2_WIN );
    browser = BrowserLoader.load();  
    assertEquals( org.eclipse.rwt.internal.browser.Mozilla1_6.class, browser.getClass() ); 
    Fixture.fakeUserAgent( USERAGENT_NETSCAPE_7_1_WIN );
    browser = BrowserLoader.load();
    assertEquals( org.eclipse.rwt.internal.browser.Mozilla1_6.class, browser.getClass() ); 
    Fixture.fakeUserAgent( USERAGENT_NETSCAPE_8_WIN );
    browser = BrowserLoader.load();
    assertEquals( org.eclipse.rwt.internal.browser.Mozilla1_7.class, browser.getClass() ); 
    assertNoSystemOut();
  }
  
  public void testOpera() {
    Browser browser;
    Fixture.fakeUserAgent( USERAGENT_OPERA_6 );
    browser = BrowserLoader.load();  
    assertEquals( org.eclipse.rwt.internal.browser.Default.class, browser.getClass() ); 
    Fixture.fakeUserAgent( USERAGENT_OPERA_7_23_WIN_2K );
    browser = BrowserLoader.load();  
    assertEquals( org.eclipse.rwt.internal.browser.Default.class, browser.getClass() ); 
    Fixture.fakeUserAgent( USERAGENT_OPERA_8_WIN );
    browser = BrowserLoader.load();  
    assertEquals( org.eclipse.rwt.internal.browser.Opera8.class, browser.getClass() ); 
    Fixture.fakeUserAgent( USERAGENT_OPERA_9_WIN );
    browser = BrowserLoader.load();  
    assertEquals( org.eclipse.rwt.internal.browser.Opera9.class, browser.getClass() ); 
    assertNoSystemOut();
  }
  
  public void testKonqueror() {
    Browser browser;
    Fixture.fakeUserAgent( USERAGENT_KONQUEROR_3_1 );
    browser = BrowserLoader.load();
    assertEquals( Konqueror3_1.class, browser.getClass() ); 
    Fixture.fakeUserAgent( USERAGENT_KONQUEROR_3_2 );
    browser = BrowserLoader.load();
    assertEquals( Konqueror3_2.class, browser.getClass() ); 
    Fixture.fakeUserAgent( USERAGENT_KONQUEROR_3_5 );
    browser = BrowserLoader.load();
    assertEquals( Konqueror3_4.class, browser.getClass() ); 
    assertNoSystemOut();
  }
  
  public void testFireFox() {
    Browser browser;
    Fixture.fakeUserAgent( USERAGENT_FIREFOX_1_0_WIN );
    browser = BrowserLoader.load();
    assertEquals( org.eclipse.rwt.internal.browser.Mozilla1_7.class, browser.getClass() ); 
    Fixture.fakeUserAgent( USERAGENT_FIREFOX_0_8_LINUX );
    browser = BrowserLoader.load();
    assertEquals( org.eclipse.rwt.internal.browser.Mozilla1_6.class, browser.getClass() );  
    Fixture.fakeUserAgent( USERAGENT_FIREFOX_1_5 );
    browser = BrowserLoader.load();
    assertEquals( org.eclipse.rwt.internal.browser.Mozilla1_7.class, browser.getClass() );  
    Fixture.fakeUserAgent( USERAGENT_FIREFOX_1_6 );
    browser = BrowserLoader.load();
    assertEquals( org.eclipse.rwt.internal.browser.Mozilla1_7.class, browser.getClass() );  
    Fixture.fakeUserAgent( USERAGENT_FIREFOX_2_0_ALPHA );
    browser = BrowserLoader.load();
    assertEquals( org.eclipse.rwt.internal.browser.Mozilla1_7.class, browser.getClass() );  
    assertNoSystemOut();
  }
  
  public void testIE() {
    Browser browser;
    Fixture.fakeUserAgent( USERAGENT_IE_2_0_WIN );
    browser = BrowserLoader.load();
    assertEquals( org.eclipse.rwt.internal.browser.Default.class, browser.getClass() ); 
    Fixture.fakeUserAgent( USERAGENT_IE_5_0_WIN );
    browser = BrowserLoader.load();
    assertEquals( org.eclipse.rwt.internal.browser.Ie5.class, browser.getClass() ); 
    Fixture.fakeUserAgent( USERAGENT_IE_5_5_WIN );
    browser = BrowserLoader.load();
    assertEquals( org.eclipse.rwt.internal.browser.Ie5_5.class, browser.getClass() ); 
    Fixture.fakeUserAgent( USERAGENT_IE_6_0_WIN );
    browser = BrowserLoader.load();
    assertEquals( org.eclipse.rwt.internal.browser.Ie6.class, browser.getClass() ); 
    Fixture.fakeUserAgent( USERAGENT_IE_7_0_WIN );
    browser = BrowserLoader.load();
    assertEquals( org.eclipse.rwt.internal.browser.Ie7.class, browser.getClass() );
    // A fictitious future IE 8.5 - tests DetectorID#getMajor 
    Fixture.fakeUserAgent( "Mozilla/4.0 (compatible; MSIE 8.5; Windows NT 5.0)" );
    browser = BrowserLoader.load();
    assertEquals( org.eclipse.rwt.internal.browser.Ie7.class, browser.getClass() ); 
    assertNoSystemOut();
  }
  
  public void testMozilla() {
    Fixture.fakeUserAgent( USERAGENT_MOZILLA_1_6 );
    Browser browser = BrowserLoader.load();
    assertEquals( org.eclipse.rwt.internal.browser.Mozilla1_6.class, browser.getClass() ); 
    assertNoSystemOut();
  }
  
  public void testCamino() {
    Fixture.fakeUserAgent( USERAGENT_CAMINO );
    Browser browser = BrowserLoader.load();
    assertEquals( org.eclipse.rwt.internal.browser.Mozilla1_7.class, browser.getClass() ); 
    assertNoSystemOut();
  }
  
  public void testEpiphany() {
    Browser browser;
    Fixture.fakeUserAgent( USERAGENT_EPIPHANY_1_8_2 );
    browser = BrowserLoader.load();
    assertEquals( org.eclipse.rwt.internal.browser.Mozilla1_7.class, browser.getClass() ); 
    Fixture.fakeUserAgent( USERAGENT_EPIPHANY_1_8_5 );
    browser = BrowserLoader.load();
    assertEquals( org.eclipse.rwt.internal.browser.Mozilla1_7.class, browser.getClass() );
    assertNoSystemOut();
  }
  
  public void testAjaxEnablement() {
    Browser browser;
    Fixture.fakeRequestParam( RequestParams.AJAX_ENABLED, "true" );
    // Default
    Fixture.fakeUserAgent( USERAGENT_DEFAULT );
    browser = BrowserLoader.load();
    assertEquals( false, browser.isAjaxEnabled() );
    // Internet Explorer
    Fixture.fakeUserAgent( USERAGENT_IE_2_0_WIN );
    browser = BrowserLoader.load();
    assertEquals( false, browser.isAjaxEnabled() );
    Fixture.fakeUserAgent( USERAGENT_IE_5_0_WIN );
    browser = BrowserLoader.load();
    assertEquals( false, browser.isAjaxEnabled() );
    Fixture.fakeUserAgent( USERAGENT_IE_5_5_WIN );
    browser = BrowserLoader.load();
    assertEquals( true, browser.isAjaxEnabled() );
    Fixture.fakeUserAgent( USERAGENT_IE_6_0_WIN );
    browser = BrowserLoader.load();
    assertEquals( true, browser.isAjaxEnabled() );
    Fixture.fakeUserAgent( USERAGENT_IE_7_0_WIN );
    browser = BrowserLoader.load();
    assertEquals( true, browser.isAjaxEnabled() );
    // Firefox
    Fixture.fakeUserAgent( USERAGENT_FIREFOX_0_8_LINUX );
    browser = BrowserLoader.load();
    assertEquals( true, browser.isAjaxEnabled() );
    Fixture.fakeUserAgent( USERAGENT_FIREFOX_1_0_WIN );
    browser = BrowserLoader.load();
    assertEquals( true, browser.isAjaxEnabled() );
    Fixture.fakeUserAgent( USERAGENT_FIREFOX_1_5  );
    browser = BrowserLoader.load();
    assertEquals( true, browser.isAjaxEnabled() );
    Fixture.fakeUserAgent( USERAGENT_FIREFOX_1_5 );
    browser = BrowserLoader.load();
    assertEquals( true, browser.isAjaxEnabled() );
    Fixture.fakeUserAgent( USERAGENT_FIREFOX_1_6 );
    browser = BrowserLoader.load();
    assertEquals( true, browser.isAjaxEnabled() );
    Fixture.fakeUserAgent( USERAGENT_FIREFOX_2_0_ALPHA );
    browser = BrowserLoader.load();
    assertEquals( true, browser.isAjaxEnabled() );
    // Netscape Navigator
    Fixture.fakeUserAgent( USERAGENT_NETSCAPE_4_7_WIN );
    browser = BrowserLoader.load();
    assertEquals( false, browser.isAjaxEnabled() );
    // Netscape with Gecko engine
    Fixture.fakeUserAgent( USERAGENT_NETSCAPE_7_1_WIN );
    browser = BrowserLoader.load();
    assertEquals( true, browser.isAjaxEnabled() );
    Fixture.fakeUserAgent( USERAGENT_NETSCAPE_6_2_WIN );
    browser = BrowserLoader.load();
    assertEquals( true, browser.isAjaxEnabled() );
    Fixture.fakeUserAgent( USERAGENT_NETSCAPE_8_WIN );
    browser = BrowserLoader.load();
    assertEquals( true, browser.isAjaxEnabled() );
    // Safari
    Fixture.fakeUserAgent( USERAGENT_SAFARI_1_3 );
    browser = BrowserLoader.load();
    assertEquals( false, browser.isAjaxEnabled() );
    Fixture.fakeUserAgent( USERAGENT_SAFARI_2_0 );
    browser = BrowserLoader.load();
    assertEquals( false, browser.isAjaxEnabled() );
    // Opera
    Fixture.fakeUserAgent( USERAGENT_OPERA_7_23_WIN_2K );
    browser = BrowserLoader.load();
    assertEquals( false, browser.isAjaxEnabled() );
    Fixture.fakeUserAgent( USERAGENT_OPERA_8_WIN );
    browser = BrowserLoader.load();
    assertEquals( false, browser.isAjaxEnabled() );
    Fixture.fakeUserAgent( USERAGENT_OPERA_9_WIN );
    browser = BrowserLoader.load();
    assertEquals( true, browser.isAjaxEnabled() );
    // Konqueror
    Fixture.fakeUserAgent( USERAGENT_KONQUEROR_3_1 );
    browser = BrowserLoader.load();
    assertEquals( false, browser.isAjaxEnabled() );
    Fixture.fakeUserAgent( USERAGENT_KONQUEROR_3_2 );
    browser = BrowserLoader.load();
    assertEquals( false, browser.isAjaxEnabled() );
    // Camino
    Fixture.fakeUserAgent( USERAGENT_CAMINO );
    browser = BrowserLoader.load();
    assertEquals( true, browser.isAjaxEnabled() );
    // Epiphany
    Fixture.fakeUserAgent( USERAGENT_EPIPHANY_1_8_2 );
    browser = BrowserLoader.load();
    assertEquals( true, browser.isAjaxEnabled() );
    Fixture.fakeUserAgent( USERAGENT_EPIPHANY_1_8_5 );
    browser = BrowserLoader.load();
    assertEquals( true, browser.isAjaxEnabled() );
    assertNoSystemOut();
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.createContext( false );
    bufferedSystemOut = System.out;
    capturedSystemOut = new ByteArrayOutputStream();
    System.setOut( new PrintStream( capturedSystemOut ) );
  }
  
  protected void tearDown() throws Exception {
    System.setOut( bufferedSystemOut );
    Fixture.tearDown();
    Fixture.removeContext();
  }

  private void assertNoSystemOut() {
    String msg = "No output shoud have been written, but was:\n" 
               + new String( capturedSystemOut.toByteArray() );
    assertEquals( msg, 0, capturedSystemOut.toByteArray().length );
  }
}