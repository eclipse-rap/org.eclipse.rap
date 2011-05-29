/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.tests;

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
      // Cleanup
      suite.addTestSuite( Cleanup.class );
      return suite;
    }
}
