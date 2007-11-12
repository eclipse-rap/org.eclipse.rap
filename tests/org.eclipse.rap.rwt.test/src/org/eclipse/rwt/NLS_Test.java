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

package org.eclipse.rwt;

import java.util.Locale;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture.TestRequest;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.swt.RWTFixture;


public class NLS_Test extends TestCase {
  
  private static Locale localeBuffer;
  
  final static class TestMessages {
    private static final String BUNDLE_NAME = "org.eclipse.rwt.messages";
    
    public String MyMessage;

    public static TestMessages get() {
      return ( TestMessages )RWT.NLS.getISO8859_1Encoded( BUNDLE_NAME,
                                                          TestMessages.class );
    }
    
    private TestMessages() {
    }
  }
  
  final static class TestMessagesUTF8 {
    private static final String BUNDLE_NAME = "org.eclipse.rwt.messages_utf8";
    
    public String MyMessage;
    
    public static TestMessagesUTF8 get() {
      Class clazz = TestMessagesUTF8.class;
      return ( TestMessagesUTF8 )RWT.NLS.getUTF8Encoded( BUNDLE_NAME, clazz );
    }
    
    private TestMessagesUTF8() {
    }
  }
  
  protected void setUp() throws Exception {
    localeBuffer = Locale.getDefault();
    Locale.setDefault( Locale.ENGLISH );
    RWTFixture.fakeContext();
  }
  
  protected void tearDown() throws Exception {
    ContextProvider.disposeContext();
    Locale.setDefault( localeBuffer );
  }
  
  public void testLocale() {
    Locale locale = RWT.getLocale();
    assertSame( Locale.getDefault(), locale );
    
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setLocale( Locale.ITALIAN );
    locale = RWT.getLocale();
    assertSame( Locale.ITALIAN, locale );
    
    RWT.setLocale( Locale.UK );
    locale = RWT.getLocale();
    assertSame( Locale.UK, RWT.getLocale() );
  }
  
  public void testNLS() {
    assertEquals( "My Message", TestMessages.get().MyMessage );
    
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setLocale( Locale.ITALIAN );
    assertEquals( "Il mio messaggio", TestMessages.get().MyMessage );
    
    RWT.setLocale( Locale.GERMAN );
    assertEquals( "Meine Nachricht", TestMessages.get().MyMessage );
    
    assertSame( TestMessages.get(), TestMessages.get() );
  }
  
  public void testNLS_UTF8() {
    assertEquals( "My Message", TestMessagesUTF8.get().MyMessage );
    
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setLocale( Locale.ITALIAN );
    assertEquals( "Il mio messaggio", TestMessagesUTF8.get().MyMessage );
    
    RWT.setLocale( Locale.GERMAN );
    assertEquals( "Meine Nachricht", TestMessagesUTF8.get().MyMessage );
    
    assertSame( TestMessagesUTF8.get(), TestMessagesUTF8.get() );
  }
}
