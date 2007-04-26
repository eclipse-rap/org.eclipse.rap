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

package org.eclipse.swt.lifecycle;

import java.io.IOException;
import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;
import com.w4t.Fixture;
import com.w4t.util.browser.Default;
import com.w4t.util.browser.Ie6;

public class JSWriter_Test extends TestCase {

  private static final String PROPERTY_NAME = "propertyName";

  private final class TestShell extends Shell {

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

  public void testUniqueWriterPerRequest() throws Exception {
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
        + "wm.add( w, \"w1\", true );"
        + "wm.setParent( w, \"w2\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
    // ensure that the WidgetManager, once initialized, is not initialized
    // twice
    writer = JSWriter.getWriterFor( shell.button );
    writer.newWidget( "qx.ui.form.Button" );
    expected +=   "var w = new qx.ui.form.Button();"
                + "wm.add( w, \"w1\", true );"
                + "wm.setParent( w, \"w2\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
    // ensure that obtaining the widget reference (var w =) is only rendered
    // once
    writer.set( "Width", 5 );
    expected += "w.setWidth( 5 );";
    assertEquals( expected, Fixture.getAllMarkup() );
    display.dispose();
  }

  public void testNewWidget() throws Exception {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.newWidget( "qx.ui.form.Button" );
    String expected
      =   "var wm = org.eclipse.swt.WidgetManager.getInstance();"
        + "var w = new qx.ui.form.Button();"
        + "wm.add( w, \"w1\", true );"
        + "wm.setParent( w, \"w2\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
    // Ensures that the "widget reference is set"-flag is set
    Fixture.fakeResponseWriter();
    writer.set( "Text", "xyz" );
    expected = "w.setText( \"xyz\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
    display.dispose();
  }

  public void testNewWidgetWithParams() throws Exception {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.newWidget( "qx.ui.form.Button", new Object[]{ "abc" } );
    String expected
        = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
        + "var w = new qx.ui.form.Button( \"abc\" );"
        + "wm.add( w, \"w1\", true );"
        + "wm.setParent( w, \"w2\" );";
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
    expected
      = "var w = new TreeItem();"
      + "wm.add( w, \"w3\", false );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testSetParent() throws IOException {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    String shellId = WidgetUtil.getId( shell );
    String buttonId = WidgetUtil.getId( shell.button );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.setParent( shell.button );
    String expected
      =   "var wm = org.eclipse.swt.WidgetManager.getInstance();"
        + "wm.setParent( wm.findWidgetById( \""
        + buttonId  
        + "\" ), \""
        + shellId
        + "\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
    
    Fixture.fakeResponseWriter();
    writer.newWidget( "xyz" );
    
    Fixture.fakeResponseWriter();
    writer.setParent( shell.button );
    expected = "wm.setParent( w, \"" + shellId + "\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
    
    Fixture.fakeResponseWriter();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( shell.button );
    adapter.setJSParent( "trallala" );
    writer.setParent( shell.button );
    expected = "wm.setParent( w, \"trallala\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
    
    Fixture.fakeResponseWriter();
    writer.setParent( "xyz" );
    expected = "wm.setParent( w, \"xyz\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testSetString() throws Exception {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.set( "text", "xyz" );
    String expected
      =   "var wm = org.eclipse.swt.WidgetManager.getInstance();"
        + "var w = wm.findWidgetById( \"w1\" );w.setText( \"xyz\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
    display.dispose();
  }

  public void testSetInt() throws Exception {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    // use a high value to test separator avoidance...
    writer.set( "width", 20000000 );
    String expected
      =   "var wm = org.eclipse.swt.WidgetManager.getInstance();"
        + "var w = wm.findWidgetById( \"w1\" );w.setWidth( 20000000 );";
    assertEquals( expected, Fixture.getAllMarkup() );
    display.dispose();
  }

  public void testSetBoolean() throws Exception {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.set( "allowStretchY", true );
    String expected
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
      + "var w = wm.findWidgetById( \"w1\" );w.setAllowStretchY( true );";
    assertEquals( expected, Fixture.getAllMarkup() );
    display.dispose();
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
      + "var w = wm.findWidgetById( \"w1\" );"
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
      + "var w = wm.findWidgetById( \"w1\" );"
      + "w.setIntegerValues( 1, 2 );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testSetIntArray() throws Exception {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.set( "intValues", new int[] { 1, 2 } );
    String expected
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
      + "var w = wm.findWidgetById( \"w1\" );"
      + "w.setIntValues( 1, 2 );";
    assertEquals( expected, Fixture.getAllMarkup() );
    display.dispose();
  }

