package org.eclipse.rwt.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.RWTHostTestSuite;
import org.eclipse.RWTQ07TestSuite;

public class RWTAllTestSuite {

  public static Test suite() {
    TestSuite suite = new TestSuite( "Test for all RWT Tests" );
    // $JUnit-BEGIN$
    suite.addTest( RWTHostTestSuite.suite() );
    suite.addTest( RWTQ07TestSuite.suite() );
    // $JUnit-END$
    return suite;
  }
}
