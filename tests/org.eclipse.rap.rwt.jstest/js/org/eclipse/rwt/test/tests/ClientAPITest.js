/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.ClientAPITest", {

  extend : qx.core.Object,

  members : {

    testProtocolAdapterDelegation : function() {
      var handler = {};

      rap.registerTypeHandler( "myTestType", handler );

      assertIdentical( handler, rwt.protocol.AdapterRegistry.getAdapter( "myTestType" ) );
      rwt.protocol.AdapterRegistry.remove( "myTestType" );
    }


  }

} );