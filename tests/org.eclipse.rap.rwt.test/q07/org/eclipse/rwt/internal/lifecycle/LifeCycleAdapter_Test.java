/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.util.Locale;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.lifecycle.ILifeCycleAdapter;
import org.eclipse.rwt.lifecycle.IWidgetLifeCycleAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


public class LifeCycleAdapter_Test extends TestCase {

  public static class CustomComposite extends Composite {
    private static final long serialVersionUID = 1L;
    public CustomComposite( Composite parent ) {
      super( parent, SWT.NONE );
    }
  }
  
  public static class TestControl extends Control {
    private static final long serialVersionUID = 1L;
    public TestControl( Composite parent ) {
      super( parent, SWT.NONE );
    }
  }
  
  public static class TestWidget extends Widget {
    private static final long serialVersionUID = 1L;
    public TestWidget( Widget parent ) {
      super( parent, SWT.NONE );
    }
  }

  public void testDisplayAdapter() {
    Display display = new Display();
    Object adapter = display.getAdapter( ILifeCycleAdapter.class );
    assertTrue( adapter instanceof IDisplayLifeCycleAdapter );
  }
  
  public void testDisplayAdapterReturnsSameAdapterForEachInvocation() {
    Display display = new Display();
    Object adapter1 = display.getAdapter( ILifeCycleAdapter.class );
    Object adapter2 = display.getAdapter( ILifeCycleAdapter.class );
    assertSame( adapter1, adapter2 );
  }
  
  public void testDisplayAdapterReturnsSameAdapterForDifferentDisplays() {
    Display display1 = new Display();
    Object adapter1 = display1.getAdapter( ILifeCycleAdapter.class );
    display1.dispose();
    Display display2 = new Display();
    Object adapter2 = display2.getAdapter( ILifeCycleAdapter.class );
    assertSame( adapter1, adapter2 );
  }
  
  public void testDisplayAdapterIsApplicationScoped() {
    Display display1 = new Display();
    Object adapter1 = display1.getAdapter( ILifeCycleAdapter.class );
    newSession();
    Display display2 = new Display();
    Object adapter2 = display2.getAdapter( ILifeCycleAdapter.class );
    assertSame( adapter1, adapter2 );
  }
  
  public void testWidgetAdapter() {
    Display display = new Display();
    Widget widget = new Shell( display );
    Object adapter = widget.getAdapter( ILifeCycleAdapter.class );
    assertTrue( adapter instanceof IWidgetLifeCycleAdapter );
  }
  
  public void testWidgetAdapterReturnsSameAdapterForEachInvocation() {
    Display display = new Display();
    Widget widget = new Shell( display );
    Object adapter1 = widget.getAdapter( ILifeCycleAdapter.class );
    Object adapter2 = widget.getAdapter( ILifeCycleAdapter.class );
    assertSame( adapter1, adapter2 );
  }
  
  public void testWidgetAdapterReturnsSameAdapterForDifferentInstancesOfSameType() {
    Display display = new Display();
    Widget widget1 = new Shell( display );
    Object adapter1 = widget1.getAdapter( ILifeCycleAdapter.class );
    Widget widget2 = new Shell( display );
    Object adapter2 = widget2.getAdapter( ILifeCycleAdapter.class );
    assertSame( adapter1, adapter2 );
  }
  
  public void testWidgetAdaptreReturnsDistinctAdapterForEachWidgetType() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Object shellAdapter = shell.getAdapter( ILifeCycleAdapter.class );
    Button button = new Button( shell, SWT.PUSH );
    Object buttonAdapter = button.getAdapter( ILifeCycleAdapter.class );
    assertNotNull( shellAdapter );
    assertNotNull( buttonAdapter );
    assertNotSame( shellAdapter, buttonAdapter );
  }

  public void testWidgetAdapterIsApplicationScoped() {
    Display display1 = new Display();
    Widget widget1 = new Shell( display1 );
    Object adapter1 = widget1.getAdapter( ILifeCycleAdapter.class );
    newSession();
    Display display2 = new Display();
    Widget widget2 = new Shell( display2 );
    Object adapter2 = widget2.getAdapter( ILifeCycleAdapter.class );
    assertSame( adapter1, adapter2 );
  }

  public void testGetAdapterWithMissingWidgetLCA() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Widget widget = new TestWidget( shell );
    try {
      widget.getAdapter( ILifeCycleAdapter.class );
      fail();
    } catch( LifeCycleAdapterException expected ) {
    }
  }

  public void testTreeItemLifeCycleAdapter() {
    Locale originalLocale = Locale.getDefault();
    try {
      Locale.setDefault( new Locale( "tr", "TR" ) );
      Display display = new Display();
      Composite shell = new Shell( display, SWT.NONE );
      Tree tree = new Tree( shell, SWT.NONE );
      TreeItem treeItem = new TreeItem( tree, SWT.NONE );
      Object treeItemLCA = treeItem.getAdapter( ILifeCycleAdapter.class );
      assertTrue( treeItemLCA instanceof IWidgetLifeCycleAdapter );
    } finally {
      Locale.setDefault( originalLocale );
    }
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  private static void newSession() {
    ContextProvider.disposeContext();
    Fixture.createServiceContext();
  }
}