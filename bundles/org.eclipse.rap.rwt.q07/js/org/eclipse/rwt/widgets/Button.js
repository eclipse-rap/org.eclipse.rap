/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/


qx.Class.define( "org.eclipse.rwt.widgets.Button", {
  extend : org.eclipse.rwt.widgets.AbstractButton,

  construct : function( buttonType ) {
    this.base( arguments, buttonType );
    switch( buttonType ) {
     case "push" :
     case "toggle":
      this.setAppearance( "button" );
     break;
     case "check":
      this.setAppearance( "check-box" );
     break;
     case "radio":
      this.setAppearance( "radio-button" );
    }
    this.initTabIndex();      
  },
  
  properties : {

    tabIndex : {
      refine : true,
      init : -1
    }
        
  }
} );