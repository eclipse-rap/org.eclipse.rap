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
var Animation =  org.eclipse.rwt.Animation;
var Processor = org.eclipse.rwt.protocol.Processor;
var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;

var shell;

qx.Class.define( "org.eclipse.rwt.test.tests.VisibilityAnimationMixinTest", {
  extend : qx.core.Object,
  
  members : {
    
    testShellFlyInTopConfigure : function() {
      shell.setAnimation( { "flyInTop" : [ 400, "linear" ] } );
  
      var animation = this.getAppearAnimation( shell );
      assertEquals( 400, animation.getDuration() );
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
      shell.hide();

      shell.show();
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

    testShellFlyInRightStartDelayFocus : function() {
      shell.setAnimation( { "flyInRight" : [ 400, "linear" ] } );
      shell.hide();
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "PUSH" ],
          "parent" : "w2"
        }
      } );
      Processor.processOperation( {
        "target" : "w1",
        "action" : "set",
        "properties" : {
          "focusControl" : [ "w3" ]
        }
      } );
      var widget = ObjectManager.getObject( "w3" );

      shell.show();
      TestUtil.flush();
      Animation._mainLoop();
      assertEquals( 0, document.body.scrollLeft );
  
      var animation = this.getAppearAnimation( shell );
      animation._loop( ( new Date().getTime() ) + 401 );
      assertEquals( 0, document.body.scrollLeft );
      assertFalse( animation.isRunning() );
      assertTrue( widget.getFocused() );
      var left = qx.html.Window.getInnerWidth( window );
      assertEquals( 60, animation.getDefaultRenderer().getLastValue() );
      assertEquals( 60, TestUtil.getElementBounds( shell.getElement() ).left );
    },


    testShellFlyOutTopConfigureNoAppearAnimation : function() {
      try{
        throw 1;
      }catch(e){}
      shell.setAnimation( { "flyOutTop" : [ 400, "linear" ] } );
  
      assertNull( this.getAppearAnimation( shell ) );
    },

    /////////
    // helper
    
    setUp : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
      shell.setTop( 40 );
      shell.setHeight( 50 );
      shell.setLeft( 60 );
      shell.setWidth( 70 );
    },
    
    tearDown : function() {
      shell.destroy();
      shell = null;
    },
    
    getAppearAnimation : function( widget ) {
      return widget._appearAnimation;
    }

  }

} );

}());