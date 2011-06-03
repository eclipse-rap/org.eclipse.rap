/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.lifecycle;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.WidgetAdapter;
import org.eclipse.swt.widgets.*;


public class JSWriter_Test extends TestCase {

  private static final String PROPERTY_NAME = "propertyName";

  private final class TestShell extends Shell {
    private static final long serialVersionUID = 1L;

    final Button button;
    final Text text;
    final Combo combo;

    public TestShell( final Display display ) {
      super( display ,SWT.NONE);
      button = new Button( this, SWT.PUSH );
      text = new Text( this, SWT.SINGLE );
      combo = new Combo( this, SWT.NONE );
      combo.setItems( new String[]{
        "test", "test2", "test3"
      } );
    }
  }
  
  public static class WidgetDisposalEntryPoint implements IEntryPoint {
    private static String dispId;
    private static String buttonId;
    
    public int createUI() {
      Display display = new Display();
      dispId = DisplayUtil.getId( display );
      Shell shell = new Shell( display, SWT.NONE );
      final Text text = new Text( shell, SWT.MULTI );
      final Tree tree = new Tree( shell, SWT.SINGLE );
      for( int i = 0; i < 5; i++ ) {
        TreeItem item = new TreeItem( tree, SWT.NONE );
        item.setText( "foo" + i );
      }
      Button button = new Button( shell, SWT.PUSH );
      button.addSelectionListener( new SelectionAdapter() {
        public void widgetSelected( final SelectionEvent event ) {
          text.dispose();
          tree.dispose();
        }
      } );
      buttonId = WidgetUtil.getId( button );
      int count = 0;
      while( count  < 2 ) {
        if( !display.readAndDispatch() ) {
          display.sleep();
        }
        count++;
      }
      return 0;
    }
  }


  public void testUniqueWriterPerRequest() {
    Display display = new Display();
    Composite shell = new TestShell( display );
    JSWriter writer1 = JSWriter.getWriterFor( shell );
    JSWriter writer2 = JSWriter.getWriterFor( shell );
    assertSame( writer1, writer2 );
  }

