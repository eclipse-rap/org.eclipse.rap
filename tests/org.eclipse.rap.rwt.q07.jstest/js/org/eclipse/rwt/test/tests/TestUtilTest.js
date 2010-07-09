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
    
    testPress : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      testUtil.flush();
      var log = [];
      widget.addEventListener( "keydown" , function( event ) { 
        log.push( "keydown" ); 
        log.push( event.getKeyIdentifier() );
      } );
      widget.addEventListener( "keypress" , function( event ) { 
        log.push( "keypress" );
        log.push( event.getKeyIdentifier() );
      } );
      widget.addEventListener( "keyinput" , function( event ) { 
        log.push( "keyinput" ); 
        log.push( event.getKeyIdentifier() );
      } );
      widget.addEventListener( "keyup" , function( event ) { 
        log.push( "keyup" );
        log.push( event.getKeyIdentifier() );
      } );
      testUtil.press( widget, "x" );
      var expected 
        = [ "keydown", "x", "keypress", "x", "keyinput", "x", "keyup", "x", ];
      assertEquals( expected, log );
      widget.destroy();
    },
    
    testShiftPress : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      testUtil.flush();
      var log = [];
      widget.addEventListener( "keydown" , function( event ) { 
        log.push( event.getModifiers() );
      } );
      widget.addEventListener( "keypress" , function( event ) { 
        log.push( event.getModifiers() );
      } );
      widget.addEventListener( "keyinput" , function( event ) { 
        log.push( event.getModifiers() );
      } );
      widget.addEventListener( "keyup" , function( event ) { 
        log.push( event.getModifiers() );
      } );
      testUtil.shiftPress( widget, "x" );
      var shift = qx.event.type.DomEvent.SHIFT_MASK;
      var expected = [ shift, shift, shift, shift ];
      assertEquals( expected, log );
      widget.destroy();
    },
    
    testCtrlPress : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      testUtil.flush();
      var log = [];
      widget.addEventListener( "keydown" , function( event ) { 
        log.push( event.getModifiers() );
      } );
      widget.addEventListener( "keypress" , function( event ) { 
        log.push( event.getModifiers() );
      } );
      widget.addEventListener( "keyinput" , function( event ) { 
        log.push( event.getModifiers() );
      } );
      widget.addEventListener( "keyup" , function( event ) { 
        log.push( event.getModifiers() );
      } );
      testUtil.ctrlPress( widget, "x" );
      var ctrl = qx.event.type.DomEvent.CTRL_MASK;
      var expected = [ ctrl, ctrl, ctrl, ctrl ];
      assertEquals( expected, log );
      widget.destroy();
    },
    
    testAltPress : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      testUtil.flush();
      var log = [];
      widget.addEventListener( "keydown" , function( event ) { 
        log.push( event.getModifiers() );
      } );
      widget.addEventListener( "keypress" , function( event ) { 
        log.push( event.getModifiers() );
      } );
      widget.addEventListener( "keyinput" , function( event ) { 
        log.push( event.getModifiers() );
      } );
      widget.addEventListener( "keyup" , function( event ) { 
        log.push( event.getModifiers() );
      } );
      testUtil.altPress( widget, "x" );
      var alt = qx.event.type.DomEvent.ALT_MASK;
      var expected = [ alt, alt, alt, alt ];
      assertEquals( expected, log );
      widget.destroy();
    }

  }

} );