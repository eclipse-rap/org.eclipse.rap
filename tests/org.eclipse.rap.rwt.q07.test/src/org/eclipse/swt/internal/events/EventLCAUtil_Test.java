/*******************************************************************************
 * Copyright (c) 2009, 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.events;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;


public class EventLCAUtil_Test extends TestCase {

  public void testTranslateModifier() {
    int stateMask = EventLCAUtil.translateModifier( "" );
    assertEquals( 0, stateMask & SWT.MODIFIER_MASK );
    assertEquals( 0, stateMask & SWT.CTRL );
    assertEquals( 0, stateMask & SWT.SHIFT );
    assertEquals( 0, stateMask & SWT.ALT );
    // Shift
    stateMask = EventLCAUtil.translateModifier( "shift," );
    assertTrue( ( stateMask & SWT.MODIFIER_MASK ) != 0 );
    assertEquals( 0, stateMask & SWT.CTRL );
    assertTrue( ( stateMask & SWT.SHIFT ) != 0 );
    assertEquals( 0, stateMask & SWT.ALT );
    // Alt
    stateMask = EventLCAUtil.translateModifier( "alt," );
    assertTrue( ( stateMask & SWT.MODIFIER_MASK ) != 0 );
    assertEquals( 0, stateMask & SWT.CTRL );
    assertEquals( 0, stateMask & SWT.SHIFT );
    assertTrue( ( stateMask & SWT.ALT ) != 0 );
    // Shift + Ctrl + Alt
    stateMask = EventLCAUtil.translateModifier( "alt,shift,ctrl" );
    assertEquals( SWT.SHIFT | SWT.CTRL | SWT.ALT,
                  stateMask & SWT.MODIFIER_MASK );
    assertEquals( stateMask, stateMask & SWT.MODIFIER_MASK );
  }

  public void testTranslateButton() {
    int button = EventLCAUtil.translateButton( 0 );
    assertEquals( 0, button & SWT.BUTTON_MASK );
    assertEquals( 0, button & SWT.BUTTON1 );
    assertEquals( 0, button & SWT.BUTTON2 );
    assertEquals( 0, button & SWT.BUTTON3 );
    assertEquals( 0, button & SWT.BUTTON4 );
    assertEquals( 0, button & SWT.BUTTON5 );

    button = EventLCAUtil.translateButton( 1 );
    assertTrue( ( button & SWT.BUTTON_MASK ) != 0 );
    assertTrue( ( button & SWT.BUTTON1 ) != 0 );
    assertEquals( 0, button & SWT.BUTTON2 );
    assertEquals( 0, button & SWT.BUTTON3 );
    assertEquals( 0, button & SWT.BUTTON4 );
    assertEquals( 0, button & SWT.BUTTON5 );

    button = EventLCAUtil.translateButton( 2 );
    assertTrue( ( button & SWT.BUTTON_MASK ) != 0 );
    assertEquals( 0, button & SWT.BUTTON1 );
    assertTrue( ( button & SWT.BUTTON2 ) != 0 );
    assertEquals( 0, button & SWT.BUTTON3 );
    assertEquals( 0, button & SWT.BUTTON4 );
    assertEquals( 0, button & SWT.BUTTON5 );

    button = EventLCAUtil.translateButton( 3 );
    assertTrue( ( button & SWT.BUTTON_MASK ) != 0 );
    assertEquals( 0, button & SWT.BUTTON1 );
    assertEquals( 0, button & SWT.BUTTON2 );
    assertTrue( ( button & SWT.BUTTON3 ) != 0 );
    assertEquals( 0, button & SWT.BUTTON4 );
    assertEquals( 0, button & SWT.BUTTON5 );

    button = EventLCAUtil.translateButton( 4 );
    assertTrue( ( button & SWT.BUTTON_MASK ) != 0 );
    assertEquals( 0, button & SWT.BUTTON1 );
    assertEquals( 0, button & SWT.BUTTON2 );
    assertEquals( 0, button & SWT.BUTTON3 );
    assertTrue( ( button & SWT.BUTTON4 ) != 0 );
    assertEquals( 0, button & SWT.BUTTON5 );

    button = EventLCAUtil.translateButton( 5 );
    assertTrue( ( button & SWT.BUTTON_MASK ) != 0 );
    assertEquals( 0, button & SWT.BUTTON1 );
    assertEquals( 0, button & SWT.BUTTON2 );
    assertEquals( 0, button & SWT.BUTTON3 );
    assertEquals( 0, button & SWT.BUTTON4 );
    assertTrue( ( button & SWT.BUTTON5 ) != 0 );
  }
}
