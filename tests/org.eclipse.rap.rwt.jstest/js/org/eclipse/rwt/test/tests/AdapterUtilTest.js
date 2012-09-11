/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.AdapterUtilTest", {

  extend : qx.core.Object,
  
  members : {
    
    testGradientHandler : function() {
      var handler = rwt.protocol.AdapterUtil.getBackgroundGradientHandler();
      var widget = new rwt.widgets.Composite();
      handler( widget, [ [ [ 255, 0, 0, 255 ], [ 255, 255, 0, 255 ] ], [ 0, 100 ], true ] );
      var gradient = widget.getBackgroundGradient();
      assertEquals( 2, gradient.length );
      assertEquals( [ 0, "rgb(255,0,0)" ], gradient[ 0 ] );
      assertEquals( [ 1, "rgb(255,255,0)" ], gradient[ 1] );
      assertFalse( gradient.horizontal === true );
    },

    testGradientHandlerReset : function() {
      var handler = rwt.protocol.AdapterUtil.getBackgroundGradientHandler();
      var widget = new rwt.widgets.Composite();
      handler( widget, [ [ 255, 0, 0, 255 ], [ 255, 255, 0, 255 ], [ 0, 100 ], true ] );
      handler( widget, null );
      assertNull( widget.getBackgroundGradient() );
    },

    testBorderHandler : function() {
      var handler = rwt.protocol.AdapterUtil.getRoundedBorderHandler();
      var widget = new rwt.widgets.Composite();
      handler( widget, [ 1, [ 0, 0, 255, 255 ], 2, 3, 4, 5 ] );
      var border = widget.getBorder();
      assertEquals( "rounded", border.getStyle() );
      assertEquals( "rgb(0,0,255)", border.getColor() );
      assertEquals( [ 2, 3, 4, 5 ], border.getRadii() );
    },

    testBorderHandlerReset : function() {
      var handler = rwt.protocol.AdapterUtil.getRoundedBorderHandler();
      var widget = new rwt.widgets.Button( "push" );
      var orgBorder = widget.getBorder();
      handler( widget, [ 1, "blue", 2, 3, 4, 5 ] );
      handler( widget, null );
      assertIdentical( orgBorder, widget.getBorder() );
    },

    testBackgroundHandler : function() {
      var handler = rwt.protocol.AdapterUtil.getControlPropertyHandler( "background" );
      var widget = new rwt.widgets.Composite();
      handler( widget, [ 1, 2, 3, 255 ] );
      assertEquals( "rgb(1,2,3)", widget.getBackgroundColor() );
    },

    testBackgroundHandlerTransparent : function() {
      var handler = rwt.protocol.AdapterUtil.getControlPropertyHandler( "background" );
      var widget = new rwt.widgets.Composite();
      handler( widget, [ 1, 2, 3, 0 ] );
      assertEquals( "transparent", widget.getBackgroundColor() );
    },

    testBackgroundHandlerReset : function() {
      var handler = rwt.protocol.AdapterUtil.getControlPropertyHandler( "background" );
      var widget = new rwt.widgets.Composite();
      var orgBackground = widget.getBackgroundColor();
      handler( widget, [ 1, 2, 3, 255 ] );
      handler( widget, null );
      assertEquals( orgBackground, widget.getBackgroundColor() );
    },

    testForegroundHandler : function() {
      var handler = rwt.protocol.AdapterUtil.getControlPropertyHandler( "foreground" );
      var widget = new rwt.widgets.Button( "push" );
      handler( widget, [ 1, 2, 3, 255 ] );
      assertEquals( "rgb(1,2,3)", widget.getTextColor() );
    },

    testForegroundHandlerReset : function() {
      var handler = rwt.protocol.AdapterUtil.getControlPropertyHandler( "foreground" );
      var widget = new rwt.widgets.Button( "push" );
      var orgForeground = widget.getTextColor();
      handler( widget, [ 1, 2, 3, 255 ] );
      handler( widget, null );
      assertEquals( orgForeground, widget.getTextColor() );
    },

    testMenuHandler : function() {
      var handler = rwt.protocol.AdapterUtil.getControlPropertyHandler( "menu" );
      var widget = new rwt.widgets.Composite();
      var menu = new rwt.widgets.Menu();
      rwt.protocol.ObjectRegistry.add( "w3", menu, "rwt.widgets.Menu" );
      handler( widget, "w3" );
      assertIdentical( menu, widget.getContextMenu() );
    },

    testMenuHandlerReset : function() {
      var handler = rwt.protocol.AdapterUtil.getControlPropertyHandler( "menu" );
      var widget = new rwt.widgets.Composite();
      var menu = new rwt.widgets.Menu();
      rwt.protocol.ObjectRegistry.add( "w3", menu, "rwt.widgets.Menu" );
      handler( widget, "w3" );
      handler( widget, null );
      assertNull( widget.getContextMenu() );
    }

  }
  
} );