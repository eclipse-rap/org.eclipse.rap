/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import junit.framework.TestCase;

import org.eclipse.swt.RWTFixture;


public class QxBorderUtil_Test extends TestCase {

  public void testGetColors() {
    Theme theme = ThemeUtil.getTheme();
    assertNull( QxBorderUtil.getQxStyle( QxBorder.NONE ) );
    // thin inset
    QxBorder thinInset = QxBorder.valueOf( "1px inset" );
    assertEquals( "solid", QxBorderUtil.getQxStyle( thinInset ) );
    assertEquals( "[ \"_Caaa6a7\", \"_Cffffff\", \"_Cffffff\", \"_Caaa6a7\" ]",
                  QxBorderUtil.getColors( thinInset, theme ).toString() );
    assertNull( QxBorderUtil.getInnerColors( thinInset, theme ) );
    // thin outset
    QxBorder thinOutset = QxBorder.valueOf( "1px outset" );
    assertEquals( "solid", QxBorderUtil.getQxStyle( thinOutset ) );
    assertEquals( "[ \"_Cffffff\", \"_Caaa6a7\", \"_Caaa6a7\", \"_Cffffff\" ]",
                  QxBorderUtil.getColors( thinOutset, theme ).toString() );
    assertNull( QxBorderUtil.getInnerColors( thinOutset, theme ) );
    // inset
    QxBorder inset = QxBorder.valueOf( "2px inset" );
    assertEquals( "solid", QxBorderUtil.getQxStyle( inset ) );
    assertEquals( "[ \"_Caaa6a7\", \"_Cffffff\", \"_Cffffff\", \"_Caaa6a7\" ]",
                  QxBorderUtil.getColors( inset, theme ).toString() );
    assertEquals( "[ \"_C8c8785\", \"_Ce4dfdc\", \"_Ce4dfdc\", \"_C8c8785\" ]",
                  QxBorderUtil.getInnerColors( inset, theme ).toString() );
    // outset
    QxBorder outset = QxBorder.valueOf( "2px outset" );
    assertEquals( "solid", QxBorderUtil.getQxStyle( outset ) );
    assertEquals( "[ \"_Ce4dfdc\", \"_C8c8785\", \"_C8c8785\", \"_Ce4dfdc\" ]",
                  QxBorderUtil.getColors( outset, theme ).toString() );
    assertEquals( "[ \"_Cffffff\", \"_Caaa6a7\", \"_Caaa6a7\", \"_Cffffff\" ]",
                  QxBorderUtil.getInnerColors( outset, theme ).toString() );
    // groove
    QxBorder groove = QxBorder.valueOf( "2px groove" );
    assertEquals( "solid", QxBorderUtil.getQxStyle( groove ) );
    assertEquals( "[ \"_Caaa6a7\", \"_Cffffff\", \"_Cffffff\", \"_Caaa6a7\" ]",
                  QxBorderUtil.getColors( groove, theme ).toString() );
    assertEquals( "[ \"_Cffffff\", \"_Caaa6a7\", \"_Caaa6a7\", \"_Cffffff\" ]",
                  QxBorderUtil.getInnerColors( groove, theme ).toString() );
    // ridge
    QxBorder ridge = QxBorder.valueOf( "2px ridge" );
    assertEquals( "solid", QxBorderUtil.getQxStyle( ridge ) );
    assertEquals( "[ \"_Cffffff\", \"_Caaa6a7\", \"_Caaa6a7\", \"_Cffffff\" ]",
                  QxBorderUtil.getColors( ridge, theme ).toString() );
    assertEquals( "[ \"_Caaa6a7\", \"_Cffffff\", \"_Cffffff\", \"_Caaa6a7\" ]",
                  QxBorderUtil.getInnerColors( ridge, theme ).toString() );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    RWTFixture.fakeNewRequest();
  }

  protected void tearDown() throws Exception {
    ThemeManager.getInstance().reset();
    RWTFixture.tearDown();
  }
}
