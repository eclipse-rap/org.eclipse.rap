/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var ObjectRegistry = rwt.remote.ObjectRegistry;
var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;

var launcher;
var logger;
var iframe;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.UrlLauncherTest", {

  extend : rwt.qx.Object,

  members : {

    setUp : function() {
      launcher = rwt.client.UrlLauncher.getInstance();
      logger = TestUtil.getLogger();
      launcher._window = {
        open : function() {
          logger.log( arguments );
        }
      };
      iframe = launcher._iframe;
    },

    tearDown : function() {
      launcher._window = window;
      launcher._iframe = iframe;
    },

    testCreateUrlLauncherByProtocol : function() {
      assertIdentical( launcher, ObjectRegistry.getObject( "rwt.client.UrlLauncher" ) );
    },

    testCreatesIFrame : function() {
      assertIdentical( document.body, iframe.parentNode );
      assertEquals( "hidden", iframe.style.visibility );
    },

    testLaunchURLInBlankTarget : function() {
      launcher.openURL( "http://foor.bar" );

      var args = logger.getLog()[ 0 ];
      assertEquals( "http://foor.bar", args[ 0 ] );
      assertEquals( "_blank", args[ 1 ] );
    },

    testLaunchFileURLsInNewWindow : function() {
      launcher.openURL( "foor.bar" );
      launcher.openURL( "ftp://foor.bar" );
      launcher.openURL( "ftps://foor.bar" );
      launcher.openURL( "HTTP://foor.bar" );
      launcher.openURL( "FTP://foor.bar" );

      assertEquals( "foor.bar", logger.getLog()[ 0 ][ 0 ] );
      assertEquals( "ftp://foor.bar", logger.getLog()[ 1 ][ 0 ] );
      assertEquals( "ftps://foor.bar", logger.getLog()[ 2 ][ 0 ] );
      assertEquals( "HTTP://foor.bar", logger.getLog()[ 3 ][ 0 ] );
      assertEquals( "FTP://foor.bar", logger.getLog()[ 4 ][ 0 ] );
    },

    testLaunchMailtoInIFrame : function() {
      var fakeFrame = this.fakeIFrame();

      launcher.openURL( "mailto:some@one.org" );

      assertEquals( "mailto:some@one.org", fakeFrame.src );
    },

    testLaunchTelInIFrame : function() {
      var fakeFrame = this.fakeIFrame();

      launcher.openURL( "tel:01234" );

      assertEquals( "tel:01234", fakeFrame.src );
    },

    fakeIFrame : function() {
      var fake = {};
      launcher._iframe = fake;
      return fake;
    }

  }

} );

}());