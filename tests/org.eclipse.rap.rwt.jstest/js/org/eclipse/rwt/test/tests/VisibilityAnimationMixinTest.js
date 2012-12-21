/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var Animation =  rwt.animation.Animation;
var Processor = rwt.remote.MessageProcessor;

var shell;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.VisibilityAnimationMixinTest", {
  extend : rwt.qx.Object,

  members : {

    testShellFlyInTopConfigure : function() {
      shell.setAnimation( { "flyInTop" : [ 400, "linear" ] } );

      var animation = this.getAppearAnimation( shell );
      assertEquals( 400, animation.getDuration() );
      assertEquals( rwt.client.Client.isMshtml(), animation.getExclusive() );
    },

    testShellFlyInTopStart : function() {
      shell.setAnimation( { "flyInTop" : [ 400, "linear" ] } );
      shell.hide();

      shell.show();
      TestUtil.flush();

      var animation = this.getAppearAnimation( shell );
      assertTrue( animation.isStarted() );
      assertTrue( shell.isSeeable() );
      assertEquals( -50, animation.getDefaultRenderer().getStartValue() );
      assertEquals( 40, animation.getDefaultRenderer().getEndValue() );
      assertEquals( -50, TestUtil.getElementBounds( shell.getElement() ).top );
    },

    testShellFlyInLeftStart : function() {
      shell.setAnimation( { "flyInLeft" : [ 400, "linear" ] } );
      shell.hide();

      shell.show();
      TestUtil.flush();

      var animation = this.getAppearAnimation( shell );
      assertTrue( animation.isStarted() );
      assertTrue( shell.isSeeable() );
      assertEquals( -70, animation.getDefaultRenderer().getStartValue() );
      assertEquals( 60, animation.getDefaultRenderer().getEndValue() );
      assertEquals( -70, TestUtil.getElementBounds( shell.getElement() ).left );
    },

    testShellFlyInRightStart : function() {
      shell.setAnimation( { "flyInRight" : [ 400, "linear" ] } );
      shell.hide();

      shell.show();
      TestUtil.flush();

      var animation = this.getAppearAnimation( shell );
      assertTrue( animation.isStarted() );
      assertTrue( shell.isSeeable() );
      var left = shell.getParent().getInnerWidth();
      assertEquals( left, animation.getDefaultRenderer().getStartValue() );
      assertEquals( 60, animation.getDefaultRenderer().getEndValue() );
      assertEquals( left, TestUtil.getElementBounds( shell.getElement() ).left );
    },

    testShellFlyInBottomStart : function() {
      shell.setAnimation( { "flyInBottom" : [ 400, "linear" ] } );
      shell.hide();

      shell.show();
      TestUtil.flush();

      var animation = this.getAppearAnimation( shell );
      assertTrue( animation.isStarted() );
      assertTrue( shell.isSeeable() );
      var top = shell.getParent().getInnerHeight();
      assertEquals( top, animation.getDefaultRenderer().getStartValue() );
      assertEquals( 40, animation.getDefaultRenderer().getEndValue() );
      assertEquals( top, TestUtil.getElementBounds( shell.getElement() ).top );
    },

    testShellFlyInTopStartOnCreate : function() {
      shell.setAnimation( { "flyInTop" : [ 400, "linear" ] } );
      TestUtil.fakeResponse( true );
      shell.hide();

      shell.show();
      TestUtil.fakeResponse( false );
      var animation = this.getAppearAnimation( shell );
      assertFalse( animation.isStarted() );
      TestUtil.flush();

      assertTrue( animation.isStarted() );
    },

    testShellFlyInTopRun : function() {
      shell.setAnimation( { "flyInTop" : [ 400, "linear" ] } );
      shell.hide();

      shell.show();
      TestUtil.flush();
      Animation._mainLoop();

      var animation = this.getAppearAnimation( shell );
      assertTrue( animation.isRunning() );
    },

    testShellFlyInTopFinish : function() {
      shell.setAnimation( { "flyInTop" : [ 400, "linear" ] } );
      shell.hide();

      shell.show();
      TestUtil.flush();
      Animation._mainLoop();
      var animation = this.getAppearAnimation( shell );
      animation._loop( ( new Date().getTime() ) + 401 );

      assertFalse( animation.isRunning() );
      assertEquals( 40, animation.getDefaultRenderer().getLastValue() );
      assertEquals( 40, TestUtil.getElementBounds( shell.getElement() ).top );
    },

    testShellFlyOutTopConfigureNoAppearAnimation : function() {
      shell.setAnimation( { "flyOutTop" : [ 400, "linear" ] } );

      assertNull( this.getAppearAnimation( shell ) );
    },

    testShellFlyOutTopConfigure : function() {
      shell.setAnimation( { "flyOutTop" : [ 400, "linear" ] } );

      var animation = this.getDisappearAnimation( shell );
      assertEquals( 400, animation.getDuration() );
      assertEquals( rwt.client.Client.isMshtml(), animation.getExclusive() );
    },

    testShellFlyOutTopStart : function() {
      shell.setAnimation( { "flyOutTop" : [ 400, "linear" ] } );
      shell.show();
      TestUtil.flush();

      shell.hide();
      TestUtil.flush();

      var animation = this.getDisappearAnimation( shell );
      assertTrue( animation.isStarted() );
      assertTrue( shell.isSeeable() );
      assertEquals( 40, animation.getDefaultRenderer().getStartValue() );
      assertEquals( -50, animation.getDefaultRenderer().getEndValue() );
      assertEquals( 40, TestUtil.getElementBounds( shell.getElement() ).top );
    },

//   TODO [tb] : This is untestable until "real" (browser-native) events are used for testing
//    testShellFlyOutBlockEvents : function() {
//      var log = [];
//      shell.setAnimation( { "flyOutTop" : [ 400, "linear" ] } );
//      shell.show();
//      shell.addEventListener( "click", function( event ) {
//        log.push( event );
//      } );
//      TestUtil.flush();
//
//      shell.hide();
//      TestUtil.click( shell );
//      TestUtil.flush();
//
//      assertEquals( 0, log.length );
//    },
//
    testShellFlyOutTopRun : function() {
      shell.setAnimation( { "flyOutTop" : [ 400, "linear" ] } );
      shell.show();
      TestUtil.flush();

      shell.hide();
      TestUtil.flush();
      Animation._mainLoop();

      var animation = this.getDisappearAnimation( shell );
      assertTrue( animation.isRunning() );
    },

    testShellFlyOutTopFinish : function() {
      shell.setAnimation( { "flyOutTop" : [ 400, "linear" ] } );
      shell.show();
      TestUtil.flush();

      shell.hide();
      TestUtil.flush();
      Animation._mainLoop();
      var animation = this.getDisappearAnimation( shell );
      animation._loop( ( new Date().getTime() ) + 401 );

      assertFalse( animation.isRunning() );
      assertEquals( -50, animation.getDefaultRenderer().getLastValue() );
      assertFalse( shell.isSeeable() );
    },

    testShellFlyOutTopDestroyRun : function() {
      shell.setAnimation( { "flyOutTop" : [ 400, "linear" ] } );
      shell.show();
      TestUtil.flush();

      shell.destroy();
      TestUtil.flush();
      Animation._mainLoop();
      TestUtil.flush();

      assertTrue( shell.isSeeable() );
      assertFalse( shell.isDisposed() );
      var animation = this.getDisappearAnimation( shell );
      assertTrue( animation.isRunning() );
    },

    testShellFlyOutTopDestroyFinish : function() {
      shell.setAnimation( { "flyOutTop" : [ 400, "linear" ] } );
      var animation = this.getDisappearAnimation( shell );
      shell.show();
      TestUtil.flush();

      shell.destroy();
      TestUtil.flush();
      Animation._mainLoop();
      TestUtil.flush();
      animation._loop( ( new Date().getTime() ) + 401 );
      TestUtil.flush();

      assertFalse( animation.isRunning() );
      assertTrue( shell.isDisposed() );
    },

    testShellFlyOutTopDestroyKeepSubwidgetsAlive : function() {
      var widget = new rwt.widgets.base.MultiCellWidget( [] );
      widget.setParent( shell );
      shell.setAnimation( { "flyOutTop" : [ 400, "linear" ] } );
      shell.show();
      TestUtil.flush();

      shell.destroy();
      widget.destroy();
      TestUtil.flush();
      Animation._mainLoop();
      TestUtil.flush();

      assertTrue( widget.isSeeable() );
      assertFalse( widget.isDisposed() );
    },

    testShellFlyOutTopDestroyKeepIndirectSubwidgetsAlive : function() {
      var parent = new rwt.widgets.Composite();
      parent.setParent( shell );
      var widget = new rwt.widgets.base.MultiCellWidget( [] );
      widget.setParent( parent );
      shell.setAnimation( { "flyOutTop" : [ 400, "linear" ] } );
      shell.show();
      TestUtil.flush();

      shell.destroy();
      widget.destroy();
      TestUtil.flush();
      Animation._mainLoop();
      TestUtil.flush();

      assertTrue( widget.isSeeable() );
      assertFalse( widget.isDisposed() );
    },

    testShellFlyOutTopDestroyFinishIndirectSubwidgetsGetDestroyed : function() {
      var parent = new rwt.widgets.Composite();
      parent.setParent( shell );
      var widget = new rwt.widgets.base.MultiCellWidget( [] );
      widget.setParent( parent );
      shell.setAnimation( { "flyOutTop" : [ 400, "linear" ] } );
      var animation = this.getDisappearAnimation( shell );
      shell.show();
      TestUtil.flush();

      shell.destroy();
      widget.destroy();
      TestUtil.flush();
      Animation._mainLoop();
      TestUtil.flush();
      animation._loop( ( new Date().getTime() ) + 401 );
      TestUtil.flush();

      assertTrue( widget.isDisposed() );
    },

    testShellFlyOutDelayActive : function() {
      var anotherShell = TestUtil.createShellByProtocol( "w3" );
      anotherShell.show();
      shell.setAnimation( { "flyOutTop" : [ 400, "linear" ] } );
      shell.hide();
      Processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "PUSH" ],
          "parent" : "w3"
        }
      } );
      shell.show();
      TestUtil.flush();
      shell.setActive( true );

      shell.hide();
      Processor.processOperation( {
        "target" : "w3",
        "action" : "set",
        "properties" : {
          "active" : true
        }
      } );
      Processor.processOperation( {
        "target" : "w1",
        "action" : "set",
        "properties" : {
          "focusControl" : [ "w4" ]
        }
      } );
      TestUtil.flush();
      Animation._mainLoop();

      assertTrue( shell.hasState( "active" ) );
      var animation = this.getDisappearAnimation( shell );
      animation._loop( ( new Date().getTime() ) + 401 );
      assertFalse( shell.hasState( "active" ) );
    },

    /////////
    // helper

    setUp : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
      TestUtil.fakeResponse( true );
      shell.setTop( 40 );
      shell.setHeight( 50 );
      shell.setLeft( 60 );
      shell.setWidth( 70 );
      TestUtil.fakeResponse( false );
    },

    tearDown : function() {
      if( !shell.isDisposed() ) {
        shell.destroy();
      }
      shell = null;
    },

    getAppearAnimation : function( widget ) {
      return widget._appearAnimation;
    },

    getDisappearAnimation : function( widget ) {
      return widget._disappearAnimation;
    }

  }

} );

}());