/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var ErrorHandler = rwt.runtime.ErrorHandler;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ErrorHandlerTest", {

  extend : rwt.qx.Object,

  members : {

    testLayoutWaitHintOverlay : function() {
      ErrorHandler.showWaitHint();

      var element = rwt.runtime.ErrorHandler._overlay;
      assertIdentical( document.body, element.parentNode );
      assertEquals( "100%", element.style.width );
      assertEquals( "100%", element.style.height );
      assertEquals( "100%", element.style.height );
      assertEquals( "absolute", element.style.position );
      assertEquals( 100000000, element.style.zIndex);
      ErrorHandler.hideErrorBox();
    },

    testWaitHintOverlayBlocksKeyEvents : function() {
      ErrorHandler.showWaitHint();
      assertTrue( rwt.event.EventHandler.getBlockKeyEvents() );
      ErrorHandler.hideErrorBox();
      assertFalse( rwt.event.EventHandler.getBlockKeyEvents() );
    }

//    TODO [tb] : Mock the entire themestore, render test icon
//
//    testShowWaitHintIcon : function() {
//      ErrorHandler.showWaitHint();
//
//      var element = rwt.runtime.ErrorHandler._box;
//      assertEquals( document.body, element.parentNode );
//      assertEquals( "absolute", element.style.position );
//      assertEquals( "transparent", element.style.backgroundColor );
//      assertEquals( "none", element.style.border );
//      assertEquals( 100000001, element.style.zIndex);
//      ErrorHandler.hideErrorBox();
//    }

  }

} );

}());