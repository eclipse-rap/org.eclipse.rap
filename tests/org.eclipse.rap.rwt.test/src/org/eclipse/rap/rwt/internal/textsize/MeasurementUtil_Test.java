/*******************************************************************************
 * Copyright (c) 2012, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.textsize;

import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.internal.textsize.MeasurementOperator.TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectImpl;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.service.UISession;
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
  public void testGetMeasurementOperator_returnsNonNullInstance() {
    assertNotNull( MeasurementUtil.getMeasurementOperator() );
  }

  @Test
  public void testGetMeasurementOperator_returnsSameInstance() {
    MeasurementOperator operator1 = MeasurementUtil.getMeasurementOperator();
    MeasurementOperator operator2 = MeasurementUtil.getMeasurementOperator();

    assertSame( operator1, operator2 );
  }

  @Test
  public void testInstallMeasurementOperator() {
    removeRemoteObject( TYPE );
    UISession uiSession = mock( UISession.class );

    MeasurementUtil.installMeasurementOperator( uiSession );

    verify( uiSession ).setAttribute( anyString(), any( MeasurementOperator.class ) );
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

  private void removeRemoteObject( String type ) {
    RemoteObjectRegistry.getInstance().remove( ( RemoteObjectImpl )getRemoteObject( type ) );
  }

}
