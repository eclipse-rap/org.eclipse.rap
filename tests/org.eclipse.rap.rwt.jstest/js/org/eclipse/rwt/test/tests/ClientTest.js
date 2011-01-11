/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.ClientTest", {

  extend : qx.core.Object,
  
  members : {
    
    testRunsLocally : function() {
      var msg = "Always run tests from an http-server!";
      assertFalse( msg, org.eclipse.rwt.Client.getRunsLocally() );
      // NOTE: If this fails, either getRunsLocally returns the wrong value
      //       or, more likely, you started tests from the filesystem
    },
    
    testQuirksmode : function() {
      // NOTE: RAP should always run in quirksmode. (Currently no exceptions.)
      assertTrue( org.eclipse.rwt.Client.isInQuirksMode() );
    },
    
    testEngine : function() {
      var client = org.eclipse.rwt.Client;
      var engines = [ "mshtml", "gecko", "webkit", "opera" ];
      var currentEngine = client.getEngine();
      assertTrue( engines.indexOf( currentEngine ) != -1 );
      var isEngine = 0;
      isEngine = client.isMshtml() ? isEngine + 1 : isEngine;
      isEngine = client.isGecko() ? isEngine + 1 : isEngine;
      isEngine = client.isOpera() ? isEngine + 1 : isEngine;
      isEngine = client.isWebkit() ? isEngine + 1 : isEngine;
      assertEquals( 1, isEngine );
      assertEquals( "string", typeof client.getBrowser() );
      // NOTE: No check for specific browsers since there many clones that are 
      // technically almost identical to the major ones and should run RAP.
    },
    
    testVersions : function() {
      var client = org.eclipse.rwt.Client;
      assertEquals( "number", typeof client.getVersion() );
      assertEquals( "number", typeof client.getMajor() );
      assertEquals( "number", typeof client.getMinor() );
      assertEquals( "number", typeof client.getRevision() );
      assertEquals( "number", typeof client.getBuild() );
    },

    testPlatform : function() {
      var client = org.eclipse.rwt.Client;
      var platforms = [ "win", "mac", "unix", "ios", "android" ];
      var currentPlatform = client.getPlatform();
      assertTrue( platforms.indexOf( currentPlatform ) != -1 );
    },

    testGraphicsSupport : function() {
      // Canvas present in all browser except IE, no check implemented.
      var svg = org.eclipse.rwt.Client.supportsSvg();
      var vml = org.eclipse.rwt.Client.supportsVml();
      var css3 = org.eclipse.rwt.Client.supportsCss3();
      assertEquals( "boolean", typeof svg );
      assertEquals( "boolean", typeof vml );
      assertEquals( "boolean", typeof css3 );
      assertTrue( "Theming support", svg || vml || css3 );
    },

    testLocale : function() {
      var client = org.eclipse.rwt.Client;
      assertEquals( "string", typeof client.getLocale() );
      assertEquals( "string", typeof client.getLanguage() );
      assertEquals( "string", typeof client.getTerritory() );
      assertEquals( "string", typeof client.getDefaultLocale() );
      assertEquals( "boolean", typeof client.usesDefaultLocale() );
    },
    
    testBoxSizingAttributes : function() {
      var attr = org.eclipse.rwt.Client.getEngineBoxSizingAttributes();
      assertTrue( attr instanceof Array );
      assertEquals( "string", typeof attr[ 0 ] );
    },
    
    testMobile : function() {
      var client = org.eclipse.rwt.Client;
      var msafari = client.isMobileSafari();
      var androidb = client.isAndroidBrowser();
      assertEquals( "boolean", typeof msafari );
      assertEquals( "boolean", typeof androidb );
      assertTrue( !( msafari && androidb ) );
      // NOTE: Since the android browser indentifies itself as safari,
      //       there is a certain risk of confusing the two. 
    }

  }

} );