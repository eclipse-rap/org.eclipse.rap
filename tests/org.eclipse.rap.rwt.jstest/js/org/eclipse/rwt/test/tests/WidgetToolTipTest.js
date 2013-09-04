/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;

var manager = rwt.widgets.util.ToolTipManager.getInstance();
var WidgetToolTip = rwt.widgets.base.WidgetToolTip;
var toolTip = rwt.widgets.base.WidgetToolTip.getInstance();
var wm = rwt.remote.WidgetManager.getInstance();

var widget;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.WidgetToolTipTest", {
  extend : rwt.qx.Object,

  members : {

    TARGETPLATFORM : [ "win", "mac", "unix" ],

    setUp : function() {
      widget = new rwt.widgets.base.Label( "Hello World 1" );
      widget.setLeft( 100 );
      widget.setTop( 10 );
      widget.setWidth( 100 );
      widget.setHeight( 20 );
      widget.addToDocument();
      TestUtil.flush();
    },

    tearDown : function() {
      widget.destroy();
    },

    testUpdateWidgetToolTipText_HoverFromDocument : function() {
      WidgetToolTip.setToolTipText( widget, "test1" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );

      assertEquals( "test1", toolTip._atom.getLabel() );
    },

    testUpdateWidgetToolTipText_HoverFromOtherWidget : function() {
      var widget2 = new rwt.widgets.base.Label( "Hello World 2" );
      widget2.addToDocument();
      TestUtil.flush();
      WidgetToolTip.setToolTipText( widget, "test1" );
      WidgetToolTip.setToolTipText( widget2, "test2" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );

      TestUtil.hoverFromTo( widget.getElement(), widget2.getElement() );

      assertEquals( "test2", toolTip._atom.getLabel() );
      widget2.destroy();
    },

    testUpdateWidgetToolTipText_HoverAgainWithDifferentText : function() {
      var widget2 = new rwt.widgets.base.Label( "Hello World 2" );
      widget2.addToDocument();
      TestUtil.flush();
      WidgetToolTip.setToolTipText( widget, "test1" );
      WidgetToolTip.setToolTipText( widget2, "test2" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      TestUtil.hoverFromTo( widget.getElement(), widget2.getElement() );

      wm.setToolTip( widget, "test3" );
      TestUtil.hoverFromTo( widget2.getElement(), widget.getElement() );

      assertEquals( "test3", toolTip._atom.getLabel() );
      widget.destroy();
    },

    testUpdateWidgetToolTipText_WhileToolTipBound : function() {
      WidgetToolTip.setToolTipText( widget, "test1" );

      TestUtil.hoverFromTo( document.body, widget.getElement() );
      wm.setToolTip( widget, "test2" );

      assertEquals( "test2", toolTip._atom.getLabel() );
    },

    testPosition_MouseRelative : function() {
      TestUtil.hoverFromTo( document.body, widget.getElement() );

      TestUtil.fakeMouseEvent( widget, "mousemove", 110, 20 );
      showToolTip( widget );

      assertEquals( 111, toolTip.getLeft() );
      assertEquals( 40, toolTip.getTop() );
    }

  }

} );

var showToolTip = function( widget ) {
  toolTip._onshowtimer();
  TestUtil.flush();
  TestUtil.forceTimerOnce();
  TestUtil.flush();
};

}());