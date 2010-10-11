/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.ShellTest", {
  extend : qx.core.Object,
  
  members : {

    testDisplayOverlayBackground : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      // first check that the default theme for overlay has no background set
      var tv = new org.eclipse.swt.theme.ThemeValues( {} );
      var backgroundColor = tv.getCssColor( "Shell-DisplayOverlay", 
                                            "background-color" );
      var backgroundImage = tv.getCssImage( "Shell-DisplayOverlay", 
                                            "background-image" );
      assertNull( backgroundImage );
// [if] This is not testable with the new default theme as we currently can't
// fake the ThemeStore. Reactivate when the ThemeStore fixture is available.
//    assertEquals( "undefined", backgroundColor );
      // create shell like the LCA would do:
      var shell = new org.eclipse.swt.widgets.Shell();
      shell.addToDocument();
      shell.addState( "rwt_APPLICATION_MODAL" );
      shell.initialize()
      shell.open();
      shell.setActive( true );
      shell.setSpace( 50, 300, 50, 200 );
      shell.setVisibility( true );
      testUtil.flush();
      // Check for overlay background-image to be "blank.gif", as IE needs
      // this to capture mouse events. 
      var overlay = qx.ui.core.ClientDocument.getInstance()._getBlocker();
      assertTrue( overlay.isSeeable() );
// [if] This is not testable with the new default theme as we currently can't
// fake the ThemeStore. Reactivate when the ThemeStore fixture is available.
//    assertEquals( "static/image/blank.gif", overlay.getBackgroundImage() );
      shell.doClose();
      overlay.hide(); // not done by doClose because this is the only shell
      testUtil.flush();
      shell.destroy();
      testUtil.flush();
    },
    
    testDisplayOverlayCopyStates : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = new org.eclipse.swt.widgets.Shell();
      shell.addToDocument();
      shell.addState( "rwt_APPLICATION_MODAL" );
      shell.addState( "rwt_myTest" );
      shell.initialize()
      shell.open();
      shell.setActive( true );
      shell.setSpace( 50, 300, 50, 200 );
      shell.setVisibility( true );
      testUtil.flush();
      // Check for overlay to have the same states as the shell 
      var overlay = qx.ui.core.ClientDocument.getInstance()._getBlocker();
      assertTrue( overlay.isSeeable() );
      assertTrue( overlay.hasState( "rwt_APPLICATION_MODAL" ) );
      assertTrue( overlay.hasState( "rwt_myTest" ) );
      shell.doClose();
      overlay.hide(); // not done by doClose because this is the only shell
      testUtil.flush();
      shell.destroy();
      testUtil.flush();      
    },

    testDisplayOverlayAddStates : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = new org.eclipse.swt.widgets.Shell();
      shell.addToDocument();
      shell.addState( "rwt_APPLICATION_MODAL" );
      shell.initialize()
      shell.open();
      shell.setActive( true );
      shell.setSpace( 50, 300, 50, 200 );
      shell.setVisibility( true );
      testUtil.flush();
      // Check for overlay to have the same states as the shell 
      var overlay = qx.ui.core.ClientDocument.getInstance()._getBlocker();
      assertTrue( overlay.isSeeable() );
      assertTrue( overlay.hasState( "rwt_APPLICATION_MODAL" ) );
      shell.addState( "rwt_myTest" );
      assertTrue( overlay.hasState( "rwt_myTest" ) );
      shell.removeState( "rwt_myTest" );
      assertFalse( overlay.hasState( "rwt_myTest" ) );
      shell.doClose();
      overlay.hide(); // not done by doClose because this is the only shell
      testUtil.flush();
      shell.destroy();
      testUtil.flush();      
    },
    
    testDisplayOverlayMultipleShells : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var overlay = qx.ui.core.ClientDocument.getInstance()._getBlocker();
      var visibilityChanges = 0;
      overlay.addEventListener( "changeVisibility", function( event) {
        visibilityChanges++;
      } );
      var shell = new org.eclipse.swt.widgets.Shell();
      shell.addToDocument();
      shell.addState( "rwt_APPLICATION_MODAL" );
      shell.initialize()
      shell.open();
      shell.setActive( true );
      shell.setSpace( 50, 300, 50, 200 );
      shell.setVisibility( true );
      testUtil.flush();
      var shell2 = new org.eclipse.swt.widgets.Shell();
      shell2.addToDocument();
      shell2.addState( "rwt_APPLICATION_MODAL" );
      shell2.addState( "rwt_myTest2" );
      shell2.initialize()
      shell2.open();
      shell2.setActive( true );
      shell2.setSpace( 100, 300, 50, 200 );
      shell2.setVisibility( true );
      testUtil.flush();
      shell.addState( "rwt_myTest1" );
      shell2.addState( "rwt_myTest2b" );
      // check for Z-index and states for shell2
      assertTrue( overlay.isSeeable() );
      assertTrue( overlay.getZIndex() > shell.getZIndex() );
      assertTrue( overlay.getZIndex() < shell2.getZIndex() );
      assertFalse( overlay.hasState( "rwt_myTest1" ) );
      assertTrue( overlay.hasState( "rwt_myTest2" ) );
      assertTrue( overlay.hasState( "rwt_myTest2b" ) );
      // close shell2, check for Z-index and states for shell1
      shell2.doClose();
      testUtil.flush();
      shell.addState( "rwt_myTest1b" );
      assertTrue( overlay.isSeeable() );
      assertTrue( overlay.getZIndex() < shell.getZIndex() );
      assertTrue( overlay.hasState( "rwt_myTest1" ) );
      assertTrue( overlay.hasState( "rwt_myTest1b" ) );
      assertFalse( overlay.hasState( "rwt_myTest2" ) );
      assertFalse( overlay.hasState( "rwt_myTest2b" ) );
      shell.doClose();
      overlay.hide(); // not done by doClose because this is the only shell
      assertEquals( 2, visibilityChanges ); // to prevent unwanted animations
      testUtil.flush();
      shell.destroy();
      testUtil.flush();            
    }

  }
  
} );