/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.theme.ThemePropertyAdapterRegistry.BoxDimensionsPropertyAdapter;
import org.eclipse.rap.rwt.internal.theme.ThemePropertyAdapterRegistry.DimensionPropertyAdapter;
import org.eclipse.rap.rwt.internal.theme.ThemePropertyAdapterRegistry.DirectPropertyAdapter;
import org.eclipse.rap.rwt.internal.theme.ThemePropertyAdapterRegistry.ImagePropertyAdapter;
import org.eclipse.rap.rwt.internal.theme.ThemePropertyAdapterRegistry.ThemePropertyAdapter;
import org.eclipse.rap.rwt.testfixture.Fixture;


public class ThemePropertyAdapterRegistry_Test extends TestCase {

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testGetPropertyAdapter() {
    ThemePropertyAdapterRegistry registry = ThemePropertyAdapterRegistry.getInstance();
    ThemePropertyAdapter booleanAdapter = registry.getPropertyAdapter( QxBoolean.class );
    ThemePropertyAdapter dimensionAdapter = registry.getPropertyAdapter( QxDimension.class );
    ThemePropertyAdapter boxDimAdapter = registry.getPropertyAdapter( QxBoxDimensions.class );
    ThemePropertyAdapter imageAdapter = registry.getPropertyAdapter( QxImage.class );
    assertEquals( DirectPropertyAdapter.class, booleanAdapter.getClass() );
    assertEquals( DimensionPropertyAdapter.class, dimensionAdapter.getClass() );
    assertEquals( BoxDimensionsPropertyAdapter.class, boxDimAdapter.getClass() );
    assertEquals( ImagePropertyAdapter.class, imageAdapter.getClass() );
  }

  public void testDimensionPropertyAdapter() {
    ThemePropertyAdapter adapter = new DimensionPropertyAdapter();
    assertEquals( "0", adapter.getKey( QxDimension.ZERO ) );
    assertEquals( "439", adapter.getKey( QxDimension.create( 23 ) ) );
    assertEquals( "ffffffd1", adapter.getKey( QxDimension.create( -1 ) ) );
    assertEquals( "dimensions", adapter.getSlot( QxDimension.ZERO ) );
    assertEquals( "0", adapter.getValue( QxDimension.ZERO ).toString() );
    assertEquals( "23", adapter.getValue( QxDimension.create( 23 ) ).toString() );
  }

  public void testBoxDimensionsPropertyAdapter() {
    ThemePropertyAdapter adapter = new BoxDimensionsPropertyAdapter();
    QxBoxDimensions testBoxDimensions = QxBoxDimensions.create( 0, 1, 2, 3 );
    assertEquals( "2144df1c", adapter.getKey( QxBoxDimensions.ZERO ) );
    assertEquals( "8bb98613", adapter.getKey( testBoxDimensions ) );
    assertEquals( "boxdims", adapter.getSlot( QxBoxDimensions.ZERO ) );
    assertEquals( "[ 0, 0, 0, 0 ]", adapter.getValue( QxBoxDimensions.ZERO ).toString() );
    assertEquals( "[ 0, 1, 2, 3 ]", adapter.getValue( testBoxDimensions ).toString() );
  }

  public void testDefaultPropertyAdapter() {
    ThemePropertyAdapter adapter = new DirectPropertyAdapter();
    assertEquals( "true", adapter.getKey( QxBoolean.TRUE ) );
    assertEquals( "false", adapter.getKey( QxBoolean.FALSE ) );
    assertNull( adapter.getSlot( QxBoolean.TRUE ) );
    assertNull( adapter.getValue( QxBoolean.TRUE ) );
  }

  public void testSameInstance() {
    ThemePropertyAdapterRegistry registryA = ThemePropertyAdapterRegistry.getInstance();
    ThemePropertyAdapterRegistry registryB = ThemePropertyAdapterRegistry.getInstance();

    assertSame( registryA, registryB );
  }

  public void testInstanceHasApplicationScope() {
    ThemePropertyAdapterRegistry registryA = ThemePropertyAdapterRegistry.getInstance();
    Fixture.tearDown();
    Fixture.setUp();

    ThemePropertyAdapterRegistry registryB = ThemePropertyAdapterRegistry.getInstance();

    assertNotSame( registryA, registryB );
  }

}
