/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define("org.eclipse.rwt.test.Application", {
  extend : qx.application.Gui,

  construct : function() {
    this.base( arguments );
  },

  members : {    
    main : function( evt ) {
      this.base( arguments );
      this.info( "Starting RAP AppSimulator" );
      org.eclipse.rwt.test.fixture.TestUtil.initRequestLog();
      org.eclipse.rwt.test.fixture.AppSimulator.start();
      this.info( "Creating testrunner" );      
      this.runner = org.eclipse.rwt.test.TestRunner.getInstance();
      org.eclipse.rwt.test.Asserts.createShortcuts();
      this.info( "DONE" );
    },
    
    // after the application is completely loaded:
    _postloaderDone : function() {
      this.base( arguments );
      this.runner.run();
    }
  }
});