/*******************************************************************************
* Copyright (c) 2010 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License 2.0 which is
* available at https://www.eclipse.org/legal/epl-2.0
*
* SPDX-License-Identifier: EPL-2.0
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
