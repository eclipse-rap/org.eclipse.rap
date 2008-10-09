/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.widgets.Button;

import junit.framework.TestCase;


public class LifeCycleAdapterUtil_Test extends TestCase {

  public void testGetPackageVariants() {
    String packageName = "org.eclipse.swt.widgets";
    String[] variants = LifeCycleAdapterUtil.getPackageVariants( packageName );
    assertEquals( 5, variants.length );
    String expected = "internal.org.eclipse.swt.widgets";
    assertEquals( expected, variants[ 0 ] );
    expected = "org.internal.eclipse.swt.widgets";
    assertEquals( expected, variants[ 1 ] );
    expected = "org.eclipse.internal.swt.widgets";
    assertEquals( expected, variants[ 2 ] );
    expected = "org.eclipse.swt.internal.widgets";
    assertEquals( expected, variants[ 3 ] );
    expected = "org.eclipse.swt.widgets.internal";
    assertEquals( expected, variants[ 4 ] );
    // default package
    packageName = "";
    variants = LifeCycleAdapterUtil.getPackageVariants( packageName );
    assertEquals( 1, variants.length );
    expected = "internal";
    assertEquals( expected, variants[ 0 ] );
    // default package as if called by LifeCycleAdapterFactory#loadWidgetLCA
    packageName = null;
    variants = LifeCycleAdapterUtil.getPackageVariants( packageName );
    assertEquals( 1, variants.length );
    expected = "internal";
    assertEquals( expected, variants[ 0 ] );
  }

  public void testGetSimpleClassName() {
    String className = LifeCycleAdapterUtil.getSimpleClassName( Button.class );
    String expected = "Button";
    assertEquals( expected, className );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    Fixture.fakeResponseWriter();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
