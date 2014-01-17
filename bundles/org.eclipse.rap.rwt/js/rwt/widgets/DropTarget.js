/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

namespace( "rwt.widgets" );

rwt.widgets.DropTarget = function( control, operations ) {
  this.control = control;
  this.actions = rwt.remote.DNDSupport.getInstance()._operationsToActions( operations );
  rwt.remote.DNDSupport.getInstance().registerDropTarget( control, operations, this );
};

rwt.widgets.DropTarget.prototype = {

  dispose : function() {
    rwt.remote.DNDSupport.getInstance().deregisterDropTarget( this.control );
  },

  setTransfer : function( transferTypes ) {
    rwt.remote.DNDSupport.getInstance().setDropTargetTransferTypes( this.control, transferTypes );
  },

  setHasDragEnterListener : function( value ) {
    rwt.remote.DNDSupport.getInstance().setHasListener( this.control, "DragEnter", value );
  },

  setHasDragOverListener : function( value ) {
    rwt.remote.DNDSupport.getInstance().setHasListener( this.control, "DragOver", value );
  },

  setHasDragLeaveListener : function( value ) {
    rwt.remote.DNDSupport.getInstance().setHasListener( this.control, "DragLeave", value );
  },

  setHasDragOperationChangedListener : function( value ) {
    rwt.remote.DNDSupport.getInstance().setHasListener( this.control, "DragOperationChanged", value );
  },

  setHasDropAcceptListener : function( value ) {
    rwt.remote.DNDSupport.getInstance().setHasListener( this.control, "DropAccept", value );
  },

  changeFeedback : function( feedback, flags ) {
    rwt.remote.DNDSupport.getInstance().setFeedback( this.control, feedback, flags );
  },

  changeDetail : function( detail ) {
    rwt.remote.DNDSupport.getInstance().setOperationOverwrite( this.control, detail );
  },

  changeDataType : function( dataType ) {
    rwt.remote.DNDSupport.getInstance().setDataType( this.control, dataType );
  }

};