  public void testInitialization() throws Exception {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    // ensure that the WidgetManager gets initialized
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.newWidget( "qx.ui.form.Button" );
    String expected
      =   "var wm = org.eclipse.swt.WidgetManager.getInstance();"
        + "var w = new qx.ui.form.Button();"
        + "wm.add( w, \"w2\", true );"
        + "wm.setParent( w, \"w3\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
    // ensure that the WidgetManager, once initialized, is not initialized
    // twice
    writer = JSWriter.getWriterFor( shell.button );
    writer.newWidget( "qx.ui.form.Button" );
    expected +=   "var w = new qx.ui.form.Button();"
                + "wm.add( w, \"w2\", true );" 
                + "wm.setParent( w, \"w3\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
    // ensure that obtaining the widget reference (var w =) is only rendered
    // once
    writer.set( "Width", 5 );
    expected += "w.setWidth( 5 );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testNewWidget() throws Exception {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.newWidget( "qx.ui.form.Button" );
    String expected
      =   "var wm = org.eclipse.swt.WidgetManager.getInstance();"
        + "var w = new qx.ui.form.Button();"
        + "wm.add( w, \"w2\", true );wm.setParent( w, \"w3\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
    // Ensures that the "widget reference is set"-flag is set
    Fixture.fakeResponseWriter();
    writer.set( "Text", "xyz" );
    expected = "w.setText( \"xyz\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testNewWidgetWithParams() throws Exception {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    Object[] arrayParam = new Object[] { Graphics.getColor( 255, 0, 0 ) };
    Object[] params = new Object[]{ "abc", arrayParam };
    writer.newWidget( "qx.ui.form.Button", params );
    String expected
      =   "var wm = org.eclipse.swt.WidgetManager.getInstance();"
        + "var w = new qx.ui.form.Button( \"abc\", [\"#ff0000\" ] );"
        + "wm.add( w, \"w2\", true );wm.setParent( w, \"w3\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
    // Ensures that the "widget reference is set"-flag is set
    Fixture.fakeResponseWriter();
    writer.set( "Text", "xyz" );
    expected = "w.setText( \"xyz\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
    // Ensures that an Item is added and marked as a 'no-control' and setParent
    // is not called
    Fixture.fakeResponseWriter();
    writer = JSWriter.getWriterFor( item );
    writer.newWidget( "TreeItem", null );
    expected = "var w = new TreeItem();wm.add( w, \"w4\", false );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testResetJSProperty() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display );
    JSWriter writer = JSWriter.getWriterFor( shell );
    Fixture.fakeResponseWriter();
    writer.reset( "testProperty" );
    String expected = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
                      + "var w = wm.findWidgetById( \""
                      + WidgetUtil.getId( shell )
                      + "\" );w.resetTestProperty();";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testResetJSPropertyChain() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display );
    JSWriter writer = JSWriter.getWriterFor( shell );
    Fixture.fakeResponseWriter();
    writer.reset( new String[] { "labelObject", "testProperty" } );
    String expected = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
                      + "var w = wm.findWidgetById( \""
                      + WidgetUtil.getId( shell )
                      + "\" );w.getLabelObject().resetTestProperty();";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testSetParent() throws IOException {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.newWidget( "xyz" );
    Fixture.fakeResponseWriter();
    writer.setParent( "xyz" );
    String expected = "wm.setParent( w, \"xyz\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testSetString() throws Exception {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.set( "text", "xyz" );
    String expected
      =   "var wm = org.eclipse.swt.WidgetManager.getInstance();"
        + "var w = wm.findWidgetById( \"w2\" );w.setText( \"xyz\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testSetInt() throws Exception {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    // use a high value to test separator avoidance...
    writer.set( "width", 20000000 );
    String expected
      =   "var wm = org.eclipse.swt.WidgetManager.getInstance();"
        + "var w = wm.findWidgetById( \"w2\" );w.setWidth( 20000000 );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testSetBoolean() throws Exception {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.set( "allowStretchY", true );
    String expected
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
      + "var w = wm.findWidgetById( \"w2\" );w.setAllowStretchY( true );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testCallWithStringArray() throws Exception {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    // test regular strings
    Fixture.fakeResponseWriter();
    writer.call( "setTextValues", new String[] { "abc", "xyz" } );
    String expected
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
      + "var w = wm.findWidgetById( \"w2\" );"
      + "w.setTextValues( \"abc\", \"xyz\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
    // test string with newline
    Fixture.fakeResponseWriter();
    writer.call( "setTextValues", new String[] { "new\nline" } );
    expected = "w.setTextValues( \"new\\nline\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testCallWithObjectArray() throws IOException {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    // test regular strings
    Fixture.fakeResponseWriter();
    Integer[] values = new Integer[] { new Integer( 1 ), new Integer( 2 ) };
    writer.call( "setIntegerValues", values );
    String expected
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
      + "var w = wm.findWidgetById( \"w2\" );"
      + "w.setIntegerValues( 1, 2 );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }
  
  public void testCallWithColorValue() throws IOException {
    Color salmon = Graphics.getColor( 250, 128, 114 );
    Color chocolate = Graphics.getColor( 210, 105, 30 );
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.call( "foo", null ); // get rid of initialization Javascript code
    Fixture.fakeResponseWriter();
    writer.call( "setColor", new Object[] { salmon } );
    String expected = "w.setColor( \"#fa8072\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
    Fixture.fakeResponseWriter();
    writer.call( "setColor", new Object[] { chocolate } );
    expected = "w.setColor( \"#d2691e\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testSetIntArray() throws Exception {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.set( "intValues", new int[] { 1, 2 } );
    String expected
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
      + "var w = wm.findWidgetById( \"w2\" );"
      + "w.setIntValues( 1, 2 );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testSetBooleanArray() throws Exception {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.set( "boolValues", new boolean[] { true, false } );
    String expected
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
      + "var w = wm.findWidgetById( \"w2\" );"
      + "w.setBoolValues( true, false );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testSetWidgetArray() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display );
    Widget widget1 = new Button( shell, SWT.PUSH );
    Widget widget2 = new Button( shell, SWT.PUSH );

    // call JSWriter once to get rid of prologue
    JSWriter writer = JSWriter.getWriterFor( shell );
    Fixture.fakeResponseWriter();
    writer.set( "foo", "bar" );

    // set property value to an empty array
    Fixture.fakeResponseWriter();
    writer.set( "buttons", "buttons", new Widget[ 0 ], null );
    assertEquals( "w.setButtons( [] );",
                  Fixture.getAllMarkup() );

    // set property value to an array that contains one item
    Fixture.fakeResponseWriter();
    writer.set( "buttons", "buttons", new Object[] { widget1 }, null );
    assertEquals( "w.setButtons( [wm.findWidgetById( \"w3\" ) ] );",
                  Fixture.getAllMarkup() );

    // set property value to an array that contains two items
    Fixture.fakeResponseWriter();
    writer.set( "buttons", "buttons", new Object[] { widget1, widget2 }, null );
    String expected
      = "w.setButtons( [wm.findWidgetById( \"w3\" ),"
      + "wm.findWidgetById( \"w4\" ) ] );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testSetWithPropertyChainAndObjectArray() throws Exception {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.set( new String[] { "prop1", "prop2" },
                new Object[] { new JSVar( "var1" ), new JSVar( "var2" ) } );
    String expected
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
      + "var w = wm.findWidgetById( \"w2\" );"
      + "w.getProp1().setProp2( var1, var2 );";
    assertEquals( expected, Fixture.getAllMarkup() );

  }

