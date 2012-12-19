/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.qx.Mixin.define( "rwt.event.DomEventPatch", {

  "members" : {

    setDomEvent : function( domEvent ) {
      this.base( arguments, domEvent );
      rwt.remote.EventUtil._shiftKey = domEvent.shiftKey;
      rwt.remote.EventUtil._ctrlKey = domEvent.ctrlKey;
      rwt.remote.EventUtil._altKey = domEvent.altKey;
      rwt.remote.EventUtil._metaKey = domEvent.metaKey;
    }

  }
} );
