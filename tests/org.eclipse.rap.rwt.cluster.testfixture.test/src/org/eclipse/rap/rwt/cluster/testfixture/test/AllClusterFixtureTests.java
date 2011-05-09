/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.cluster.testfixture.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.rap.rwt.cluster.testfixture.client.*;
import org.eclipse.rap.rwt.cluster.testfixture.db.DatabaseServerTest;
import org.eclipse.rap.rwt.cluster.testfixture.server.ServletEngineTest;


public class AllClusterFixtureTests {

  public static Test suite() {
    TestSuite suite = new TestSuite( AllClusterFixtureTests.class.getName() );
    suite.addTestSuite( RWTClientTest.class );
    suite.addTestSuite( HttpUrlBuilderTest.class );
    suite.addTestSuite( ResponseTest.class );
    suite.addTestSuite( ServletEngineTest.class );
    suite.addTestSuite( DatabaseServerTest.class );
    return suite;
  }
}
