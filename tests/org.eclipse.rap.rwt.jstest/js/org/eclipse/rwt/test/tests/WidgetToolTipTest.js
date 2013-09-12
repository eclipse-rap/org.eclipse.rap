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

var orgGetConfig;
var config;
var shell;
var widget;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.WidgetToolTipTest", {
  extend : rwt.qx.Object,

  members : {

    TARGETPLATFORM : [ "win", "mac", "unix" ],

    setUp : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
      shell.setLeft( 10 );
      shell.setTop( 20 );
      shell.show();
      widget = new rwt.widgets.base.Label( "Hello World 1" );
      widget.setLeft( 100 );
      widget.setTop( 10 );
      widget.setWidth( 100 );
      widget.setHeight( 20 );
      widget.setParent( shell );
      TestUtil.flush();
      orgGetConfig = rwt.widgets.util.ToolTipConfig.getConfig;
      rwt.widgets.util.ToolTipConfig.getConfig = function() {
        return config;
      };
      config = {
        "position" : "mouse"
      };
    },

    tearDown : function() {
      toolTip.hide();
      toolTip.setBoundToWidget( null );
      rwt.widgets.util.ToolTipConfig.getConfig = orgGetConfig;
      shell.destroy();
    },

    testUpdateWidgetToolTipText_HoverFromDocument : function() {
      WidgetToolTip.setToolTipText( widget, "test1" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      showToolTip();

      assertEquals( "test1", toolTip._label.getCellContent( 0 ) );
    },

    testUpdateWidgetToolTipText_HoverFromOtherWidget : function() {
      var widget2 = new rwt.widgets.base.Label( "Hello World 2" );
      widget2.addToDocument();
      TestUtil.flush();
      WidgetToolTip.setToolTipText( widget, "test1" );
      WidgetToolTip.setToolTipText( widget2, "test2" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );

      TestUtil.hoverFromTo( widget.getElement(), widget2.getElement() );
      showToolTip();

      assertEquals( "test2", toolTip._label.getCellContent( 0 ) );
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

      WidgetToolTip.setToolTipText( widget, "test3" );
      TestUtil.hoverFromTo( widget2.getElement(), widget.getElement() );
      showToolTip();

      assertEquals( "test3", toolTip._label.getCellContent( 0 ) );
      widget.destroy();
    },

    testUpdateWidgetToolTipText_WhileToolTipBound : function() {
      WidgetToolTip.setToolTipText( widget, "test1" );

      TestUtil.hoverFromTo( document.body, widget.getElement() );
      WidgetToolTip.setToolTipText( widget, "test2" );

      assertEquals( "test2", toolTip._label.getCellContent( 0 ) );
    },

    testPosition_MouseRelative : function() {
      WidgetToolTip.setToolTipText( widget, "foobar" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );

      TestUtil.fakeMouseEvent( widget, "mousemove", 110, 20 );
      showToolTip();

      assertEquals( 111, parseInt( toolTip._style.left, 10 ) );
      assertEquals( 40, parseInt( toolTip._style.top, 10 ) );
    },

    testPosition_AdjustToolTipPosition : function() {
      WidgetToolTip.setToolTipText( widget, "foobar" );
      widget.adjustToolTipPosition = function( position ) {
        return [ position[ 0 ] + 1, position[ 1 ] + 1 ];
      };
      TestUtil.hoverFromTo( document.body, widget.getElement() );

      TestUtil.fakeMouseEvent( widget, "mousemove", 110, 20 );
      showToolTip();

      assertEquals( 112, parseInt( toolTip._style.left, 10 ) );
      assertEquals( 41, parseInt( toolTip._style.top, 10 ) );
    },

    testPosition_HorizontalCenterBottom : function() {
      config = { "position" : "horizontal-center" };
      WidgetToolTip.setToolTipText( widget, "foobar" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );

      TestUtil.fakeMouseEvent( widget, "mousemove", 110, 20 );
      showToolTip();

      var expectedLeft = Math.round( 10 + 1 + 100 + 100 / 2 - toolTip.getWidthValue() / 2 );
      var expectedTop = 20 + 1 + 10 + 20 + 3; // shell + border + top + height + offset
      assertEquals( expectedLeft, parseInt( toolTip._style.left, 10 ) );
      assertEquals( expectedTop, parseInt( toolTip._style.top, 10 ) );
    },

    testPosition_HorizontalLeft : function() {
      config = { "position" : "align-left" };
      WidgetToolTip.setToolTipText( widget, "foobar" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );

      TestUtil.fakeMouseEvent( widget, "mousemove", 110, 20 );
      showToolTip();

      var expectedLeft = Math.round( 10 + 1 + 100 );
      var expectedTop = 20 + 1 + 10 + 20 + 3; // shell + border + top + height + offset
      assertEquals( expectedLeft, parseInt( toolTip._style.left, 10 ) );
      assertEquals( expectedTop, parseInt( toolTip._style.top, 10 ) );
    },

    testPosition_VerticalCenterRight : function() { // TODO : restrict by fallback to horizontal
      config = { "position" : "vertical-center" };
      WidgetToolTip.setToolTipText( widget, "foobar" );

      TestUtil.hoverFromTo( document.body, widget.getElement() );
      showToolTip();

      var expectedLeft = 10 + 1 + 100 + 100 + 3;
      var expectedTop = 20 + 1 + 10 + 20 / 2 - toolTip.getBoxHeight() / 2;
      assertEquals( expectedLeft, parseInt( toolTip._style.left, 10 ) );
      assertEquals( expectedTop, parseInt( toolTip._style.top, 10 ) );
    },

    testPosition_VerticalCenterLeft : function() { // TODO : restrict by fallback to horizontal
      config = { "position" : "vertical-center" };
      WidgetToolTip.setToolTipText( widget, "foobar" );
      var totalWidth =  rwt.widgets.base.ClientDocument.getInstance().getClientWidth();
      var left = Math.round( totalWidth / 2 );
      widget.setLeft( left );

      TestUtil.hoverFromTo( document.body, widget.getElement() );
      showToolTip();

      var expectedLeft = 10 + 1 + left - toolTip.getBoxWidth() - 3;
      var expectedTop = 20 + 1 + 10 + 20 / 2 - toolTip.getBoxHeight() / 2;
      assertEquals( expectedLeft, parseInt( toolTip._style.left, 10 ) );
      assertEquals( expectedTop, parseInt( toolTip._style.top, 10 ) );
    },

    testPosition_HorizontalCenterTop : function() {
      config = { "position" : "horizontal-center" };
      WidgetToolTip.setToolTipText( widget, "foobar" );
      var totalHeight =  rwt.widgets.base.ClientDocument.getInstance().getClientHeight();
      var top = Math.round( totalHeight / 3 ) + 50;

      widget.setTop( top );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      showToolTip();

      var tooltipHeight = toolTip.getHeightValue();
      var expectedLeft = Math.round( 10 + 1 + 100 + ( 100 / 2 ) - toolTip.getWidthValue() / 2 );
      var expectedTop = 20 + 1 + top - tooltipHeight - 3; // shell + border + top - tooltipHeight - offset
      assertEquals( expectedLeft, parseInt( toolTip._style.left, 10 ) );
      assertEquals( expectedTop, parseInt( toolTip._style.top, 10 ) );
    },


    testPosition_HorizontalCenterRestrictToPageLeft : function() {
      config = { "position" : "horizontal-center" };
      WidgetToolTip.setToolTipText( widget, "foobarfoobarfoobarfoobar" );
      widget.setLeft( 0 );
      TestUtil.hoverFromTo( document.body, widget.getElement() );

      TestUtil.fakeMouseEvent( widget, "mousemove", 110, 20 );
      showToolTip();

      assertEquals( 0, parseInt( toolTip._style.left, 10 ) );
    },

    testPosition_HorizontalCenterRestrictToPageRight : function() {
      config = { "position" : "horizontal-center" };
      WidgetToolTip.setToolTipText( widget, "foobarfoobarfoobarfoobar" );
      var totalWidth = rwt.widgets.base.ClientDocument.getInstance().getClientWidth();
      widget.setLeft( totalWidth - 30  );
      TestUtil.hoverFromTo( document.body, widget.getElement() );

      TestUtil.fakeMouseEvent( widget, "mousemove", 110, 20 );
      showToolTip();

      var right =   totalWidth
                  - parseInt( toolTip._style.left, 10 )
                  - parseInt( toolTip._style.width, 10 );
      assertEquals( 0, right );
    },

    testPosition_MouseRelativeRestrictToPageBottom : function() {
      WidgetToolTip.setToolTipText( widget, "foobar" );
      var totalHeight = rwt.widgets.base.ClientDocument.getInstance().getClientHeight();
      widget.setTop( totalHeight - 40 );
      TestUtil.hoverFromTo( document.body, widget.getElement() );

      TestUtil.fakeMouseEvent( widget, "mousemove", 110, totalHeight - 10 );
      showToolTip();

      var bottom =   totalHeight
                   - parseInt( toolTip._style.top, 10 )
                   - parseInt( toolTip._style.height, 10 );
      assertEquals( 0, bottom );
    },

    testAppear_DefaultDelay : function() {
      config = {};
      WidgetToolTip.setToolTipText( widget, "foobar" );

      TestUtil.hoverFromTo( document.body, widget.getElement() );

      assertTrue( toolTip._showTimer.isEnabled() );
      assertEquals( 1000, toolTip._showTimer.getInterval() );
    },

    testTextIsEmptyString_StartsTimer : function() {
      widget.setToolTipText( "" );

      TestUtil.hoverFromTo( document.body, widget.getElement() );

      assertTrue( toolTip._showTimer.isEnabled() );
      assertIdentical( widget, toolTip.getBoundToWidget() );
    },

    testTextIsEmptyString_DoesNotShow : function() {
      widget.setToolTipText( "" );

      TestUtil.hoverFromTo( document.body, widget.getElement() );
      TestUtil.forceInterval( toolTip._showTimer );

      assertFalse( toolTip.isSeeable() );
    },

    testTextIsEmptyString_ShowsWhenTextIsUpdated : function() {
      widget.setToolTipText( "" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      TestUtil.forceInterval( toolTip._showTimer );

      widget.setToolTipText( "foo" );
      toolTip.updateText();

      assertTrue( toolTip.isSeeable() );
    },

    testAppear_DefaultDelayNotRestartedOnMouseMove : function() {
      WidgetToolTip.setToolTipText( widget, "foobar" );

      TestUtil.hoverFromTo( document.body, widget.getElement() );
      toolTip._showTimer.stop(); // not ideal, but can't think of a better way to test this
      TestUtil.mouseMove( widget );

      assertFalse( toolTip._showTimer.isEnabled() );
      assertEquals( 1000, toolTip._showTimer.getInterval() );
    },

    testAppear_CustomDelayRestartedOnMouseMove : function() {
      config = { "appearOn" : "rest" };
      WidgetToolTip.setToolTipText( widget, "foobar" );

      TestUtil.hoverFromTo( document.body, widget.getElement() );
      toolTip._showTimer.stop(); // not ideal, but can't think of a better way to test this
      TestUtil.mouseMove( widget );

      assertTrue( toolTip._showTimer.isEnabled() );
      assertEquals( 1000, toolTip._showTimer.getInterval() );
    },

    testAppear_CustomDelay : function() {
      config = { "appearDelay" : 123 };
      WidgetToolTip.setToolTipText( widget, "foobar" );

      TestUtil.hoverFromTo( document.body, widget.getElement() );

      assertTrue( toolTip._showTimer.isEnabled() );
      assertEquals( 123, toolTip._showTimer.getInterval() );
    },

    testDisappear_DefaultDelay : function() {
      config = {};
      WidgetToolTip.setToolTipText( widget, "foobar" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      showToolTip();

      TestUtil.hoverFromTo( widget.getElement(), document.body );

      assertTrue( toolTip._hideTimer.isEnabled() );
      assertEquals( 200, toolTip._hideTimer.getInterval() );
    },

    testDisappear_CustomDelay : function() {
      config = { "disappearDelay" : 456 };
      WidgetToolTip.setToolTipText( widget, "foobar" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      showToolTip();

      TestUtil.hoverFromTo( widget.getElement(), document.body );

      assertTrue( toolTip._hideTimer.isEnabled() );
      assertEquals( 456, toolTip._hideTimer.getInterval() );
    },

    testDoNotHideAfterMouseOut : function() {
      WidgetToolTip.setToolTipText( widget, "test1" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      showToolTip();

      TestUtil.hoverFromTo( widget.getElement(), document.body );

      assertTrue( toolTip.isSeeable() );
    },

    testHideAfterMouseOutAndTimer : function() {
      WidgetToolTip.setToolTipText( widget, "test1" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      showToolTip();

      TestUtil.hoverFromTo( widget.getElement(), document.body );
      TestUtil.forceInterval( toolTip._hideTimer );

      assertFalse( toolTip.isSeeable() );
      assertNull( toolTip.getBoundToWidget() );
    },

    testHideAfterMouseMoveAndTimer : function() {
      config = { "disappearOn" : "move" };
      WidgetToolTip.setToolTipText( widget, "test1" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      showToolTip();

      TestUtil.mouseMove( widget );
      TestUtil.forceInterval( toolTip._hideTimer );

      assertFalse( toolTip.isSeeable() );
    },

    testHideOnMouseOverWithoutTimer : function() {
      config = { "disappearOn" : "exit" };
      WidgetToolTip.setToolTipText( widget, "test1" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      showToolTip();

      TestUtil.hoverFromTo( widget.getElement(), toolTip.getElement() );

      assertFalse( toolTip.isSeeable() );
    },

    testHideOnMouseOverWithoutAnimation : function() {
      config = { "disappearOn" : "exit" };
      WidgetToolTip.setToolTipText( widget, "test1" );
      toolTip.setAnimation( { "fadeOut" : [ 400, "linear" ] } );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      showToolTip();

      TestUtil.hoverFromTo( widget.getElement(), toolTip.getElement() );

      assertFalse( toolTip._disappearAnimation.isStarted() );
      assertFalse( toolTip.isSeeable() );
      assertTrue( toolTip._disappearAnimation.getDefaultRenderer().isActive() );
      toolTip.setAnimation( {} );
    },

    testStopHideTimerWhenReAppearWhileVisible : function() {
      var widget2 = new rwt.widgets.base.Label( "Hello World 2" );
      widget2.addToDocument();
      TestUtil.flush();
      WidgetToolTip.setToolTipText( widget, "test1" );
      WidgetToolTip.setToolTipText( widget2, "test2" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      showToolTip();

      TestUtil.hoverFromTo( widget.getElement(), widget2.getElement() );
      showToolTip();

      assertFalse( toolTip._hideTimer.isEnabled() );
    },

    testStartShowTimerAfterHideIfAppearOnRest : function() {
      config = { "appearOn" : "rest", "disappearOn" : "move" };
      WidgetToolTip.setToolTipText( widget, "test1" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      showToolTip();

      TestUtil.mouseMove( widget );
      TestUtil.forceInterval( toolTip._hideTimer );

      assertTrue( toolTip._showTimer.isEnabled() );
    },

    testSkipShowTimerIfAlreadyVisible : function() {
      config = { "appearOn" : "enter" };
      var widget2 = new rwt.widgets.base.Label( "Hello World 2" );
      widget2.addToDocument();
      TestUtil.flush();
      WidgetToolTip.setToolTipText( widget, "test1" );
      WidgetToolTip.setToolTipText( widget2, "test2" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      showToolTip();

      TestUtil.hoverFromTo( widget.getElement(), document.body );
      TestUtil.hoverFromTo( document.body, widget2.getElement() );

      assertFalse( toolTip._showTimer.isEnabled() );
      assertFalse( toolTip._hideTimer.isEnabled() );
      assertTrue( toolTip.isSeeable() );
      assertEquals( "test2", toolTip._label.getCellContent( 0 ) );
    },

    testSkipShowTimerIfRecentlyHidden : function() {
      config = { "appearOn" : "enter" };
      var widget2 = new rwt.widgets.base.Label( "Hello World 2" );
      widget2.addToDocument();
      TestUtil.flush();
      WidgetToolTip.setToolTipText( widget, "test1" );
      WidgetToolTip.setToolTipText( widget2, "test2" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      showToolTip();

      TestUtil.hoverFromTo( widget.getElement(), document.body );
      TestUtil.forceInterval( toolTip._hideTimer );
      TestUtil.hoverFromTo( document.body, widget2.getElement() );

      assertFalse( toolTip._showTimer.isEnabled() );
      assertFalse( toolTip._hideTimer.isEnabled() );
      assertTrue( toolTip.isSeeable() );
      assertEquals( "test2", toolTip._label.getCellContent( 0 ) );
    },

    testDoNotSkipShowTimerIfHiddenASecondAgo : function() {
      config = { "appearOn" : "enter" };
      var widget2 = new rwt.widgets.base.Label( "Hello World 2" );
      widget2.addToDocument();
      TestUtil.flush();
      WidgetToolTip.setToolTipText( widget, "test1" );
      WidgetToolTip.setToolTipText( widget2, "test2" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      showToolTip();

      TestUtil.hoverFromTo( widget.getElement(), document.body );
      TestUtil.forceInterval( toolTip._hideTimer );
      toolTip._hideTimeStamp = ( new Date() ).getTime() - 1001;
      TestUtil.hoverFromTo( document.body, widget2.getElement() );

      assertTrue( toolTip._showTimer.isEnabled() );
      assertFalse( toolTip.isSeeable() );
    },

    testSkipAppearAnimationIfRecentlyHidden : function() {
      config = { "appearOn" : "enter" };
      var widget2 = new rwt.widgets.base.Label( "Hello World 2" );
      widget2.addToDocument();
      TestUtil.flush();
      toolTip.setAnimation( { "fadeIn" : [ 400, "linear" ] } );
      WidgetToolTip.setToolTipText( widget, "test1" );
      WidgetToolTip.setToolTipText( widget2, "test2" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      showToolTip();

      TestUtil.hoverFromTo( widget.getElement(), document.body );
      TestUtil.forceInterval( toolTip._hideTimer );
      TestUtil.hoverFromTo( document.body, widget2.getElement() );

      assertFalse( toolTip._appearAnimation.isStarted() );
      assertTrue( toolTip._appearAnimation.getDefaultRenderer().isActive() );
      assertTrue( toolTip.isSeeable() );
      toolTip.setAnimation( {} );
    },

    testDoNotSkipShowTimerIfTargetAppearsOnRest : function() {
      var widget2 = new rwt.widgets.base.Label( "Hello World 2" );
      widget2.addToDocument();
      TestUtil.flush();
      WidgetToolTip.setToolTipText( widget, "test1" );
      WidgetToolTip.setToolTipText( widget2, "test2" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      showToolTip();

      TestUtil.hoverFromTo( widget.getElement(), document.body );
      TestUtil.hoverFromTo( document.body, widget2.getElement() );

      assertTrue( toolTip._showTimer.isEnabled() );
    }

  }

} );

var showToolTip = function( widget ) {
  toolTip._onshowtimer();
};

}());
