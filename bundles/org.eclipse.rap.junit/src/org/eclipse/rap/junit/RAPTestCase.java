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

package org.eclipse.rap.junit;

import junit.framework.ComparisonFailure;
import junit.framework.TestCase;

import org.eclipse.rwt.internal.lifecycle.UICallBackManager;
import org.eclipse.swt.widgets.Display;

public class RAPTestCase extends TestCase {

  public RAPTestCase() {
    super();
  }
  
  public RAPTestCase( final String name ) {
    super( name );
  }

  /**
   * Asserts that a condition is true. If it isn't it throws an
   * AssertionFailedError with the given message.
   */
  static public void assertTrue( String message, boolean condition ) {
    notifyClient();
    if( !condition )
      fail( message );
  }

  /**
   * Asserts that a condition is true. If it isn't it throws an
   * AssertionFailedError.
   */
  static public void assertTrue( boolean condition ) {
    assertTrue( null, condition );
  }

  /**
   * Asserts that a condition is false. If it isn't it throws an
   * AssertionFailedError with the given message.
   */
  static public void assertFalse( String message, boolean condition ) {
    assertTrue( message, !condition );
  }

  /**
   * Asserts that a condition is false. If it isn't it throws an
   * AssertionFailedError.
   */
  static public void assertFalse( boolean condition ) {
    assertFalse( null, condition );
  }

  /**
   * Asserts that two objects are equal. If they are not an AssertionFailedError
   * is thrown with the given message.
   */
  static public void assertEquals( String message,
                                   Object expected,
                                   Object actual )
  {
    notifyClient();
    if( expected == null && actual == null )
      return;
    if( expected != null && expected.equals( actual ) )
      return;
    failNotEquals( message, expected, actual );
  }

  /**
   * Asserts that two objects are equal. If they are not an AssertionFailedError
   * is thrown.
   */
  static public void assertEquals( Object expected, Object actual ) {
    assertEquals( null, expected, actual );
  }

  /**
   * Asserts that two Strings are equal.
   */
  static public void assertEquals( String message,
                                   String expected,
                                   String actual )
  {
    notifyClient();
    if( expected == null && actual == null )
      return;
    if( expected != null && expected.equals( actual ) )
      return;
    throw new ComparisonFailure( message, expected, actual );
  }

  /**
   * Asserts that two Strings are equal.
   */
  static public void assertEquals( String expected, String actual ) {
    assertEquals( null, expected, actual );
  }

  /**
   * Asserts that two doubles are equal concerning a delta. If they are not an
   * AssertionFailedError is thrown with the given message. If the expected
   * value is infinity then the delta value is ignored.
   */
  static public void assertEquals( String message,
                                   double expected,
                                   double actual,
                                   double delta )
  {
    notifyClient();
    if( Double.compare( expected, actual ) == 0 )
      return;
    if( !( Math.abs( expected - actual ) <= delta ) )
      failNotEquals( message, new Double( expected ), new Double( actual ) );
  }

  /**
   * Asserts that two doubles are equal concerning a delta. If the expected
   * value is infinity then the delta value is ignored.
   */
  static public void assertEquals( double expected, double actual, double delta )
  {
    assertEquals( null, expected, actual, delta );
  }

  /**
   * Asserts that two floats are equal concerning a delta. If they are not an
   * AssertionFailedError is thrown with the given message. If the expected
   * value is infinity then the delta value is ignored.
   */
  static public void assertEquals( String message,
                                   float expected,
                                   float actual,
                                   float delta )
  {
    // handle infinity specially since subtracting to infinite values gives NaN
    // and the
    // the following test fails
    notifyClient();
    if( Float.isInfinite( expected ) ) {
      if( !( expected == actual ) )
        failNotEquals( message, new Float( expected ), new Float( actual ) );
    } else if( !( Math.abs( expected - actual ) <= delta ) )
      failNotEquals( message, new Float( expected ), new Float( actual ) );
  }

  /**
   * Asserts that two floats are equal concerning a delta. If the expected value
   * is infinity then the delta value is ignored.
   */
  static public void assertEquals( float expected, float actual, float delta ) {
    assertEquals( null, expected, actual, delta );
  }

  /**
   * Asserts that two longs are equal. If they are not an AssertionFailedError
   * is thrown with the given message.
   */
  static public void assertEquals( String message, long expected, long actual )
  {
    assertEquals( message, new Long( expected ), new Long( actual ) );
  }