  public void testSetBooleanArray() throws Exception {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.set( "boolValues", new boolean[] { true, false } );
    String expected 
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
      + "var w = wm.findWidgetById( \"w1\" );"
      + "w.setBoolValues( true, false );";
    assertEquals( expected, Fixture.getAllMarkup() );
    display.dispose();
  }
  
  public void testSetWidgetArray() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display );
    Widget widget1 = new Button( shell, SWT.PUSH );
    Widget widget2 = new Button( shell, SWT.PUSH );
    
    // call JSWriter once to get rid of prologue 
    JSWriter writer = JSWriter.getWriterFor( shell );
    Fixture.fakeBrowser( new Default( true, true ) );
    Fixture.fakeResponseWriter();
    writer.set( "foo", "bar" );

    // set property value to an empty array 
    Fixture.fakeBrowser( new Default( true, true ) );
    Fixture.fakeResponseWriter();
    writer.set( "buttons", "buttons", new Widget[ 0 ], null );
    assertEquals( "w.setButtons( [] );", 
                  Fixture.getAllMarkup() );
    
    // set property value to an array that contains one item
    Fixture.fakeBrowser( new Default( true, true ) );
    Fixture.fakeResponseWriter();
    writer.set( "buttons", "buttons", new Object[] { widget1 }, null );
    assertEquals( "w.setButtons( [wm.findWidgetById( \"w2\" ) ] );", 
                  Fixture.getAllMarkup() );
    
