/*******************************************************************************
 * Copyright (c) 2008, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.eclipse.rap.rwt.internal.theme.ThemeTestUtil.RESOURCE_LOADER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.service.ApplicationContext;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class CssImage_Test {

  @Rule
  public TestContext context = new TestContext();

  private ApplicationContext applicationContext;

  @Before
  public void setUp() {
    applicationContext = getApplicationContext();
  }

  @Test( expected = NullPointerException.class )
  public void testValueOf_nullArgument() {
    CssImage.valueOf( null, RESOURCE_LOADER );
  }

  @Test( expected = NullPointerException.class )
  public void testValueOf_nullLoader() {
    CssImage.valueOf( "", null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValueOf_emptyString() {
    CssImage.valueOf( "", RESOURCE_LOADER );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValueOf_notExistingCursorFile() {
    CssImage.valueOf( "not-existing.png", RESOURCE_LOADER );
  }

  @Test( expected = NullPointerException.class )
  public void testCreateGradient_nullColors() {
    CssImage.createGradient( null, new float[] {}, true );
  }

  @Test( expected = NullPointerException.class )
  public void testCreateGradient_nullPercents() {
    CssImage.createGradient( new String[] {}, null, true );
  }

  @Test
  public void testNone() {
    assertSame( CssImage.NONE, CssImage.valueOf( "none", null ) );
    assertSame( CssImage.NONE, CssImage.valueOf( "none", RESOURCE_LOADER ) );
    assertTrue( CssImage.NONE.none );
    assertNull( CssImage.NONE.path );
    assertNull( CssImage.NONE.loader );
    assertNull( CssImage.NONE.gradientColors );
    assertNull( CssImage.NONE.gradientPercents );
    assertTrue( CssImage.NONE.vertical );
    assertEquals( 0, CssImage.NONE.width );
    assertEquals( 0, CssImage.NONE.height );
  }

  @Test
  public void testCreateImage() {
    CssImage qxImage = CssImage.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    assertFalse( qxImage.none );
    assertEquals( Fixture.IMAGE_50x100, qxImage.path );
    assertSame( RESOURCE_LOADER, qxImage.loader );
    assertNull( qxImage.gradientColors );
    assertNull( qxImage.gradientPercents );
    assertTrue( qxImage.vertical );
    assertEquals( 50, qxImage.width );
    assertEquals( 100, qxImage.height );
    assertFalse( qxImage.isGradient() );
  }

  @Test
  public void testCreateVerticalGradient() {
    String[] gradientColors = new String[] { "#FF0000", "#00FF00", "#0000FF" };
    float[] gradientPercents = new float[] { 0f, 50f, 100f };
    CssImage qxImage = CssImage.createGradient( gradientColors, gradientPercents, true );
    assertSame( gradientColors, qxImage.gradientColors );
    assertSame( gradientPercents, qxImage.gradientPercents );
    assertTrue( qxImage.vertical );
    assertTrue( qxImage.none );
    assertNull( qxImage.path );
    assertNull( qxImage.loader );
    assertEquals( 0, qxImage.width );
    assertEquals( 0, qxImage.height );
    assertTrue( qxImage.isGradient() );
  }

  @Test
  public void testCreateHorizontalGradient() {
    String[] gradientColors = new String[] { "#FF0000", "#00FF00", "#0000FF" };
    float[] gradientPercents = new float[] { 0f, 50f, 100f };
    CssImage qxImage = CssImage.createGradient( gradientColors, gradientPercents, false );
    assertSame( gradientColors, qxImage.gradientColors );
    assertSame( gradientPercents, qxImage.gradientPercents );
    assertFalse( qxImage.vertical );
    assertTrue( qxImage.none );
    assertNull( qxImage.path );
    assertNull( qxImage.loader );
    assertEquals( 0, qxImage.width );
    assertEquals( 0, qxImage.height );
    assertTrue( qxImage.isGradient() );
  }

  @Test
  public void testDefaultString() {
    assertEquals( "none", CssImage.NONE.toDefaultString() );
    assertEquals( "", CssImage.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER ).toDefaultString() );
  }

  @Test
  public void testHashCode() {
    assertEquals( -1526341861, CssImage.NONE.hashCode() );
    CssImage qxImage1 = CssImage.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    CssImage qxImage2 = CssImage.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    assertEquals( qxImage1, qxImage2 );
    assertEquals( qxImage1.hashCode(), qxImage2.hashCode() );
    String[] gradientColors = new String[] { "#FF0000", "#00FF00", "#0000FF" };
    float[] gradientPercents = new float[] { 0f, 50f, 100f };
    CssImage gradient1 = CssImage.createGradient( gradientColors, gradientPercents, true );
    CssImage gradient2 = CssImage.createGradient( gradientColors, gradientPercents, true );
    assertEquals( gradient1, gradient2 );
    assertEquals( gradient1.hashCode(), gradient2.hashCode() );
  }

  @Test
  public void testHashCode_GradientWithMoreColors() {
    String[] gradientColors1
      = new String[] { "#FFFFFF", "#00AA00", "#00AA00", "#00AA00", "#FFFFFF" };
    float[] gradientPercents1 = new float[] { 0f, 48f, 52f, 56f, 100f };
    String[] gradientColors2
      = new String[] { "#FFFFFF", "#AA0000", "#AA0000", "#AA0000", "#FFFFFF" };
    float[] gradientPercents2 = new float[] { 0f, 48f, 52f, 56f, 100f };
    CssImage gradient1 = CssImage.createGradient( gradientColors1, gradientPercents1, true );
    CssImage gradient2 = CssImage.createGradient( gradientColors2, gradientPercents2, true );
    assertFalse( gradient1.hashCode() == gradient2.hashCode() );
  }

  @Test
  public void testIsGradientFalseForNone() {
    CssImage nonImage = CssImage.NONE;
    assertFalse( nonImage.isGradient() );
  }

  @Test
  public void testGetResourceName() {
    CssImage image = CssImage.NONE;
    assertNull( image.getResourcePath( applicationContext ) );
    image = CssImage.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    assertEquals( "themes/images/ba873d77.png", image.getResourcePath( applicationContext ) );
  }

  @Test
  public void testCreateSWTImageFromNone() throws IOException {
    CssImage image = CssImage.NONE;
    try {
      CssImage.createSwtImage( image );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testCreateSWTImageFromGradient() throws IOException {
    String[] gradientColors = new String[] { "#FF0000", "#00FF00", "#0000FF" };
    float[] gradientPercents = new float[] { 0f, 50f, 100f };
    CssImage gradient = CssImage.createGradient( gradientColors, gradientPercents, true );
    try {
      CssImage.createSwtImage( gradient );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testCreateSWTImage() throws IOException {
    Display display = new Display();
    CssImage image = CssImage.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    Image swtImage = CssImage.createSwtImage( image );
    assertNotNull( swtImage );
    assertSame( display, swtImage.getDevice() );
  }

  @Test
  public void testGetResourcePath() {
    CssImage image = CssImage.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );

    assertTrue( image.getResourcePath( applicationContext ).startsWith( "themes/images/" ) );
  }

  @Test
  public void testResourcePathsDiffer() {
    CssImage image1 = CssImage.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    CssImage image2 = CssImage.valueOf( Fixture.IMAGE_100x50, RESOURCE_LOADER );

    String path1 = image1.getResourcePath( applicationContext );
    String path2 = image2.getResourcePath( applicationContext );
    assertFalse( path1.equals( path2 ) );
  }

  @Test
  public void testGetResourcePathWithNone() {
    assertNull( CssImage.NONE.getResourcePath( applicationContext ) );
  }

  @Test
  public void testGetResourcePathWithGradient() {
    assertNull( createGradient().getResourcePath( applicationContext ) );
  }

  @Test
  public void testGetResourceAsStream() throws IOException {
    CssImage image = CssImage.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    InputStream inputStream = image.getResourceAsStream();

    assertTrue( inputStream.available() > 0 );
    inputStream.close();
  }

  @Test
  public void testGetResourceAsStreamWithNone() throws IOException {
    assertNull( CssImage.NONE.getResourceAsStream() );
  }

  @Test
  public void testGetResourceAsStreamWithGradient() throws IOException {
    assertNull( createGradient().getResourceAsStream() );
  }

  private static CssImage createGradient() {
    String[] gradientColors = new String[] { "#FF0000", "#0000FF" };
    float[] gradientPercents = new float[] { 0f, 100f };
    return CssImage.createGradient( gradientColors, gradientPercents, false );
  }

}
