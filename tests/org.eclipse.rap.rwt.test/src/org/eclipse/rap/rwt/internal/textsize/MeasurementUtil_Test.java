/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.textsize;

import static org.junit.Assert.assertEquals;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class MeasurementUtil_Test {

  private static final String TEXT_TO_MEASURE = " text \"to\" measure ";

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCreateProbeParamObject() {
    Probe probe = createProbe();

    JsonArray probeObject = MeasurementUtil.createProbeParamObject( probe );

    JsonArray expected = new JsonArray()
      .add( MeasurementUtil.getId( probe ) )
      .add( TEXT_TO_MEASURE )
      .add( new JsonArray().add( "fontName" ) )
      .add( 1 )
      .add( false )
      .add( false )
      .add( -1 )
      .add( true );
    assertEquals( expected, probeObject );
  }

  @Test
  public void testCreateItemParamObject() {
    MeasurementItem item = createMeasurementItem();

    JsonArray itemObject = MeasurementUtil.createItemParamObject( item );

    JsonArray expected = new JsonArray()
      .add( MeasurementUtil.getId( item ) )
      .add( " text \"to\" measure " )
      .add( new JsonArray().add( "fontName" ) )
      .add( 1 )
      .add( false )
      .add( false )
      .add( 17 )
      .add( false );
    assertEquals( expected, itemObject );
  }

  private Probe createProbe() {
    FontData fontData = new FontData( "fontName", 1, SWT.NORMAL );
    return new Probe( TEXT_TO_MEASURE, fontData );
  }

  private MeasurementItem createMeasurementItem() {
    FontData fontData = new FontData( "fontName", 1, SWT.NORMAL );
    return new MeasurementItem( TEXT_TO_MEASURE, fontData, 17, TextSizeUtil.STRING_EXTENT );
  }

}
