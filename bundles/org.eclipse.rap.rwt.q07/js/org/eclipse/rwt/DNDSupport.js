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
    this._currentTargetWidget = null;
    this._requestScheduled = false;
    this._dragSources = {};
    this._dropTargets = {};
    this._actionOverwrite = null;
    this._dropTargetEventQueue = {};
    this._feedback = null;
    this._feedbackCode = 0;
    this._feedbackWidget = null;
    this._dataTypeOverwrite = null;
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
    
    isDragSource : function( widget ) {
      var hash = widget.toHashCode();
      return typeof this._dragSources[ hash ] != "undefined";       
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
          var doc = qx.ui.core.ClientDocument.getInstance();
          doc.addEventListener( "mouseover", this._onMouseOver, this );
          doc.addEventListener( "keydown", this._onKeyEvent, this );
          doc.addEventListener( "keyup", this._onKeyEvent, this );
          this.setCurrentTargetWidget( event.getOriginalTarget() );
          this._fakeMouseEvent( "mouseout" ); // fix for bug 296348
          var sourceWidget = dndHandler.__dragCache.sourceWidget;
          var feedbackWidget = this._getFeedbackWidget( control, sourceWidget );
          // Note: Unlike SWT, the feedbackWidget can not be rendered behind
          // the cursor, i.e. with a negative offset, as the widget would
          // get all the mouse-events instead of a potential drop-target. 
          dndHandler.setFeedbackWidget( feedbackWidget, 10, 20 );
          event.startDrag();
        }
        this._sendDragSourceEvent( target, "dragStart", event.getMouseEvent() );
      }
    },

    _dragEndHandler : function( event ) {
      var target = event.getCurrentTarget();
      var mouseEvent = event.getMouseEvent();
      this._sendDragSourceEvent( target, "dragFinished", mouseEvent );
      this._cleanUp()
    },

    _sendDragSourceEvent : function( widget, type, mouseEvent ) {
      var req = org.eclipse.swt.Request.getInstance();
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var id = wm.findIdByWidget( widget );
      var x = 0;
      var y = 0;
      if( mouseEvent instanceof qx.event.type.MouseEvent ) {
        x = mouseEvent.getPageX();
        y = mouseEvent.getPageY();
      }
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
      widget.setSupportsDropMethod( qx.lang.Function.returnTrue );
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
      this.setFeedback( target, null, 0 );
      this._currentDropTarget = null;
      this._actionOverwrite = null;
      this._dataTypeOverwrite = null;
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
      var item = this._getCurrentItemTarget();
      var itemId = item != null ? wm.findIdByWidget( item ) : null
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
      param[ eventName + ".item" ] = itemId;
      param[ eventName + ".operation" ] = operation;
      param[ eventName + ".feedback" ] = this._feedbackCode;
      param[ eventName + ".dataType" ] = this._dataTypeOverwrite;
      param[ eventName + ".source" ] = source;
      param[ eventName + ".time" ] = time;
      event[ "param" ] = param;
      this._dropTargetEventQueue[ type ] = event;
      if( !this._requestScheduled ) {
        var req = org.eclipse.swt.Request.getInstance();
        req.addEventListener( "send", this._onSend, this );
        this._requestScheduled = true;
        qx.client.Timer.once( req.send, req, 200 );
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

    _getCurrentItemTarget : function() {
      var result = null;
      var target = this._getCurrentFeedbackTarget();
      if( target instanceof org.eclipse.swt.widgets.TableRow ) {
        var table = this._currentDropTarget;
        var index = target.getItemIndex()
        result = table._items[ index ];
      } else {
        result = target;
      }
      return result;
    },

    //////////
    // actions

    _setAction : function( newAction ) {
      // NOTE: using setCurrentAction would conflict with key events
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance();
      var oldAction = dndHandler.getCurrentAction();
      if( oldAction != newAction ) {
        dndHandler.clearActions();
        dndHandler.setAction( newAction );
      }
    },
    
    _operationsToActions : function( operations ) {
      var result = {};
      for( var i = 0; i < operations.length; i++ ) {
        var action = operations[ i ] == "link" ? "alias" : operations[ i ];
        result[ action ] = action != null;
      }
      return result;
    },
    
    _computeCurrentAction : function( domEvent, target ) {
      var result;
      if( this._actionOverwrite != null ) {
        result = this._actionOverwrite;
      } else {
        result = "move";
        var shift = domEvent.isShiftPressed(); 
        var ctrl = domEvent.isCtrlPressed();
        var alt = domEvent.isAltPressed();
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

    ///////////
    // feedback

    // TODO [tb] : allow overwrite using DropTarget.setDropTargetEffect?
    _createFeedback : function( widget ) {
      if( this._feedback == null ) {
        if( widget instanceof org.eclipse.swt.widgets.Tree ) {
          this._feedback = new org.eclipse.rwt.TreeDNDFeedback( widget );
        } else if( widget instanceof org.eclipse.swt.widgets.Table ) {
          this._feedback = new org.eclipse.rwt.TableDNDFeedback( widget );
        }
      }
    },

    _renderFeedback : function( target ) {
      if( this._feedback != null ) {
        var target = this._getCurrentFeedbackTarget()
        this._feedback.renderFeedback( target );
      }
    },
    
    _getCurrentFeedbackTarget : function() {
      var result = null;
      var widget = this._currentTargetWidget;
      if( widget instanceof org.eclipse.swt.widgets.TableRow ) {
        result = widget;
      } else {
        while(    widget != null
               && result == null 
               && widget != this._currentDropTarget ) 
        {
          if( widget instanceof org.eclipse.swt.widgets.TreeItem ) {
            result = widget;
          } else {
            widget = widget.getParent();
          }
        }
      }
      return result;
    },

    // TODO [tb] : allow overwrite using DragSourceEvent.image?
    _getFeedbackWidget : function( control, target ) {
      var item = target;
      var success = false;
      if( this._feedbackWidget == null ) {
        this._feedbackWidget 
          = new org.eclipse.rwt.widgets.MultiCellWidget( [ "image", "label" ] );
        this._feedbackWidget.setOpacity( 0.7 );
        this._feedbackWidget.setEnabled( false );
        this._feedbackWidget.setPadding( 2 );
      }
      while( !success && item != control ) {
        if( item instanceof org.eclipse.swt.widgets.TreeItem ) {
          success = true;
          this._configureTreeItemFeedback( item );
        } else if( item instanceof org.eclipse.swt.widgets.TableRow ) {
          success = true;
          this.configureTablwRowFeedback( item );
        }
        if( !success ) {
          item = item.getParent();
        }
      }
      return success ? this._feedbackWidget : null;
    },

    _configureTreeItemFeedback : function( item ) {
      var widget = this._feedbackWidget;
      if( item.getIcon() != null ) {
        var iconObject = item.getIconObject();
        widget.setCellContent( 0, item.getIcon() );
        widget.setCellDimension( 0, 
                                 iconObject.getPreferredInnerWidth(),
                                 iconObject.getPreferredInnerHeight() );
      } else {
        var backgroundColor = item.getLabelObject().getBackgroundColor();
        var textColor = item.getLabelObject().getTextColor();
        widget.setBackgroundColor( backgroundColor );
        widget.setTextColor( textColor );
        widget.setCellContent( 1, item.getLabel() );
        widget.setFont( item.getLabelObject().getFont() );
      }
    },
    
    configureTablwRowFeedback : function( item ) {
      var widget = this._feedbackWidget;
      if( item.getElement().childNodes.length > 0  ) {
        var rowDiv = item.getElement();
        var cellDiv = rowDiv.childNodes[ 0 ];
        widget.setCellContent( 1, cellDiv.innerHTML ) ;
        // TODO [tb] : get cell-only fonts how?
        widget.setFont( item.getFont() );
        if( cellDiv.style.backgroundColor != "" ) {
          widget.setBackgroundColor( cellDiv.style.backgroundColor );
        } else {
          widget.setBackgroundColor( rowDiv.style.backgroundColor );
        }
        if( cellDiv.style.color != "" ) {
          widget.setTextColor( cellDiv.style.color );
        } else {
          widget.setTextColor( rowDiv.style.color );
        }
      }
    },
    
    _resetFeedbackWidget : function() {
      if( this._feedbackWidget != null ) {
        this._feedbackWidget.setParent( null );
        this._feedbackWidget.setFont( null );
        this._feedbackWidget.setCellContent( 0, null );
        this._feedbackWidget.setCellDimension( 0, null, null );
        this._feedbackWidget.setCellContent( 1, null );
        this._feedbackWidget.setBackgroundColor( null );
      }
    },

    ///////////////
    // eventhandler

    _onSend : function( event ) {
      this._attachTropTargetEvents();
      this._requestScheduled = false;
      var req = org.eclipse.swt.Request.getInstance();
      req.removeEventListener( "send", this._onSend, this );
    },

    _onMouseOver : function( event ) {
      var target = event.getTarget();
      if( this._feedback != null ) {
        var node = event.getDomTarget();
        if( !this._feedback.isFeedbackNode( node ) ) {
          this.setCurrentTargetWidget( target );
        }
      } else {
        this.setCurrentTargetWidget( target );
      }
    },

    setCurrentTargetWidget : function( target ) {
      this._currentTargetWidget = target;
      this._renderFeedback();
    },

    _onKeyEvent : function( event ) {
      if( event.getType() == "keyup" && event.getKeyIdentifier() == "Alt" ) {
        // NOTE: This combination causes problems with future dom events, 
        // so instead we cancel the operation.
        this._sendDragSourceEvent( this._currentDragSource, 
                                   "dragFinished", 
                                   event );
        this.cancel();
      } else if( this._currentDropTarget != null ) {
        var dndHandler = qx.event.handler.DragAndDropHandler.getInstance();
        var action 
          = this._computeCurrentAction( event, this._currentDropTarget );
        this._setAction( action );
        dndHandler._renderCursor();
      }
    },

    /////////
    // helper

    _cleanUp : function() {
      this._fakeMouseEvent( "mouseover" ); // fix for bug 296348
      this.setCurrentTargetWidget( null );
      if( this._currentDropTarget != null) {
        this.setFeedback( this._currentDropTarget, null, 0 );
        this._currentDropTarget = null;
      }
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance();
      dndHandler.setFeedbackWidget( null );
      this._resetFeedbackWidget();
      this._currentDragSource = null;
      this._dataTypeOverwrite = null;
      var doc = qx.ui.core.ClientDocument.getInstance();
      doc.removeEventListener( "mouseover", this._onMouseOver, this );
      doc.removeEventListener( "keydown", this._onKeyEvent, this );
      doc.removeEventListener( "keyup", this._onKeyEvent, this );
    },

    _fakeMouseEvent : function( type ) {
      var domTarget = this._currentTargetWidget._getTargetNode();
      var eventHandler = qx.event.handler.EventHandler;
      var target = eventHandler.getTargetObject( null, 
                                                 this._currentTargetWidget,
                                                 true );
      var domEvent = {
        "type" : type,
        "target" : domTarget,
        "button" : 0,
        "wheelData" : 0,
        "detail" : 0,
        "pageX" : 0,
        "pageY" : 0,
        "clientX" : 0,
        "clientY" : 0,
        "screenX" : 0,
        "screenY" : 0,
        "shiftKey" : false,
        "ctrlKey" : false,
        "altKey" : false,
        "metaKey" : false,
        "preventDefault" : function(){}
      };
      var event = new qx.event.type.MouseEvent( type, 
                                                domEvent, 
                                                domTarget, 
                                                target,
                                                this._currentTargetWidget,
                                                null );
      target.dispatchEvent( event );
    },

    //////////////////
    // server response 
    
    cancel : function() {
      if( this._currentDragSource != null ) {
        var dndHandler = qx.event.handler.DragAndDropHandler.getInstance();
        dndHandler.globalCancelDrag();
        this._cleanUp();
      }
    },

    setOperationOverwrite : function( widget, operation ) {
      if( widget == this._currentDropTarget ) {
        var action = operation == "link" ? "alias" : operation;
        var dndHandler = qx.event.handler.DragAndDropHandler.getInstance();
        this._actionOverwrite = action;
        this._setAction( action );
        dndHandler._renderCursor();
      }
    },
    
    setFeedback : function( widget, feedback, code ) {
      if( widget == this._currentDropTarget ) {
        if( feedback != null ) {
          this._createFeedback( widget );
          if( this._feedback != null ) {
            var feedbackMap = {};
            for( var i = 0; i < feedback.length; i++ ) {
              feedbackMap[ feedback[ i ] ] = true;
            }
            this._feedback.setFeedback( feedbackMap );
            this._renderFeedback();
          }
        } else if( this._feedback != null ) {
          this._feedback.dispose();
          this._feedback = null;
        }
        this._feedbackCode = code;
      }
    },
    
    setDataType : function( widget, type ) {
      if( widget == this._currentDropTarget ) {
        this._dataTypeOverwrite = type;
      }
    }
  
  }
    
} );

