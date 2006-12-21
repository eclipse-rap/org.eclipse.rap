/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.widgets;

import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.graphics.Image;

/**
 * @author georg
 */
public class Button_Test extends TestCase {

  public void testImage() {
    Display display = new Display();
    Composite shell = new Shell( display, RWT.NONE );
    
    Button button = new Button( shell, RWT.NONE );
    button.setImage( Image.find( RWTFixture.IMAGE1 ) );
    assertSame( Image.find( RWTFixture.IMAGE1 ), button.getImage() );
    assertEquals( 1, Image.size() );
    
    Button button2 = new Button( shell, RWT.NONE );
    button2.setImage( Image.find( RWTFixture.IMAGE2 ) );
    assertSame( Image.find( RWTFixture.IMAGE2 ), button2.getImage() );
    assertEquals( 2, Image.size() );

    button2.setImage( null );
    assertEquals( null, button2.getImage() );

    Button arrowButton = new Button( shell, RWT.ARROW );
    arrowButton.setImage( Image.find( RWTFixture.IMAGE1 ) );
    assertEquals( null, arrowButton.getImage() );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
