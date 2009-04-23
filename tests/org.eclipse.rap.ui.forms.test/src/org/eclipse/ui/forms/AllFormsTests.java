/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.forms;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.ui.forms.internal.widgets.FormsQ07TestSuite;
import org.eclipse.ui.forms.widgets.FormsHostTestSuite;

public class AllFormsTests extends TestSuite {

  /*
   * Returns the entire test suite.
   */
  public static Test suite() {
    return new AllFormsTests();
  }

  /*
   * Constructs a new test suite.
   */
  public AllFormsTests() {
    addTest( FormsHostTestSuite.suite() );
    addTest( FormsQ07TestSuite.suite() );
  }
}
