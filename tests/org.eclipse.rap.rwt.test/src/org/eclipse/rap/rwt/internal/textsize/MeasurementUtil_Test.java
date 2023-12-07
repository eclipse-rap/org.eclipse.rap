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
import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.eclipse.rap.rwt.internal.textsize.MeasurementOperator.TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectImpl;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class MeasurementUtil_Test {

  private static final FontData FONT_DATA = new FontData( "fontName", 1, SWT.NORMAL );
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

  @Test
  public void testAppendStartupTextSizeProbe_withoutUISession() {
    ( ( UISessionImpl )ContextProvider.getUISession() ).shutdown();
    createStartupProbe();
    ProtocolMessageWriter writer = mock( ProtocolMessageWriter.class );

    MeasurementUtil.appendStartupTextSizeProbe( writer );

    verify( writer ).appendCall( eq( "rwt.client.TextSizeMeasurement" ),
                                 eq( "measureItems" ),
                                 any( JsonObject.class ) );
  }

  private void createStartupProbe() {
    getApplicationContext().getProbeStore().createProbe( FONT_DATA );
  }

  private Probe createProbe() {
    return new Probe( TEXT_TO_MEASURE, FONT_DATA );
  }

  private MeasurementItem createMeasurementItem() {
    return new MeasurementItem( TEXT_TO_MEASURE, FONT_DATA, 17, TextSizeUtil.STRING_EXTENT );
  }

  private void removeRemoteObject( String type ) {
    RemoteObjectRegistry.getInstance().remove( ( RemoteObjectImpl )getRemoteObject( type ) );
  }

}
