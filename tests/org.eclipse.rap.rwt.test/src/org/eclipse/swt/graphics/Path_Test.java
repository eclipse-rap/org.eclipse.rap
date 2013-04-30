/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.graphics;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.junit.Before;
import org.junit.Test;


public class Path_Test {

  private Device device;
  private Path path;

  @Before
  public void setUp() {
    device = mock( Device.class );
    path = new Path( device );
  }

  @Test
  public void testCreate() {
    PathData pathData = path.getPathData();

    assertSame( device, path.getDevice() );
    assertEquals( 0, pathData.types.length );
    assertEquals( 0, pathData.points.length );
  }

  @Test
  public void testCreate_withInitialPathData() {
    PathData initialPathData = new PathData();
    initialPathData.types = new byte[]{ SWT.PATH_MOVE_TO, SWT.PATH_LINE_TO };
    initialPathData.points = new float[]{ 10, 20, 10, 10 };

    path = new Path( device, initialPathData );

    PathData pathData = path.getPathData();
    assertArrayEquals( new byte[] { SWT.PATH_MOVE_TO, SWT.PATH_LINE_TO }, pathData.types );
    assertArrayEquals( new float[] { 10, 20, 10, 10 }, pathData.points, 0 );
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreate_withInitialPathDataAndInvalidTypes() {
    PathData initialPathData = new PathData();
    initialPathData.types = new byte[]{ SWT.PATH_MOVE_TO, 67 };
    initialPathData.points = new float[]{ 10, 20, 10, 10 };

    path = new Path( device, initialPathData );
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void testCreate_withInitialPathDataAndInvalidPoints() {
    PathData initialPathData = new PathData();
    initialPathData.types = new byte[]{ SWT.PATH_MOVE_TO, SWT.PATH_LINE_TO };
    initialPathData.points = new float[]{ 10, 20, 10 };

    path = new Path( device, initialPathData );
  }

  @Test
  public void testGetCurrentPoint_initial() {
    float[] currentPoint = new float[ 2 ];
    path.getCurrentPoint( currentPoint );

    assertEquals( 0, currentPoint[ 0 ], 0 );
    assertEquals( 0, currentPoint[ 1 ], 0 );
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetCurrentPoint_withNullArgument() {
    path.getCurrentPoint( null );
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetCurrentPoint_withArgumentWithSmallerSize() {
    path.getCurrentPoint( new float[ 1 ] );
  }

  @Test(expected = SWTException.class)
  public void testGetCurrentPoint_withDisposedPath() {
    path.dispose();

    path.getCurrentPoint( new float[ 2 ] );
  }

  @Test
  public void testMoveTo() {
    path.moveTo( 10, 20 );

    float[] currentPoint = new float[ 2 ];
    path.getCurrentPoint( currentPoint );

    assertEquals( 10, currentPoint[ 0 ], 0 );
    assertEquals( 20, currentPoint[ 1 ], 0 );
  }

  @Test(expected = SWTException.class)
  public void testMoveTo_withDisposedPath() {
    path.dispose();

    path.moveTo( 10, 20 );
  }

  @Test
  public void testMoveTo_notLastOperation() {
    path.lineTo( 10, 10 );

    PathData pathData = path.getPathData();
    assertArrayEquals( new byte[] { SWT.PATH_MOVE_TO, SWT.PATH_LINE_TO }, pathData.types );
    assertArrayEquals( new float[] { 0, 0, 10, 10 }, pathData.points, 0 );
  }

  @Test
  public void testMoveTo_lastOperation() {
    path.lineTo( 10, 10 );
    path.moveTo( 10, 20 );

    PathData pathData = path.getPathData();
    assertArrayEquals( new byte[] { SWT.PATH_MOVE_TO, SWT.PATH_LINE_TO }, pathData.types );
    assertArrayEquals( new float[] { 0, 0, 10, 10 }, pathData.points, 0 );
  }

  @Test
  public void testMoveTo_noMultipleSequentialOperations() {
    path.moveTo( 1, 2 );
    path.moveTo( 3, 4 );
    path.lineTo( 10, 10 );

    PathData pathData = path.getPathData();
    assertArrayEquals( new byte[] { SWT.PATH_MOVE_TO, SWT.PATH_LINE_TO }, pathData.types );
    assertArrayEquals( new float[] { 3, 4, 10, 10 }, pathData.points, 0 );
  }

  @Test
  public void testLineTo() {
    path.lineTo( 10, 10 );
    path.lineTo( 30, 10 );

    PathData pathData = path.getPathData();
    assertArrayEquals( new byte[] { SWT.PATH_MOVE_TO, SWT.PATH_LINE_TO, SWT.PATH_LINE_TO },
                       pathData.types );
    assertArrayEquals( new float[] { 0, 0, 10, 10, 30, 10 }, pathData.points, 0 );
  }

  @Test(expected = SWTException.class)
  public void testLineTo_withDisposedPath() {
    path.dispose();

    path.lineTo( 10, 20 );
  }

  @Test
  public void testLineTo_currentPoint() {
    path.lineTo( 10, 20 );

    float[] currentPoint = new float[ 2 ];
    path.getCurrentPoint( currentPoint );

    assertEquals( 10, currentPoint[ 0 ], 0 );
    assertEquals( 20, currentPoint[ 1 ], 0 );
  }

  @Test
  public void testQuadTo() {
    path.quadTo( 10, 10, 20, 5 );

    PathData pathData = path.getPathData();
    assertArrayEquals( new byte[] { SWT.PATH_MOVE_TO, SWT.PATH_QUAD_TO }, pathData.types );
    assertArrayEquals( new float[] { 0, 0, 10, 10, 20, 5 }, pathData.points, 0 );
  }

  @Test(expected = SWTException.class)
  public void testQuadTo_withDisposedPath() {
    path.dispose();

    path.quadTo( 10, 10, 20, 5 );
  }

  @Test
  public void testQuadTo_currentPoint() {
    path.quadTo( 10, 10, 20, 5 );

    float[] currentPoint = new float[ 2 ];
    path.getCurrentPoint( currentPoint );

    assertEquals( 20, currentPoint[ 0 ], 0 );
    assertEquals( 5, currentPoint[ 1 ], 0 );
  }

  @Test
  public void testCubicTo() {
    path.cubicTo( 10, 10, 20, 20, 30, 5 );

    PathData pathData = path.getPathData();
    assertArrayEquals( new byte[] { SWT.PATH_MOVE_TO, SWT.PATH_CUBIC_TO }, pathData.types );
    assertArrayEquals( new float[] { 0, 0, 10, 10, 20, 20, 30, 5 }, pathData.points, 0 );
  }

  @Test(expected = SWTException.class)
  public void testCubicTo_withDisposedPath() {
    path.dispose();

    path.cubicTo( 10, 10, 20, 20, 30, 5 );
  }

  @Test
  public void testCubicTo_currentPoint() {
    path.cubicTo( 10, 10, 20, 20, 30, 5 );

    float[] currentPoint = new float[ 2 ];
    path.getCurrentPoint( currentPoint );

    assertEquals( 30, currentPoint[ 0 ], 0 );
    assertEquals( 5, currentPoint[ 1 ], 0 );
  }

  @Test
  public void testClose() {
    path.lineTo( 10, 10 );
    path.lineTo( 30, 10 );

    path.close();

    PathData pathData = path.getPathData();
    byte[] expectedTypes = new byte[] {
      SWT.PATH_MOVE_TO, SWT.PATH_LINE_TO, SWT.PATH_LINE_TO, SWT.PATH_CLOSE
    };
    assertArrayEquals( expectedTypes, pathData.types );
    assertArrayEquals( new float[] { 0, 0, 10, 10, 30, 10 }, pathData.points, 0 );
  }

  @Test(expected = SWTException.class)
  public void testClose_withDisposedPath() {
    path.dispose();

    path.close();
  }

  @Test
  public void testClose_currentPoint() {
    path.lineTo( 10, 10 );
    path.lineTo( 30, 10 );
    path.close();

    float[] currentPoint = new float[ 2 ];
    path.getCurrentPoint( currentPoint );

    assertEquals( 0, currentPoint[ 0 ], 0 );
    assertEquals( 0, currentPoint[ 1 ], 0 );
  }

  @Test
  public void testAddPath() {
    path.lineTo( 10, 10 );
    Path pathToAdd = new Path( device );
    pathToAdd.lineTo( 20, 10 );

    path.addPath( pathToAdd );

    PathData pathData = path.getPathData();
    byte[] expectedTypes = new byte[] {
      SWT.PATH_MOVE_TO, SWT.PATH_LINE_TO, SWT.PATH_MOVE_TO, SWT.PATH_LINE_TO
    };
    assertArrayEquals( expectedTypes, pathData.types );
    assertArrayEquals( new float[] { 0, 0, 10, 10, 0, 0, 20, 10 }, pathData.points, 0 );
  }

  @Test(expected = SWTException.class)
  public void testAddPath_withDisposedPath() {
    path.dispose();

    path.addPath( new Path( device ) );
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddPath_withNullArgument() {
    path.addPath( null );
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddPath_withDisposedArgument() {
    Path pathToAdd = new Path( device );
    pathToAdd.dispose();

    path.addPath( pathToAdd );
  }

  @Test
  public void testAddPath_currentPoint() {
    path.lineTo( 10, 10 );
    Path pathToAdd = new Path( device );
    pathToAdd.lineTo( 20, 10 );
    path.addPath( pathToAdd );

    float[] currentPoint1 = new float[ 2 ];
    pathToAdd.getCurrentPoint( currentPoint1 );
    float[] currentPoint2 = new float[ 2 ];
    path.getCurrentPoint( currentPoint2 );

    assertEquals( currentPoint1[ 0 ], currentPoint2[ 0 ], 0 );
    assertEquals( currentPoint1[ 1 ], currentPoint2[ 1 ], 0 );
  }

  @Test
  public void testAddPath_adjustCurrentPoint() {
    path.lineTo( 10, 10 );
    Path pathToAdd = new Path( device );
    pathToAdd.lineTo( 20, 10 );
    pathToAdd.moveTo( 30, 30 );
    path.addPath( pathToAdd );

    float[] currentPoint1 = new float[ 2 ];
    pathToAdd.getCurrentPoint( currentPoint1 );
    float[] currentPoint2 = new float[ 2 ];
    path.getCurrentPoint( currentPoint2 );

    assertEquals( currentPoint1[ 0 ], currentPoint2[ 0 ], 0 );
    assertEquals( currentPoint1[ 1 ], currentPoint2[ 1 ], 0 );
  }

  @Test
  public void testAddRectangle() {
    path.addRectangle( 10, 10, 30, 20 );

    PathData pathData = path.getPathData();
    byte[] expectedTypes = new byte[] {
      SWT.PATH_MOVE_TO, SWT.PATH_LINE_TO, SWT.PATH_LINE_TO, SWT.PATH_LINE_TO, SWT.PATH_CLOSE
    };
    assertArrayEquals( expectedTypes, pathData.types );
    assertArrayEquals( new float[] { 10, 10, 40, 10, 40, 30, 10, 30 }, pathData.points, 0 );
  }

  @Test(expected = SWTException.class)
  public void testAddRectangle_withDisposedPath() {
    path.dispose();

    path.addRectangle( 10, 10, 30, 20 );
  }

  @Test
  public void testAddRectangle_currentPoint() {
    path.addRectangle( 10, 10, 30, 20 );

    float[] currentPoint = new float[ 2 ];
    path.getCurrentPoint( currentPoint );

    assertEquals( 10, currentPoint[ 0 ], 0 );
    assertEquals( 10, currentPoint[ 1 ], 0 );
  }

}
