/*******************************************************************************
* Copyright (c) 2010 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.rap.ui.tests;

import org.eclipse.jface.tests.viewers.Bug264226TableViewerTest;
import org.eclipse.rap.interactiondesign.tests.*;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
      TestSuite suite = new TestSuite( "Test for org.eclipse.rap.ui" );
      // Eclipse JFace Tests
      suite.addTest( new org.eclipse.jface.tests.AllTests() );
      // Eclipse UI Tests
// [if] Enable Eclipse UI tests when conflict with IAD tests is solved
//      suite.addTest( new org.eclipse.ui.tests.UiTestSuite() );
      // IAD API Tests
      suite.addTestSuite( ConfigurableStackTest.class );
      suite.addTestSuite( ConfigurationActionTest.class );
      suite.addTestSuite( ElementBuilderTest.class );
      suite.addTestSuite( LayoutModelTest.class );
      suite.addTestSuite( LayoutRegistryTest.class );
      suite.addTestSuite( PresentationFactoryTest.class );
      // RAP UI Tests
      suite.addTestSuite( ServiceHandlerExtensionTest.class );
      // RAP JFace Tests
      suite.addTestSuite( Bug264226TableViewerTest.class );
      return suite;
    }
}