  public void testSetWithStringArray() throws Exception {
    Display display = new Display();
    Shell shell = new Shell( display );
    Widget widget = new Button( shell, SWT.NONE );
    JSWriter writer = JSWriter.getWriterFor( widget );

    // call JSWriter once to get rid of prologue
    Fixture.markInitialized( widget );
    Fixture.fakeResponseWriter();
    writer.set( "foo", "bar" );
    
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( "stringArray", new String[] { "a", "b", "c" } );
    String[] newValue = new String[] { "c", "b", "a" };
    writer.set( "stringArray", "stringArray", newValue, null );
    String expected = "w.setStringArray( [ \"c\", \"b\", \"a\" ] );";
    assertEquals( expected, Fixture.getAllMarkup() );

    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    String[] value = new String[] { "c", "b", "a" };
    adapter.preserve( "stringArray", value );
    writer.set( "stringArray", "stringArray", value, null );
    assertEquals( "", Fixture.getAllMarkup() );
  }

  public void testSetChangedString() throws Exception {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.text );
    shell.text.setText( "" );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    writer.set( "text", "value", shell.text.getText() );
    String expected
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
      + "var w = wm.findWidgetById( \"w4\" );"
      + "w.setValue( \"\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell.text );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    writer.set( "text", "value", shell.text.getText() );
    assertEquals( "", Fixture.getAllMarkup() );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    shell.text.setText( "hello" );
    writer.set( "text", "value", shell.text.getText() );
    assertEquals( "w.setValue( \"hello\" );", Fixture.getAllMarkup() );
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    writer.set( "text", "value", shell.text.getText() );
    assertEquals( "", Fixture.getAllMarkup() );
  }

  public void testCall() throws Exception {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.call( "function1", null );
    String expected
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
      + "var w = wm.findWidgetById( \"w2\" );"
      + "w.function1();";
    assertEquals( expected, Fixture.getAllMarkup() );
    writer.call( "function2", null );
    expected += "w.function2();";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testCallFieldAssignment() {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    Fixture.fakeResponseWriter();
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.callFieldAssignment( new JSVar( "w" ), "field", "value" );
    String expected = "w.field = value;";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testCallStatic() throws IOException {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    Fixture.fakeResponseWriter();
    writer.callStatic( "doSomethingStatic", null );
    String expected
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
      + "doSomethingStatic();";
    assertEquals( expected, Fixture.getAllMarkup() );
    Fixture.fakeResponseWriter();
    Object[] args = new Object[]{
      shell.button
    };
    writer.callStatic( "doSomethingStaticWithButton", args );
    expected = "doSomethingStaticWithButton( wm.findWidgetById( \"w2\" ) );";
    assertEquals( expected, Fixture.getAllMarkup() );
    writer.call( "test", null ); // creates widget ref
    Fixture.fakeResponseWriter();
    args = new Object[]{
      shell.button
    };
    writer.callStatic( "doSomethingStaticWithButtonRef", args );
    expected = "doSomethingStaticWithButtonRef( w );";
    assertEquals( expected, Fixture.getAllMarkup() );
    Fixture.fakeResponseWriter();
    args = new Object[] { new String[] { "xyz" } };
    writer.callStatic( "doSomething", args );
    expected = "doSomething( [ \"xyz\" ] );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testTargetedCallForWidget() throws IOException {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    Object[] args = new Object[]{
      shell
    };
    writer.call( shell, "doSomethingWithShell", args );
    String expected
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
      + "var t = wm.findWidgetById( \"w2\" );"
      + "t.doSomethingWithShell( wm.findWidgetById( \"w2\" ) );";
    assertEquals( expected, Fixture.getAllMarkup() );
    Fixture.fakeResponseWriter();
    writer.call( shell, "doSomething", new Object[ 0 ] );
    expected = "var t = wm.findWidgetById( \"w2\" );t.doSomething();";
    assertEquals( expected, Fixture.getAllMarkup() );
    Fixture.fakeResponseWriter();
    writer.call( shell, "doSomething", new Object[]{
      new Integer( 123 )
    } );
    expected = "var t = wm.findWidgetById( \"w2\" );t.doSomething( 123 );";
    assertEquals( expected, Fixture.getAllMarkup() );
    Fixture.fakeResponseWriter();
    args = new Object[] { "abc", new Integer( 123 ) };
    writer.call( shell, "doSomething", args );
    expected =   "var t = wm.findWidgetById( \"w2\" );"
               + "t.doSomething( \"abc\", 123 );";
    assertEquals( expected, Fixture.getAllMarkup() );
    Fixture.fakeResponseWriter();
    args = new Object[] { shell, shell.combo.getItems() };
    writer.call( shell, "doSomething", args );
    expected =   "var t = wm.findWidgetById( \"w2\" );"
               + "t.doSomething( wm.findWidgetById( \"w2\" ), "
               + "[ \"test\", \"test2\", \"test3\" ] );";
    assertEquals( expected, Fixture.getAllMarkup() );
    Fixture.fakeResponseWriter();
    args = new Object[] { shell, new String[ 0 ] };
    writer.call( shell, "doSomething", args );
    expected =   "var t = wm.findWidgetById( \"w2\" );"
               + "t.doSomething( wm.findWidgetById( \"w2\" ), "
               + "[] );";
    assertEquals( expected, Fixture.getAllMarkup() );
    Fixture.fakeResponseWriter();
    writer.call( shell, "doSomething", null );
    expected = "var t = wm.findWidgetById( \"w2\" );t.doSomething();";
    assertEquals( expected, Fixture.getAllMarkup() );
    Fixture.fakeResponseWriter();
    args = new Object[] { null };
    writer.call( shell, "doSomething", args );
    expected =   "var t = wm.findWidgetById( \"w2\" );"
               + "t.doSomething( null );";
    assertEquals( expected, Fixture.getAllMarkup() );
    Fixture.fakeResponseWriter();
    writer.call( shell.button, "buttonFunction", null );
    expected = "var w = wm.findWidgetById( \"w3\" );w.buttonFunction();";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testCallWithCharacterParam() throws IOException {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    Object[] args = new Object[] { new Character( 'A' ) };
    writer.call( new JSVar( "a" ), "setChar", args );
    String expected
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
      + "a.setChar( \"A\" );";
    assertEquals( expected, Fixture.getAllMarkup() );

  }

  public void testTargetedCallForJSVar() throws IOException {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    Fixture.fakeResponseWriter();
    writer.call( new JSVar( "a" ), "doSomething", null );
    String expected
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
      + "a.doSomething();";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testAddListener() throws Exception {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    // add listener directly to the widget
    writer.addListener( "execute", "buttonExecuted1" );
    String expected
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
      + "var w = wm.findWidgetById( \"w2\" );"
      + "w.addEventListener( \"execute\", buttonExecuted1 );";
    assertEquals( expected, Fixture.getAllMarkup() );
    writer.addListener( "execute", "buttonExecuted2" );
    expected += "w.addEventListener( \"execute\", buttonExecuted2 );";
    assertEquals( expected, Fixture.getAllMarkup() );
    // add listener of a property object of the widget
    writer.addListener( "prop", "type", "jshandler" );
    expected += "w.getProp().addEventListener( \"type\", jshandler );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testRemoveListener() throws Exception {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.button );

    // add listener directly to the widget
    writer.removeListener( "execute", "buttonExecuted1" );
    String expected
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
      + "var w = wm.findWidgetById( \"w2\" );"
      + "w.removeEventListener( \"execute\", buttonExecuted1 );";
    assertEquals( expected, Fixture.getAllMarkup() );
    writer.removeListener( "execute", "buttonExecuted2" );
    expected += "w.removeEventListener( \"execute\", buttonExecuted2 );";
    assertEquals( expected, Fixture.getAllMarkup() );

    // add listener of a property object of the widget
    writer.removeListener( "prop", "type", "jshandler" );
    expected += "w.getProp().removeEventListener( \"type\", jshandler );";
    assertEquals( expected, Fixture.getAllMarkup() );
    
    // ensure instance listener hack of [rh]:
    // JSWriter#removeListener(String,String,String)
    writer.removeListener( "type", "this.jshandler" );
    expected += "w.removeEventListener( \"type\", w.jshandler, w );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testUpdateListenerAction() throws Exception {
    // set up widget hierarchy for testing
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Button button = new Button( shell, SWT.PUSH );
    // Test initial rendering with no listeners
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( display );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    JSWriter writer = JSWriter.getWriterFor( button );
    JSListenerInfo jsListenerInfo = new JSListenerInfo( "execute",
                                                        "selectedEvent",
                                                        JSListenerType.ACTION );
    writer.updateListener( jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( button ) );
    assertEquals( "", Fixture.getAllMarkup() );
    SelectionListener selectionListener = new SelectionAdapter() {};
    // Test initial rendering with listeners
    button.addSelectionListener( selectionListener );
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    writer.updateListener( jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( button ) );
    String expected
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
      + "var w = wm.findWidgetById( \"w3\" );"
      + "w.addEventListener( \"execute\", selectedEvent );";
    assertEquals( expected, Fixture.getAllMarkup() );
    // Test adding the first listeners
    Fixture.fakeResponseWriter();
    button.removeSelectionListener( selectionListener );
    Fixture.markInitialized( button );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    button.addSelectionListener( selectionListener );
    writer.updateListener( jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( button ) );
    // Test adding a further listener: leads to no markup
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    SelectionListener selectionListener2 = new SelectionAdapter() {};
    button.addSelectionListener( selectionListener2 );
    writer.updateListener( jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( button ) );
    assertEquals( "", Fixture.getAllMarkup() );
    // Test removing all the above added listener
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    button.removeSelectionListener( selectionListener );
    button.removeSelectionListener( selectionListener2 );
    writer.updateListener( jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( button ) );
    assertEquals( "w.removeEventListener( \"execute\", selectedEvent );",
                  Fixture.getAllMarkup() );
    // Test that no changes to listeners do not cause any additional markup
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    writer.updateListener( jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( button ) );
    assertEquals( "", Fixture.getAllMarkup() );
  }

  public void testUpdateListenerStateAndActionWithStateFirst() throws Exception
  {
    // set up widget hierarchy for testing
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Button checkBox = new Button( shell, SWT.CHECK );
    // Test initial rendering with no action listeners
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( display );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    JSWriter writer = JSWriter.getWriterFor( checkBox );
    JSListenerInfo jsListenerInfo
      = new JSListenerInfo( "type",
                            "event",
                            JSListenerType.STATE_AND_ACTION );
    writer.updateListener( jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( checkBox ) );
    String expected
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
      + "var w = wm.findWidgetById( \"w3\" );"
      + "w.addEventListener( \"type\", event );";
    assertEquals( expected, Fixture.getAllMarkup() );
    // Test rendering with action listener added
    WidgetAdapter adapter = ( WidgetAdapter )WidgetUtil.getAdapter( checkBox );
    adapter.setInitialized( true );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    SelectionListener selectionListener = new SelectionAdapter() {};
    SelectionEvent.addListener( checkBox, selectionListener );
    Fixture.fakeResponseWriter();
    writer.updateListener( jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( checkBox ) );
    expected =   "w.removeEventListener( \"type\", event );"
               + "w.addEventListener( \"type\", eventAction );";
    assertEquals( expected, Fixture.getAllMarkup() );
    // Test adding a further listener: leads to no markup
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    SelectionListener selectionListener2 = new SelectionAdapter() {};
    SelectionEvent.addListener( checkBox, selectionListener2 );
    writer.updateListener( jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( checkBox ) );
    assertEquals( "", Fixture.getAllMarkup() );
    // Test removing all the above added listener
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    SelectionEvent.removeListener( checkBox, selectionListener );
    SelectionEvent.removeListener( checkBox, selectionListener2 );
    writer.updateListener( jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( checkBox ) );
    expected = "w.removeEventListener( \"type\", eventAction );"
             + "w.addEventListener( \"type\", event );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testUpdateListenerStateAndActionWithActionFirst()
    throws Exception
  {
    // set up widget hierarchy for testing
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Button checkBox = new Button( shell, SWT.CHECK );
    // Test initial rendering with action listeners
    Fixture.markInitialized( display );
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    SelectionListener selectionListener = new SelectionAdapter() {};
    SelectionEvent.addListener( checkBox, selectionListener );
    JSWriter writer = JSWriter.getWriterFor( checkBox );
    JSListenerInfo jsListenerInfo
      = new JSListenerInfo( "type",
                            "event",
                            JSListenerType.STATE_AND_ACTION );
    writer.updateListener( jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( checkBox ) );
    String expected
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
      + "var w = wm.findWidgetById( \"w3\" );"
      + "w.addEventListener( \"type\", eventAction );";
    assertEquals( expected, Fixture.getAllMarkup() );
    // Test adding a further listener: leads to no markup
    WidgetAdapter adapter = ( WidgetAdapter )WidgetUtil.getAdapter( checkBox );
    adapter.setInitialized( true );
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    SelectionListener selectionListener2 = new SelectionAdapter() {};
    SelectionEvent.addListener( checkBox, selectionListener2 );
    writer.updateListener( jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( checkBox ) );
    assertEquals( "", Fixture.getAllMarkup() );
    // Test removing all the above added listener
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    SelectionEvent.removeListener( checkBox, selectionListener );
    SelectionEvent.removeListener( checkBox, selectionListener2 );
    writer.updateListener( jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( checkBox ) );
    expected = "w.removeEventListener( \"type\", eventAction );"
             + "w.addEventListener( \"type\", event );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testUpdateListenerOnPropertyAction() throws Exception {
    // set up widget hierarchy for testing
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE);
    Button button = new Button( shell, SWT.NONE );

    // Test initial rendering with no listeners
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( display );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    JSWriter writer = JSWriter.getWriterFor( button );
    JSListenerInfo jsListenerInfo
      = new JSListenerInfo( "execute", "selectedEvent", JSListenerType.ACTION );
    writer.updateListener( PROPERTY_NAME,
                           jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( button ) );
    assertEquals( "", Fixture.getAllMarkup() );

    SelectionListener selectionListener = new SelectionAdapter() {};
    // Test initial rendering with listeners
    button.addSelectionListener( selectionListener );
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    writer.updateListener( PROPERTY_NAME,
                           jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( button ) );
    String expected
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
      + "var w = wm.findWidgetById( \"w3\" );"
      + "w.getPropertyName().addEventListener( \"execute\", selectedEvent );";
    assertEquals( expected, Fixture.getAllMarkup() );

    // Test adding the first listeners
    Fixture.fakeResponseWriter();
    button.removeSelectionListener( selectionListener );
    Fixture.markInitialized( button );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    button.addSelectionListener( selectionListener );
    writer.updateListener( PROPERTY_NAME,
                           jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( button ) );

    // Test adding a further listener: leads to no markup
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    SelectionListener selectionListener2 = new SelectionAdapter() {};
    button.addSelectionListener( selectionListener2 );
    writer.updateListener( PROPERTY_NAME,
                           jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( button ) );
    assertEquals( "", Fixture.getAllMarkup() );

    // Test removing all the above added listener
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    button.removeSelectionListener( selectionListener );
    button.removeSelectionListener( selectionListener2 );
    writer.updateListener( PROPERTY_NAME,
                           jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( button ) );
    expected
      = "w.getPropertyName().removeEventListener"
      + "( \"execute\", selectedEvent );";
    assertEquals( expected,
                  Fixture.getAllMarkup() );

    // Test that no changes to listeners do not cause any additional markup
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    writer.updateListener( PROPERTY_NAME,
                           jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( button ) );
    assertEquals( "", Fixture.getAllMarkup() );
  }

  public void testUpdateListenerOnPropertyStateAndAction() throws IOException {
    // set up widget hierarchy for testing
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Button checkBox = new Button( shell, SWT.CHECK );

    // Test initial rendering with no action listeners
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( display );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    JSWriter writer = JSWriter.getWriterFor( checkBox );
    JSListenerInfo jsListenerInfo
      = new JSListenerInfo( "type", "event", JSListenerType.STATE_AND_ACTION );
    writer.updateListener( PROPERTY_NAME,
                           jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( checkBox ) );
    String expected
      =   "var wm = org.eclipse.swt.WidgetManager.getInstance();"
        + "var w = wm.findWidgetById( \"w3\" );"
        + "w.getPropertyName().addEventListener( \"type\", event );";
    assertEquals( expected, Fixture.getAllMarkup() );

    // Test rendering with action listener added
    WidgetAdapter adapter = ( WidgetAdapter )WidgetUtil.getAdapter( checkBox );
    adapter.setInitialized( true );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    SelectionListener selectionListener = new SelectionAdapter() {};
    SelectionEvent.addListener( checkBox, selectionListener );
    Fixture.fakeResponseWriter();
    writer.updateListener( PROPERTY_NAME,
                           jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( checkBox ) );
    expected
      = "w.getPropertyName().removeEventListener( \"type\", event );"
      + "w.getPropertyName().addEventListener( \"type\", eventAction );";
    assertEquals( expected, Fixture.getAllMarkup() );

    // Test adding a further listener: leads to no markup
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    SelectionListener selectionListener2 = new SelectionAdapter() {};
    SelectionEvent.addListener( checkBox, selectionListener2 );
    writer.updateListener( PROPERTY_NAME,
                           jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( checkBox ) );
    assertEquals( "", Fixture.getAllMarkup() );

    // Test removing all the above added listener
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    SelectionEvent.removeListener( checkBox, selectionListener );
    SelectionEvent.removeListener( checkBox, selectionListener2 );
    writer.updateListener( PROPERTY_NAME,
                           jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( checkBox ) );
    expected
      = "w.getPropertyName().removeEventListener( \"type\", eventAction );"
      + "w.getPropertyName().addEventListener( \"type\", event );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testDispose() throws Exception {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.dispose();
    String expected
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
      + "wm.dispose( \"w2\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }
  
  public void testDisposeWithDisposedControl() throws IOException {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    shell.button.dispose();
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.dispose();
    String expected
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
      + "wm.dispose( \"w2\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testWigetRef() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Widget widget1 = new Label( shell, SWT.NONE );
    Widget widget2 = new Label( shell, SWT.NONE );
    JSWriter writer1 = JSWriter.getWriterFor( widget1 );
    JSWriter writer2 = JSWriter.getWriterFor( widget2 );

    Fixture.fakeResponseWriter();
    writer1.set( "visible", false );
    String findWidget = "findWidgetById";
    assertTrue( Fixture.getAllMarkup().indexOf( findWidget ) != -1 );

    Fixture.fakeResponseWriter();
    writer1.set( "visible", true );
    assertTrue( Fixture.getAllMarkup().indexOf( findWidget ) == -1 );

    Fixture.fakeResponseWriter();
    writer2.set( "visible", true );
    assertTrue( Fixture.getAllMarkup().indexOf( findWidget ) != -1 );

    Fixture.fakeResponseWriter();
    writer2.set( "visible", true );
    assertTrue( Fixture.getAllMarkup().indexOf( findWidget ) == -1 );

    Fixture.fakeResponseWriter();
    writer1.set( "visible", true );
    assertTrue( Fixture.getAllMarkup().indexOf( findWidget ) != -1 );
  }

  // see bug 195735: Widget disposal causes NullPointerException
  // https://bugs.eclipse.org/bugs/show_bug.cgi?id=195735
  public void testWidgetDisposal() throws Exception {
    // Run requests to initialize the 'system'
    Fixture.fakeNewRequest();
    RWTFactory.getEntryPointManager().register( EntryPointManager.DEFAULT, WidgetDisposalEntryPoint.class );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    lifeCycle.execute();
    Fixture.fakeNewRequest();
    String dispId = WidgetDisposalEntryPoint.dispId;
    Fixture.fakeRequestParam( RequestParams.UIROOT, dispId );
    lifeCycle.execute();
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, dispId );
    Fixture.fakeRequestParam( RequestParams.UIROOT, dispId );
    String buttonId = WidgetDisposalEntryPoint.buttonId;
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    lifeCycle.execute();
  }
  
  public void testEscapeString() throws IOException {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Label label = new Label( shell , SWT.NONE );
    JSWriter writer = JSWriter.getWriterFor( label  );
    // Run once, thus JSWriter will outut the prologue which is not under test
    writer.set( "dummy", false );

    Fixture.fakeResponseWriter();
    writer.set( "html", "abc" );
    assertEquals( "w.setHtml( \"abc\" );", Fixture.getAllMarkup() );

    Fixture.fakeResponseWriter();
    writer.set( "html", "a\"bc" );
    assertEquals( "w.setHtml( \"a\\\"bc\" );", Fixture.getAllMarkup() );

    Fixture.fakeResponseWriter();
    writer.set( "html", "\"" );
    assertEquals( "w.setHtml( \"\\\"\" );", Fixture.getAllMarkup() );

    Fixture.fakeResponseWriter();
    writer.set( "html", "back\\slash" );
    assertEquals( "w.setHtml( \"back\\\\slash\" );", Fixture.getAllMarkup() );

    Fixture.fakeResponseWriter();
    writer.set( "html", "\\\"" );
    assertEquals( "w.setHtml( \"\\\\\\\"\" );", Fixture.getAllMarkup() );

    Fixture.fakeResponseWriter();
    writer.set( "html", "\n" );
    assertEquals( "w.setHtml( \"\\n\" );", Fixture.getAllMarkup() );

    Fixture.fakeResponseWriter();
    writer.set( "html", "\r\n" );
    assertEquals( "w.setHtml( \"\\n\" );", Fixture.getAllMarkup() );

    Fixture.fakeResponseWriter();
    writer.set( "html", "\r\n\n" );
    assertEquals( "w.setHtml( \"\\n\\n\" );", Fixture.getAllMarkup() );

    Fixture.fakeResponseWriter();
    writer.set( "html", "\r" );
    assertEquals( "w.setHtml( \"\\n\" );", Fixture.getAllMarkup() );

    Fixture.fakeResponseWriter();
    writer.set( "html", "\r\n\r" );
    assertEquals( "w.setHtml( \"\\n\\n\" );", Fixture.getAllMarkup() );
  }
  
  public void testVarAssignment() {
    Display display = new Display();
    Widget widget = new Shell( display );
    JSWriter writer = JSWriter.getWriterFor( widget );
    Fixture.fakeResponseWriter();
    writer.varAssignment( new JSVar( "foo" ), "getFoo" );
    String expected
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
      + "var w = wm.findWidgetById( \"w2\" );"
    	+	"var foo = w.getFoo();";
    assertEquals( expected, Fixture.getAllMarkup() );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
    Fixture.fakePhase( PhaseId.RENDER );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
