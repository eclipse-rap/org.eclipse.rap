/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.rwt.internal.lifecycle;

import java.util.Enumeration;
import javax.servlet.http.HttpSession;
import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.internal.widgets.compositekit.CompositeLCA;
import org.eclipse.rap.rwt.lifecycle.ILifeCycleAdapter;
import org.eclipse.rap.rwt.lifecycle.IWidgetLifeCycleAdapter;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.Fixture;
import com.w4t.engine.service.ContextProvider;

public class LifeCycleAdapter_Test extends TestCase {

  public static class CustomComposite extends Composite {

    public CustomComposite( final Composite parent ) {
      super( parent, RWT.NONE );
    }
  }
  public static class TestControl extends Control {

    public TestControl( final Composite parent ) {
      super( parent, RWT.NONE );
    }
  }

  public void testDisplayLifeCycleAdapter() {
    Display display1 = new Display();
    Object adapter1 = display1.getAdapter( ILifeCycleAdapter.class );
    assertTrue( adapter1 instanceof IDisplayLifeCycleAdapter );
    Object adapter2 = display1.getAdapter( ILifeCycleAdapter.class );
    assertSame( adapter1, adapter2 );
    removeDisplay();
    Display display2 = new Display();
    Object adapter3 = display2.getAdapter( ILifeCycleAdapter.class );
    assertSame( adapter2, adapter3 );
    RWTFixture.tearDown();
    RWTFixture.setUp();
    Fixture.fakeResponseWriter();
    Display display3 = new Display();
    Object adapter4 = display3.getAdapter( ILifeCycleAdapter.class );
    assertSame( adapter3, adapter4 );
  }

  public void testWidgetLifeCycleAdapter() {
    Display display = new Display();
    Composite shell1 = new Shell( display , RWT.NONE );
    Object shell1LCA = shell1.getAdapter( ILifeCycleAdapter.class );
    assertTrue( shell1LCA instanceof IWidgetLifeCycleAdapter );
    Composite shell2 = new Shell( display , RWT.NONE );
    Object shell2LCA = shell2.getAdapter( ILifeCycleAdapter.class );
    assertTrue( shell2LCA instanceof IWidgetLifeCycleAdapter );
    assertSame( shell1LCA, shell2LCA );
    Button button = new Button( shell2, RWT.PUSH );
    Object buttonLCA = button.getAdapter( ILifeCycleAdapter.class );
    assertTrue( buttonLCA instanceof IWidgetLifeCycleAdapter );
    CustomComposite customComposite = new CustomComposite( shell2 );
    Object customComposite1LCA = customComposite.getAdapter( ILifeCycleAdapter.class );
    assertTrue( customComposite1LCA.getClass().equals( CompositeLCA.class ) );
    CustomComposite customComposite2 = new CustomComposite( shell2 );
    Object customComposite2LCA = customComposite2.getAdapter( ILifeCycleAdapter.class );
    assertSame( customComposite1LCA, customComposite2LCA );
    Composite composite = new Composite( shell2, RWT.NONE );
    Object compositeLCA = composite.getAdapter( ILifeCycleAdapter.class );
    assertTrue( compositeLCA instanceof IWidgetLifeCycleAdapter );
    assertNotSame( customComposite1LCA, compositeLCA );
    RWTFixture.tearDown();
    RWTFixture.setUp();
    Fixture.fakeResponseWriter();
    display = new Display();
    Composite otherSessionShell = new Shell( display , RWT.NONE );
    Object otherSessionAdapter = otherSessionShell.getAdapter( ILifeCycleAdapter.class );
    assertSame( shell1LCA, otherSessionAdapter );
  }

  public void testLCAClassNameVariants() {
    String packageName = "org.eclipse.rap.rwt.widgets";
    String[] variants = LifeCycleAdapterFactory.getPackageVariants( packageName );
    String expected = "internal.org.eclipse.rap.rwt.widgets";
    assertEquals( expected, variants[ 0 ] );
    expected = "org.internal.eclipse.rap.rwt.widgets";
    assertEquals( expected, variants[ 1 ] );
    expected = "org.eclipse.internal.rap.rwt.widgets";
    assertEquals( expected, variants[ 2 ] );
    expected = "org.eclipse.rap.internal.rwt.widgets";
    assertEquals( expected, variants[ 3 ] );
    expected = "org.eclipse.rap.rwt.internal.widgets";
    assertEquals( expected, variants[ 4 ] );
    expected = "org.eclipse.rap.rwt.widgets.internal";
    assertEquals( expected, variants[ 5 ] );
    assertEquals( 6, variants.length );
    // default package
    packageName = "";
    variants = LifeCycleAdapterFactory.getPackageVariants( packageName );
    expected = "internal";
    assertEquals( expected, variants[ 0 ] );
    assertEquals( 1, variants.length );
    // default package as if called by LifeCycleAdapterFactory#loadWidgetLCA
    packageName = null;
    variants = LifeCycleAdapterFactory.getPackageVariants( packageName );
    expected = "internal";
    assertEquals( expected, variants[ 0 ] );
    assertEquals( 1, variants.length );
  }

  private void removeDisplay() {
    HttpSession session = ContextProvider.getSession();
    Enumeration attributeNames = session.getAttributeNames();
    String toRemove = null;
    while( toRemove == null && attributeNames.hasMoreElements() ) {
      String nextElement = ( String )attributeNames.nextElement();
      Object attribute = session.getAttribute( nextElement );
      if( attribute instanceof Display ) {
        toRemove = nextElement;
      }
    }
    session.removeAttribute( toRemove );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    Fixture.fakeResponseWriter();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}