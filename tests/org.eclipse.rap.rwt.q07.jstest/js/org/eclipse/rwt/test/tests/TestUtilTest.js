/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.TestUtilTest", {

  extend : qx.core.Object,
  
  members : {
    
    testGetElementBounds : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var parent = document.createElement( "div" );
      var child = document.createElement( "div" );
      parent.style.width = "100px";
      parent.style.height = "200px";
      parent.appendChild( child );
      child.style.top = "20px";
      child.style.left = "30px";
      child.style.width = "50px";
      child.style.height = "70px";
      var bounds = testUtil.getElementBounds( child );
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
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var parent = document.createElement( "div" );
      var child = document.createElement( "div" );
      parent.style.width = "100px";
      parent.style.height = "200px";
      parent.appendChild( child );
      child.style.width = "50px";
      child.style.height = "70px";
      var bounds = testUtil.getElementBounds( child );
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
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var parent = document.createElement( "div" );
      var child = document.createElement( "div" );
      parent.style.width = "100px";
      parent.style.height = "200px";
      parent.appendChild( child );
      child.style.right = "20px";
      child.style.bottom = "30px";
      child.style.width = "50px";
      child.style.height = "70px";
      var bounds = testUtil.getElementBounds( child );
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
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var parent = document.createElement( "div" );
      var child = document.createElement( "div" );
      parent.style.width = "100px";
      parent.style.height = "200px";
      parent.appendChild( child );
      child.style.top = "20px";
      child.style.left = "30px";
      child.style.width = "50px";
      child.style.height = "70px";
      var bounds = testUtil.getElementLayout( child );
      var expected = [ 30, 20, 50, 70 ];
      assertEquals( expected, bounds );
    },
   
    testGetElementBoundsNoParent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var child = document.createElement( "div" );
      var log = [];
      try {
        testUtil.getElementBounds( child );
      } catch( ex ) {
        log.push( ex );
      }
      assertEquals( 1, log.length );
    },
   
    testGetElementBoundsNoPercantageOnChild : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
        testUtil.getElementBounds( child );
      } catch( ex ) {
        log.push( ex );
      }
      assertEquals( 1, log.length );
    },
   
    testGetElementBoundsNoPercantageOnParent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
        testUtil.getElementBounds( child );
      } catch( ex ) {
        log.push( ex );
      }
      assertEquals( 1, log.length );
    },
    
    testGetElementBoundsWidthImage : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var bounds = testUtil.getElementBounds( child );
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
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var bounds = testUtil.getElementBounds( child );
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
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var bounds = testUtil.getElementBounds( child );
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
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var div = document.createElement( "div" );
      div.style.backgroundImage = "url( test.gif )";
      var result = testUtil.getCssBackgroundImage( div );
      assertEquals( result.length - 8, result.indexOf( "test.gif" ) );
    },
    
    testFakeAppearance : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.setAppearance( "my-appearance" );
      testUtil.fakeAppearance( "my-appearance", {
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
    
    testOverwriteAppearance : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new org.eclipse.rwt.widgets.Button( "push" );
      assertEquals( "push-button", widget.getAppearance() );
      testUtil.fakeAppearance( "push-button", {
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
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.fakeAppearance( "my-appearance", {
        style : function( states ) {
          return {
            backgroundImage : "my-appearance.gif"
          };
        }
      } );
      testUtil.restoreAppearance();
      var widget = new qx.ui.basic.Terminator();
      widget.setAppearance( "my-appearance" );
      widget._renderAppearance();
      assertNull( widget.getBackgroundImage() );
      widget.destroy();
    },

    testRestoreAppearance : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new org.eclipse.rwt.widgets.Button( "push" );
      assertEquals( "push-button", widget.getAppearance() );
      testUtil.fakeAppearance( "push-button", {
        style : function( states ) {
          return {
            backgroundImage : "my-appearance.gif"
          };
        }
      } );
      widget._renderAppearance();
      testUtil.restoreAppearance();
      widget._renderAppearance();
      assertNull( widget.getBackgroundImage() );
      widget.destroy();
    },
    
    testFakeWheel : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      testUtil.flush();
      var log = [];
      widget.addEventListener( "mousewheel", function( evt ) {
        log.push( evt.getWheelDelta() );
      } );
      testUtil.fakeWheel( widget, 2 );
      testUtil.fakeWheel( widget, -3 );
      assertEquals( [ 2, -3 ], log );
      widget.destroy();
    },
    
    testFakeMouseEventDomModifier : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      testUtil.flush();
      var log = [];
      widget.addEventListener( "mousedown", function( evt ) {
        log.push( evt.getModifiers() );
      } );
      var node = widget._getTargetNode();
      var left = qx.event.type.MouseEvent.buttons.left;
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 0, 0, 0 );
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 0, 0, 1 );
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 0, 0, 2 );
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 0, 0, 4 );
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 0, 0, 7 );
      assertEquals( [ 0, 1, 2, 4, 7 ], log );
      widget.destroy();
    },
    
    testGetElementFontShorthand : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var el = document.createElement( "div" );
      el.style.font = "10px italic bold Arial";
      var font = testUtil.getElementFont( el );
      assertTrue( font.indexOf( "10px" ) != -1 );
      assertTrue( font.indexOf( "Arial" ) != -1 );
      assertTrue( font.indexOf( "italic" ) != -1 );
      assertTrue( font.indexOf( "bold" ) != -1 );
    },

    testGetElementFontSingleProps : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var el = document.createElement( "div" );
      el.style.font = "10px italic bold Arial";
      el.style.fontFamily = "Arial";
      el.style.fontSize = "10px";
      el.style.fontStyle = "italic";
      el.style.fontWeight = "bold";
      var font = testUtil.getElementFont( el );      
      assertTrue( font.indexOf( "10px" ) != -1 );
      assertTrue( font.indexOf( "Arial" ) != -1 );
      assertTrue( font.indexOf( "italic" ) != -1 );
      assertTrue( font.indexOf( "bold" ) != -1 );
    },

    testDoubleClick : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      testUtil.flush();
      var log = [];
      widget.addEventListener( "click" , function() { log.push( "click" ); } );
      widget.addEventListener( "dblclick" , function() { 
        log.push( "dblclick" ); 
      } );
      testUtil.doubleClick( widget );
      assertEquals( [ "click", "click", "dblclick" ], log );
      widget.destroy();
    },
   
    testPressPrintable : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      testUtil.flush();
      var log = this._addKeyLogger( widget, true, true, false );
      widget.focus();
      testUtil.press( widget, "x" );
      // NOTE [tb] : the identifier is always uppercase
      var expected = [ "keydown", "X", "keypress", "X", "keyup", "X", ];
      assertEquals( expected, log );
      widget.destroy();
    },    
   
    testPressPrintableSpecialChar : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      testUtil.flush();
      var log = this._addKeyLogger( widget, true, true, false );
      widget.focus();
      testUtil.press( widget, "Space" );
      var expected 
        = [ "keydown", "Space", "keypress", "Space", "keyup", "Space", ];
      assertEquals( expected, log );
      widget.destroy();
    },    
   
    testPressPrintableSpecialCharNoKeyInput : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      testUtil.flush();
      var log = this._addKeyLogger( widget, true, true, false );
      widget.focus();
      testUtil.press( widget, "Enter" );
      var expected = [ "keydown", "Enter", "keypress", "Enter" ];
      if( qx.core.Variant.isSet( "qx.client", "opera" ) ) {
        expected.push( "keyup", "Enter" );
      } else {
        expected.push( "keyup", "Enter" );
      }
      assertEquals( expected, log );
      widget.destroy();
    },    

    testPressNonPrintable : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      testUtil.flush();
      var log = this._addKeyLogger( widget, true, true, false );
      widget.focus();
      testUtil.press( widget, "Left" );
      // NOTE [tb] : the identifier is currently always uppercase
      var expected 
        = [ "keydown", "Left", "keypress", "Left", "keyup", "Left", ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testShiftPress : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      testUtil.flush();
      var log = this._addKeyLogger( widget, false, false, true );
      testUtil.shiftPress( widget, "x" );
      var shift = qx.event.type.DomEvent.SHIFT_MASK;
      var expected = [ shift, shift, shift ];
      assertEquals( expected, log );
      widget.destroy();
    },
    
    testCtrlPress : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      testUtil.flush();
      var log = this._addKeyLogger( widget, false, false, true );
      testUtil.ctrlPress( widget, "x" );
      var ctrl = qx.event.type.DomEvent.CTRL_MASK;
      var expected = [ ctrl, ctrl, ctrl ];
      assertEquals( expected, log );
      widget.destroy();
    },
    
    testAltPress : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      testUtil.flush();
      var log = this._addKeyLogger( widget, false, false, true );
      testUtil.altPress( widget, "x" );
      var alt = qx.event.type.DomEvent.ALT_MASK;
      var expected = [ alt, alt, alt ];
      assertEquals( expected, log );
      widget.destroy();
    },
    
    testHoverFromTo : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget1 = new qx.ui.basic.Terminator();
      var widget2 = new qx.ui.basic.Terminator();
      widget1.addToDocument();
      widget2.addToDocument();
      testUtil.flush();
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
      testUtil.hoverFromTo( node1, node2 );
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
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        assertTrue( "Test1", true );
        testUtil.store( 1 );
      },
      function( value1, value2 ) {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        assertEquals( 1, value1 );
        assertIdentical( undefined, value2 );
        testUtil.store( 1, 2 );
      },
      function( value1, value2 ) {
        assertEquals( 1, value1 );
        assertEquals( 2, value2 );
      }  
    ],
    
    testDelayTest : [
      // NOTE: accuarcy of timeout is about 16-32 ms
      function() {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
        testUtil.delayTest( 70 );
        testUtil.store( store );
      },
      function( store ) {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        assertEquals( "delayed at least 40ms", 1, store.x );
        store.inc( 70 );
        testUtil.delayTest( 30 );
        testUtil.store( store );
      },
      function( store ) {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        assertEquals( "delayed less than 70ms", 1, store.x );
        testUtil.delayTest( 60 );
        testUtil.store( store );
      },
      function( store ) {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        assertEquals( "sum of delay greater 70ms", 2, store.x );
        store.inc( 50 );
        testUtil.store( store );
      },
      function( store ) {
        assertEquals( "no more delay", 2, store.x );
      }
    ],
    
    testScheduleResponse : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.initRequestLog();
      var x = 1;
      testUtil.scheduleResponse( function(){
        x = 2;
      } );
      org.eclipse.swt.Request.getInstance().send();        
      assertEquals( 2, x );
      x = 1;
      org.eclipse.swt.Request.getInstance().send();        
      assertEquals( 1, x );
    },
    
    /////////
    // helper
    
    _addKeyLogger : function( widget, type, identifier, modifier ) {
      var log = [];
      var logger = function( event ) {
        if( type ) {
          log.push( event.getType() );
        }
        if( identifier ) {
          log.push( event.getKeyIdentifier() );
        }
        if( modifier ) {
          log.push( event.getModifiers() );
        }
      }
      widget.addEventListener( "keydown", logger );
      widget.addEventListener( "keypress", logger );
      widget.addEventListener( "keyup", logger );
      return log;
    }

  }

} );