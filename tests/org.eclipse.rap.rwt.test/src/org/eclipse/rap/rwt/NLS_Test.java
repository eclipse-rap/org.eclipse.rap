/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSoure - ongoing implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rap.rwt;

import java.util.Locale;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestRequest;


public class NLS_Test extends TestCase {

  private Locale localeBuffer;

  @Override
  protected void setUp() throws Exception {
    localeBuffer = Locale.getDefault();
    Locale.setDefault( Locale.ENGLISH );
    Fixture.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
    Locale.setDefault( localeBuffer );
  }

  public void testNLS() {
    assertEquals( "My Message", TestMessages.get().MyMessage );

    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setLocales( Locale.ITALIAN );
    assertEquals( "Il mio messaggio", TestMessages.get().MyMessage );

    RWT.setLocale( Locale.GERMAN );
    assertEquals( "Meine Nachricht", TestMessages.get().MyMessage );

    assertSame( TestMessages.get(), TestMessages.get() );
  }

  public void testNLS_UTF8() {
    assertEquals( "My Message", TestMessagesUTF8.get().MyMessage );

    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setLocales( Locale.ITALIAN );
    assertEquals( "Il mio messaggio", TestMessagesUTF8.get().MyMessage );

    RWT.setLocale( Locale.GERMAN );
    assertEquals( "Meine Nachricht", TestMessagesUTF8.get().MyMessage );

    assertSame( TestMessagesUTF8.get(), TestMessagesUTF8.get() );
  }

  public void testNLSWithIncompleteLocalization() {
    assertEquals( "", TestIncompleteMessages.get().NoTranslationAvailable );
  }

  final static class TestMessages {
    private static final String BUNDLE_NAME = "org.eclipse.rap.rwt.messages";

    public String MyMessage;

    public static TestMessages get() {
      return RWT.NLS.getISO8859_1Encoded( BUNDLE_NAME, TestMessages.class );
    }

    private TestMessages() {
    }
  }

  final static class TestMessagesUTF8 {
    private static final String BUNDLE_NAME = "org.eclipse.rap.rwt.messages_utf8";

    public String MyMessage;

    public static TestMessagesUTF8 get() {
      return RWT.NLS.getUTF8Encoded( BUNDLE_NAME, TestMessagesUTF8.class );
    }

    private TestMessagesUTF8() {
    }
  }

  final static class TestIncompleteMessages {
    private static final String BUNDLE_NAME = "org.eclipse.rap.rwt.incomplete_messages";

    public String NoTranslationAvailable;

    public static TestIncompleteMessages get() {
      return RWT.NLS.getISO8859_1Encoded( BUNDLE_NAME, TestIncompleteMessages.class );
    }

    private TestIncompleteMessages() {
    }
  }

}
