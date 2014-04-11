/*******************************************************************************
 * Copyright (c) 2012, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

describe( "WidgetProxyFactory", function() {

  var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
  var Processor = rwt.remote.MessageProcessor;
  var ObjectManager = rwt.remote.ObjectRegistry;
  var WidgetProxyFactory = rwt.scripting.WidgetProxyFactory;
  var EventBinding = rwt.scripting.EventBinding;

  var text;

  beforeEach( function() {
    TestUtil.createShellByProtocol( "w2" );
    Processor.processOperation( {
      "target" : "w3",
      "action" : "create",
      "type" : "rwt.widgets.Text",
      "properties" : {
        "style" : [ "SINGLE", "RIGHT" ],
        "parent" : "w2"
      }
    } );
    TestUtil.flush();
    text = ObjectManager.getObject( "w3" );
    text.focus();
  } );

  afterEach( function() {
    Processor.processOperation( {
      "target" : "w2",
      "action" : "destroy"
    } );
    text = null;
    org.eclipse.rwt.test.fixture.Fixture.reset();
  } );

  it( "is used by rap.getObject", function() {
    var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );

    var otherProxy = rap.getObject( "w3" );

    expect( otherProxy ).toBe( widgetProxy );
  } );

  it( "creates only one proxy per object", function() {
    var widgetProxy1 = WidgetProxyFactory.getWidgetProxy( text );
    var widgetProxy2 = WidgetProxyFactory.getWidgetProxy( text );

    expect( widgetProxy1 ).toBe( widgetProxy2 );
  } );

  it( "disposes proxy with original", function() {
    var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );

    text.destroy();
    TestUtil.flush();

    expect( TestUtil.hasNoObjects( widgetProxy ) ).toBeTruthy();
  } );

  it( "disposes user data", function() {
    var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );
    widgetProxy.setData( "key", {} );
    var data = rwt.remote.HandlerUtil.getServerData( text );
    expect( TestUtil.hasNoObjects( data ) ).toBeFalsy();

    text.destroy();
    TestUtil.flush();

    expect( TestUtil.hasNoObjects( data ) ).toBeTruthy();
  } );

  describe( "any proxy", function() {

    it( "has setter and getter", function() {
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );

      widgetProxy.setText( "foo" );

      expect( text.getValue() ).toBe( "foo" );
    } );

    it( "has setVisible instead of setVisibility", function() {
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );

      widgetProxy.setVisible( false );

      expect( text.getVisibility() ).toBeFalsy();
    } );

    it( "has working setData and getData", function() {
      var widgetProxy1 = WidgetProxyFactory.getWidgetProxy( text );
      var widgetProxy2 = WidgetProxyFactory.getWidgetProxy( text );

      widgetProxy1.setData( "myKey", 24 );

      expect( widgetProxy2.getData( "myWrongKey" ) ).toBeNull();
      expect( widgetProxy2.getData( "myKey" ) ).toBe( 24 );
    } );

    describe( "setData", function() {

      it( "throws exception for too many arguments", function() {
        var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );
        expect( function(){
          widgetProxy.setData( "myKey", 24, "foo" );
        } ).toThrow();
      } );

      it( "throws exception for too few arguments", function() {
        var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );
        expect( function(){
          widgetProxy.setData( 24 );
        } ).toThrow();
      } );

    } );

    describe( "setData", function() {

      it( "throws exception for too many arguments", function() {
        var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );
        expect( function() {
          widgetProxy.getData( "myKey", 24 );
        } ).toThrow();
      } );

      it( "throws exception for too few arguments", function() {
        var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );
        expect( function() {
          widgetProxy.getData();
        } ).toThrow();
      } );

    } );

    it( "delegates setBackground and getBackground", function() {
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );

      widgetProxy.setBackground( [ 1, 2, 3 ] );

      expect( widgetProxy.getBackground() ).toEqual( [ 1, 2, 3 ] );
    } );

    it( "delegates setForeground and getForeground", function() {
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );

      widgetProxy.setForeground( [ 1, 2, 3 ] );

      expect( widgetProxy.getForeground() ).toEqual( [ 1, 2, 3 ] );
    } );

    it( "delegates setToolTipText and getToolTipText", function() {
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );

      widgetProxy.setToolTipText( "foo" );

      expect( widgetProxy.getToolTipText() ).toBe( "foo" );
    } );

    it( "delegates getVisible and setVisible", function() {
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );

      widgetProxy.setVisible( false );

      expect( widgetProxy.getVisible() ).toBeFalsy();
    } );

    it( "delegats setEnabled and getEnabled", function() {
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );

      widgetProxy.setEnabled( false );

      expect( widgetProxy.getEnabled() ).toBeFalsy();
    } );

    it( "delegats setCursor and getCursor", function() {
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );

      widgetProxy.setCursor( SWT.CURSOR_HELP );

      expect( widgetProxy.getCursor() ).toBe( SWT.CURSOR_HELP );
    } );

    describe( "$el", function() {

      it( "exists for any widget in rwt.widgets", function() {
        var fake = new rwt.widgets.base.Terminator();
        fake.classname = "rwt.widgets.Foo";
        var widgetProxy = WidgetProxyFactory.getWidgetProxy( fake );

        expect( widgetProxy.$el instanceof Object ).toBeTruthy();
      } );

      it( "does not exist for any widget rwt.widgets.base", function() {
        var fake = new rwt.widgets.base.Terminator();
        var widgetProxy = WidgetProxyFactory.getWidgetProxy( fake );

        expect( widgetProxy.$el ).toBeUndefined();
      } );

      it( "wraps same widget", function() {
        text.setHtmlAttribute( "foo", "bar" );
        var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );

        expect( widgetProxy.$el.attr( "foo" ) ).toBe( "bar" );
      } );

    } );

  } );

  describe( "text proxy", function() {

    it( "synchronizes text value to server", function() {
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );

      widgetProxy.setText( "foo" );
      rwt.remote.Connection.getInstance().send();
      var msg = TestUtil.getMessageObject();
      expect( msg.findSetProperty( "w3", "text" ) ).toBe( "foo" );
    } );

    it( "delegates getText", function() {
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );
      text.setValue( "foo" );

      var value = widgetProxy.getText();

      expect( value ).toBe( "foo" );
    } );

    it( "delegates getSelection", function() {
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );
      text.setValue( "foo" );
      text.setSelection( [ 1,2 ] );

      var value = widgetProxy.getSelection();

      expect( value ).toEqual( [ 1, 2 ] );
    } );

    it( "returns true for getEditable", function() {
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );

      var value = widgetProxy.getEditable();

      expect( value ).toBeTruthy();
    } );

    it( "returns false for getEditable", function() {
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );
      widgetProxy.setEditable( false );

      var value = widgetProxy.getEditable();

      expect( value ).toBeFalsy();
    } );

    describe( "forceFocus", function() {

      it( "focuses widget", function() {
        var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );
        text.blur();

        var value = widgetProxy.forceFocus();

        expect( text.isFocused() ).toBeTruthy();
        expect( value ).toBeTruthy();
      } );

      it( "does not focus invisible widget", function() {
        var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );
        text.blur();
        text.setVisibility( false );

        var value = widgetProxy.forceFocus();

        expect( text.isFocused() ).toBeFalsy();
        expect( value ).toBeFalsy();
      } );

      it( "does not focus widget with invisible parent", function() {
        var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );
        text.blur();
        text.getParent().setVisibility( false );
        TestUtil.flush();

        var value = widgetProxy.forceFocus();

        expect( text.isFocused() ).toBeFalsy();
        expect( value ).toBeFalsy();
      } );

      it( "does not focus disabled widget", function() {
        var widgetProxy = WidgetProxyFactory.getWidgetProxy( text );
        text.blur();
        text.setEnabled( false );

        var value = widgetProxy.forceFocus();

        expect( text.isFocused() ).toBeFalsy();
        expect( value ).toBeFalsy();
      } );

    } );

  } );

  describe( "list proxy", function(){

    it( "getSelection returns selection", function() {
      Processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ ],
          "parent" : "w2",
          "items" : [ "a", "b", "c" ]
        }
      } );
      var list = ObjectManager.getObject( "w4" );
      TestUtil.flush();
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( list );
      TestUtil.click( list.getItems()[ 1 ] );

      var value = widgetProxy.getSelection();

      expect( value ).toEqual( [ "b" ] );
    } );

    it( "getSelection does not escape text", function() {
      Processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ ],
          "parent" : "w2",
          "items" : [ "a", "b & x", "c" ]
        }
      } );
      var list = ObjectManager.getObject( "w4" );
      TestUtil.flush();
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( list );
      TestUtil.click( list.getItems()[ 1 ] );

      var value = widgetProxy.getSelection();

      expect( value ).toEqual( [ "b & x" ] );
    } );

  } );

  describe( "canvas proxy", function(){

    it( "redraw fires Paint event", function() {
      Processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.Canvas",
        "properties" : {
          "style" : [ ],
          "parent" : "w2"
        }
      } );
      var canvas = ObjectManager.getObject( "w4" );
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( canvas );
      var spy = jasmine.createSpy( "paint listener" );
      TestUtil.flush();
      EventBinding.addListener( canvas, "Paint", spy );

      widgetProxy.redraw();

      expect( spy ).toHaveBeenCalled();
      canvas.destroy();
    } );

    it( "getCientArea returns clientArea", function() {
      Processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.Canvas",
        "properties" : {
          "style" : [ ],
          "parent" : "w2",
          "clientArea" : [ 10, 20, 30, 40 ]
        }
      } );
      var canvas = ObjectManager.getObject( "w4" );
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( canvas );
      var result = widgetProxy.getClientArea();

      expect( result ).toEqual( [ 10, 20, 30, 40 ] );
      canvas.destroy();
    } );

  } );

  describe( "combo proxy", function(){

    it( "fires Modify event", function() {
      var combo = TestUtil.createWidgetByProtocol( "w4", "w2", "rwt.widgets.Combo" );
      TestUtil.flush();
      var spy = jasmine.createSpy( "modify listener" );

      EventBinding.addListener( combo, "Modify", spy );
      TestUtil.fakeResponse( true );
      combo._field.setValue( "foo" );
      TestUtil.fakeResponse( false);

      expect( spy ).toHaveBeenCalled();
      combo.destroy();
    } );

    it( "delegates setText and getText", function() {
      var combo = TestUtil.createWidgetByProtocol( "w4", "w2", "rwt.widgets.Combo" );
      TestUtil.flush();
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( combo );

      widgetProxy.setText( "foo" );

      expect( widgetProxy.getText() ).toEqual( "foo" );
    } );

    it( "delegates setSelection and getSelection", function() {
      var combo = TestUtil.createWidgetByProtocol( "w4", "w2", "rwt.widgets.Combo" );
      TestUtil.flush();
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( combo );
      combo.setText( "foo" );
      widgetProxy.setSelection( [ 1, 2 ] );

      var value = widgetProxy.getSelection();

      expect( value ).toEqual( [ 1, 2 ] );
    } );

  } );

  describe( "button", function() {

    it( "delegates setText and getText", function() {
      var button = TestUtil.createWidgetByProtocol( "w4", "w2", "rwt.widgets.Button" );
      TestUtil.flush();
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( button );

      widgetProxy.setText( "foo" );

      expect( widgetProxy.getText() ).toEqual( "foo" );
    } );

    it( "delegates setSelection and getSelection", function() {
      rwt.remote.EventUtil.setSuspended( true );
      rwt.remote.MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "PUSH" ],
          "parent" : "w2"
        }
      } );
      rwt.remote.EventUtil.setSuspended( false );
      var button = rwt.remote.ObjectRegistry.getObject( "w4" );
      TestUtil.flush();
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( button );

      widgetProxy.setSelection( true );

      expect( widgetProxy.getSelection() ).toBeTruthy();
    } );

  } );

  it( "label proxy delegates setText and getText", function() {
    var label = TestUtil.createWidgetByProtocol( "w4", "w2", "rwt.widgets.Label" );
    TestUtil.flush();
    var widgetProxy = WidgetProxyFactory.getWidgetProxy( label );

    widgetProxy.setText( "foo" );

    expect( widgetProxy.getText() ).toEqual( "foo" );
  } );

  describe( "scale proxy", function(){

    it( "scale proxy delegates getSelection ", function() {
      var scale = TestUtil.createWidgetByProtocol( "w4", "w2", "rwt.widgets.Scale" );
      TestUtil.flush();
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( scale );

      widgetProxy.setSelection( 23 );

      expect( widgetProxy.getSelection() ).toEqual( 23 );
    } );

    it( "scale proxy delegates getMinimum and getMaximum", function() {
      var scale = TestUtil.createWidgetByProtocol( "w4", "w2", "rwt.widgets.Scale" );
      TestUtil.flush();
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( scale );

      TestUtil.protocolSet( "w4", { "minimum" : 20, "maximum" : 30 } );

      expect( widgetProxy.getMinimum() ).toEqual( 20 );
      expect( widgetProxy.getMaximum() ).toEqual( 30 );
    } );

  } );

  describe( "spinner proxy", function() {

    it( "delegates setSelection and getSelection", function() {
      var spinner = TestUtil.createWidgetByProtocol( "w4", "w2", "rwt.widgets.Spinner" );
      TestUtil.flush();
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( spinner );

      widgetProxy.setSelection( 23 );

      expect( widgetProxy.getSelection() ).toEqual( 23 );
    } );

    it( "delegates getminimum and getMaximum", function() {
      var spinner = TestUtil.createWidgetByProtocol( "w4", "w2", "rwt.widgets.Spinner" );
      TestUtil.flush();
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( spinner );

      TestUtil.protocolSet( "w4", { "minimum" : 20, "maximum" : 30 } );

      expect( widgetProxy.getMinimum() ).toEqual( 20 );
      expect( widgetProxy.getMaximum() ).toEqual( 30 );
    } );

    it( "delegates getText", function() {
      var spinner = TestUtil.createWidgetByProtocol( "w4", "w2", "rwt.widgets.Spinner" );
      TestUtil.flush();
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( spinner );

      spinner._textfield.setValue( "foo" );

      expect( widgetProxy.getText() ).toEqual( "foo" );
    } );

  } );

  describe( "progressbar proxy", function() {

    it( "delegates setSelection and getSelection", function() {
      var bar = TestUtil.createWidgetByProtocol( "w4", "w2", "rwt.widgets.ProgressBar" );
      TestUtil.flush();
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( bar );

      widgetProxy.setSelection( 23 );

      expect( widgetProxy.getSelection() ).toEqual( 23 );
    } );

    it( "delegates getminimum and getMaximum", function() {
      var bar = TestUtil.createWidgetByProtocol( "w4", "w2", "rwt.widgets.ProgressBar" );
      TestUtil.flush();
      var widgetProxy = WidgetProxyFactory.getWidgetProxy( bar );

      TestUtil.protocolSet( "w4", { "minimum" : 20, "maximum" : 30 } );

      expect( widgetProxy.getMinimum() ).toEqual( 20 );
      expect( widgetProxy.getMaximum() ).toEqual( 30 );
    } );

  } );

} );

