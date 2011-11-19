/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.SystemTest", {

  extend : qx.core.Object,

  members : {

    testInitialization : function() {
      var alias = qx.io.Alias.getInstance();
      assertEquals( "./rwt-resources/resource/static/image/blank.gif",
                    alias.resolve( "static/image/blank.gif" ) );
      assertEquals( "./rwt-resources/resource/widget/rap/cursors/move.gif",
                    alias.resolve( "widget/cursors/move.gif" ) );
    }

  }

} );
