/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.WidgetAdapter;


public class WidgetSerialization_Test extends TestCase {
  
  private static class TestListener implements Listener {
    public void handleEvent( Event event ) {
    }
  }

  private static class TestWidget extends Widget {
  }
  
  private Widget widget;

  public void testWidgetAdapterIsSerializable() throws Exception {
    WidgetAdapter adapter = getWidgetAdapter( widget );
    adapter.setInitialized( true );
    
    Widget deserializedWidget = Fixture.serializeAndDeserialize( widget );
    IWidgetAdapter deserializedAdapter = getWidgetAdapter( deserializedWidget );
    
    assertNotNull( deserializedAdapter );
    assertEquals( adapter.getId(), deserializedAdapter.getId() );
    assertEquals( adapter.isInitialized(), deserializedAdapter.isInitialized() );
  }
  
  public void testStyleIsSerializable() throws Exception {
    int style = 1234;
    widget.style = style;
    
    Widget deserializedWidget = Fixture.serializeAndDeserialize( widget );
    
    assertEquals( style, deserializedWidget.style );
  }
  
  public void testStateIsSerializable() throws Exception {
    int state = 5678;
    widget.state = state;
    
    Widget deserializedWidget = Fixture.serializeAndDeserialize( widget );
    
    assertEquals( state, deserializedWidget.state );
  }
  
  public void testDataIsSerializable() throws Exception {
    String data = "data";
    String key = "key";
    String keyedData = "keyedData";
    widget.setData( data );
    widget.setData( key, keyedData );

    Widget deserializedWidget = Fixture.serializeAndDeserialize( widget );

    assertEquals( data, deserializedWidget.getData() );
    assertEquals( keyedData, deserializedWidget.getData( key ) );
  }
  
  public void testListenerIsSerializable() throws Exception {
    widget.addListener( SWT.Dispose, new TestListener() );
    
    Widget deserializedWidget = Fixture.serializeAndDeserialize( widget );
    
    Listener[] listeners = deserializedWidget.getListeners( SWT.Dispose );
    assertEquals( 1, listeners.length );
  }

  public void testPreservedValuesAreNotSerialized() throws Exception {
    String propertyName = "foo";
    WidgetAdapter adapter = getWidgetAdapter( widget );
    adapter.preserve( propertyName, "bar" );
    
    Widget deserializedWidget = Fixture.serializeAndDeserialize( widget );
    IWidgetAdapter deserializedAdapter = getWidgetAdapter( deserializedWidget );

    assertNull( deserializedAdapter.getPreserved( propertyName ) );
  }

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    widget = new TestWidget();
    widget.display = new Display();
  }
  
  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  private static WidgetAdapter getWidgetAdapter( Widget widget ) {
    return ( WidgetAdapter )widget.getAdapter( IWidgetAdapter.class );
  }
}
