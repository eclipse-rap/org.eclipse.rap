/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.ShellTest", {
  extend : qx.core.Object,

  members : {

    testDisplayOverlayBackground : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      // first check that the default theme for overlay has no background set
      var tv = new org.eclipse.swt.theme.ThemeValues( {} );
      var backgroundImage = tv.getCssImage( "Shell-DisplayOverlay",
                                            "background-image" );
      assertNull( backgroundImage );
// [if] This is not testable with the new default theme as we currently can't
// fake the ThemeStore. Reactivate when the ThemeStore fixture is available.
//    assertEquals( "undefined", backgroundColor );
      // create shell like the LCA would do:
      TestUtil.fakeResponse( true );
      var shell = new rwt.widgets.Shell( [ "APPLICATION_MODAL" ] );
      shell.addState( "rwt_APPLICATION_MODAL" );
      shell.initialize();
      shell.open();
      shell.setActive( true );
      shell.setSpace( 50, 300, 50, 200 );
      shell.setVisibility( true );
      TestUtil.flush();
      TestUtil.fakeResponse( false );
      // Check for overlay background-image to be "blank.gif", as IE needs
      // this to capture mouse events.
      var overlay = rwt.widgets.base.ClientDocument.getInstance()._getBlocker();
      assertTrue( overlay.isSeeable() );
// [if] This is not testable with the new default theme as we currently can't
// fake the ThemeStore. Reactivate when the ThemeStore fixture is available.
//    assertEquals( "static/image/blank.gif", overlay.getBackgroundImage() );
      shell.doClose();
      overlay.hide(); // not done by doClose because this is the only shell
      TestUtil.flush();
      shell.destroy();
      TestUtil.flush();
    },

    testDisplayOverlayCopyStates : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.fakeResponse( true );
      var shell = new rwt.widgets.Shell( [ "APPLICATION_MODAL" ] );
      shell.addState( "rwt_APPLICATION_MODAL" );
      shell.addState( "rwt_myTest" );
      shell.initialize()
      shell.open();
      shell.setActive( true );
      shell.setSpace( 50, 300, 50, 200 );
      shell.setVisibility( true );
      TestUtil.flush();
      TestUtil.fakeResponse( false );
      // Check for overlay to have the same states as the shell
      var overlay = rwt.widgets.base.ClientDocument.getInstance()._getBlocker();
      assertTrue( overlay.isSeeable() );
      assertTrue( overlay.hasState( "rwt_APPLICATION_MODAL" ) );
      assertTrue( overlay.hasState( "rwt_myTest" ) );
      shell.doClose();
      overlay.hide(); // not done by doClose because this is the only shell
      TestUtil.flush();
      shell.destroy();
      TestUtil.flush();
    },

    testDisplayOverlayAddStates : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.fakeResponse( true );
      var shell = new rwt.widgets.Shell( [ "APPLICATION_MODAL" ] );
      shell.addState( "rwt_APPLICATION_MODAL" );
      shell.initialize();
      shell.open();
      shell.setActive( true );
      shell.setSpace( 50, 300, 50, 200 );
      shell.setVisibility( true );
      TestUtil.flush();
      TestUtil.fakeResponse( false );
      // Check for overlay to have the same states as the shell
      var overlay = rwt.widgets.base.ClientDocument.getInstance()._getBlocker();
      assertTrue( overlay.isSeeable() );
      assertTrue( overlay.hasState( "rwt_APPLICATION_MODAL" ) );
      shell.addState( "rwt_myTest" );
      assertTrue( overlay.hasState( "rwt_myTest" ) );
      shell.removeState( "rwt_myTest" );
      assertFalse( overlay.hasState( "rwt_myTest" ) );
      shell.doClose();
      overlay.hide(); // not done by doClose because this is the only shell
      TestUtil.flush();
      shell.destroy();
      TestUtil.flush();
    },

    testDisplayOverlayMultipleShells : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var overlay = rwt.widgets.base.ClientDocument.getInstance()._getBlocker();
      var visibilityChanges = 0;
      overlay.addEventListener( "changeVisibility", function( event) {
        visibilityChanges++;
      } );
      TestUtil.fakeResponse( true );
      var shell = new rwt.widgets.Shell( [ "APPLICATION_MODAL" ] );
      shell.addState( "rwt_APPLICATION_MODAL" );
      shell.initialize();
      shell.open();
      shell.setActive( true );
      shell.setSpace( 50, 300, 50, 200 );
      shell.setVisibility( true );
      TestUtil.flush();
      var shell2 = new rwt.widgets.Shell( [ "APPLICATION_MODAL" ] );
      shell2.addState( "rwt_APPLICATION_MODAL" );
      shell2.initialize();
      shell2.addState( "rwt_myTest2" );
      shell2.open();
      shell2.setActive( true );
      shell2.setSpace( 100, 300, 50, 200 );
      shell2.setVisibility( true );
      TestUtil.flush();
      TestUtil.fakeResponse( false );
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
      TestUtil.flush();
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
      TestUtil.flush();
      shell.destroy();
      TestUtil.flush();
    },

    testCustomVariant : function() {
      var shell = new rwt.widgets.Shell( [ "APPLICATION_MODAL" ] );
      var variant = "variant_myCustomVariant";
      shell.addState( variant );
      assertTrue( shell._captionBar.hasState( variant) );
      assertTrue( shell._captionTitle.hasState( variant) );
      assertTrue( shell._minimizeButton.hasState( variant) );
      assertTrue( shell._maximizeButton.hasState( variant) );
      assertTrue( shell._restoreButton.hasState( variant) );
      assertTrue( shell._closeButton.hasState( variant) );
      shell.removeState( variant );
      assertFalse( shell._captionBar.hasState( variant) );
      assertFalse( shell._captionTitle.hasState( variant) );
      assertFalse( shell._minimizeButton.hasState( variant) );
      assertFalse( shell._maximizeButton.hasState( variant) );
      assertFalse( shell._restoreButton.hasState( variant) );
      assertFalse( shell._closeButton.hasState( variant) );
    },

    testDefaultButtonState : function() {
      var shell = new rwt.widgets.Shell( [ "APPLICATION_MODAL" ] );
      var button = new rwt.widgets.Button( "push" );
      assertFalse( button.hasState( "default") );
      shell.setDefaultButton( button );
      assertTrue( button.hasState( "default") );
      shell.setDefaultButton( null );
      assertFalse( button.hasState( "default") );
      button.destroy();
      shell.destroy();
    },

    testFiresParentShellChangedEvent : function() {
      var shell = this._createDefaultShell( {} );
      var parentShell = this._createDefaultShell( {} );
      var log = 0;
      shell.addEventListener( "parentShellChanged", function() {
        log++;
      } );

      shell.setParentShell( parentShell );

      assertTrue( log > 0 );
      shell.destroy();
      parentShell.destroy();
    },

    _createDefaultShell : function( styles, noFlush ) {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.fakeResponse( true );
      var shell = new rwt.widgets.Shell( styles );
      shell.initialize();
      shell.open();
      shell.setActive( true );
      shell.setSpace( 50, 300, 50, 200 );
      shell.setVisibility( true );
      TestUtil.fakeResponse( false );
      if( !noFlush ) {
        var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
        TestUtil.flush();
      }
      return shell;
    }
  }

} );