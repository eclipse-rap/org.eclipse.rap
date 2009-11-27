/*******************************************************************************
 * Copyright (c) 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - ongoing development
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.DNDSupport", {
  type : "singleton",
  extend : qx.core.Object,

  construct : function() {
    this.base( arguments );
    this._currentDragSource = null;
    this._currentDropTarget = null;
    this._requestScheduled = false;
    this._dragSources = {};
    this._dropTargets = {};
    this._actionOverwrite = null;
    this._dropTargetEventQueue = {};    
  },

  members : {
    
    /////////////
    // dragSource
    
    registerDragSource : function( widget, operations ) {
      widget.addEventListener( "dragstart", this._dragStartHandler, this ); 
      widget.addEventListener( "dragend", this._dragEndHandler, this );
      var hash = widget.toHashCode();
      this._dragSources[ hash ] = { 
        "dataTypes" : [], 
        "actions" : this._operationsToActions( operations )
      }; 
    },
    
    setDragSourceTransferTypes : function( widget, transferTypes ) {
      var hash = widget.toHashCode();
      this._dragSources[ hash ][ "dataTypes" ] = transferTypes;       
    },
    
    deregisterDragSource : function( widget ) {
      widget.removeEventListener( "dragstart", this._dragStartHandler, this );
      widget.removeEventListener( "dragend", this._dragEndHandler, this );
      var hash = widget.toHashCode();
      delete this._dragSources[ hash ];
    },

    _dragStartHandler : function( event ) {
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var target = event.getCurrentTarget();
      var control = wm.findControl( event.getTarget() );
      if( control == target ) {
        var hash = target.toHashCode();      
        var dataTypes = this._dragSources[ hash ].dataTypes;
        if( dataTypes.length > 0 ) {
          for( var i = 0; i < dataTypes.length; i++ ) {
            event.addData( dataTypes[ i ], true );         
          }
          this._actionOverwrite = null;
          this._currentDragSource = target;
          var dndHandler = qx.event.handler.DragAndDropHandler.getInstance();
          dndHandler.clearActions();    
          event.startDrag();
        }
        this._sendDragSourceEvent( target, "dragStart", event.getMouseEvent() );
      }
    },

    _dragEndHandler : function( event ) {
      var target = event.getCurrentTarget();
      var mouseEvent = event.getMouseEvent();
      this._currentDropTarget = null;
      this._sendDragSourceEvent( target, "dragFinished", mouseEvent );      
    },

    _sendDragSourceEvent : function( widget, type, mouseEvent ) {
      var req = org.eclipse.swt.Request.getInstance();
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var id = wm.findIdByWidget( widget );
      var x = mouseEvent.getPageX();
      var y = mouseEvent.getPageY();
      var eventName = "org.eclipse.swt.dnd." + type;
      req.addEvent( eventName, id );
      req.addParameter( eventName + ".x", x );
      req.addParameter( eventName + ".y", y );
      var time = org.eclipse.swt.EventUtil.eventTimestamp();
      req.addParameter( eventName + ".time", time );        
      req.send();
    },
    
    /////////////
    // dropTarget
    
    registerDropTarget : function( widget, operations ) {      
      widget.addEventListener( "dragover", this._dragOverHandler, this );
      widget.addEventListener( "dragmove", this._dragMoveHandler, this );
      widget.addEventListener( "dragout", this._dragOutHandler, this );
      widget.addEventListener( "dragdrop", this._dragDropHandler, this );
      var hash = widget.toHashCode();
      this._dropTargets[ hash ] = { 
        "actions" : this._operationsToActions( operations ) 
      };
      widget.setSupportsDropMethod( this._supportsDropMethod );
    },
    
    setDropTargetTransferTypes : function( widget, transferTypes ) {
      widget.setDropDataTypes( transferTypes );       
    },

    deregisterDropTarget : function( widget ) {
      widget.setDropDataTypes( [] );
      widget.removeEventListener( "dragover", this._dragOverHandler, this );
      widget.removeEventListener( "dragmove", this._dragMoveHandler, this );
      widget.removeEventListener( "dragout", this._dragOutHandler, this );
      widget.removeEventListener( "dragdrop", this._dragDropHandler, this );
      var hash = widget.toHashCode();
      delete this._dropTargets[ hash ]; 
      widget.setSupportsDropMethod( null );
    },
    
    _supportsDropMethod : function( dragCache ) {
      return true;
    },
    
    _dragOverHandler : function( event ) {
      var target = event.getCurrentTarget();
      var hash = target.toHashCode();
      var mouseEvent = event.getMouseEvent();
      this._currentDropTarget = target;
      var action = this._computeCurrentAction( mouseEvent, target );
      this._sendDropTargetEvent( target, "dragEnter", mouseEvent, action );
    },
    
    _dragMoveHandler : function( event ) {
      var target = event.getCurrentTarget();
      var mouseEvent = event.getMouseEvent();
      var action = this._computeCurrentAction( mouseEvent, target );
      this._setAction( action );
      this._sendDropTargetEvent( target, "dragOver", mouseEvent, action );        
    },    
    
    _dragOutHandler : function( event ) {
      var target = event.getCurrentTarget();
      var mouseEvent = event.getMouseEvent();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance();
      dndHandler.clearActions();
      this._currentDropTarget = null;
      this._actionOverwrite = null;
      if( this._isDropTargetEventScheduled( "dragEnter" ) ) {
        this._cancelDropTargetEvent( "dragEnter" );
        this._cancelDropTargetEvent( "dragOver" );
      } else {
        this._sendDropTargetEvent( target, "dragLeave", mouseEvent, "none" );
      }
    },
         
    _dragDropHandler : function( event ) {
      var target = event.getCurrentTarget();
      var mouseEvent = event.getMouseEvent();
      var action = this._computeCurrentAction( mouseEvent, target );
      this._sendDropTargetEvent( target, "dropAccept", mouseEvent, action ); 
    },
    
    _sendDropTargetEvent : function( widget, type, mouseEvent, action ) {
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var id = wm.findIdByWidget( widget );
      var x = mouseEvent.getPageX();
      var y = mouseEvent.getPageY();
      var source = wm.findIdByWidget( this._currentDragSource ); 
      var time = org.eclipse.swt.EventUtil.eventTimestamp();
      var operation = action == "alias" ? "link" : action; 
      var eventName = "org.eclipse.swt.dnd." + type;
      var event = {};
      event[ "id" ] = id;
      event[ "eventName" ] = eventName;
      var param = {};
      param[ eventName + ".x" ] = x;
      param[ eventName + ".y" ] = y;
      param[ eventName + ".operation" ] = operation;
      param[ eventName + ".source" ] = source;
      param[ eventName + ".time" ] = time;
      event[ "param" ] = param;
      this._dropTargetEventQueue[ type ] = event;
      if( !this._requestScheduled ) {
        var req = org.eclipse.swt.Request.getInstance();
        req.addEventListener( "send", this._onSend, this );
        this._requestScheduled = true;          
        qx.client.Timer.once( req.send, req, 500 );        
      }
    },
    
    _isDropTargetEventScheduled : function( type ) {
      return typeof this._dropTargetEventQueue[ type ] != "undefined";
    },
    
    _cancelDropTargetEvent : function( type ) {
      delete this._dropTargetEventQueue[ type ];
    },
    
    _attachTropTargetEvents : function() {
      var req = org.eclipse.swt.Request.getInstance();
      var events = this._dropTargetEventQueue;
      for( var type in events ) {
        var event = events[ type ];
        req.addEvent( event.eventName, event.id );
        for( var key in event.param ) {
          req.addParameter( key, event.param[ key ] );
        }        
      }
      this._dropTargetEventQueue = {};
    },

    _computeCurrentAction : function( mouseEvent, target ) {
      var result;
      if( this._actionOverwrite != null ) {
        result = this._actionOverwrite;
      } else {
        result = "move";
        var shift = mouseEvent.isShiftPressed(); 
        var ctrl = mouseEvent.isCtrlPressed();
        var alt = mouseEvent.isAltPressed();
        if( ctrl && !shift && !alt ) {
          result = "copy";
        } else if( alt && !shift && !ctrl ) {
          result = "alias";
        } else if( !alt && shift && ctrl ) {
          result = "alias";
        }
        var dropTargetHash = target.toHashCode();
        var dropActions = this._dropTargets[ dropTargetHash ].actions;
        var dragSourceHash = this._currentDragSource.toHashCode();
        var dragActions = this._dragSources[ dragSourceHash ].actions;      
        if( !dragActions[ result ] || !dropActions[ result ] ) {
          result = "none";
        }       
      }      
      return result;      
   },
   
   /////////
   // helper
    
    _onSend : function( event ) {
      this._attachTropTargetEvents();
      this._requestScheduled = false;
      var req = org.eclipse.swt.Request.getInstance();
      req.removeEventListener( "send", this._onSend, this );
    },
    
    _setAction : function( action ) {
      // NOTE: using setCurrentAction would conflict with key events
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance();
      dndHandler.clearActions();
      dndHandler.setAction( action );      
    },
    
    _operationsToActions : function( operations ) {
      var result = {};
      for( var i = 0; i < operations.length; i++ ) {
        var action = operations[ i ] == "link" ? "alias" : operations[ i ];
        result[ action ] = action != null;
      }
      return result;
    },    
    
    //////////////////
    // server response 
    
    cancel : function() {
      this._currentDropTarget = null;
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance();
      dndHandler.globalCancelDrag();
    },
    
    setOperationOverwrite : function( widget, operation ) {
      if( widget == this._currentDropTarget ) {
        var action = operation == "link" ? "alias" : operation;
        var dndHandler = qx.event.handler.DragAndDropHandler.getInstance();
        this._actionOverwrite = action;
        this._setAction( action );
        dndHandler._renderCursor();
      }
    }
  
  }
    
} );

