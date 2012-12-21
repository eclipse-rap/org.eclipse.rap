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

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.HandlerUtilTest", {

  extend : rwt.qx.Object,

  members : {

    testGradientHandler : function() {
      var handler = rwt.remote.HandlerUtil.getBackgroundGradientHandler();
      var widget = new rwt.widgets.Composite();
      handler( widget, [ [ [ 255, 0, 0, 255 ], [ 255, 255, 0, 255 ] ], [ 0, 100 ], true ] );
      var gradient = widget.getBackgroundGradient();
      assertEquals( 2, gradient.length );
      assertEquals( [ 0, "rgb(255,0,0)" ], gradient[ 0 ] );
      assertEquals( [ 1, "rgb(255,255,0)" ], gradient[ 1] );
      assertFalse( gradient.horizontal === true );
    },

    testGradientHandlerReset : function() {
      var handler = rwt.remote.HandlerUtil.getBackgroundGradientHandler();
      var widget = new rwt.widgets.Composite();
      handler( widget, [ [ 255, 0, 0, 255 ], [ 255, 255, 0, 255 ], [ 0, 100 ], true ] );
      handler( widget, null );
      assertNull( widget.getBackgroundGradient() );
    },

    testBorderHandler : function() {
      var handler = rwt.remote.HandlerUtil.getRoundedBorderHandler();
      var widget = new rwt.widgets.Composite();
      handler( widget, [ 1, [ 0, 0, 255, 255 ], 2, 3, 4, 5 ] );
      var border = widget.getBorder();
      assertEquals( "rounded", border.getStyle() );
      assertEquals( "rgb(0,0,255)", border.getColor() );
      assertEquals( [ 2, 3, 4, 5 ], border.getRadii() );
    },

    testBorderHandlerReset : function() {
      var handler = rwt.remote.HandlerUtil.getRoundedBorderHandler();
      var widget = new rwt.widgets.Button( "push" );
      var orgBorder = widget.getBorder();
      handler( widget, [ 1, "blue", 2, 3, 4, 5 ] );
      handler( widget, null );
      assertIdentical( orgBorder, widget.getBorder() );
    },

    testBackgroundHandler : function() {
      var handler = rwt.remote.HandlerUtil.getControlPropertyHandler( "background" );
      var widget = new rwt.widgets.Composite();
      handler( widget, [ 1, 2, 3, 255 ] );
      assertEquals( "rgb(1,2,3)", widget.getBackgroundColor() );
    },

    testBackgroundHandlerTransparent : function() {
      var handler = rwt.remote.HandlerUtil.getControlPropertyHandler( "background" );
      var widget = new rwt.widgets.Composite();
      handler( widget, [ 1, 2, 3, 0 ] );
      assertEquals( "transparent", widget.getBackgroundColor() );
    },

    testBackgroundHandlerReset : function() {
      var handler = rwt.remote.HandlerUtil.getControlPropertyHandler( "background" );
      var widget = new rwt.widgets.Composite();
      var orgBackground = widget.getBackgroundColor();
      handler( widget, [ 1, 2, 3, 255 ] );
      handler( widget, null );
      assertEquals( orgBackground, widget.getBackgroundColor() );
    },

    testForegroundHandler : function() {
      var handler = rwt.remote.HandlerUtil.getControlPropertyHandler( "foreground" );
      var widget = new rwt.widgets.Button( "push" );
      handler( widget, [ 1, 2, 3, 255 ] );
      assertEquals( "rgb(1,2,3)", widget.getTextColor() );
    },

    testForegroundHandlerReset : function() {
      var handler = rwt.remote.HandlerUtil.getControlPropertyHandler( "foreground" );
      var widget = new rwt.widgets.Button( "push" );
      var orgForeground = widget.getTextColor();
      handler( widget, [ 1, 2, 3, 255 ] );
      handler( widget, null );
      assertEquals( orgForeground, widget.getTextColor() );
    },

    testMenuHandler : function() {
      var handler = rwt.remote.HandlerUtil.getControlPropertyHandler( "menu" );
      var widget = new rwt.widgets.Composite();
      var menu = new rwt.widgets.Menu();
      rwt.remote.ObjectRegistry.add( "w3", menu, "rwt.widgets.Menu" );
      handler( widget, "w3" );
      assertIdentical( menu, widget.getContextMenu() );
    },

    testMenuHandlerReset : function() {
      var handler = rwt.remote.HandlerUtil.getControlPropertyHandler( "menu" );
      var widget = new rwt.widgets.Composite();
      var menu = new rwt.widgets.Menu();
      rwt.remote.ObjectRegistry.add( "w3", menu, "rwt.widgets.Menu" );
      handler( widget, "w3" );
      handler( widget, null );
      assertNull( widget.getContextMenu() );
    },

    testAddDestroyableChild : function() {
      var parent = new rwt.widgets.Composite();
      var childOne = new rwt.widgets.Composite();
      var childTwo = new rwt.widgets.Composite();

      rwt.remote.HandlerUtil.addDestroyableChild( parent, childOne );
      rwt.remote.HandlerUtil.addDestroyableChild( parent, childTwo );

      var result = rwt.remote.HandlerUtil.getDestroyableChildren( parent );
      assertEquals( 2, result.length );
      assertTrue( result.indexOf( childOne ) !== -1 );
      assertTrue( result.indexOf( childTwo ) !== -1 );
    },

    testRemoveDestroyableChild : function() {
      var parent = new rwt.widgets.Composite();
      var childOne = new rwt.widgets.Composite();
      var childTwo = new rwt.widgets.Composite();

      rwt.remote.HandlerUtil.addDestroyableChild( parent, childOne );
      rwt.remote.HandlerUtil.addDestroyableChild( parent, childTwo );
      rwt.remote.HandlerUtil.removeDestroyableChild( parent, childOne );

      var expected = [ childTwo ];
      assertEquals( expected, rwt.remote.HandlerUtil.getDestroyableChildren( parent ) );
    },

    testSetParent : function() {
      var parent = new rwt.widgets.Composite();
      var child = new rwt.widgets.Composite();
      rwt.remote.ObjectRegistry.add( "c", child );

      rwt.remote.HandlerUtil.setParent( child, "p" );
      rwt.remote.ObjectRegistry.add( "p", parent );

      assertIdentical( parent, child.getParent() );
      assertEquals( [ child ], rwt.remote.HandlerUtil.getDestroyableChildren( parent ) );
      parent.destroy();
    },

    testDestructor : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var parent = new rwt.widgets.Composite();
      var widget = new rwt.widgets.Composite();
      var child = new rwt.widgets.Composite();
      rwt.remote.ObjectRegistry.add( "p", parent );
      rwt.remote.ObjectRegistry.add( "w", widget );
      rwt.remote.ObjectRegistry.add( "c", child );
      rwt.remote.HandlerUtil.setParent( child, "w" );
      rwt.remote.HandlerUtil.setParent( widget, "p" );

      rwt.remote.HandlerUtil.getWidgetDestructor()( widget );
      TestUtil.flush();

      assertTrue( widget.isDisposed() );
      assertTrue( child.isDisposed() );
      assertEquals( [], rwt.remote.HandlerUtil.getDestroyableChildren( parent ) );
      parent.destroy();
    }

  }

} );