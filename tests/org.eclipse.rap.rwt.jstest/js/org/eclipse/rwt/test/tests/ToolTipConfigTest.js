/*******************************************************************************
 * Copyright (c) 2009, 2014 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var ToolTipConfig = rwt.widgets.util.ToolTipConfig;
var getConfig = function( widget ) {
  return ToolTipConfig.getConfig( widget );
};
var stringArray = [ "a", "a", "a", "a", "a", "a", "a", "a", "a" ];
var createDateTimeDate = function() {
  return new rwt.widgets.DateTimeDate( "medium", stringArray, stringArray, stringArray, "", "MDY" );
};
var createDateTimeTime = function() {
  return new rwt.widgets.DateTimeTime( "medium" );
};
var createDateTimeCalendar = function() {
  return new rwt.widgets.DateTimeCalendar( "medium", stringArray, stringArray, stringArray, "", "MDY" );
};



rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ToolTipConfigTest", {
  extend : rwt.qx.Object,

  members : {

    TARGETPLATFORM : [ "win", "mac", "unix" ],

    testCompositeConfig : function() {
      var widget = new rwt.widgets.Composite();

      var config = ToolTipConfig.getConfig( widget );

      assertEquals( "mouse", config.position );
    },

    testToolItemConfig : function() {
      var widget = new rwt.widgets.ToolItem();

      var config = ToolTipConfig.getConfig( widget );

      assertEquals( "horizontal-center", config.position );
    },

    testVerticalToolItemConfig : function() {
      var widget = new rwt.widgets.ToolItem();
      widget.addState( "rwt_VERTICAL" );

      var config = getConfig( widget );

      assertEquals( "vertical-center", config.position );
    },

    testScaleConfig : function() {
      var widget = new rwt.widgets.Scale();

      var config = ToolTipConfig.getConfig( widget );

      assertEquals( "horizontal-center", config.position );
      assertEquals( -2, config.overlap );
      assertFalse( config.autoHide );
    },

    testScaleConfigVertical : function() {
      var widget = new rwt.widgets.Scale();
      widget.addState( "rwt_VERTICAL" );

      var config = getConfig( widget );

      assertEquals( "vertical-center", config.position );
      assertEquals( -2, config.overlap );
      assertFalse( config.autoHide );
    },

    testProgressBarConfigHorizontal : function() {
      var widget = new rwt.widgets.ProgressBar();
      widget.addState( "rwt_HORIZONTAL" );

      var config = ToolTipConfig.getConfig( widget );

      assertEquals( "horizontal-center", config.position );
      assertEquals( 3, config.overlap );
      assertFalse( config.autoHide );
    },

    testProgressBarConfigVertical : function() {
      var widget = new rwt.widgets.ProgressBar();
      widget.addState( "rwt_VERTICAL" );

      var config = ToolTipConfig.getConfig( widget );

      assertEquals( "vertical-center", config.position );
      assertEquals( 3, config.overlap );
      assertFalse( config.autoHide );
    },

    testPushButton : function() {
      var widget = new rwt.widgets.Button();
      widget.setText( "foo" );

      var config = ToolTipConfig.getConfig( widget );

      assertEquals( "horizontal-center", config.position );
      assertEquals( "enter", config.appearOn );
      assertEquals( "exit", config.disappearOn );
      assertEquals( "undefined", typeof config.overlap );
    },

    testCheckButton : function() {
      var widget = new rwt.widgets.Button( "check" );
      widget.setText( "foo" );

      var config = ToolTipConfig.getConfig( widget );

      assertEquals( "horizontal-center", config.position );
      assertEquals( "enter", config.appearOn );
      assertEquals( "exit", config.disappearOn );
      assertEquals( -1, config.overlap );
    },

    testRadioButton : function() {
      var widget = new rwt.widgets.Button( "radio" );
      widget.setText( "foo" );

      var config = ToolTipConfig.getConfig( widget );

      assertEquals( "horizontal-center", config.position );
      assertEquals( "enter", config.appearOn );
      assertEquals( "exit", config.disappearOn );
      assertEquals( -1, config.overlap );
    },

    testTabItem : function() {
      var widget = new rwt.widgets.TabItem();

      var config = ToolTipConfig.getConfig( widget );

      assertEquals( "horizontal-center", config.position );
      assertEquals( "enter", config.appearOn );
      assertEquals( "exit", config.disappearOn );
    },

    testCTabItem : function() {
      var widget = new rwt.widgets.CTabItem( new rwt.widgets.CTabFolder() );

      var config = ToolTipConfig.getConfig( widget );

      assertEquals( "horizontal-center", config.position );
      assertEquals( "enter", config.appearOn );
      assertEquals( "exit", config.disappearOn );
    },

    testLabelWithTextConfig : function() {
      var widget = new rwt.widgets.Label( {} );
      widget.setText( "foo" );

      var config = ToolTipConfig.getConfig( widget );

      assertEquals( "align-left", config.position );
      assertEquals( "rest", config.appearOn );
      assertEquals( "exit", config.disappearOn );
    },

    testLabelWithImageOnlyConfig : function() {
      var widget = new rwt.widgets.Label( {} );
      widget.setImage( "foo.jpg", 10, 10 );

      var config = ToolTipConfig.getConfig( widget );

      assertEquals( "horizontal-center", config.position );
      assertEquals( "enter", config.appearOn );
      assertEquals( "exit", config.disappearOn );
    },

    testControlDecoratorConfig : function() {
      var widget = new rwt.widgets.ControlDecorator();

      var config = ToolTipConfig.getConfig( widget );

      assertEquals( "horizontal-center", config.position );
      assertEquals( "enter", config.appearOn );
      assertEquals( "exit", config.disappearOn );
      assertEquals( -1, config.overlap );
      assertFalse( config.autoHide );
      assertTrue( config.appearDelay <= 100 );
    },

    testLeftAlignedWidgetInCoolBar : function() {
      var widget = new rwt.widgets.Text();
      var coolbar = new rwt.widgets.CoolBar();
      widget.setParent( coolbar );

      var config = ToolTipConfig.getConfig( widget );

      assertEquals( "horizontal-center", config.position );
      assertEquals( "enter", config.appearOn );
      assertEquals( "exit", config.disappearOn );
    },

    testAlwaysLeftAlignedWidgets : function() {
      assertEquals( "align-left", getConfig( new rwt.widgets.Text() ).position );
      assertEquals( "align-left", getConfig( new rwt.widgets.Spinner() ).position );
      assertEquals( "align-left", getConfig( new rwt.widgets.Combo() ).position );
      assertEquals( "align-left", getConfig( createDateTimeDate() ).position );
      assertEquals( "align-left", getConfig( createDateTimeTime() ).position );
    },

    testAlwaysAutoHideWidgets : function() {
      assertTrue( getConfig( new rwt.widgets.Text() ).autoHide );
      assertTrue( getConfig( new rwt.widgets.Spinner() ).autoHide );
      assertTrue( getConfig( new rwt.widgets.Combo() ).autoHide );
      assertTrue( getConfig( createDateTimeDate() ).autoHide );
      assertTrue( getConfig( createDateTimeTime() ).autoHide );
    },

    testAlwaysMouseRelativeWidgets : function() {
      assertEquals( "mouse", getConfig( new rwt.widgets.Composite() ).position );
      assertEquals( "mouse", getConfig( new rwt.widgets.ScrolledComposite() ).position );
      assertEquals( "mouse", getConfig( new rwt.widgets.Group() ).position );
      assertEquals( "mouse", getConfig( new rwt.widgets.Shell( [] ) ).position );
      assertEquals( "mouse", getConfig( new rwt.widgets.List( false ) ).position );
      assertEquals( "mouse", getConfig( createDateTimeCalendar() ).position );
    },

    testAlwaysOnRestAppearWidgets : function() {
      assertEquals( "rest", getConfig( new rwt.widgets.Composite() ).appearOn );
      assertEquals( "rest", getConfig( new rwt.widgets.ScrolledComposite() ).appearOn );
      assertEquals( "rest", getConfig( new rwt.widgets.Group() ).appearOn );
      assertEquals( "rest", getConfig( new rwt.widgets.Shell( [] ) ).appearOn );
      assertEquals( "rest", getConfig( new rwt.widgets.List( false ) ).appearOn );
      assertEquals( "rest", getConfig( createDateTimeCalendar() ).appearOn );
      assertEquals( "rest", getConfig( new rwt.widgets.Text() ).appearOn );
      assertEquals( "rest", getConfig( new rwt.widgets.Spinner() ).appearOn );
      assertEquals( "rest", getConfig( new rwt.widgets.Combo() ).appearOn );
    },

    testAlwaysOnEnterAppearWidgets : function() {
      assertEquals( "enter", getConfig( new rwt.widgets.ToolItem() ).appearOn );
      assertEquals( "enter", getConfig( new rwt.widgets.Scale() ).appearOn );
      assertEquals( "enter", getConfig( new rwt.widgets.Slider() ).appearOn );
      assertEquals( "enter", getConfig( new rwt.widgets.ProgressBar() ).appearOn );
    }

  }

} );

}());
