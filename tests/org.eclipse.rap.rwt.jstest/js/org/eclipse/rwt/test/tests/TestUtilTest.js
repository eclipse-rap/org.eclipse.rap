/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.TestUtilTest", {

  extend : rwt.qx.Object,

  members : {

    testSendSynchronousRequestsBug : function() {
      var req = rwt.remote.Server.getInstance();
      req.send();
      var counter = req.getRequestCounter();
      req.sendImmediate( false );
      counter++;
      assertEquals( counter, req.getRequestCounter() );
      req.send();
      counter++;
      assertEquals( counter, req.getRequestCounter() );
      req.send();
      counter++;
      assertEquals( counter, req.getRequestCounter() );
    },

    testGetElementBounds : function() {
      var parent = document.createElement( "div" );
      var child = document.createElement( "div" );
      parent.style.width = "100px";
      parent.style.height = "200px";
      parent.appendChild( child );
      child.style.top = "20px";
      child.style.left = "30px";
      child.style.width = "50px";
      child.style.height = "70px";
      var bounds = TestUtil.getElementBounds( child );
      var expected = {
        "top" : 20,
        "left" : 30,
        "width" : 50,
        "height" : 70,
        "right" : 20,
        "bottom" : 110
      };
      assertEquals( expected, bounds );
    },


    testGetElementBoundsNoLeftTop : function() {
      var parent = document.createElement( "div" );
      var child = document.createElement( "div" );
      parent.style.width = "100px";
      parent.style.height = "200px";
      parent.appendChild( child );
      child.style.width = "50px";
      child.style.height = "70px";
      var bounds = TestUtil.getElementBounds( child );
      var expected = {
        "top" : 0,
        "left" : 0,
        "width" : 50,
        "height" : 70,
        "right" : 50,
        "bottom" : 130
      };
      assertEquals( expected, bounds );
    },

    testGetElementBoundsUsingRightBottom : function() {
      var parent = document.createElement( "div" );
      var child = document.createElement( "div" );
      parent.style.width = "100px";
      parent.style.height = "200px";
      parent.appendChild( child );
      child.style.right = "20px";
      child.style.bottom = "30px";
      child.style.width = "50px";
      child.style.height = "70px";
      var bounds = TestUtil.getElementBounds( child );
      var expected = {
        "top" : 100,
        "left" : 30,
        "width" : 50,
        "height" : 70,
        "right" : 20,
        "bottom" : 30
      };
      assertEquals( expected, bounds );
    },

    testGetElementLayout : function() {
      var parent = document.createElement( "div" );
      var child = document.createElement( "div" );
      parent.style.width = "100px";
      parent.style.height = "200px";
      parent.appendChild( child );
      child.style.top = "20px";
      child.style.left = "30px";
      child.style.width = "50px";
      child.style.height = "70px";
      var bounds = TestUtil.getElementLayout( child );
      var expected = [ 30, 20, 50, 70 ];
      assertEquals( expected, bounds );
    },

    testGetElementBoundsNoParent : function() {
      var child = document.createElement( "div" );
      var log = [];
      try {
        TestUtil.getElementBounds( child );
      } catch( ex ) {
        log.push( ex );
      }
      assertEquals( 1, log.length );
    },

    testGetElementBoundsNoPercantageOnChild : function() {
      var parent = document.createElement( "div" );
      var child = document.createElement( "div" );
      parent.style.width = "100%";
      parent.style.height = "200px";
      parent.appendChild( child );
      child.style.top = "20px";
      child.style.left = "30px";
      child.style.width = "50px";
      child.style.height = "70px";
      var log = [];
      try {
        TestUtil.getElementBounds( child );
      } catch( ex ) {
        log.push( ex );
      }
      assertEquals( 1, log.length );
    },

    testGetElementBoundsNoPercantageOnParent : function() {
      var parent = document.createElement( "div" );
      var child = document.createElement( "div" );
      parent.style.width = "100px";
      parent.style.height = "200px";
      parent.appendChild( child );
      child.style.top = "20px";
      child.style.left = "30px";
      child.style.width = "100%";
      child.style.height = "70px";
      var log = [];
      try {
        TestUtil.getElementBounds( child );
      } catch( ex ) {
        log.push( ex );
      }
      assertEquals( 1, log.length );
    },

    testGetElementBoundsWidthImage : function() {
      var parent = document.createElement( "div" );
      var child = document.createElement( "div" );
      parent.style.width = "100px";
      parent.style.height = "200px";
      parent.appendChild( child );
      child.style.top = "20px";
      child.style.left = "30px";
      child.style.width = "50px";
      child.style.height = "70px";
      child.style.backgroundRepeat = "no-repeat";
      child.style.backgroundPosition = "center";
      var bounds = TestUtil.getElementBounds( child );
      var expected = {
        "top" : 20,
        "left" : 30,
        "width" : 50,
        "height" : 70,
        "right" : 20,
        "bottom" : 110
      };
      assertEquals( expected, bounds );
    },

    testGetElementBoundsWidthBorder : function() {
      var parent = document.createElement( "div" );
      var child = document.createElement( "div" );
      parent.style.width = "100px";
      parent.style.height = "200px";
      parent.style.border = "5px solid black";
      parent.appendChild( child );
      child.style.top = "20px";
      child.style.left = "30px";
      child.style.width = "50px";
      child.style.height = "70px";
      var bounds = TestUtil.getElementBounds( child );
      var expected = {
        "top" : 20,
        "left" : 30,
        "width" : 50,
        "height" : 70,
        "right" : 10,
        "bottom" : 100
      };
      assertEquals( expected, bounds );
    },

    testGetElementBoundsWidthDifferentEdges : function() {
      var parent = document.createElement( "div" );
      var child = document.createElement( "div" );
      parent.style.width = "100px";
      parent.style.height = "200px";
      parent.style.borderWidth = "5px 4px 3px 2px";
      parent.appendChild( child );
      child.style.top = "20px";
      child.style.left = "30px";
      child.style.width = "50px";
      child.style.height = "70px";
      var bounds = TestUtil.getElementBounds( child );
      var expected = {
        "top" : 20,
        "left" : 30,
        "width" : 50,
        "height" : 70,
        "right" : 14,
        "bottom" : 102
      };
      assertEquals( expected, bounds );
    },

    testGetCssBackgroundImage : function() {
      // TODO : test for IE-filter
      var div = document.createElement( "div" );
      div.style.backgroundImage = "url( test.gif )";
      var result = TestUtil.getCssBackgroundImage( div );
      assertEquals( result.length - 8, result.indexOf( "test.gif" ) );
    },

    testFakeAppearance : function() {
      var widget = new rwt.widgets.base.Terminator();
      TestUtil.fakeAppearance( "my-appearance", {
        style : function( states ) {
          return {
            backgroundImage : "my-appearance.gif"
          };
        }
      } );
      widget.setAppearance( "my-appearance" );
      widget._renderAppearance();
      assertEquals( "my-appearance.gif", widget.getBackgroundImage() );
      widget.destroy();
    },

    testOverwriteAppearance : function() {
      var widget = new rwt.widgets.Button( "push" );
      assertEquals( "push-button", widget.getAppearance() );
      TestUtil.fakeAppearance( "push-button", {
        style : function( states ) {
          return {
            backgroundImage : "my-appearance.gif"
          };
        }
      } );
      widget._renderAppearance();
      assertEquals( "my-appearance.gif", widget.getBackgroundImage() );
      widget.destroy();
    },

    testDeleteFakeAppearance : function() {
      TestUtil.fakeAppearance( "my-appearance", {
        style : function( states ) {
          return {
            backgroundImage : "my-appearance.gif"
          };
        }
      } );
      TestUtil.restoreAppearance();
      var widget = new rwt.widgets.base.Terminator();
      try{
        widget.setAppearance( "my-appearance" );
        widget._renderAppearance();
      }catch( ex ) {
      }
      assertNull( widget.getBackgroundImage() );
      widget.destroy();
    },

    testRestoreAppearance : function() {
      var widget = new rwt.widgets.Button( "push" );
      assertEquals( "push-button", widget.getAppearance() );
      TestUtil.fakeAppearance( "push-button", {
        style : function( states ) {
          return {
            backgroundImage : "my-appearance.gif"
          };
        }
      } );
      widget._renderAppearance();
      TestUtil.restoreAppearance();
      widget._renderAppearance();
      assertNull( widget.getBackgroundImage() );
      widget.destroy();
    },

    testFakeWheel : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      var log = [];
      widget.addEventListener( "mousewheel", function( evt ) {
        log.push( evt.getWheelDelta() );
      } );
      TestUtil.fakeWheel( widget, 2 );
      TestUtil.fakeWheel( widget, -3 );
      assertEquals( [ 2, -3 ], log );
      widget.destroy();
    },

    testFakeMouseEventDomModifier : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      var log = [];
      widget.addEventListener( "mousedown", function( evt ) {
        log.push( evt.getModifiers() );
      } );
      var node = widget._getTargetNode();
      var left = rwt.event.MouseEvent.buttons.left;
      TestUtil.fakeMouseEventDOM( node, "mousedown", left, 0, 0, 0 );
      TestUtil.fakeMouseEventDOM( node, "mousedown", left, 0, 0, 1 );
      TestUtil.fakeMouseEventDOM( node, "mousedown", left, 0, 0, 2 );
      TestUtil.fakeMouseEventDOM( node, "mousedown", left, 0, 0, 4 );
      TestUtil.fakeMouseEventDOM( node, "mousedown", left, 0, 0, 7 );
      assertEquals( [ 0, 1, 2, 4, 7 ], log );
      widget.destroy();
    },

    testGetElementFontShorthand : function() {
      var el = document.createElement( "div" );
      el.style.font = "10px italic bold Arial";
      var font = TestUtil.getElementFont( el );
      assertTrue( font.indexOf( "10px" ) != -1 );
      assertTrue( font.indexOf( "Arial" ) != -1 );
      assertTrue( font.indexOf( "italic" ) != -1 );
      assertTrue( font.indexOf( "bold" ) != -1 );
    },

    testGetElementFontSingleProps : function() {
      var el = document.createElement( "div" );
      el.style.font = "10px italic bold Arial";
      el.style.fontFamily = "Arial";
      el.style.fontSize = "10px";
      el.style.fontStyle = "italic";
      el.style.fontWeight = "bold";
      var font = TestUtil.getElementFont( el );
      assertTrue( font.indexOf( "10px" ) != -1 );
      assertTrue( font.indexOf( "Arial" ) != -1 );
      assertTrue( font.indexOf( "italic" ) != -1 );
      assertTrue( font.indexOf( "bold" ) != -1 );
    },

    testDoubleClick : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      var log = [];
      widget.addEventListener( "click" , function() { log.push( "click" ); } );
      widget.addEventListener( "dblclick" , function() {
        log.push( "dblclick" );
      } );
      TestUtil.doubleClick( widget );
      assertEquals( [ "click", "click", "dblclick" ], log );
      widget.destroy();
    },

    testSendKeyPress : rwt.util.Variant.select("qx.client",  {
      "gecko|opera" : function() {
        assertTrue( TestUtil._sendKeyPress( "a" ) );
        assertTrue( TestUtil._sendKeyPress( "A" ) );
        assertTrue( TestUtil._sendKeyPress( 65 ) );
        assertTrue( TestUtil._sendKeyPress( "Enter" ) );
        assertTrue( TestUtil._sendKeyPress( 13 ) );
        assertTrue( TestUtil._sendKeyPress( "F1" ) );
        assertTrue( TestUtil._sendKeyPress( 112 ) );
        assertTrue( TestUtil._sendKeyPress( "Space" ) );
        assertTrue( TestUtil._sendKeyPress( 32 ) );
        assertTrue( TestUtil._sendKeyPress( "Escape" ) );
        assertTrue( TestUtil._sendKeyPress( 27 ) );
        assertTrue( TestUtil._sendKeyPress( "Tab" ) );
        assertTrue( TestUtil._sendKeyPress( 9 ) );
        assertTrue( TestUtil._sendKeyPress( "Backspace" ) );
        assertTrue( TestUtil._sendKeyPress( 8 ) );
        if( rwt.client.Client.isOpera() ) {
          assertFalse( TestUtil._sendKeyPress( "Win" ) );
          assertFalse( TestUtil._sendKeyPress( 91 ) );
        } else {
          assertTrue( TestUtil._sendKeyPress( "Win" ) ); // opera false
          assertTrue( TestUtil._sendKeyPress( 91 ) );
        }
        assertTrue( TestUtil._sendKeyPress( "Left" ) );
        assertTrue( TestUtil._sendKeyPress( 37 ) );
        assertTrue( TestUtil._sendKeyPress( "Apps" ) );
        assertTrue( TestUtil._sendKeyPress( 93 ) );
        assertFalse( TestUtil._sendKeyPress( "Shift" ) );
        assertFalse( TestUtil._sendKeyPress( 16 ) );
        assertFalse( TestUtil._sendKeyPress( "Control" ) );
        assertFalse( TestUtil._sendKeyPress( 17 ) );
        assertFalse( TestUtil._sendKeyPress( "Alt" ) );
        assertFalse( TestUtil._sendKeyPress( 18 ) );
      },
      "default" : function() {
        assertTrue( TestUtil._sendKeyPress( "a" ) );
        assertTrue( TestUtil._sendKeyPress( "A" ) );
        assertTrue( TestUtil._sendKeyPress( 65 ) );
        assertTrue( TestUtil._sendKeyPress( "Enter" ) );
        assertTrue( TestUtil._sendKeyPress( 13 ) );
        assertFalse( TestUtil._sendKeyPress( "F1" ) );
        assertFalse( TestUtil._sendKeyPress( 112 ) );
        assertTrue( TestUtil._sendKeyPress( "Space" ) );
        assertTrue( TestUtil._sendKeyPress( 32 ) );
        if( rwt.client.Client.isWebkit() ) {
          assertFalse( TestUtil._sendKeyPress( "Escape" ) );
          assertFalse( TestUtil._sendKeyPress( 27 ) );
        } else {
          assertTrue( TestUtil._sendKeyPress( "Escape" ) );
          assertTrue( TestUtil._sendKeyPress( 27 ) );
        }
        assertFalse( TestUtil._sendKeyPress( "Tab" ) );
        assertFalse( TestUtil._sendKeyPress( 9 ) );
        assertFalse( TestUtil._sendKeyPress( "Backspace" ) );
        assertFalse( TestUtil._sendKeyPress( 8 ) );
        assertFalse( TestUtil._sendKeyPress( "Win" ) );
        assertFalse( TestUtil._sendKeyPress( 91 ) );
        assertFalse( TestUtil._sendKeyPress( "Left" ) );
        assertFalse( TestUtil._sendKeyPress( 37 ) );
        assertFalse( TestUtil._sendKeyPress( "Apps" ) );
        assertFalse( TestUtil._sendKeyPress( 93 ) );
        assertFalse( TestUtil._sendKeyPress( "Shift" ) );
        assertFalse( TestUtil._sendKeyPress( 16 ) );
        assertFalse( TestUtil._sendKeyPress( "Control" ) );
        assertFalse( TestUtil._sendKeyPress( 17 ) );
        assertFalse( TestUtil._sendKeyPress( "Alt" ) );
        assertFalse( TestUtil._sendKeyPress( 18 ) );
      }
    } ),

    testPressPrintable : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      var log = this._addKeyLogger( widget, true, true, false );
      widget.focus();
      TestUtil.press( widget, "x" );
      // NOTE [tb] : the identifier is always uppercase
      var expected = [ "keydown", "X", "keypress", "X", "keyup", "X" ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testPressPrintableSpecialChar : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      var log = this._addKeyLogger( widget, true, true, false );
      widget.focus();
      TestUtil.press( widget, "Space" );
      var expected = [ "keydown", "Space", "keypress", "Space", "keyup", "Space" ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testPressPrintableSpecialCharNoKeyInput : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      var log = this._addKeyLogger( widget, true, true, false );
      widget.focus();
      TestUtil.press( widget, "Enter" );
      var expected = [ "keydown", "Enter", "keypress", "Enter" ];
      if( rwt.util.Variant.isSet( "qx.client", "opera" ) ) {
        expected.push( "keyup", "Enter" );
      } else {
        expected.push( "keyup", "Enter" );
      }
      assertEquals( expected, log );
      widget.destroy();
    },

    testPressNonPrintable : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      var log = this._addKeyLogger( widget, true, true, false );
      widget.focus();
      TestUtil.press( widget, "Left" );
      // NOTE [tb] : the identifier is currently always uppercase
      var expected = [ "keydown", "Left", "keypress", "Left", "keyup", "Left" ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testPressAppsKey : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      var log = this._addKeyLogger( widget, true, true, false );
      widget.focus();
      TestUtil.press( widget, "Apps" );
      var expected = [ "keydown", "Apps", "keypress", "Apps", "keyup", "Apps" ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testPressEnter : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      var log = [];
      widget.addEventListener( "keypress", function( event ) {
        log.push( event.getDomEvent().keyCode );
        log.push( event.getDomEvent().charCode );
      } );
      widget.focus();
      TestUtil.press( widget, "Enter" );
      var expected = rwt.util.Variant.select( "qx.client", {
        "webkit" : [ 13, 13 ],
        "mshtml|opera|newmshtml" : [ 13, undefined ],
        "default" : [ 13, 0 ]
      } );
      assertEquals( expected, log );
      widget.destroy();
    },

    testShiftPress : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      var log = this._addKeyLogger( widget, false, false, true );
      TestUtil.shiftPress( widget, "x" );
      var shift = rwt.event.DomEvent.SHIFT_MASK;
      var expected = [ shift, shift, shift ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testCtrlPress : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      var log = this._addKeyLogger( widget, false, false, true );
      TestUtil.ctrlPress( widget, "x" );
      var ctrl = rwt.event.DomEvent.CTRL_MASK;
      var expected = [ ctrl, ctrl, ctrl ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testAltPress : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      var log = this._addKeyLogger( widget, false, false, true );
      TestUtil.altPress( widget, "x" );
      var alt = rwt.event.DomEvent.ALT_MASK;
      var expected = [ alt, alt, alt ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testHoverFromTo : function() {
      var widget1 = new rwt.widgets.base.Terminator();
      var widget2 = new rwt.widgets.base.Terminator();
      widget1.addToDocument();
      widget2.addToDocument();
      TestUtil.flush();
      var node1 = widget1._getTargetNode();
      var node2 = widget2._getTargetNode();
      var log = [];
      var handler = function( event ) {
        log.push( event.getType() );
        log.push( event.getTarget(), event.getRelatedTarget() );
      };
      widget1.addEventListener( "mouseover", handler );
      widget1.addEventListener( "mouseout", handler );
      widget2.addEventListener( "mouseover", handler );
      widget2.addEventListener( "mouseout", handler );
      TestUtil.hoverFromTo( node1, node2 );
      var expected = [
        "mouseout",
        widget1,
        widget2,
        "mouseover",
        widget2,
        widget1
      ];
      assertEquals( expected, log );
      widget1.destroy();
      widget2.destroy();
    },

    testStore : [
      function() {
        assertTrue( "Test1", true );
        TestUtil.store( 1 );
      },
      function( value1, value2 ) {
        assertEquals( 1, value1 );
        assertIdentical( undefined, value2 );
        TestUtil.store( 1, 2 );
      },
      function( value1, value2 ) {
        assertEquals( 1, value1 );
        assertEquals( 2, value2 );
      }
    ],

    // NOTE : This test randomly fails in old IE due to very bad timer accuaracy.
    //        Possibly connected to test order. Test succeeds when choosing larger timespans,
    //        but this would make it less meaningful on other browser.
    testDelayTest : rwt.client.Client.isMshtml() ? null : [
      // NOTE: accuarcy of timeout is about 16-32 ms
      function() {
        var store = {
          x : 0,
          inc : function( delay ) {
            var that = this;
            window.setTimeout( function() {
              that.x++;
            }, delay );
          }
        };
        store.inc( 40 );
        TestUtil.delayTest( 70 );
        TestUtil.store( store );
      },
      function( store ) {
        assertEquals( "delayed at least 40ms", 1, store.x );
        store.inc( 70 );
        TestUtil.delayTest( 30 );
        TestUtil.store( store );
      },
      function( store ) {
        assertEquals( "delayed less than 70ms", 1, store.x );
        TestUtil.delayTest( 60 );
        TestUtil.store( store );
      },
      function( store ) {
        assertEquals( "sum of delay greater 70ms", 2, store.x );
        store.inc( 50 );
        TestUtil.store( store );
      },
      function( store ) {
        assertEquals( "no more delay", 2, store.x );
      }
    ],

    testScheduleResponse : function() {
      TestUtil.initRequestLog();
      var x = 1;
      TestUtil.scheduleResponse( function(){
        x = 2;
      } );
      rwt.remote.Server.getInstance().send();
      assertEquals( 2, x );
      x = 1;
      rwt.remote.Server.getInstance().send();
      assertEquals( 1, x );
    },

    testCatchErrorPage : function() {
      TestUtil.clearErrorPage();
      rwt.runtime.ErrorHandler.showErrorPage( "foobar" );
      assertEquals( "foobar", TestUtil.getErrorPage() );
      TestUtil.clearErrorPage();
      assertNull( TestUtil.getErrorPage() );
    },

    testCleanUpKeyUtil : function() {
      var keyUtil = rwt.remote.KeyEventSupport.getInstance();
      var widget = this._createWidget();
      widget.setUserData( "isControl", true );
      widget.setUserData( "keyListener", true );
      widget.focus();
      var bindings = { "66" : true };
      keyUtil.setKeyBindings( bindings );
      TestUtil.press( widget, "a", false, 0 );
      TestUtil.press( widget, "b", false, 0 );
      TestUtil.press( widget, "c", false, 0 );
      TestUtil.forceTimerOnce();
      assertIdentical( bindings, keyUtil._keyBindings );
      TestUtil.cleanUpKeyUtil();
      assertEquals( {}, keyUtil._keyBindings );
      widget.destroy();
    },

    ///////////////////
    // Protocol related

    testCreateShellByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      assertTrue( shell instanceof rwt.widgets.Shell );
      shell.destroy();
    },

    testResetObjectManager : function() {
      TestUtil.createShellByProtocol( "w2" );

      assertTrue( null != rwt.remote.ObjectRegistry.getObject( "w1" ) );
      TestUtil.resetObjectManager();

      assertTrue( null == rwt.remote.ObjectRegistry.getObject( "w2" ) );
      assertTrue( null != rwt.remote.ObjectRegistry.getObject( "w1" ) );
    },

    testProtocolListen : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      TestUtil.protocolListen( "w2", { "Move" : true } );
      assertTrue( shell._hasMoveListener );
      shell.destroy();
    },

    testProtocolSet : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      TestUtil.protocolSet( "w2", { "customVariant" : "variant_blue" } );
      assertTrue( shell.hasState( "variant_blue" ) );
      shell.destroy();
    },

    testHasNoObjectsTrue : function() {
      var object = {
        "foo" : 1,
        "bar" : "a",
        "bang" : true,
        "puff" : null,
        "doing" : undefined
      };
      assertTrue( TestUtil.hasNoObjects( object ) );
    },

    testHasNoObjectsFalse : function() {
      assertFalse( TestUtil.hasNoObjects( { "x" : {} } ) );
      assertFalse( TestUtil.hasNoObjects( { "x" : [] } ) );
      assertFalse( TestUtil.hasNoObjects( { "x" : new Boolean( true ) } ) );
      assertFalse( TestUtil.hasNoObjects( { "x" : function(){} } ) );
      assertFalse( TestUtil.hasNoObjects( { "x" : /./ } ) );
    },

    testCreateXMLHttpRequest : function() {
      TestUtil.clearXMLHttpRequests();
      var request = rwt.remote.Request.createXHR();

      assertIdentical( request, TestUtil.getXMLHttpRequests()[ 0 ] );
    },

    testCreateMultipleXMLHttpRequest : function() {
      TestUtil.clearXMLHttpRequests();
      var requestOne = rwt.remote.Request.createXHR();
      var requestTwo = rwt.remote.Request.createXHR();

      assertIdentical( requestOne, TestUtil.getXMLHttpRequests()[ 0 ] );
      assertIdentical( requestTwo, TestUtil.getXMLHttpRequests()[ 1 ] );
    },

    testLogger : function() {
      var logger = TestUtil.getLogger();

      logger.log( 1 );
      logger.log( 2 );
      logger.log( 3 );

      assertEquals( [ 1, 2, 3 ], logger.getLog() );
    },

    /////////
    // helper

    _createWidget : function() {
      var result = new rwt.widgets.base.MultiCellWidget( [] );
      result.addToDocument();
      result.setLocation( 0, 0 );
      result.setDimension( 100, 100 );
      rwt.widgets.base.Widget.flushGlobalQueues();
      rwt.remote.ObjectRegistry.add( "w11", result );
      return result;
    },

    /*global glob: true */
    _addKeyLogger : function( widget, type, identifier, modifier ) {
      var log = [];
      var logger = function( event ) {
        if( typeof glob === "undefined" ) {
          glob = [];
        }
        glob.push( event );
        if( type ) {
          log.push( event.getType() );
        }
        if( identifier ) {
          log.push( event.getKeyIdentifier() );
        }
        if( modifier ) {
          log.push( event.getModifiers() );
        }
      };
      widget.addEventListener( "keydown", logger );
      widget.addEventListener( "keypress", logger );
      widget.addEventListener( "keyup", logger );
      return log;
    }

  }

} );

}());