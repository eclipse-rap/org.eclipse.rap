/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.forms.widgets;

import junit.framework.Test;
import junit.framework.TestSuite;

public class FormsHostTestSuite {

  public static Test suite() {
	TestSuite suite
	  = new TestSuite( "Tests for org.eclipse.ui.forms.widgets" );
	//$JUnit-BEGIN$
	suite.addTestSuite( FormText_Test.class );
	suite.addTestSuite( Hyperlink_Test.class );
	suite.addTestSuite( ImageHyperlink_Test.class );
	suite.addTestSuite( ToggleHyperlink_Test.class );
	//$JUnit-END$
	return suite;
  }

}
