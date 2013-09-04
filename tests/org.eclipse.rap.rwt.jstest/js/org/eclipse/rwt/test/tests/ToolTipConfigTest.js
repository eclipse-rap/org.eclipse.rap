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
var ToolTipConfig = rwt.widgets.util.ToolTipConfig;

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
    }

  }

} );

}());