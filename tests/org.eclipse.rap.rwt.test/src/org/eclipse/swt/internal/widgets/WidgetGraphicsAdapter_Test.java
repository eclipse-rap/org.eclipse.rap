/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;


public class WidgetGraphicsAdapter_Test extends TestCase {

  private Display display;
  private Shell shell;

  public void testRoundedBorderInitialValues() {
    final Control control = new Composite( shell, SWT.NONE );
    Object adapter = control.getAdapter( IWidgetGraphicsAdapter.class );
    IWidgetGraphicsAdapter gfxAdapter = ( IWidgetGraphicsAdapter )adapter;
    assertEquals( 0, gfxAdapter.getRoundedBorderWidth() );
    assertNull( gfxAdapter.getRoundedBorderColor() );
    assertNotNull( gfxAdapter.getRoundedBorderRadius() );
  }

  public void testRoundedBorderRadii() {
    WidgetGraphicsAdapter graphicsAdapter = new WidgetGraphicsAdapter();
    Rectangle radius = graphicsAdapter.getRoundedBorderRadius();
    assertNotNull( radius );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), radius );

    graphicsAdapter.setRoundedBorder( 0, null, 1, 2, 3, 4 );
    radius = graphicsAdapter.getRoundedBorderRadius();
    assertEquals( new Rectangle( 1, 2, 3, 4 ), radius );

    graphicsAdapter.setRoundedBorder( 0, null, 1, 2, 3, 4 );
    radius = graphicsAdapter.getRoundedBorderRadius();
    radius.x = 99;
    assertEquals( new Rectangle( 1, 2, 3, 4 ), graphicsAdapter.getRoundedBorderRadius() );
  }

  public void testRoundedBorderColor() {
    WidgetGraphicsAdapter graphicsAdapter = new WidgetGraphicsAdapter();
    Color blue = Graphics.getColor( 0, 0, 255 );
    graphicsAdapter.setRoundedBorder( 2, blue, 1, 2, 3, 4 );
    assertEquals( 2, graphicsAdapter.getRoundedBorderWidth() );
    assertEquals( blue, graphicsAdapter.getRoundedBorderColor() );
    assertEquals( new Rectangle( 1, 2, 3, 4 ), graphicsAdapter.getRoundedBorderRadius() );

  }

  public void testBackgroundGradient() {
    Control control = new Composite( shell, SWT.NONE );
    Object adapter = control.getAdapter( IWidgetGraphicsAdapter.class );
    IWidgetGraphicsAdapter graphicsAdapter = ( IWidgetGraphicsAdapter )adapter;
    assertNull( graphicsAdapter.getBackgroundGradientColors() );
    assertNull( graphicsAdapter.getBackgroundGradientPercents() );
    Color blue = Graphics.getColor( 0, 0, 255 );
    Color green = Graphics.getColor( 0, 255, 0 );
    Color[] gradientColors = new Color[] { blue, green, blue };
    int[] percents = new int[] { 0, 50, 100 };
    graphicsAdapter.setBackgroundGradient( gradientColors, percents, false );
    assertEquals( blue, graphicsAdapter.getBackgroundGradientColors()[ 0 ] );
    assertEquals( green, graphicsAdapter.getBackgroundGradientColors()[ 1 ] );
    assertEquals( blue, graphicsAdapter.getBackgroundGradientColors()[ 2 ] );
    assertEquals( 0, graphicsAdapter.getBackgroundGradientPercents()[ 0 ] );
    assertEquals( 50, graphicsAdapter.getBackgroundGradientPercents()[ 1 ] );
    assertEquals( 100, graphicsAdapter.getBackgroundGradientPercents()[ 2 ] );
    assertFalse( graphicsAdapter.isBackgroundGradientVertical() );

    graphicsAdapter.setBackgroundGradient( null, null, true );
    assertNull( graphicsAdapter.getBackgroundGradientColors() );
    assertNull( graphicsAdapter.getBackgroundGradientPercents() );
    assertTrue( graphicsAdapter.isBackgroundGradientVertical() );

    percents = new int[] { 0, 100 };
    try {
      graphicsAdapter.setBackgroundGradient( gradientColors, percents, true );
      fail( "Must throw exception for invalid arguments" );
    } catch( IllegalArgumentException expected ) {
    }

    gradientColors = new Color[] { blue, null, blue };
    percents = new int[] { 0, 50, 100 };
    try {
      graphicsAdapter.setBackgroundGradient( gradientColors, percents, true );
      fail( "Must throw exception for invalid arguments" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testBackgroundGradientSafeCopy() {
    Object adapter = shell.getAdapter( IWidgetGraphicsAdapter.class );
    IWidgetGraphicsAdapter graphicsAdapter = ( IWidgetGraphicsAdapter )adapter;
    Color[] colors = { Graphics.getColor( new RGB( 1, 2, 3 ) ) };
    int[] percentages = { 1 };
    graphicsAdapter.setBackgroundGradient( colors, percentages, true );
    percentages[ 0 ] = 2;
    assertEquals( 1, graphicsAdapter.getBackgroundGradientPercents()[ 0 ] );
  }
  
  public void testIsSerializable() throws Exception {
    WidgetGraphicsAdapter graphicsAdapter = new WidgetGraphicsAdapter();
    graphicsAdapter.setRoundedBorder( 1, createColor(), 3, 4, 5, 6 );
    Color[] colors = new Color[] { createColor() };
    graphicsAdapter.setBackgroundGradient( colors, new int[] { 100 }, false );

    WidgetGraphicsAdapter deserialized = Fixture.serializeAndDeserialize( graphicsAdapter );
    
    assertEquals( graphicsAdapter.getRoundedBorderWidth(), deserialized.getRoundedBorderWidth() );
    assertEquals( graphicsAdapter.getRoundedBorderColor(), deserialized.getRoundedBorderColor() );
    assertEquals( graphicsAdapter.getRoundedBorderRadius(), deserialized.getRoundedBorderRadius() );
    assertTrue( Arrays.equals( graphicsAdapter.getBackgroundGradientColors(), 
                               deserialized.getBackgroundGradientColors() ) );
    assertTrue( Arrays.equals( graphicsAdapter.getBackgroundGradientPercents(), 
                               deserialized.getBackgroundGradientPercents() ) );
  }

  private Color createColor() {
    return new Color( display, 1, 2, 3 );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
