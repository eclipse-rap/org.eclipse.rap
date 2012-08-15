/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var writer;

var getMessage = function() {
  return JSON.parse( writer.createMessage() );
};

qx.Class.define( "org.eclipse.rwt.test.tests.ProtocolWriterTest", {

  extend : qx.core.Object,

  members : {

    testDispose : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      writer.dispose();

      assertTrue( TestUtil.hasNoObjects( writer, true ) );
    },

    testHasNoOperations : function() {
      assertFalse( writer.hasOperations() );
    },


    testHasOperationsAfterAppend : function() {
      writer.appendSet( "target", "foo", 23 );

      assertTrue( writer.hasOperations() );
    },


    testEmptyMessage : function() {
      var messageString = writer.createMessage();

      var message = JSON.parse( messageString );
      assertEquals( {}, message.meta );
      assertEquals( [], message.operations );
    },

    testMessageWithRequestCounter : function() {
      writer.appendMeta( "requestCounter", 1 );

      assertEquals( 1, getMessage().meta.requestCounter );
    },

    testWriteMessageAfterDispose : function() {
      writer.dispose();
      try {
        writer.createMessage();
        fail();
      } catch( expected ) {
      }
    },

    testAppendAfterDispose : function() {
      writer.dispose();
      try {
        writer.appendSet( "target", "foo", 23 );
        fail();
      } catch( expected ) {
      }
    },

    testMessageWithCall : function() {
      var shellId = "w2";
      var methodName = "methodName";
      var properties = {};
      properties[ "key1" ] = "a";
      properties[ "key2"] = "b" ;

      writer.appendCall( shellId, methodName, properties );

      var operation = getMessage().operations[ 0 ];
      assertEquals( "call", operation[ 0 ] );
      assertEquals( shellId, operation[ 1 ] );
      assertEquals( methodName, operation[ 2 ] );
      assertEquals( properties, operation[ 3 ] );
    },


    testMessageWithTwoCalls : function() {
      var shellId = "w2";
      var methodName = "methodName";
      var properties = {};
      properties[ "key1" ] = 5;
      properties[ "key2" ] = "b";
      properties[ "key3" ] = false;

      writer.appendCall( shellId, methodName, null );
      writer.appendCall( shellId, methodName, properties );

      var operation = getMessage().operations[ 1 ];
      assertEquals( shellId, operation[ 1 ] );
      assertEquals( methodName, operation[ 2 ] );
      assertEquals( properties, operation[ 3 ] );
    },

    testMessageWithSet : function() {
      var buttonId = "w5";

      writer.appendSet( buttonId, "text", "newText" );
      writer.appendSet( buttonId, "image", "aUrl" );
      writer.appendSet( buttonId, "fake", 1 );

      var message = getMessage();
      assertEquals( 1, message.operations.length );
      var operation = message.operations[ 0 ];
      assertEquals( "set", operation[ 0 ] );
      assertEquals( buttonId, operation[ 1 ] );
      assertEquals( "newText", operation[ 2 ][ "text" ] );
      assertEquals( "aUrl", operation[ 2 ][ "image" ] );
      assertEquals( 1, operation[ 2 ][ "fake" ] );
    },

    testMessageWithSetTwice : function() {
      var shellId = "w2";
      var buttonId = "w5";

      writer.appendSet( shellId, "text", "newText2" );
      writer.appendSet( shellId, "image", false );
      writer.appendSet( shellId, "fake", 2 );
      writer.appendSet( buttonId, "text", "newText" );
      writer.appendSet( buttonId, "image", true );
      writer.appendSet( buttonId, "fake", 1 );

      var message = getMessage();
      assertEquals( 2, message.operations.length );
      var operation = message.operations[ 1 ];
      assertEquals( buttonId, operation[ 1 ] );
      assertEquals( "newText", operation[ 2 ][ "text" ] );
      assertEquals( 1, operation[ 2 ][ "fake" ] );
      assertTrue( operation[ 2 ][ "image" ] );
    },

    testMessageWithSetDuplicateProperty : function() {
      var buttonId = "w5";

      writer.appendSet( buttonId, "text", "newText" );
      writer.appendSet( buttonId, "text", "newText" );

      var message = getMessage();
      assertEquals( 1, message.operations.length );
      var operation = message.operations[ 0 ];
      assertEquals( buttonId, operation[ 1 ] );
      assertEquals( "newText", operation[ 2 ][ "text" ] );
    },

    testMessageWithCallBetweenSetDuplicateProperty : function() {
      var buttonId = "w5";

      writer.appendSet( buttonId, "text", "newText" );
      writer.appendCall( buttonId, "methodName", null );
      writer.appendSet( buttonId, "text", "newText" );

      var message = getMessage();
      assertEquals( 3, message.operations.length );
      assertEquals( "newText", message.operations[ 0 ][ 2 ][ "text" ] );
      assertEquals( "methodName", message.operations[ 1 ][ 2 ] );
      assertEquals( "newText", message.operations[ 2 ][ 2 ][ "text" ] );
    },

    /////////
    // Helper

    setUp : function() {
      writer = new org.eclipse.rwt.protocol.Writer();
    },

    tearDown : function() {
      writer.dispose();
      writer = null;
    },

    _getDummyTarget : function( targetId ) {
      var log = [];
      var targetObject = {
        _userData : {
        },
        _parent : null,
        _styleMap : null,
        setParent : function( parent ) {
          this._parent = parent;
        },
        _renderAppearance : function() {
        },
        getParent : function() {
          return this._parent;
        },
        addState : function( state ) {
          log.push( state );
        },
        setWidth : function( value ) {
          log.push( "width", value );
        },
        setHeight : function( value ) {
          log.push( "height", value );
        },
        setCoolness : function( value ) {
          log.push( "coolness", value );
        },
        setFail : function( value ) {
          throw "myerror";
        },
        setMyData : function( key, value ) {
          this._userData[ key ] = value;
        },
        getMyData : function( key ) {
          return this._userData[ key ];
        },
        getProperties : function() {
          return this._prop;
        },
        getStyleMap : function() {
          return org.eclipse.rwt.protocol.AdapterUtil.createStyleMap( this._prop.style );
        },
        doFoo : function( arg ) {
          log.push( "foo", arg );
        },
        destroy : function() {
          this.setParent( null );
          log.push( "destroy" );
        },
        getDisposed : function() {
          return false;
        },
        setToolTip : function() {
          // NOTE: Currently needed (as getDisposed) by WidgetManager
          // should not be the case.
        },
        setHasFooListener : function( value ) {
          log.push( "fooListener", value );
        },
        classname : "myclass"
      };
      targetObject.getLog = function() {
        return log;
      };
      if( typeof targetId === "string" ) {
        var adapter = org.eclipse.rwt.protocol.AdapterRegistry.getAdapter( "dummyType" );
        org.eclipse.rwt.protocol.ObjectManager.add( targetId, targetObject, adapter );
      }
      return targetObject;
    },

    _getDummyFactory : function() {
      var constr = function( properties ) {
        if( properties ) {
          org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( this, properties.style );
        }
        this._prop = properties;
      };
      constr.prototype = this._getDummyTarget();
      var result = function( prop ) {
        return new constr( prop );
      };
      return result;
    },

    _getTargetById : function( id ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      return widgetManager.findWidgetById( id );
    },

    _isControl : function( target ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      return widgetManager.isControl( target );
    },

    _getDummyWidget : function( targetId ) {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var result = new qx.ui.basic.Terminator();
      result.addToDocument();
      result.setLeft( 10 );
      result.setTop( 10 );
      result.setWidth( 100 );
      result.setHeight( 20 );
      TestUtil.flush();
      if( typeof targetId === "string" ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        widgetManager.add( result, targetId, true, "dummyType" );
      }
      return result;
    }

  }

} );

}());