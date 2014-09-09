/*******************************************************************************
 * Copyright (c) 2011, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.eclipse.rap.rwt.internal.theme.ThemePropertyAdapterRegistry.getInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.theme.ThemePropertyAdapterRegistry.BoxDimensionsPropertyAdapter;
import org.eclipse.rap.rwt.internal.theme.ThemePropertyAdapterRegistry.DimensionPropertyAdapter;
import org.eclipse.rap.rwt.internal.theme.ThemePropertyAdapterRegistry.DirectPropertyAdapter;
import org.eclipse.rap.rwt.internal.theme.ThemePropertyAdapterRegistry.ImagePropertyAdapter;
import org.eclipse.rap.rwt.internal.theme.ThemePropertyAdapterRegistry.ThemePropertyAdapter;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ThemePropertyAdapterRegistry_Test {

  private ApplicationContextImpl applicationContext;
  private ThemePropertyAdapterRegistry registry;

  @Before
  public void setUp() {
    Fixture.setUp();
    applicationContext = getApplicationContext();
    registry = ThemePropertyAdapterRegistry.getInstance( applicationContext );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testGetPropertyAdapter() {
    ThemePropertyAdapter booleanAdapter = registry.getPropertyAdapter( CssBoolean.class );
    ThemePropertyAdapter dimensionAdapter = registry.getPropertyAdapter( CssDimension.class );
    ThemePropertyAdapter boxDimAdapter = registry.getPropertyAdapter( CssBoxDimensions.class );
    ThemePropertyAdapter imageAdapter = registry.getPropertyAdapter( CssImage.class );
    assertEquals( DirectPropertyAdapter.class, booleanAdapter.getClass() );
    assertEquals( DimensionPropertyAdapter.class, dimensionAdapter.getClass() );
    assertEquals( BoxDimensionsPropertyAdapter.class, boxDimAdapter.getClass() );
    assertEquals( ImagePropertyAdapter.class, imageAdapter.getClass() );
  }

  @Test
  public void testDimensionPropertyAdapter() {
    ThemePropertyAdapter adapter = new DimensionPropertyAdapter();
    assertEquals( "0", adapter.getKey( CssDimension.ZERO ) );
    assertEquals( "439", adapter.getKey( CssDimension.create( 23 ) ) );
    assertEquals( "ffffffd1", adapter.getKey( CssDimension.create( -1 ) ) );
    assertEquals( "dimensions", adapter.getSlot( CssDimension.ZERO ) );
    assertEquals( "0", adapter.getValue( CssDimension.ZERO ).toString() );
    assertEquals( "23", adapter.getValue( CssDimension.create( 23 ) ).toString() );
  }

  @Test
  public void testBoxDimensionsPropertyAdapter() {
    ThemePropertyAdapter adapter = new BoxDimensionsPropertyAdapter();
    CssBoxDimensions testBoxDimensions = CssBoxDimensions.create( 0, 1, 2, 3 );
    assertEquals( "2144df1c", adapter.getKey( CssBoxDimensions.ZERO ) );
    assertEquals( "8bb98613", adapter.getKey( testBoxDimensions ) );
    assertEquals( "boxdims", adapter.getSlot( CssBoxDimensions.ZERO ) );
    assertEquals( "[0,0,0,0]", adapter.getValue( CssBoxDimensions.ZERO ).toString() );
    assertEquals( "[0,1,2,3]", adapter.getValue( testBoxDimensions ).toString() );
  }

  @Test
  public void testDefaultPropertyAdapter() {
    ThemePropertyAdapter adapter = new DirectPropertyAdapter();
    assertEquals( "true", adapter.getKey( CssBoolean.TRUE ) );
    assertEquals( "false", adapter.getKey( CssBoolean.FALSE ) );
    assertNull( adapter.getSlot( CssBoolean.TRUE ) );
    assertNull( adapter.getValue( CssBoolean.TRUE ) );
  }

  @Test
  public void testSameInstance() {
    ThemePropertyAdapterRegistry registryB = getInstance( applicationContext );

    assertSame( registry, registryB );
  }

  @Test
  public void testInstanceHasApplicationScope() {
    Fixture.tearDown();
    Fixture.setUp();

    ThemePropertyAdapterRegistry registryB = getInstance( applicationContext );

    assertNotSame( registry, registryB );
  }

}
