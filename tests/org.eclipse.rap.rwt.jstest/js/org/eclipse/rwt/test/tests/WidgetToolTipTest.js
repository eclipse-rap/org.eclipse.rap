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
var offset = 4;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.WidgetToolTipTest", {
  extend : rwt.qx.Object,

  members : {

    TARGETPLATFORM : [ "win", "mac", "unix" ],

    setUp : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
      shell.setLeft( 10 );
      shell.setTop( 20 );
      shell.show();
      widget = this._createWidget( 100, 10, 100, 20, "Hello World 1" );
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

    _createWidget : function( left, top, width, height, text ) {
      var widget = new rwt.widgets.base.Label( text );
      widget.setLeft( left );
      widget.setTop( top );
      widget.setWidth( width );
      widget.setHeight( height );
      widget.setParent( shell );
      TestUtil.flush();
      return widget;
    },

    /////////
    // Tests:

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

    testPosition_HorizontalCenter_Bottom : function() {
      config = { "position" : "horizontal-center" };
      WidgetToolTip.setToolTipText( widget, "foobar" );

      TestUtil.hoverFromTo( document.body, widget.getElement() );
      TestUtil.fakeMouseEvent( widget, "mousemove", 110, 20 );
      showToolTip();

      var expectedLeft = Math.round( 10 + 1 + 100 + 100 / 2 - toolTip.getWidthValue() / 2 );
      var expectedTop = 20 + 1 + 10 + 20 + offset;
      assertEquals( expectedLeft, parseInt( toolTip._style.left, 10 ) );
      assertEquals( expectedTop, parseInt( toolTip._style.top, 10 ) );
    },

    testPosition_HorizontalCenter_BottomWithCustomTargetBounds : function() {
      config = { "position" : "horizontal-center" };
      WidgetToolTip.setToolTipText( widget, "foobar" );
      widget.getToolTipTargetBounds = function() {
        return { "top" : 2, "left" : 2, "width" : 10, "height" : 10 };
      };
      TestUtil.hoverFromTo( document.body, widget.getElement() );

      TestUtil.fakeMouseEvent( widget, "mousemove", 110, 20 );
      showToolTip();

      var expectedLeft = Math.round( 10 + 1 + 100 + 2 + 10 / 2 - toolTip.getWidthValue() / 2 );
      var expectedTop = 20 + 1 + 10 + 2 + 10 + offset;
      assertEquals( expectedLeft, parseInt( toolTip._style.left, 10 ) );
      assertEquals( expectedTop, parseInt( toolTip._style.top, 10 ) );
    },

    testPosition_HorizontalLeft : function() {
      config = { "position" : "align-left" };
      WidgetToolTip.setToolTipText( widget, "foobar" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );

      TestUtil.fakeMouseEvent( widget, "mousemove", 110, 20 );
      showToolTip();

      var expectedLeft = 10 + 1 + 100 + offset;
      var expectedTop = 20 + 1 + 10 + 20 + offset; // shell + border + top + height + offset
      assertEquals( expectedLeft, parseInt( toolTip._style.left, 10 ) );
      assertEquals( expectedTop, parseInt( toolTip._style.top, 10 ) );
    },

    testPosition_VerticalCenterRight : function() { // TODO : restrict by fallback to horizontal
      config = { "position" : "vertical-center" };
      WidgetToolTip.setToolTipText( widget, "foobar" );

      TestUtil.hoverFromTo( document.body, widget.getElement() );
      showToolTip();

      var expectedLeft = 10 + 1 + 100 + 100 + offset;
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

      var expectedLeft = 10 + 1 + left - toolTip.getBoxWidth() - offset;
      var expectedTop = 20 + 1 + 10 + 20 / 2 - toolTip.getBoxHeight() / 2;
      assertEquals( expectedLeft, parseInt( toolTip._style.left, 10 ) );
      assertEquals( expectedTop, parseInt( toolTip._style.top, 10 ) );
    },

    testPosition_HorizontalCenter_TopByAbsoluteTargetPosition : function() {
      config = { "position" : "horizontal-center" };
      WidgetToolTip.setToolTipText( widget, "foobar" );
      var totalHeight =  rwt.widgets.base.ClientDocument.getInstance().getClientHeight();
      var top = Math.round( totalHeight / 3 ) + 50;

      widget.setTop( top );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      showToolTip();

      var tooltipHeight = toolTip.getHeightValue();
      var expectedLeft = Math.round( 10 + 1 + 100 + ( 100 / 2 ) - toolTip.getWidthValue() / 2 );
      var expectedTop = 20 + 1 + top - tooltipHeight - offset;
      assertEquals( expectedLeft, parseInt( toolTip._style.left, 10 ) );
      assertEquals( expectedTop, parseInt( toolTip._style.top, 10 ) );
    },

    testPosition_HorizontalCenter_RestrictToPageLeft : function() {
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

    testHideAfterKeyDownAndTimer : function() {
      config = { "disappearOn" : "move" };
      WidgetToolTip.setToolTipText( widget, "test1" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      showToolTip();

      TestUtil.press( widget, "A" );
      TestUtil.forceInterval( toolTip._hideTimer );

      assertFalse( toolTip.isSeeable() );
    },

    testDoHideAfterModifierKeyDownAndTimer : function() {
      config = { "disappearOn" : "move" };
      WidgetToolTip.setToolTipText( widget, "test1" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      showToolTip();

      TestUtil.press( widget, "Control" );
      TestUtil.press( widget, "Alt" );
      TestUtil.press( widget, "Shift" );
      TestUtil.press( widget, "Meta" );
      TestUtil.press( widget, "Win" );

      assertFalse( toolTip._hideTimer.isEnabled() );
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
    },

    testPointer_AddedToDom : function() {
      config = { "position" : "horizontal-center" };
      WidgetToolTip.setToolTipText( widget, "foobar" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );

      toolTip.setPointers( [ [ "foo.gif", 10, 20 ], null, null, null ] );
      TestUtil.fakeMouseEvent( widget, "mousemove", 110, 20 );
      showToolTip();

      var pointer = toolTip._getPointerElement();
      assertIdentical( toolTip.getElement(), pointer.parentNode );
      assertEquals( "absolute", pointer.style.position );
    },

    testDoNotHideOnMouseOverPointer : function() {
      config = { "position" : "horizontal-center", "disappearOn" : "exit" };
      WidgetToolTip.setToolTipText( widget, "test1" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      toolTip.setPointers( [ [ "foo.gif", 10, 20 ], null, null, null ] );
      TestUtil.fakeMouseEvent( widget, "mousemove", 110, 20 );
      showToolTip();

      TestUtil.hoverFromTo( widget.getElement(), toolTip._getPointerElement() );

      assertTrue( toolTip.isSeeable() );
    },

    testDoNotSetTargetOnMouseOverPointer : function() {
      config = { "position" : "horizontal-center", "disappearOn" : "exit" };
      WidgetToolTip.setToolTipText( widget, "test1" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      toolTip.setPointers( [ [ "foo.gif", 10, 20 ], null, null, null ] );
      TestUtil.fakeMouseEvent( widget, "mousemove", 110, 20 );
      showToolTip();

      TestUtil.hoverFromTo( widget.getElement(), toolTip._getPointerElement() );

      assertIdentical( widget, toolTip.getBoundToWidget() );
    },

    testPointer_setDisplayNone : function() {
      config = { "position" : "horizontal-center" };
      WidgetToolTip.setToolTipText( widget, "foobar" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );

      toolTip.setPointers( [ [ "foo.gif", 10, 20 ], null, null, null ] );
      TestUtil.fakeMouseEvent( widget, "mousemove", 110, 20 );
      showToolTip();
      toolTip.setPointers( [ null, null, null, null ] );
      TestUtil.fakeMouseEvent( widget, "mousemove", 110, 20 );
      showToolTip();

      var pointer = toolTip._getPointerElement();
      assertEquals( "none", pointer.style.display );
    },

    testPointer_setDisplayDefault : function() {
      config = { "position" : "horizontal-center" };
      WidgetToolTip.setToolTipText( widget, "foobar" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );

      toolTip.setPointers( [ null, null, null, null ] );
      TestUtil.fakeMouseEvent( widget, "mousemove", 110, 20 );
      showToolTip();
      toolTip.setPointers( [ [ "foo.gif", 10, 20 ], null, null, null ] );
      TestUtil.fakeMouseEvent( widget, "mousemove", 110, 20 );
      showToolTip();

      var pointer = toolTip._getPointerElement();
      assertEquals( "", pointer.style.display );
    },

    testPointer_CenterUpPosition : function() {
      config = { "position" : "horizontal-center" };
      WidgetToolTip.setToolTipText( widget, "foobar" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );

      toolTip.setPointers( [ [ "foo.gif", 10, 20 ], null, null, null ] );
      TestUtil.fakeMouseEvent( widget, "mousemove", 110, 20 );
      showToolTip();

      var pointer = toolTip._getPointerElement();
      assertTrue( TestUtil.getCssBackgroundImage( pointer ).indexOf( "foo.gif" ) !== -1 );
      var expectedLeft = Math.floor( ( toolTip.getBoxWidth() - 2 ) / 2 - 5 );
      var actualLeft = parseInt( pointer.style.left, 10 );
      assertTrue( Math.abs( expectedLeft - actualLeft ) <= 1 ); // exact center may not be possible
      assertEquals( "-20px", pointer.style.top );
      assertEquals( "10px", pointer.style.width );
      assertEquals( "20px", pointer.style.height );
    },

    testPointer_CenterUpPositionRestrictedToPage : function() {
      config = { "position" : "horizontal-center" };
      shell.setBorder( null );
      WidgetToolTip.setToolTipText( widget, "foobarfoobarfoobarfoobar" );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      toolTip.setPointers( [ [ "foo.gif", 10, 20 ], null, null, null ] );

      widget.setLeft( 0 );
      TestUtil.fakeMouseEvent( widget, "mousemove", 110, 20 );
      showToolTip();

      var pointer = toolTip._getPointerElement();
      var expectedLeft = 10 + 50 - 1 - 5; // shell-left + half widget - border- half tooltip
      assertEquals( 0, toolTip.getLeft() );
      assertEquals( expectedLeft + "px", pointer.style.left );
    },

    testPointer_CenterDownPosition : function() {
      config = { "position" : "horizontal-center" };
      WidgetToolTip.setToolTipText( widget, "foobar" );
      var totalHeight =  rwt.widgets.base.ClientDocument.getInstance().getClientHeight();

      widget.setTop( totalHeight - 100 );
      toolTip.setPointers( [ null, null, [ "foo.gif", 10, 20 ], null ] );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      showToolTip();

      var pointer = toolTip._getPointerElement();
      assertTrue( TestUtil.getCssBackgroundImage( pointer ).indexOf( "foo.gif" ) !== -1 );
      var expectedLeft = Math.floor( ( toolTip.getBoxWidth() - 2 ) / 2 - 5 );
      var actualLeft = parseInt( pointer.style.left, 10 );
      assertTrue( Math.abs( expectedLeft - actualLeft ) <= 1 ); // exact center may not be possible
      assertEquals( ( toolTip.getBoxHeight() - 2 ) + "px", pointer.style.top );
      assertEquals( "10px", pointer.style.width );
      assertEquals( "20px", pointer.style.height );
    },

    testPointer_CenterLeftPosition : function() {
      config = { "position" : "vertical-center" };
      WidgetToolTip.setToolTipText( widget, "foobar" );

      toolTip.setPointers( [ null, null, null, [ "foo.gif", 20, 10 ] ] );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      showToolTip();

      var pointer = toolTip._getPointerElement();
      assertTrue( TestUtil.getCssBackgroundImage( pointer ).indexOf( "foo.gif" ) !== -1 );
      var expectedTop = Math.floor( ( toolTip.getBoxHeight() - 2 ) / 2 - 5 );
      var actualTop = parseInt( pointer.style.top, 10 );
      assertTrue( Math.abs( expectedTop - actualTop ) <= 1 ); // exact center may not be possible
      assertEquals( "-20px", pointer.style.left );
      assertEquals( "20px", pointer.style.width );
      assertEquals( "10px", pointer.style.height );
    },

    testPointer_CenterRightPosition : function() {
      config = { "position" : "vertical-center" };
      var totalWidth =  rwt.widgets.base.ClientDocument.getInstance().getClientWidth();
      var left = Math.round( totalWidth / 2 );
      widget.setLeft( left );
      WidgetToolTip.setToolTipText( widget, "foobar" );

      toolTip.setPointers( [ null, [ "foo.gif", 20, 10 ], null, null ] );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      showToolTip();

      var pointer = toolTip._getPointerElement();
      assertTrue( TestUtil.getCssBackgroundImage( pointer ).indexOf( "foo.gif" ) !== -1 );
      var expectedTop = Math.floor( ( toolTip.getBoxHeight() - 2 ) / 2 - 5 );
      var actualTop = parseInt( pointer.style.top, 10 );
      assertTrue( Math.abs( expectedTop - actualTop ) <= 1 ); // exact center may not be possible
      assertEquals( ( toolTip.getBoxWidth() - 2 ) + "px", pointer.style.left );
      assertEquals( "20px", pointer.style.width );
      assertEquals( "10px", pointer.style.height );
    },

    testPointer_LeftAlignUpPosition : function() {
      config = { "position" : "align-left" };
      WidgetToolTip.setToolTipText( widget, "foobar" );
      toolTip.setPaddingLeft( 12 );
      TestUtil.hoverFromTo( document.body, widget.getElement() );

      toolTip.setPointers( [ [ "foo.gif", 10, 20 ], null, null, null ] );
      TestUtil.fakeMouseEvent( widget, "mousemove", 110, 20 );
      showToolTip();

      var pointer = toolTip._getPointerElement();
      assertTrue( TestUtil.getCssBackgroundImage( pointer ).indexOf( "foo.gif" ) !== -1 );
      assertEquals( "12px", pointer.style.left );
      assertEquals( "-20px", pointer.style.top );
      assertEquals( "10px", pointer.style.width );
      assertEquals( "20px", pointer.style.height );
    },

    testPointer_LeftAlignDownPosition : function() {
      config = { "position" : "align-left" };
      WidgetToolTip.setToolTipText( widget, "foobar" );
      toolTip.setPaddingLeft( 12 );
      var totalHeight =  rwt.widgets.base.ClientDocument.getInstance().getClientHeight();

      widget.setTop( Math.round( totalHeight / 3 ) + 50 );
      toolTip.setPointers( [ null, null, [ "foo.gif", 10, 20 ], null ] );
      TestUtil.hoverFromTo( document.body, widget.getElement() );
      showToolTip();

      var pointer = toolTip._getPointerElement();
      assertTrue( TestUtil.getCssBackgroundImage( pointer ).indexOf( "foo.gif" ) !== -1 );
      assertEquals( "12px", pointer.style.left );
      assertEquals( ( toolTip.getBoxHeight() - 2 ) + "px", pointer.style.top );
      assertEquals( "10px", pointer.style.width );
      assertEquals( "20px", pointer.style.height );
    },

  }

} );

var showToolTip = function( widget ) {
  toolTip._onshowtimer();
};

}());
