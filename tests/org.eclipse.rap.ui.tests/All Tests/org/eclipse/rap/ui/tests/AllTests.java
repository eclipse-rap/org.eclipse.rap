/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.tests;

import org.eclipse.jface.internal.util.SerializableEventManagerTest;
import org.eclipse.jface.tests.viewers.Bug264226TableViewerTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
      TestSuite suite = new TestSuite( "Test for org.eclipse.rap.ui" );      
      // Cleanup
      suite.addTestSuite( Cleanup.class );
      // Eclipse UI Tests
      suite.addTest( new org.eclipse.ui.tests.UiTestSuite() );
      // Eclipse JFace Tests
      suite.addTest( new org.eclipse.jface.tests.AllTests() );
      // RAP UI Tests
      suite.addTestSuite( ServiceHandlerExtensionTest.class );
      suite.addTestSuite( RWTConfigurationWrapper.class );
      // RAP JFace Tests
      suite.addTestSuite( Bug264226TableViewerTest.class );
      suite.addTestSuite(SerializableEventManagerTest.class );
      // Cleanup
      suite.addTestSuite( Cleanup.class );
      return suite;
    }
}
