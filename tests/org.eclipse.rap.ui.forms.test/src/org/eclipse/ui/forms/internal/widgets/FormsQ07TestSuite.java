/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.forms.internal.widgets;

import org.eclipse.ui.forms.internal.widgets.formtextkit.FormTextLCA_Test;
import org.eclipse.ui.forms.internal.widgets.hyperlinkkit.HyperlinkLCA_Test;
import org.eclipse.ui.forms.internal.widgets.imagehyperlinkkit.ImageHyperlinkLCA_Test;
import org.eclipse.ui.forms.internal.widgets.togglehyperlinkkit.ToggleHyperlinkLCA_Test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class FormsQ07TestSuite {

  public static Test suite() {
	TestSuite suite
	  = new TestSuite( "Tests for org.eclipse.ui.forms.internal.widgets" );
	//$JUnit-BEGIN$
	suite.addTestSuite( HyperlinkLCA_Test.class );
	suite.addTestSuite( ImageHyperlinkLCA_Test.class );
	suite.addTestSuite( ToggleHyperlinkLCA_Test.class );
	suite.addTestSuite( FormTextLCA_Test.class );
	//$JUnit-END$
	return suite;
  }

}
