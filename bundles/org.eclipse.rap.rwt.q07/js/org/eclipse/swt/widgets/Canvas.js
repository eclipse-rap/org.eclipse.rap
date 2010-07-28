/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
 
qx.Class.define( "org.eclipse.swt.widgets.Canvas", {
  extend : org.eclipse.swt.widgets.Composite,

  construct : function() {
    this.base( arguments );
    this._gc = null;
  },
  
  destruct : function() {
    if( this._gc != null ) {
      this._gc.dispose();
      this._gc = null;
    }
  },
  
  members : {
    
    getGC : function() {
      if( this._gc == null ) {
        this._gc = new org.eclipse.swt.graphics.GC( this );
      }
      return this._gc;
    }
    
  }
} );