    // set property value to an array that contains two items
    Fixture.fakeBrowser( new Default( true, true ) );
    Fixture.fakeResponseWriter();
    writer.set( "buttons", "buttons", new Object[] { widget1, widget2 }, null );
    String expected 
      = "w.setButtons( [wm.findWidgetById( \"w2\" )," 
      + "wm.findWidgetById( \"w3\" ) ] );";
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
      + "var w = wm.findWidgetById( \"w1\" );"
      + "w.getProp1().setProp2( var1, var2 );";
    assertEquals( expected, Fixture.getAllMarkup() );
    display.dispose();
    
  }
  
  public void testSetChangedString() throws Exception {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.text );
    shell.text.setText( "" );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    writer.set( "text", "value", shell.text.getText() );
    String expected
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
      + "var w = wm.findWidgetById( \"w4\" );"
      + "w.setValue( \"\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( display );
    RWTFixture.markInitialized( shell.text );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    writer.set( "text", "value", shell.text.getText() );
    assertEquals( "", Fixture.getAllMarkup() );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    shell.text.setText( "hello" );
    writer.set( "text", "value", shell.text.getText() );
    assertEquals( "w.setValue( \"hello\" );", Fixture.getAllMarkup() );
    Fixture.fakeResponseWriter();
    RWTFixture.preserveWidgets();
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
      + "var w = wm.findWidgetById( \"w1\" );"
      + "w.function1();";
    assertEquals( expected, Fixture.getAllMarkup() );
    writer.call( "function2", null );
    expected += "w.function2();";
    assertEquals( expected, Fixture.getAllMarkup() );
    display.dispose();
  }

  public void testCallFieldAssignment() throws IOException {
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
    expected = "doSomethingStaticWithButton( wm.findWidgetById( \"w1\" ) );";
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
      + "var t = wm.findWidgetById( \"w1\" );"
      + "t.doSomethingWithShell( wm.findWidgetById( \"w1\" ) );";
    assertEquals( expected, Fixture.getAllMarkup() );
    Fixture.fakeResponseWriter();
    writer.call( shell, "doSomething", new Object[ 0 ] );
    expected = "var t = wm.findWidgetById( \"w1\" );t.doSomething();";
    assertEquals( expected, Fixture.getAllMarkup() );
    Fixture.fakeResponseWriter();
    writer.call( shell, "doSomething", new Object[]{
      new Integer( 123 )
    } );
    expected = "var t = wm.findWidgetById( \"w1\" );t.doSomething( 123 );";
    assertEquals( expected, Fixture.getAllMarkup() );
    Fixture.fakeResponseWriter();
    args = new Object[] { "abc", new Integer( 123 ) };
    writer.call( shell, "doSomething", args );
    expected =   "var t = wm.findWidgetById( \"w1\" );"
               + "t.doSomething( \"abc\", 123 );";
    assertEquals( expected, Fixture.getAllMarkup() );
    Fixture.fakeResponseWriter();
    args = new Object[] { shell, shell.combo.getItems() };
    writer.call( shell, "doSomething", args );
    expected =   "var t = wm.findWidgetById( \"w1\" );"
               + "t.doSomething( wm.findWidgetById( \"w1\" ), "
               + "[ \"test\", \"test2\", \"test3\" ] );";
    assertEquals( expected, Fixture.getAllMarkup() );
    Fixture.fakeResponseWriter();
    args = new Object[] { shell, new String[ 0 ] };
    writer.call( shell, "doSomething", args );
    expected =   "var t = wm.findWidgetById( \"w1\" );"
               + "t.doSomething( wm.findWidgetById( \"w1\" ), "
               + "[] );";
    assertEquals( expected, Fixture.getAllMarkup() );
    Fixture.fakeResponseWriter();
    writer.call( shell, "doSomething", null );
    expected = "var t = wm.findWidgetById( \"w1\" );t.doSomething();";
    assertEquals( expected, Fixture.getAllMarkup() );
    Fixture.fakeResponseWriter();
    args = new Object[] { null };
    writer.call( shell, "doSomething", args );
    expected =   "var t = wm.findWidgetById( \"w1\" );"
               + "t.doSomething( null );";
    assertEquals( expected, Fixture.getAllMarkup() );
    Fixture.fakeResponseWriter();
    writer.call( shell.button, "buttonFunction", null );
    expected = "var w = wm.findWidgetById( \"w2\" );w.buttonFunction();";
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
      + "var w = wm.findWidgetById( \"w1\" );"
      + "w.addEventListener( \"execute\", buttonExecuted1 );";
    assertEquals( expected, Fixture.getAllMarkup() );
    writer.addListener( "execute", "buttonExecuted2" );
    expected += "w.addEventListener( \"execute\", buttonExecuted2 );";
    assertEquals( expected, Fixture.getAllMarkup() );
    // add listener of a property object of the widget
    writer.addListener( "prop", "type", "jshandler" );
    expected += "w.getProp().addEventListener( \"type\", jshandler );";
    assertEquals( expected, Fixture.getAllMarkup() );
    
    display.dispose();
  }
  
  public void testRemoveListener() throws Exception {
    Display display = new Display();
    TestShell shell = new TestShell( display );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    
    // add listener directly to the widget 
    writer.removeListener( "execute", "buttonExecuted1" );
    String expected 
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();" 
      + "var w = wm.findWidgetById( \"w1\" );" 
      + "w.removeEventListener( \"execute\", buttonExecuted1 );";
    assertEquals( expected, Fixture.getAllMarkup() );
    writer.removeListener( "execute", "buttonExecuted2" );
    expected += "w.removeEventListener( \"execute\", buttonExecuted2 );";
    assertEquals( expected, Fixture.getAllMarkup() );
    
    // add listener of a property object of the widget
    writer.removeListener( "prop", "type", "jshandler" );
    expected += "w.getProp().removeEventListener( \"type\", jshandler );";
    assertEquals( expected, Fixture.getAllMarkup() );
    
    display.dispose();
  }

  public void testUpdateListenerAction() throws Exception {
    // set up widget hierarchy for testing
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Button button = new Button( shell, SWT.PUSH );
    // Test initial rendering with no listeners
    Fixture.fakeBrowser( new Ie6( true, true ) );
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( display );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
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
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
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
    RWTFixture.markInitialized( button );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    button.addSelectionListener( selectionListener );
    writer.updateListener( jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( button ) );
    // Test adding a further listener: leads to no markup
    Fixture.fakeResponseWriter();
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    SelectionListener selectionListener2 = new SelectionAdapter() {};
    button.addSelectionListener( selectionListener2 );
    writer.updateListener( jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( button ) );
    assertEquals( "", Fixture.getAllMarkup() );
    // Test removing all the above added listener
    Fixture.fakeResponseWriter();
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    button.removeSelectionListener( selectionListener );
    button.removeSelectionListener( selectionListener2 );
    writer.updateListener( jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( button ) );
    assertEquals( "w.removeEventListener( \"execute\", selectedEvent );",
                  Fixture.getAllMarkup() );
    // Test that no changes to listeners do not cause any additional markup
    Fixture.fakeResponseWriter();
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    writer.updateListener( jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( button ) );
    assertEquals( "", Fixture.getAllMarkup() );
  }

  public void testUpdateListenerStateAndActionWithStateFirst() throws Exception
  {
    // set up widget hierarchy for testing
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    TabFolder folder = new TabFolder( shell, SWT.NONE );
    TabItem item = new TabItem( folder, SWT.NONE );
    // Test initial rendering with no action listeners
    Fixture.fakeBrowser( new Ie6( true, true ) );
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( display );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    JSWriter writer = JSWriter.getWriterFor( item );
    JSListenerInfo jsListenerInfo 
      = new JSListenerInfo( "type",
                            "event",
                            JSListenerType.STATE_AND_ACTION );
    writer.updateListener( jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( folder ) );
    String expected
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
      + "var w = wm.findWidgetById( \"w4\" );"
      + "w.addEventListener( \"type\", event );";
    assertEquals( expected, Fixture.getAllMarkup() );
    // Test rendering with action listener added
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    adapter.setInitialized( true );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    SelectionListener selectionListener = new SelectionAdapter() {};
    SelectionEvent.addListener( folder, selectionListener );
    Fixture.fakeResponseWriter();
    writer.updateListener( jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( folder ) );
    expected =   "w.removeEventListener( \"type\", event );"
               + "w.addEventListener( \"type\", eventAction );";
    assertEquals( expected, Fixture.getAllMarkup() );
    // Test adding a further listener: leads to no markup
    Fixture.fakeResponseWriter();
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    SelectionListener selectionListener2 = new SelectionAdapter() {};
    SelectionEvent.addListener( folder, selectionListener2 );
    writer.updateListener( jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( folder ) );
    assertEquals( "", Fixture.getAllMarkup() );
    // Test removing all the above added listener
    Fixture.fakeResponseWriter();
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    SelectionEvent.removeListener( folder, selectionListener );
    SelectionEvent.removeListener( folder, selectionListener2 );
    writer.updateListener( jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( folder ) );
    expected = "w.removeEventListener( \"type\", eventAction );"
             + "w.addEventListener( \"type\", event );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }
  
  public void testUpdateListenerStateAndActionWithActionFirst()
    throws Exception
  {
    // set up widget hierarchy for testing
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    TabFolder folder = new TabFolder( shell, SWT.NONE );
    TabItem item = new TabItem( folder, SWT.NONE );
    // Test initial rendering with action listeners
    Fixture.fakeBrowser( new Ie6( true, true ) );
    RWTFixture.markInitialized( display );
    Fixture.fakeResponseWriter();
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    SelectionListener selectionListener = new SelectionAdapter() {};
    SelectionEvent.addListener( folder, selectionListener );
    JSWriter writer = JSWriter.getWriterFor( item );
    JSListenerInfo jsListenerInfo
      = new JSListenerInfo( "type",
                            "event",
                            JSListenerType.STATE_AND_ACTION );
    writer.updateListener( jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( folder ) );
    String expected 
      = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
      + "var w = wm.findWidgetById( \"w4\" );"
      + "w.addEventListener( \"type\", eventAction );";
    assertEquals( expected, Fixture.getAllMarkup() );
    // Test adding a further listener: leads to no markup
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    adapter.setInitialized( true );
    Fixture.fakeResponseWriter();
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    SelectionListener selectionListener2 = new SelectionAdapter() {};
    SelectionEvent.addListener( folder, selectionListener2 );
    writer.updateListener( jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( folder ) );
    assertEquals( "", Fixture.getAllMarkup() );
    // Test removing all the above added listener
    Fixture.fakeResponseWriter();
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    SelectionEvent.removeListener( folder, selectionListener );
    SelectionEvent.removeListener( folder, selectionListener2 );
    writer.updateListener( jsListenerInfo,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( folder ) );
    expected = "w.removeEventListener( \"type\", eventAction );"
             + "w.addEventListener( \"type\", event );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }
  
  public void testUpdateListenerOnPropertyAction() throws Exception {
    // set up widget hierarchy for testing
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE);
    Button button = new Button( shell, SWT.NONE );

    // Test initial rendering with no listeners
    Fixture.fakeBrowser( new Ie6( true, true ) );
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( display );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
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
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
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
    RWTFixture.markInitialized( button );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    button.addSelectionListener( selectionListener );
    writer.updateListener( PROPERTY_NAME,
                           jsListenerInfo, 
                           Props.SELECTION_LISTENERS, 
                           SelectionEvent.hasListener( button ) );

    // Test adding a further listener: leads to no markup
    Fixture.fakeResponseWriter();
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    SelectionListener selectionListener2 = new SelectionAdapter() {};
    button.addSelectionListener( selectionListener2 );
    writer.updateListener( PROPERTY_NAME,
                           jsListenerInfo, 
                           Props.SELECTION_LISTENERS, 
                           SelectionEvent.hasListener( button ) );
    assertEquals( "", Fixture.getAllMarkup() );

    // Test removing all the above added listener
    Fixture.fakeResponseWriter();
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
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
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    writer.updateListener( PROPERTY_NAME,
                           jsListenerInfo, 
                           Props.SELECTION_LISTENERS, 
                           SelectionEvent.hasListener( button ) );
    assertEquals( "", Fixture.getAllMarkup() );
  }

  public void testUpdateListenerOnPropertyStateAndAction() throws IOException {
    // set up widget hierarchy for testing
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    TabFolder folder = new TabFolder( shell, SWT.NONE );
    TabItem item = new TabItem( folder, SWT.NONE );
    
    // Test initial rendering with no action listeners
    Fixture.fakeBrowser( new Ie6( true, true ) );
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( display );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    JSWriter writer = JSWriter.getWriterFor( item );
    JSListenerInfo jsListenerInfo
      = new JSListenerInfo( "type", "event", JSListenerType.STATE_AND_ACTION );
    writer.updateListener( PROPERTY_NAME,
                           jsListenerInfo, 
                           Props.SELECTION_LISTENERS, 
                           SelectionEvent.hasListener( folder ) );
    String expected 
      =   "var wm = org.eclipse.swt.WidgetManager.getInstance();" 
        + "var w = wm.findWidgetById( \"w4\" );" 
        + "w.getPropertyName().addEventListener( \"type\", event );";
    assertEquals( expected, Fixture.getAllMarkup() );
    
    // Test rendering with action listener added
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    adapter.setInitialized( true );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    SelectionListener selectionListener = new SelectionAdapter() {};
    SelectionEvent.addListener( folder, selectionListener );
    Fixture.fakeResponseWriter();
    writer.updateListener( PROPERTY_NAME,
                           jsListenerInfo, 
                           Props.SELECTION_LISTENERS, 
                           SelectionEvent.hasListener( folder ) );
    expected 
      = "w.getPropertyName().removeEventListener( \"type\", event );"
      + "w.getPropertyName().addEventListener( \"type\", eventAction );";
    assertEquals( expected, Fixture.getAllMarkup() );
    
    // Test adding a further listener: leads to no markup
    Fixture.fakeResponseWriter();
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    SelectionListener selectionListener2 = new SelectionAdapter() {};
    SelectionEvent.addListener( folder, selectionListener2 );
    writer.updateListener( PROPERTY_NAME,
                           jsListenerInfo, 
                           Props.SELECTION_LISTENERS, 
                           SelectionEvent.hasListener( folder ) );
    assertEquals( "", Fixture.getAllMarkup() );
    
    // Test removing all the above added listener
    Fixture.fakeResponseWriter();
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    SelectionEvent.removeListener( folder, selectionListener );
    SelectionEvent.removeListener( folder, selectionListener2 );
    writer.updateListener( PROPERTY_NAME,
                           jsListenerInfo, 
                           Props.SELECTION_LISTENERS, 
                           SelectionEvent.hasListener( folder ) );
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
      + "wm.dispose( \"w1\" );";
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
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
    Fixture.fakeResponseWriter();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
