/*******************************************************************************
 * Copyright (c) 2008, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.widgets.Button;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class LifeCycleAdapterUtil_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testGetKitPackageVariantsWithClass() {
    String[] variants
      = LifeCycleAdapterUtil.getKitPackageVariants( Button.class );
    assertEquals( 5, variants.length );
    String expected = "internal.org.eclipse.swt.widgets.buttonkit";
    assertEquals( expected, variants[ 0 ] );
    expected = "org.internal.eclipse.swt.widgets.buttonkit";
    assertEquals( expected, variants[ 1 ] );
    expected = "org.eclipse.internal.swt.widgets.buttonkit";
    assertEquals( expected, variants[ 2 ] );
    expected = "org.eclipse.swt.internal.widgets.buttonkit";
    assertEquals( expected, variants[ 3 ] );
    expected = "org.eclipse.swt.widgets.internal.buttonkit";
  }

  @Test
  public void testGetKitPackageVariants() {
    String packageName = "org.eclipse.swt.widgets";
    String className = "Item";
    String[] variants
      = LifeCycleAdapterUtil.getKitPackageVariants( packageName, className );
    assertEquals( 5, variants.length );
    String expected = "internal.org.eclipse.swt.widgets.itemkit";
    assertEquals( expected, variants[ 0 ] );
    expected = "org.internal.eclipse.swt.widgets.itemkit";
    assertEquals( expected, variants[ 1 ] );
    expected = "org.eclipse.internal.swt.widgets.itemkit";
    assertEquals( expected, variants[ 2 ] );
    expected = "org.eclipse.swt.internal.widgets.itemkit";
    assertEquals( expected, variants[ 3 ] );
    expected = "org.eclipse.swt.widgets.internal.itemkit";
    // default package
    packageName = "";
    variants
      = LifeCycleAdapterUtil.getKitPackageVariants( packageName, className );
    assertEquals( 1, variants.length );
    expected = "internal.itemkit";
    assertEquals( expected, variants[ 0 ] );
    // default package as if called by LifeCycleAdapterFactory#loadWidgetLCA
    packageName = null;
    variants
      = LifeCycleAdapterUtil.getKitPackageVariants( packageName, className );
    assertEquals( 1, variants.length );
    expected = "internal.itemkit";
    assertEquals( expected, variants[ 0 ] );
  }

  @Test
  public void testGetKitPackageVariantsWithTurkishLocale() {
    Locale originalLocale = Locale.getDefault();
    String packageName = "org.eclipse.swt.widgets";
    String className = "Item";
    try {
      Locale.setDefault( new Locale( "tr", "TR" ) );
      String[] variants
        = LifeCycleAdapterUtil.getKitPackageVariants( packageName, className );
      assertEquals( 5, variants.length );
      String expected = "internal.org.eclipse.swt.widgets.itemkit";
      assertEquals( expected, variants[ 0 ] );
      expected = "org.internal.eclipse.swt.widgets.itemkit";
      assertEquals( expected, variants[ 1 ] );
      expected = "org.eclipse.internal.swt.widgets.itemkit";
      assertEquals( expected, variants[ 2 ] );
      expected = "org.eclipse.swt.internal.widgets.itemkit";
      assertEquals( expected, variants[ 3 ] );
      expected = "org.eclipse.swt.widgets.internal.itemkit";
    } finally {
      Locale.setDefault( originalLocale );
    }
  }

  @Test
  public void testGetSimpleClassName() {
    String className = LifeCycleAdapterUtil.getSimpleClassName( Button.class );
    String expected = "Button";
    assertEquals( expected, className );
  }

}
