/*******************************************************************************
 * Copyright (c) 2009, 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
 
 qx.Mixin.define( "org.eclipse.rwt.DomEventPatch",
{
  "members" : {
    
    setDomEvent : function( domEvent ) {
      this.base( arguments, domEvent );
      org.eclipse.swt.EventUtil._shiftKey = domEvent.shiftKey;
      org.eclipse.swt.EventUtil._ctrlKey = domEvent.ctrlKey;
      org.eclipse.swt.EventUtil._altKey = domEvent.altKey;
      org.eclipse.swt.EventUtil._metaKey = domEvent.metaKey;
    }
    
  }
} );