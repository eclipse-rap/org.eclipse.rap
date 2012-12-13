/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
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
    TestSuite result = new TestSuite( ClusterTestSuite.class.getName() );
    result.addTestSuite( JettySessionFailover_Test.class );
    result.addTestSuite( JettySessionCleanup_Test.class );
    result.addTestSuite( JettyServerPush_Test.class );
    result.addTestSuite( TomcatSessionFailover_Test.class );
    result.addTestSuite( TomcatSessionCleanup_Test.class );
    result.addTestSuite( TomcatServerPush_Test.class );
    result.addTestSuite( SessionSerialization_Test.class );
    return result;
  }

}
