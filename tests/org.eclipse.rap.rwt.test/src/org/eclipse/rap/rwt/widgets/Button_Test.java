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
import org.eclipse.rap.rwt.graphics.Image_Test;

/**
 * @author georg
 */
public class Button_Test extends TestCase {

  public void testImage() {
    Display display = new Display();
    Composite shell = new Shell( display, RWT.NONE );
    Button button = new Button( shell, RWT.NONE );
    button.setImage( Image.find( Image_Test.IMAGE1 ) );
    assertSame( Image.find( Image_Test.IMAGE1 ), button.getImage() );
    assertEquals( 1, Image.size() );
    Button button2 = new Button( shell, RWT.NONE );
    button2.setImage( Image.find( Image_Test.IMAGE2 ) );
    assertSame( Image.find( Image_Test.IMAGE2 ), button2.getImage() );
    assertEquals( 2, Image.size() );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
