/*******************************************************************************
* Copyright (c) 2010 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.rap.interactiondesign.tests;

import org.eclipse.rap.ui.tests.Cleanup;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
      TestSuite suite = new TestSuite( "Test for RAP IAD API" );      
      // IAD API Tests
      suite.addTestSuite( ConfigurableStackTest.class );
      suite.addTestSuite( ConfigurationActionTest.class );
      suite.addTestSuite( ElementBuilderTest.class );
      suite.addTestSuite( LayoutModelTest.class );
      suite.addTestSuite( LayoutRegistryTest.class );
      suite.addTestSuite( PresentationFactoryTest.class );      
      // Cleanup
      suite.addTestSuite( Cleanup.class );
      return suite;
    }
}
