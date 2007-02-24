/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.rwt.widgets;

import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;


public class Text_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testInitialValues() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Text text = new Text( shell, RWT.NONE );
    assertEquals( -1, text.getTextLimit() );
  }
  
  public void testTextLimit() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Text text = new Text( shell, RWT.NONE );
    text.setTextLimit( -1 );
    assertEquals( -1, text.getTextLimit() );
    text.setTextLimit( -20 );
    assertEquals( -20, text.getTextLimit() );
    text.setTextLimit( -12345 );
    assertEquals( -12345, text.getTextLimit() );
    text.setTextLimit( 20 );
    assertEquals( 20, text.getTextLimit() );
    try {
      text.setTextLimit( 0 );
      fail( "Must not allow to set textLimit to zero" );
    } catch( IllegalArgumentException e ) {
      // as expected
    }
  }
}
