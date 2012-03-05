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
    },
    
    getAppearAnimation : function( widget ) {
      return widget._appearAnimation;
    }

  }

} );

}());