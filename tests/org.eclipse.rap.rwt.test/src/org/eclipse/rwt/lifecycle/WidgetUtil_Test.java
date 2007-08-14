/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.lifecycle;

import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;

public class WidgetUtil_Test extends TestCase {

  // TODO [rh] this test is currently obsolete since IWidgetAdapter is obtained
  //      in Widget#getAdapter (see comment there) and not via the 
  //      AdapterManager anymore -- testDummy is here to prevent JUnit warning
//  public void testGetAdapter() {
//    RWTFixture.deregisterAdapterFactories();
//    Display display = new Display();
//    Shell shell = new Shell( display, SWT.NONE );
//    try {
//      WidgetUtil.getAdapter( shell );
//      fail( "Must throw exception if no adapter could be found." );
//    } catch( IllegalStateException e ) {
//      // expected
//    }
//  }

  public void testDummy() {
    assertTrue( true );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
