/*******************************************************************************
 * Copyright (c) 2012, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 *    Kyle Smith - Add evaluate method
 ******************************************************************************/

rwt.remote.HandlerRegistry.add( "rwt.client.JavaScriptExecutor", {

  factory : function() {
    return rwt.client.JavaScriptExecutor.getInstance();
  },

  service : true,

  destructor : rwt.util.Functions.returnTrue,

  methods : [
    "execute",
    "evaluate"
  ],

  methodHandler : {
    "execute" : function( object, args ) {
      object.execute( args.content );
    },
    "evaluate" : function( object, args ) {
      object.evaluate( args.futureId, args.content );
    }
  }

} );