  /**
   * Asserts that two longs are equal.
   */
  static public void assertEquals( long expected, long actual ) {
    assertEquals( null, expected, actual );
  }

  /**
   * Asserts that two booleans are equal. If they are not an
   * AssertionFailedError is thrown with the given message.
   */
  static public void assertEquals( String message,
                                   boolean expected,
                                   boolean actual )
  {
    assertEquals( message,
                  Boolean.valueOf( expected ),
                  Boolean.valueOf( actual ) );
  }

  /**
   * Asserts that two booleans are equal.
   */
  static public void assertEquals( boolean expected, boolean actual ) {
    assertEquals( null, expected, actual );
  }

  /**
   * Asserts that two bytes are equal. If they are not an AssertionFailedError
   * is thrown with the given message.
   */
  static public void assertEquals( String message, byte expected, byte actual )
  {
    assertEquals( message, new Byte( expected ), new Byte( actual ) );
  }

  /**
   * Asserts that two bytes are equal.
   */
  static public void assertEquals( byte expected, byte actual ) {
    assertEquals( null, expected, actual );
  }

  /**
   * Asserts that two chars are equal. If they are not an AssertionFailedError
   * is thrown with the given message.
   */
  static public void assertEquals( String message, char expected, char actual )
  {
    assertEquals( message, new Character( expected ), new Character( actual ) );
  }

  /**
   * Asserts that two chars are equal.
   */
  static public void assertEquals( char expected, char actual ) {
    assertEquals( null, expected, actual );
  }

  /**
   * Asserts that two shorts are equal. If they are not an AssertionFailedError
   * is thrown with the given message.
   */
  static public void assertEquals( String message, short expected, short actual )
  {
    assertEquals( message, new Short( expected ), new Short( actual ) );
  }

  /**
   * Asserts that two shorts are equal.
   */
  static public void assertEquals( short expected, short actual ) {
    assertEquals( null, expected, actual );
  }

  /**
   * Asserts that two ints are equal. If they are not an AssertionFailedError is
   * thrown with the given message.
   */
  static public void assertEquals( String message, int expected, int actual ) {
    assertEquals( message, new Integer( expected ), new Integer( actual ) );
  }

  /**
   * Asserts that two ints are equal.
   */
  static public void assertEquals( int expected, int actual ) {
    assertEquals( null, expected, actual );
  }

  /**
   * Asserts that an object isn't null.
   */
  static public void assertNotNull( Object object ) {
    assertNotNull( null, object );
  }

  /**
   * Asserts that an object isn't null. If it is an AssertionFailedError is
   * thrown with the given message.
   */
  static public void assertNotNull( String message, Object object ) {
    assertTrue( message, object != null );
  }

  /**
   * Asserts that an object is null.
   */
  static public void assertNull( Object object ) {
    assertNull( null, object );
  }

  /**
   * Asserts that an object is null. If it is not an AssertionFailedError is
   * thrown with the given message.
   */
  static public void assertNull( String message, Object object ) {
    assertTrue( message, object == null );
  }

  /**
   * Asserts that two objects refer to the same object. If they are not an
   * AssertionFailedError is thrown with the given message.
   */
  static public void assertSame( String message, Object expected, Object actual )
  {
    notifyClient();
    if( expected == actual )
      return;
    failNotSame( message, expected, actual );
  }

  /**
   * Asserts that two objects refer to the same object. If they are not the same
   * an AssertionFailedError is thrown.
   */
  static public void assertSame( Object expected, Object actual ) {
    assertSame( null, expected, actual );
  }

  /**
   * Asserts that two objects do not refer to the same object. If they do refer
   * to the same object an AssertionFailedError is thrown with the given
   * message.
   */
  static public void assertNotSame( String message,
                                    Object expected,
                                    Object actual )
  {
    notifyClient();
    if( expected == actual )
      failSame( message );
  }

  /**
   * Asserts that two objects do not refer to the same object. If they do refer
   * to the same object an AssertionFailedError is thrown.
   */
  static public void assertNotSame( Object expected, Object actual ) {
    assertNotSame( null, expected, actual );
  }

  private static void notifyClient() {
    UICallBackManager.getInstance().sendImmediately();
    Display.getCurrent().sleep();
  }
}
