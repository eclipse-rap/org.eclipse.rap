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
import org.eclipse.rap.rwt.cluster.testfixture.db.DatabaseServer_Test;
import org.eclipse.rap.rwt.cluster.testfixture.server.ServletEngine_Test;


public class ClusterFixtureTestSuite {

  public static Test suite() {
    TestSuite suite = new TestSuite( ClusterFixtureTestSuite.class.getName() );
    suite.addTestSuite( RWTClient_Test.class );
    suite.addTestSuite( HttpUrlBuilder_Test.class );
    suite.addTestSuite( Response_Test.class );
    suite.addTestSuite( ServletEngine_Test.class );
    suite.addTestSuite( DatabaseServer_Test.class );
    return suite;
  }
}
