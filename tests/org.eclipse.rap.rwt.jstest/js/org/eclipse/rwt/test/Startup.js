/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

org.eclipse.rwt.System.getInstance().addEventListener( "uiready", function() {
  qx.Class.__initializeClass( org.eclipse.swt.Request );
  qx.Class.patch( org.eclipse.swt.Request, org.eclipse.rwt.test.fixture.RAPRequestPatch );
  org.eclipse.rwt.KeyEventSupport.getInstance()._sendRequestAsync = function() {
    org.eclipse.swt.Request.getInstance()._sendImmediate( true );
  };
  org.eclipse.rwt.protocol.Processor.processMessage( {
    "meta": {
      "requestCounter": -1
    },
    "operations": [ [ "create", "w1", "rwt.Display" ] ]
  } );
  org.eclipse.swt.Request.getInstance().setRequestCounter( 0 );
  org.eclipse.rwt.test.fixture.TestUtil.initRequestLog();
  org.eclipse.rwt.test.Asserts.createShortcuts();
  org.eclipse.rwt.test.TestRunner.getInstance().run();
} );