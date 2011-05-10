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
package org.eclipse.rap.rwt.cluster.test;

import junit.framework.Test;
import junit.framework.TestSuite;


public class ClusterTestSuite {

  public static Test suite() {
    TestSuite suite = new TestSuite( ClusterTestSuite.class.getName() );
    suite.addTestSuite( SessionFailover_Test.class );
    suite.addTestSuite( SessionSerialization_Test.class );
    suite.addTestSuite( SingleServer_Test.class );
    return suite;
  }
}
