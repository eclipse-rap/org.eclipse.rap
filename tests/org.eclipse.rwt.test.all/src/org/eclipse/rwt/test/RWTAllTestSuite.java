/******************************************************************************* 
* Copyright (c) 2010, 2011 EclipseSource and others.
* All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/
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
