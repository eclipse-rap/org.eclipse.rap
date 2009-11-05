/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
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
}
