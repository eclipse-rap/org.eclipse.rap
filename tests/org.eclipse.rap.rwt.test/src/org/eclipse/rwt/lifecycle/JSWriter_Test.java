/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.lifecycle;

import java.io.IOException;
import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.internal.widgets.WidgetAdapter;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;


@SuppressWarnings("deprecation")
public class JSWriter_Test extends TestCase {

  private static final String PROPERTY_NAME = "propertyName";
  private static final String PROP_SELECTION_LISTENER = "listener_selection";
  private static final String WM_SETUP_CODE
    = "var wm = org.eclipse.swt.WidgetManager.getInstance();";

  private Display display;
  private TestShell shell;
  private String shellId;
  private String buttonId;
  private String textId;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
    Fixture.fakePhase( PhaseId.RENDER );
    display = new Display();
    shell = new TestShell( display );
    shellId = WidgetUtil.getId( shell );
    buttonId = WidgetUtil.getId( shell.button );
    textId = WidgetUtil.getId( shell.text );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testUniqueWriterPerRequest() {
    JSWriter writer1 = JSWriter.getWriterFor( shell );
    JSWriter writer2 = JSWriter.getWriterFor( shell );

    assertSame( writer1, writer2 );
  }

  public void testInitialization() throws Exception {
    // ensure that the WidgetManager gets initialized
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.newWidget( "rap.Button" );

    String expected =   WM_SETUP_CODE
                      + "var w = new rap.Button();"
                      + "wm.add( w, \"" + buttonId + "\", true );"
                      + "wm.setParent( w, \"" + shellId + "\" );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testInitialization2() throws Exception {
    JSWriter writer1 = JSWriter.getWriterFor( shell.button );
    writer1.newWidget( "rap.Button" );
    // ensure that the WidgetManager, once initialized, is not initialized twice, and
    // ensure that obtaining the widget reference (var w =) is only rendered once
    JSWriter writer2 = JSWriter.getWriterFor( shell.button );
    writer2.set( "Width", 5 );

    String expected =   WM_SETUP_CODE
                      + "var w = new rap.Button();"
                      + "wm.add( w, \"" + buttonId + "\", true );"
                      + "wm.setParent( w, \"" + shellId + "\" );"
                      + "w.setWidth( 5 );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testNewWidget() throws Exception {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.newWidget( "rap.Button" );
    // Ensure that the "widget reference is set"-flag is set
    writer.set( "Text", "xyz" );

    String expected =   WM_SETUP_CODE
                      + "var w = new rap.Button();"
                      + "wm.add( w, \""
                      + buttonId
                      + "\", true );wm.setParent( w, \""
                      + shellId
                      + "\" );"
                      + "w.setText( \"xyz\" );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testNewWidgetWithParams() throws Exception {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    Object[] arrayParam = new Object[] { Graphics.getColor( 255, 0, 0 ) };
    Object[] params = new Object[]{ "abc", arrayParam };
    writer.newWidget( "rap.Button", params );
    // Ensure that the "widget reference is set"-flag is set
    writer.set( "Text", "xyz" );

    String expected = WM_SETUP_CODE
                      + "var w = new rap.Button( \"abc\", [\"#ff0000\" ] );"
                      + "wm.add( w, \""
                      + buttonId
                      + "\", true );wm.setParent( w, \""
                      + shellId
                      + "\" );"
                      + "w.setText( \"xyz\" );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testNewWidgetWithItem() throws Exception {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    JSWriter writer = JSWriter.getWriterFor( item );
    // Ensures that an Item is added and marked as a 'no-control' and setParent is not called
    writer.newWidget( "TreeItem", null );

    String itemId = WidgetUtil.getId( item );
    String expected = WM_SETUP_CODE
                      + "var w = new TreeItem();"
                      + "wm.add( w, \"" + itemId + "\", false );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testResetJSProperty() throws IOException {
    JSWriter writer = JSWriter.getWriterFor( shell );
    writer.reset( "testProperty" );

    String expected = WM_SETUP_CODE
                      + "var w = wm.findWidgetById( \""
                      + shellId
                      + "\" );w.resetTestProperty();";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testResetJSPropertyChain() throws IOException {
    JSWriter writer = JSWriter.getWriterFor( shell );
    writer.reset( new String[] { "labelObject", "testProperty" } );

    String expected = WM_SETUP_CODE
                      + "var w = wm.findWidgetById( \""
                      + shellId
                      + "\" );w.getLabelObject().resetTestProperty();";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testSetParent() throws IOException {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.newWidget( "rap.Button" );
    writer.setParent( "xyz" );

    String expected = "wm.setParent( w, \"xyz\" );";
    assertTrue( ProtocolTestUtil.getMessageScript().contains( expected ) );
  }

  public void testSetString() throws Exception {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.set( "text", "xyz" );

    String expected = WM_SETUP_CODE + getSetupCode( shell.button ) + "w.setText( \"xyz\" );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testSetInt() throws Exception {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    // use a high value to test separator avoidance...
    writer.set( "width", 20000000 );

    String expected = WM_SETUP_CODE + getSetupCode( shell.button ) + "w.setWidth( 20000000 );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testSetBoolean() throws Exception {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.set( "allowStretchY", true );

    String expected = WM_SETUP_CODE + getSetupCode( shell.button ) + "w.setAllowStretchY( true );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testCallWithStringArray() throws Exception {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    // test regular strings
    writer.call( "setTextValues", new String[] { "abc", "xyz" } );

    String expected = WM_SETUP_CODE
                      + getSetupCode( shell.button )
                      + "w.setTextValues( \"abc\", \"xyz\" );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );

    // test string with newline
    Fixture.fakeResponseWriter();
    writer.call( "setTextValues", new String[] { "new\nline" } );

    expected = "w.setTextValues( \"new\\nline\" );";
    assertTrue( ProtocolTestUtil.getMessageScript().contains( expected ) );
  }

  public void testCallWithObjectArray() throws IOException {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.call( "setIntegerValues", new Integer[] { new Integer( 1 ), new Integer( 2 ) } );

    String expected = WM_SETUP_CODE + getSetupCode( shell.button ) + "w.setIntegerValues( 1, 2 );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testCallWithColorValue() throws IOException {
    Color salmon = new Color( display, 250, 128, 114 );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.call( "setColor", new Object[] { salmon } );

    String expected = WM_SETUP_CODE + getSetupCode( shell.button ) + "w.setColor( \"#fa8072\" );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testCallWithRGBValue() throws IOException {
    RGB salmon = new Color( display, 250, 128, 114 ).getRGB();
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.call( "setColor", new Object[] { salmon } );

    String expected = WM_SETUP_CODE + getSetupCode( shell.button ) + "w.setColor( \"#fa8072\" );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testSetIntArray() throws Exception {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.set( "intValues", new int[] { 1, 2 } );

    String expected = WM_SETUP_CODE + getSetupCode( shell.button ) + "w.setIntValues( 1, 2 );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testSetBooleanArray() throws Exception {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.set( "boolValues", new boolean[] { true, false } );

    String expected = WM_SETUP_CODE
                      + getSetupCode( shell.button )
                      + "w.setBoolValues( true, false );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testSetWidgetArray() throws IOException {
    // set property value to an empty array
    JSWriter writer = JSWriter.getWriterFor( shell );
    writer.set( "buttons", "buttons", new Widget[ 0 ], null );

    String expected = WM_SETUP_CODE + getSetupCode( shell ) + "w.setButtons( [] );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testSetWidgetArray2() throws IOException {
    Widget button = new Button( shell, SWT.PUSH );
    JSWriter writer = JSWriter.getWriterFor( shell );
    // set property value to an array that contains one item
    writer.set( "buttons", "buttons", new Object[] { button }, null );

    String buttonId = WidgetUtil.getId( button );
    String expected = WM_SETUP_CODE
                      + getSetupCode( shell )
                      + "w.setButtons( [wm.findWidgetById( \"" + buttonId + "\" ) ] );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testSetWidgetArray3() throws IOException {
    Widget widget1 = new Button( shell, SWT.PUSH );
    Widget widget2 = new Button( shell, SWT.PUSH );
    JSWriter writer = JSWriter.getWriterFor( shell );
    // set property value to an array that contains two items
    writer.set( "buttons", "buttons", new Object[] { widget1, widget2 }, null );

    String widgetId1 = WidgetUtil.getId( widget1 );
    String widgetId2 = WidgetUtil.getId( widget2 );
    String expected = WM_SETUP_CODE
                      + getSetupCode( shell )
                      + "w.setButtons( [wm.findWidgetById( \"" + widgetId1 + "\" ),"
                      + "wm.findWidgetById( \"" + widgetId2 + "\" ) ] );";
    assertTrue( ProtocolTestUtil.getMessageScript().contains( expected ) );
  }

  public void testSetWithPropertyChainAndObjectArray() throws Exception {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.set( new String[] { "prop1", "prop2" },
                new Object[] { new JSVar( "var1" ), new JSVar( "var2" ) } );

    String expected = WM_SETUP_CODE
                      + getSetupCode( shell.button )
                      + "w.getProp1().setProp2( var1, var2 );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testSetWithStringArray() throws Exception {
    Widget widget = new Button( shell, SWT.NONE );
    JSWriter writer = JSWriter.getWriterFor( widget );
    Fixture.clearPreserved();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( "stringArray", new String[] { "a", "b", "c" } );
    String[] newValue = new String[] { "c", "b", "a" };
    writer.set( "stringArray", "stringArray", newValue, null );

    String expected = WM_SETUP_CODE
                      + getSetupCode( widget )
                      + "w.setStringArray( [ \"c\", \"b\", \"a\" ] );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testSetWithStringArrayPreserved() throws Exception {
    Widget widget = new Button( shell, SWT.NONE );
    JSWriter writer = JSWriter.getWriterFor( widget );
    String[] value = new String[] { "c", "b", "a" };
    Fixture.markInitialized( widget );
    Fixture.clearPreserved();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( "stringArray", value );
    writer.set( "stringArray", "stringArray", value, null );

    assertEquals( 0, Fixture.getProtocolMessage().getOperationCount() );
  }

  public void testSetChangedString() throws Exception {
    JSWriter writer = JSWriter.getWriterFor( shell.text );
    shell.text.setText( "" );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    writer.set( "text", "value", shell.text.getText() );

    String expected = WM_SETUP_CODE
                      + "var w = wm.findWidgetById( \"" + textId + "\" );"
                      + "w.setValue( \"\" );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testSetChangedString2() throws Exception {
    JSWriter writer = JSWriter.getWriterFor( shell.text );
    shell.text.setText( "" );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell.text );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    writer.set( "text", "value", shell.text.getText() );

    assertEquals( 0, Fixture.getProtocolMessage().getOperationCount() );
  }

  public void testSetChangedString3() throws Exception {
    JSWriter writer = JSWriter.getWriterFor( shell.text );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell.text );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    shell.text.setText( "hello" );
    writer.set( "text", "value", shell.text.getText() );

    String expected = WM_SETUP_CODE + getSetupCode( shell.text ) + "w.setValue( \"hello\" );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testCall() throws Exception {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.call( "function1", null );

    String expected = WM_SETUP_CODE + getSetupCode( shell.button ) + "w.function1();";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testCall2() throws Exception {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.call( "function1", null );
    writer.call( "function2", null );

    String expected = WM_SETUP_CODE + getSetupCode( shell.button ) + "w.function1();w.function2();";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testCallFieldAssignment() {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.callFieldAssignment( new JSVar( "w" ), "field", "value" );

    String expected = "w.field = value;";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testCallStatic() throws IOException {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.callStatic( "doSomethingStatic", null );

    String expected = WM_SETUP_CODE + "doSomethingStatic();";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testCallStatic2() throws IOException {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.callStatic( "doSomethingStaticWithButton", new Object[]{ shell.button } );

    String expected = WM_SETUP_CODE
                      + "doSomethingStaticWithButton( wm.findWidgetById( \"" + buttonId + "\" ) );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testCallStatic3() throws IOException {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.call( "test", null ); // creates widget ref
    writer.callStatic( "doSomethingStaticWithButtonRef", new Object[]{ shell.button } );

    String expected = WM_SETUP_CODE
                      + getSetupCode( shell.button )
                      + "w.test();doSomethingStaticWithButtonRef( w );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testCallStatic4() throws IOException {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.callStatic( "doSomething", new Object[] { new String[] { "xyz" } } );

    String expected = WM_SETUP_CODE + "doSomething( [ \"xyz\" ] );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testTargetedCallForWidget() throws IOException {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.call( shell, "doSomethingWithShell", new Object[]{ shell } );

    String expected = WM_SETUP_CODE
                      + "var t = wm.findWidgetById( \"" + shellId + "\" );"
                      + "t.doSomethingWithShell( wm.findWidgetById( \"" + shellId + "\" ) );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testTargetedCallForWidget2() throws IOException {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.call( shell, "doSomething", new Object[ 0 ] );

    String expected = WM_SETUP_CODE
                      + "var t = wm.findWidgetById( \"" + shellId + "\" );t.doSomething();";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testTargetedCallForWidget3() throws IOException {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.call( shell, "doSomething", new Object[]{ new Integer( 123 ) } );

    String expected = WM_SETUP_CODE
                      + "var t = wm.findWidgetById( \"" + shellId + "\" );"
                      + "t.doSomething( 123 );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testTargetedCallForWidget4() throws IOException {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.call( shell, "doSomething", new Object[] { "abc", new Integer( 123 ) } );

    String expected = WM_SETUP_CODE
                      + "var t = wm.findWidgetById( \"" + shellId + "\" );"
                      + "t.doSomething( \"abc\", 123 );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testTargetedCallForWidget5() throws IOException {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.call( shell, "doSomething", new Object[] { shell, shell.combo.getItems() } );

    String expected = WM_SETUP_CODE
                      + "var t = wm.findWidgetById( \"" + shellId + "\" );"
                      + "t.doSomething( wm.findWidgetById( \"" + shellId + "\" ), "
                      + "[ \"test\", \"test2\", \"test3\" ] );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testTargetedCallForWidget6() throws IOException {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    Object[] args = new Object[] { shell, new String[ 0 ] };
    writer.call( shell, "doSomething", args );

    String expected = WM_SETUP_CODE
                      + "var t = wm.findWidgetById( \"" + shellId + "\" );"
                      + "t.doSomething( wm.findWidgetById( \"" + shellId + "\" ), [] );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testTargetedCallForWidget7() throws IOException {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.call( shell, "doSomething", null );

    String expected = WM_SETUP_CODE
                      + "var t = wm.findWidgetById( \"" + shellId + "\" );t.doSomething();";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testTargetedCallForWidget8() throws IOException {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.call( shell, "doSomething", new Object[] { null } );

    String expected = WM_SETUP_CODE
                      + "var t = wm.findWidgetById( \"" + shellId + "\" );"
                      + "t.doSomething( null );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testTargetedCallForWidget9() throws IOException {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.call( shell.button, "buttonFunction", null );

    String expected = WM_SETUP_CODE + getSetupCode( shell.button ) + "w.buttonFunction();";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testCallWithCharacterParam() throws IOException {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.call( new JSVar( "a" ), "setChar", new Object[] { new Character( 'A' ) } );

    String expected = WM_SETUP_CODE + "a.setChar( \"A\" );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testTargetedCallForJSVar() throws IOException {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.call( new JSVar( "a" ), "doSomething", null );

    String expected = WM_SETUP_CODE + "a.doSomething();";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testAddListener() throws Exception {
    // add listener directly to the widget
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.addListener( "execute", "buttonExecuted1" );

    String expected = WM_SETUP_CODE
                      + getSetupCode( shell.button )
                      + "w.addEventListener( \"execute\", buttonExecuted1 );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testAddListener2() throws Exception {
    // add listeners directly to the widget
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.addListener( "execute", "buttonExecuted1" );
    writer.addListener( "execute", "buttonExecuted2" );

    String expected = WM_SETUP_CODE
                      + getSetupCode( shell.button )
                      + "w.addEventListener( \"execute\", buttonExecuted1 );"
                      + "w.addEventListener( \"execute\", buttonExecuted2 );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testAddListener3() throws Exception {
    // add listener of a property object of the widget
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.addListener( "prop", "type", "jshandler" );

    String expected = WM_SETUP_CODE
                      + getSetupCode( shell.button )
                      + "w.getProp().addEventListener( \"type\", jshandler );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testRemoveListener() throws Exception {
    // remove listener directly from the widget
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.removeListener( "execute", "buttonExecuted1" );

    String expected = WM_SETUP_CODE
                      + getSetupCode( shell.button )
                      + "w.removeEventListener( \"execute\", buttonExecuted1 );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testRemoveListener2() throws Exception {
    // remove two listeners directly from the widget
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.removeListener( "execute", "buttonExecuted1" );
    writer.removeListener( "execute", "buttonExecuted2" );

    String expected = WM_SETUP_CODE
                      + getSetupCode( shell.button )
                      + "w.removeEventListener( \"execute\", buttonExecuted1 );"
                      + "w.removeEventListener( \"execute\", buttonExecuted2 );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testRemoveListener3() throws Exception {
    // remove listener from a property object of the widget
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.removeListener( "prop", "type", "jshandler" );

    String expected = WM_SETUP_CODE
                      + getSetupCode( shell.button )
                      + "w.getProp().removeEventListener( \"type\", jshandler );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testRemoveListener4() throws Exception {
    // ensure instance listener hack of [rh]: JSWriter#removeListener(String,String,String)
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.removeListener( "type", "this.jshandler" );

    String expected = WM_SETUP_CODE
                      + getSetupCode( shell.button )
                      + "w.removeEventListener( \"type\", w.jshandler, w );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testUpdateListenerAction() throws Exception {
    // Test initial rendering with no listeners
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( display );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    JSListenerInfo jsListenerInfo = new JSListenerInfo( "execute",
                                                        "selectedEvent",
                                                        JSListenerType.ACTION );
    writer.updateListener( jsListenerInfo,
                           PROP_SELECTION_LISTENER,
                           SelectionEvent.hasListener( shell.button ) );
    assertEquals( 0, Fixture.getProtocolMessage().getOperationCount() );

    // Test initial rendering with listeners
    SelectionListener selectionListener = new SelectionAdapter() {};
    shell.button.addSelectionListener( selectionListener );
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    writer.updateListener( jsListenerInfo,
                           PROP_SELECTION_LISTENER,
                           SelectionEvent.hasListener( shell.button ) );
    String expected = WM_SETUP_CODE
                      + getSetupCode( shell.button )
                      + "w.addEventListener( \"execute\", selectedEvent );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );

    // Test adding the first listeners
    Fixture.fakeResponseWriter();
    shell.button.removeSelectionListener( selectionListener );
    Fixture.markInitialized( shell.button );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    shell.button.addSelectionListener( selectionListener );
    writer.updateListener( jsListenerInfo,
                           PROP_SELECTION_LISTENER,
                           SelectionEvent.hasListener( shell.button ) );
    // Test adding a further listener: leads to no markup
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    SelectionListener selectionListener2 = new SelectionAdapter() {};
    shell.button.addSelectionListener( selectionListener2 );
    writer.updateListener( jsListenerInfo,
                           PROP_SELECTION_LISTENER,
                           SelectionEvent.hasListener( shell.button ) );
    assertEquals( 0, Fixture.getProtocolMessage().getOperationCount() );

    // Test removing all the above added listener
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    shell.button.removeSelectionListener( selectionListener );
    shell.button.removeSelectionListener( selectionListener2 );
    writer.updateListener( jsListenerInfo,
                           PROP_SELECTION_LISTENER,
                           SelectionEvent.hasListener( shell.button ) );
    assertEquals( "w.removeEventListener( \"execute\", selectedEvent );", ProtocolTestUtil.getMessageScript() );

    // Test that no changes to listeners do not cause any additional markup
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    writer.updateListener( jsListenerInfo,
                           PROP_SELECTION_LISTENER,
                           SelectionEvent.hasListener( shell.button ) );
    assertEquals( 0, Fixture.getProtocolMessage().getOperationCount() );
  }

  public void testUpdateListenerStateAndActionWithStateFirst() throws Exception {
    // Test initial rendering with no action listeners
    Fixture.markInitialized( display );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    JSListenerInfo jsListenerInfo = new JSListenerInfo( "type",
                                                        "event",
                                                        JSListenerType.STATE_AND_ACTION );
    writer.updateListener( jsListenerInfo,
                           PROP_SELECTION_LISTENER,
                           SelectionEvent.hasListener( shell.button ) );
    String expected = WM_SETUP_CODE
                      + getSetupCode( shell.button )
                      + "w.addEventListener( \"type\", event );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );

    // Test rendering with action listener added
    WidgetAdapter adapter = ( WidgetAdapter )WidgetUtil.getAdapter( shell.button );
    adapter.setInitialized( true );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    SelectionListener selectionListener = new SelectionAdapter() {};
    SelectionEvent.addListener( shell.button, selectionListener );
    Fixture.fakeResponseWriter();
    writer.updateListener( jsListenerInfo,
                           PROP_SELECTION_LISTENER,
                           SelectionEvent.hasListener( shell.button ) );
    expected = "w.removeEventListener( \"type\", event );"
               + "w.addEventListener( \"type\", eventAction );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );

    // Test adding a further listener: leads to no markup
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    SelectionListener selectionListener2 = new SelectionAdapter() {};
    SelectionEvent.addListener( shell.button, selectionListener2 );
    writer.updateListener( jsListenerInfo,
                           PROP_SELECTION_LISTENER,
                           SelectionEvent.hasListener( shell.button ) );
    assertEquals( 0, Fixture.getProtocolMessage().getOperationCount() );

    // Test removing all the above added listener
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    SelectionEvent.removeListener( shell.button, selectionListener );
    SelectionEvent.removeListener( shell.button, selectionListener2 );
    writer.updateListener( jsListenerInfo,
                           PROP_SELECTION_LISTENER,
                           SelectionEvent.hasListener( shell.button ) );
    expected = "w.removeEventListener( \"type\", eventAction );"
               + "w.addEventListener( \"type\", event );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testUpdateListenerStateAndActionWithActionFirst() throws Exception {
    // Test initial rendering with action listeners
    Fixture.markInitialized( display );
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    SelectionEvent.addListener( shell.button, selectionListener );
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    JSListenerInfo jsListenerInfo = new JSListenerInfo( "type",
                                                        "event",
                                                        JSListenerType.STATE_AND_ACTION );
    writer.updateListener( jsListenerInfo,
                           PROP_SELECTION_LISTENER,
                           SelectionEvent.hasListener( shell.button ) );
    String expected = WM_SETUP_CODE
                      + getSetupCode( shell.button )
                      + "w.addEventListener( \"type\", eventAction );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );

    // Test adding a further listener: leads to no markup
    WidgetAdapter adapter = ( WidgetAdapter )WidgetUtil.getAdapter( shell.button );
    adapter.setInitialized( true );
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    SelectionListener selectionListener2 = new SelectionAdapter() {};
    SelectionEvent.addListener( shell.button, selectionListener2 );
    writer.updateListener( jsListenerInfo,
                           PROP_SELECTION_LISTENER,
                           SelectionEvent.hasListener( shell.button ) );
    assertEquals( 0, Fixture.getProtocolMessage().getOperationCount() );

    // Test removing all the above added listener
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    SelectionEvent.removeListener( shell.button, selectionListener );
    SelectionEvent.removeListener( shell.button, selectionListener2 );
    writer.updateListener( jsListenerInfo,
                           PROP_SELECTION_LISTENER,
                           SelectionEvent.hasListener( shell.button ) );
    expected = "w.removeEventListener( \"type\", eventAction );"
               + "w.addEventListener( \"type\", event );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testUpdateListenerOnPropertyAction() throws Exception {
    // Test initial rendering with no listeners
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( display );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    JSListenerInfo jsListenerInfo = new JSListenerInfo( "execute",
                                                        "selectedEvent",
                                                        JSListenerType.ACTION );
    writer.updateListener( PROPERTY_NAME,
                           jsListenerInfo,
                           PROP_SELECTION_LISTENER,
                           SelectionEvent.hasListener( shell.button ) );
    assertEquals( 0, Fixture.getProtocolMessage().getOperationCount() );

    // Test initial rendering with listeners
    SelectionListener selectionListener = new SelectionAdapter() {};
    shell.button.addSelectionListener( selectionListener );
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    writer.updateListener( PROPERTY_NAME,
                           jsListenerInfo,
                           PROP_SELECTION_LISTENER,
                           SelectionEvent.hasListener( shell.button ) );
    String expected = WM_SETUP_CODE
                      + getSetupCode( shell.button )
                      + "w.getPropertyName().addEventListener( \"execute\", selectedEvent );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );

    // Test adding the first listeners
    Fixture.fakeResponseWriter();
    shell.button.removeSelectionListener( selectionListener );
    Fixture.markInitialized( shell.button );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    shell.button.addSelectionListener( selectionListener );
    writer.updateListener( PROPERTY_NAME,
                           jsListenerInfo,
                           PROP_SELECTION_LISTENER,
                           SelectionEvent.hasListener( shell.button ) );

    // Test adding a further listener: leads to no markup
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    SelectionListener selectionListener2 = new SelectionAdapter() {};
    shell.button.addSelectionListener( selectionListener2 );
    writer.updateListener( PROPERTY_NAME,
                           jsListenerInfo,
                           PROP_SELECTION_LISTENER,
                           SelectionEvent.hasListener( shell.button ) );
    assertEquals( 0, Fixture.getProtocolMessage().getOperationCount() );

    // Test removing all the above added listener
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    shell.button.removeSelectionListener( selectionListener );
    shell.button.removeSelectionListener( selectionListener2 );
    writer.updateListener( PROPERTY_NAME,
                           jsListenerInfo,
                           PROP_SELECTION_LISTENER,
                           SelectionEvent.hasListener( shell.button ) );
    expected = "w.getPropertyName().removeEventListener" + "( \"execute\", selectedEvent );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );

    // Test that no changes to listeners do not cause any additional markup
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    writer.updateListener( PROPERTY_NAME,
                           jsListenerInfo,
                           PROP_SELECTION_LISTENER,
                           SelectionEvent.hasListener( shell.button ) );
    assertEquals( 0, Fixture.getProtocolMessage().getOperationCount() );
  }

  public void testUpdateListenerOnPropertyStateAndAction() throws IOException {
    // Test initial rendering with no action listeners
    Fixture.markInitialized( display );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    JSListenerInfo jsListenerInfo = new JSListenerInfo( "type",
                                                        "event",
                                                        JSListenerType.STATE_AND_ACTION );
    writer.updateListener( PROPERTY_NAME,
                           jsListenerInfo,
                           PROP_SELECTION_LISTENER,
                           SelectionEvent.hasListener( shell.button ) );
    String expected = WM_SETUP_CODE
                      + getSetupCode( shell.button )
                      + "w.getPropertyName().addEventListener( \"type\", event );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );

    // Test rendering with action listener added
    WidgetAdapter adapter = ( WidgetAdapter )WidgetUtil.getAdapter( shell.button );
    adapter.setInitialized( true );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    SelectionListener selectionListener = new SelectionAdapter() {};
    SelectionEvent.addListener( shell.button, selectionListener );
    Fixture.fakeResponseWriter();
    writer.updateListener( PROPERTY_NAME,
                           jsListenerInfo,
                           PROP_SELECTION_LISTENER,
                           SelectionEvent.hasListener( shell.button ) );
    expected = "w.getPropertyName().removeEventListener( \"type\", event );"
               + "w.getPropertyName().addEventListener( \"type\", eventAction );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );

    // Test adding a further listener: leads to no markup
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    SelectionListener selectionListener2 = new SelectionAdapter() {};
    SelectionEvent.addListener( shell.button, selectionListener2 );
    writer.updateListener( PROPERTY_NAME,
                           jsListenerInfo,
                           PROP_SELECTION_LISTENER,
                           SelectionEvent.hasListener( shell.button ) );
    assertEquals( 0, Fixture.getProtocolMessage().getOperationCount() );

    // Test removing all the above added listener
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    SelectionEvent.removeListener( shell.button, selectionListener );
    SelectionEvent.removeListener( shell.button, selectionListener2 );
    writer.updateListener( PROPERTY_NAME,
                           jsListenerInfo,
                           PROP_SELECTION_LISTENER,
                           SelectionEvent.hasListener( shell.button ) );
    expected = "w.getPropertyName().removeEventListener( \"type\", eventAction );"
               + "w.getPropertyName().addEventListener( \"type\", event );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }
  //////////////////////////////// <<<<<<<<<<<<<<<<<<<<<<<<<<<

  public void testDispose() throws Exception {
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.dispose();

    String expected = WM_SETUP_CODE + "wm.dispose( \"" + buttonId + "\" );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testDisposeWithDisposedControl() throws IOException {
    shell.button.dispose();
    JSWriter writer = JSWriter.getWriterFor( shell.button );
    writer.dispose();

    String expected = WM_SETUP_CODE + "wm.dispose( \"" + buttonId + "\" );";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testWigetRef() throws IOException {
    Widget widget1 = new Label( shell, SWT.NONE );
    Widget widget2 = new Label( shell, SWT.NONE );
    JSWriter writer1 = JSWriter.getWriterFor( widget1 );
    JSWriter writer2 = JSWriter.getWriterFor( widget2 );

    Fixture.fakeResponseWriter();
    writer1.set( "visible", false );
    String findWidget = "findWidgetById";
    assertTrue( ProtocolTestUtil.getMessageScript().contains( findWidget ) );

    Fixture.fakeResponseWriter();
    writer1.set( "visible", true );
    assertTrue( ProtocolTestUtil.getMessageScript().indexOf( findWidget ) == -1 );

    Fixture.fakeResponseWriter();
    writer2.set( "visible", true );
    assertTrue( ProtocolTestUtil.getMessageScript().contains( findWidget ) );

    Fixture.fakeResponseWriter();
    writer2.set( "visible", true );
    assertTrue( ProtocolTestUtil.getMessageScript().indexOf( findWidget ) == -1 );

    Fixture.fakeResponseWriter();
    writer1.set( "visible", true );
    assertTrue( ProtocolTestUtil.getMessageScript().contains( findWidget ) );
  }

  public void testVarAssignment() {
    JSWriter writer = JSWriter.getWriterFor( shell );
    writer.varAssignment( new JSVar( "foo" ), "getFoo" );

    String expected = WM_SETUP_CODE + getSetupCode( shell ) + "var foo = w.getFoo();";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testSubsequentCalls() throws IOException {
    JSWriter writer = JSWriter.getWriterFor( shell );
    writer.call( "foo", null );
    writer.call( "bar", null );

    String expected = WM_SETUP_CODE
                      + "var w = wm.findWidgetById( \"" + shellId + "\" );"
                      + "w.foo();"
                      + "w.bar();";
    assertEquals( expected, ProtocolTestUtil.getMessageScript() );
  }

  public void testSubsequentCallsWithIntermediateOperation() throws IOException {
    ProtocolMessageWriter protocolWriter = ContextProvider.getProtocolWriter();
    JSWriter writer = JSWriter.getWriterFor( shell );
    writer.call( "foo", null );
    protocolWriter.appendSet( "xy", "text", "" );
    writer.call( "bar", null );

    Message message = Fixture.getProtocolMessage();
    CallOperation operation1 = ( CallOperation )message.getOperation( 0 );
    CallOperation operation2 = ( CallOperation )message.getOperation( 2 );
    String expected1 = WM_SETUP_CODE
                       + "var w = wm.findWidgetById( \"" + shellId + "\" );"
                       + "w.foo();";
    String expected2 = WM_SETUP_CODE
                       + "var w = wm.findWidgetById( \"" + shellId + "\" );"
                       + "w.bar();";
    assertEquals( expected1, operation1.getProperty( "content" ) );
    assertEquals( expected2, operation2.getProperty( "content" ) );
  }

  private static String getSetupCode( Widget widget ) {
    return "var w = wm.findWidgetById( \"" + WidgetUtil.getId( widget ) + "\" );";
  }

  private static class TestShell extends Shell {
    private static final long serialVersionUID = 1L;

    final Button button;
    final Text text;
    final Combo combo;

    public TestShell( Display display ) {
      super( display ,SWT.NONE );
      button = new Button( this, SWT.PUSH );
      text = new Text( this, SWT.SINGLE );
      combo = new Combo( this, SWT.NONE );
      combo.setItems( new String[] { "test", "test2", "test3" } );
    }
  }

}
