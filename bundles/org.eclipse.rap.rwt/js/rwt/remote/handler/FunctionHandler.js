/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.remote.HandlerRegistry.add( "rwt.scripting.Function", {

  factory : function( properties ) {
    var scriptCode = properties.scriptCode;
    if( !scriptCode ) {
      scriptCode = rap.getObject( properties.scriptId ).getText();
    }
    var name = properties.name;
    return rwt.scripting.FunctionFactory.createFunction( scriptCode, name );
  },

  isPublic : true

} );
