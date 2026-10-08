/*******************************************************************************
 * Copyright (c) 2011, 2022 EclipseSource and others.
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

rwt.remote.HandlerRegistry.add( "rwt.widgets.Display", {

  factory : function( properties ) {
    return new rwt.widgets.Display( properties );
  },

  destructor : null, // destroy is currently not called for display

  properties : [
    "overflow",
    "exitConfirmation",
    "mnemonicActivator",
    "focusControl",
    "enableUiTests",
    "disableShutdownRequest",
    "activeKeys",
    "cancelKeys"
  ],

  methods : [
    "allowEvent",
    "cancelEvent",
    "beep"
  ],

  propertyHandler : {
    "activeKeys" : function( object, value ) {
      var map = rwt.util.Objects.fromArray( value );
      rwt.remote.KeyEventSupport.getInstance().setKeyBindings( map );
    },
    "cancelKeys" : function( object, value ) {
      var map = rwt.util.Objects.fromArray( value );
      rwt.remote.KeyEventSupport.getInstance().setCancelKeys( map );
    }
  },

  listeners : [ "KeyDown", "Resize" ]

} );
