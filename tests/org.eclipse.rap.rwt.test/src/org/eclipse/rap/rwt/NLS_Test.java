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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Locale;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class NLS_Test {

  private Locale localeBuffer;

  @Before
  public void setUp() {
    localeBuffer = Locale.getDefault();
    Locale.setDefault( Locale.ENGLISH );
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
    Locale.setDefault( localeBuffer );
  }

  @Test
  public void testNLS_default() {
    assertEquals( "My Message", TestMessages.get().MyMessage );
  }

  @Test
  public void testNLS_italian() {
    RWT.setLocale( Locale.ITALIAN );

    assertEquals( "Il mio messaggio", TestMessages.get().MyMessage );
  }

  @Test
  public void testNLS_german() {
    RWT.setLocale( Locale.GERMAN );

    assertEquals( "Meine Nachricht", TestMessages.get().MyMessage );
  }

  @Test
  public void testNLS_sameInstance() {
    assertSame( TestMessages.get(), TestMessages.get() );
  }

  @Test
  public void testNLS_UTF8_default() {
    assertEquals( "My Message", TestMessagesUTF8.get().MyMessage );
  }

  @Test
  public void testNLS_UTF8_italian() {
    RWT.setLocale( Locale.ITALIAN );

    assertEquals( "Il mio messaggio", TestMessagesUTF8.get().MyMessage );
  }

  @Test
  public void testNLS_UTF8_german() {
    RWT.setLocale( Locale.GERMAN );

    assertEquals( "Meine Nachricht", TestMessagesUTF8.get().MyMessage );
  }

  @Test
  public void testNLS_UTF8_sameInstance() {
    assertSame( TestMessagesUTF8.get(), TestMessagesUTF8.get() );
  }

  @Test
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
