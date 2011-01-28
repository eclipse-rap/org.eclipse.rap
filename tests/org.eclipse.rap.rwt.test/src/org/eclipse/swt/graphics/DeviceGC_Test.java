/*******************************************************************************
 * Copyright (c) 2011 Rüdiger Herrmann and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Rüdiger Herrmann - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.graphics;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;


public class DeviceGC_Test extends TestCase {

  private Display display;
  private GC gc;

  public void testInitialValues() {
    assertEquals( display.getSystemFont(), gc.getFont() );
    Color white = display.getSystemColor( SWT.COLOR_WHITE );
    assertEquals( white, gc.getBackground() );
    Color black = display.getSystemColor( SWT.COLOR_BLACK );
    assertEquals( black, gc.getForeground() );
  }

  public void testSetAlpha() {
    gc.setAlpha( 123 );
    assertEquals( 123, gc.getAlpha() );
  }
  
  public void testSetLineWidth() {
    gc.setLineWidth( 5 );
    assertEquals( 5, gc.getLineWidth() );
  }
  
  public void testSetLineJoin() {
    gc.setLineJoin( SWT.JOIN_ROUND );
    assertEquals( SWT.JOIN_ROUND, gc.getLineJoin() );
  }
  
  public void testSetLineCap() {
    gc.setLineCap( SWT.CAP_ROUND );
    assertEquals( SWT.CAP_ROUND, gc.getLineCap() );
  }
  
  public void testSetFont() {
    Font font = createFont();
    gc.setFont( font );
    assertEquals( font, gc.getFont() );
  }

  public void testSetFontWithNullFont() {
    Font font = createFont();
    gc.setFont( font );
    gc.setFont( null );
    assertEquals( display.getSystemFont(), gc.getFont() );
  }
  
  public void testSetBackground() {
    Color color = createColor();
    gc.setBackground( color );
    assertEquals( color, gc.getBackground() );
  }

  public void testSetForeground() {
    Color color = createColor();
    gc.setForeground( color );
    assertEquals( color, gc.getForeground() );
  }

  public void testGetClipping() {
    Rectangle clipping = gc.getClipping();
    assertEquals( display.getBounds(), clipping );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    gc = new GC( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  private Font createFont() {
    return new Font( display, "font-name", 11, SWT.NORMAL );
  }

  private Color createColor() {
    return new Color( display, 1, 2, 3 );
  }
}
