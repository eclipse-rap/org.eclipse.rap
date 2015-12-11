/*******************************************************************************
 * Copyright (c) 2008, 2015 Innoopract Informationssysteme GmbH and others.
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.eclipse.swt.widgets.Button;
import org.junit.Test;


public class LifeCycleAdapterUtil_Test {

  @Test
  public void testGetKitPackageVariants_withPackage() {
    String[] variants = LifeCycleAdapterUtil.getKitPackageVariants( Button.class.getName() );

    String[] expected = {
      "org.internal.eclipse.swt.widgets.buttonkit",
      "org.eclipse.internal.swt.widgets.buttonkit",
      "org.eclipse.swt.internal.widgets.buttonkit",
      "org.eclipse.swt.widgets.internal.buttonkit"
    };
    assertArrayEquals( expected, variants );
  }

  @Test
  public void testGetKitPackageVariants_withTurkishLocale() {
    Locale originalLocale = Locale.getDefault();
    try {
      Locale.setDefault( new Locale( "tr", "TR" ) );
      testGetKitPackageVariants_withPackage();
    } finally {
      Locale.setDefault( originalLocale );
    }
  }

  @Test
  public void testGetSimpleClassName_withoutPackage() {
    String className = LifeCycleAdapterUtil.getSimpleClassName( "Button" );
    assertEquals( "Button", className );
  }

  @Test
  public void testGetPackageName() {
    String packageName = LifeCycleAdapterUtil.getPackageName( "org.eclipse.swt.widgets.Button" );
    assertEquals( "org.eclipse.swt.widgets", packageName );
  }

  @Test
  public void testGetPackageName_withoutPackage() {
    String packageName = LifeCycleAdapterUtil.getPackageName( "Button" );
    assertEquals( "", packageName );
  }

}
