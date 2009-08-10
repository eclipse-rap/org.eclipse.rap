/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/


/**
 * This class contains static functions for toolbar items.
 */
qx.Class.define( "org.eclipse.swt.ToolItemUtil", {

  statics : {
    createSeparator : function( id, parent, isFlat ) {
      var sep = new qx.ui.toolbar.Separator();
      var line = sep.getFirstChild();
      sep.setUserData( "line", line );
      if( isFlat ) {
        sep.addState( "rwt_FLAT" );
        line.addState( "rwt_FLAT" );
      }
      org.eclipse.swt.WidgetManager.getInstance().add( sep, id, false );
      sep.setParent( parent );
      parent.add( sep );
    },

    setControl : function( sep, control ) {
      if( control ) {
        control.moveSelfAfter( sep );
        control.setDisplay( true );
        sep.setUserData( "control", control );
        sep.setDisplay( false );
      } else {
        var oldcontrol = sep.getUserData( "control", control );
        if( oldcontrol ) {
          oldcontrol.setDisplay( false );
        }
        sep.setDisplay( true );
      }
    },

    createRadio : function( id, parent, selected, neighbour ) {
      var radio = new qx.ui.toolbar.RadioButton();
      radio.setDisableUncheck( true );
      parent.add( radio );
      if( neighbour ) {
        radio.radioManager = neighbour.radioManager;
      } else {
        radio.radioManager = new qx.ui.selection.RadioManager();
      }
      radio.radioManager.add( radio );
      if( selected ) {
        radio.radioManager.setSelected( radio );
      }
      radio.setLabel( "(empty)" );
      radio.getLabelObject().setMode( "html" );
      radio.setLabel( "" );
      org.eclipse.swt.WidgetManager.getInstance().add( radio, id, false );
      radio.setParent( parent );
      org.eclipse.swt.ToolItemUtil._registerMouseListeners( radio );
    },

    createPush : function( id, parent, isFlat ) {
      var push = new qx.ui.toolbar.Button();
      if( isFlat ) {
        push.addState( "rwt_FLAT" );
      }
      push.setShow( "both" );
      push.setLabel( "(empty)" );
      push.getLabelObject().setMode( qx.constant.Style.LABEL_MODE_HTML );
      push.setLabel( "" );
      parent.add( push );
      org.eclipse.swt.WidgetManager.getInstance().add( push, id, false );
      org.eclipse.swt.ToolItemUtil._registerMouseListeners( push );
    },

    createDropDown : function( id, parent, isFlat ) {
      org.eclipse.swt.ToolItemUtil.createPush( id, parent, isFlat );
      var button 
        = org.eclipse.swt.WidgetManager.getInstance().findWidgetById( id );
      var dropDown = new qx.ui.toolbar.Button( "", "widget/arrows/down.gif" );
      dropDown.setHeight( "100%" );
      dropDown.setUserData( "buttonId", id );
      if( isFlat ) {
        dropDown.addState( "rwt_FLAT" );
      }
      parent.add( dropDown );
      var dropDownId = org.eclipse.swt.ToolItemUtil._getDropDownId( button );
      org.eclipse.swt.WidgetManager.getInstance().add( dropDown, dropDownId, false );
      // Register enable listener that keeps enabled state of dropDown in sync
      // with the enabeled state of the actual button
      // TODO [rh] check whether this listener must be removed upon disposal
      button.addEventListener( "changeEnabled", 
                               org.eclipse.swt.ToolItemUtil._onDropDownChangeEnabled );
      // Register beforeRemoveDom listener that disposes the dropDown when the
      // actual button is disposed.
      button.addEventListener( "beforeRemoveDom", 
                               org.eclipse.swt.ToolItemUtil._onDropDownDisposed );
    },
    
    _getDropDownId : function( button ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var buttonId = widgetManager.findIdByWidget( button );
      var dropDownId = buttonId + "_dropDown";
      return dropDownId;
    },
    
    _onDropDownDisposed : function( evt ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var button = evt.getTarget();
      button.removeEventListener( "beforeRemoveDom", 
                                  org.eclipse.swt.ToolItemUtil._onDropDownDisposed );
      var dropDownId = org.eclipse.swt.ToolItemUtil._getDropDownId( button );
      widgetManager.dispose( dropDownId );
    },

    _onDropDownChangeEnabled : function( evt ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var button = evt.getTarget();
      var dropDownId = org.eclipse.swt.ToolItemUtil._getDropDownId( button );
      var dropDown = widgetManager.findWidgetById( dropDownId );
      dropDown.setEnabled( button.getEnabled() );
    },

    updateDropDownListener : function( id, remove ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var dropDown = widgetManager.findWidgetById( id );
      var listener = org.eclipse.swt.ToolItemUtil._dropDownSelected;
      if( remove ) {
        dropDown.removeEventListener( "execute", listener );
      } else {
        dropDown.addEventListener( "execute", listener );
      }
    },

    createCheck : function( id, parent ) {
      var button = new qx.ui.toolbar.CheckBox();
      parent.add( button );
      org.eclipse.swt.WidgetManager.getInstance().add( button, id, false );
      org.eclipse.swt.ToolItemUtil._registerMouseListeners( button );
    },

    _dropDownSelected : function( evt ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var dropDown = evt.getTarget();
      var dropDownId = widgetManager.findIdByWidget( dropDown );
      var buttonId = dropDown.getUserData( "buttonId" );
      var button = widgetManager.findWidgetById( buttonId );
      var element = button.getElement();
      var left = qx.html.Location.getPageBoxLeft( element );
      var top = qx.html.Location.getPageBoxBottom( element );
      var req = org.eclipse.swt.Request.getInstance();
      org.eclipse.swt.EventUtil.doWidgetSelected( dropDownId, left, top, 0, 0 );
    },
    
    setImage : function( toolItem, image ) {
      toolItem.setUserData( "image", image );
      org.eclipse.swt.ToolItemUtil._updateImages( toolItem );
    },
    
    setHotImage : function( toolItem, image ) {
      toolItem.setUserData( "hotImage", image );
      org.eclipse.swt.ToolItemUtil._updateImages( toolItem );
    },
    
    _onMouseOver : function( evt ) {
      var toolItem = evt.getTarget();
      toolItem.addState( "over" );
      org.eclipse.swt.ToolItemUtil._updateImages( toolItem );
    },
    
    _onMouseOut : function( evt ) {
      var toolItem = evt.getTarget();
      toolItem.removeState( "over" );
      org.eclipse.swt.ToolItemUtil._updateImages( toolItem );
    },
    
    _updateImages : function( toolItem ) {
      var image = null;
      if( toolItem.hasState( "over" ) && toolItem.getEnabled() ) {
        image = toolItem.getUserData( "hotImage" );
        if( image === null ) {
          image = toolItem.getUserData( "image" );
        }
      } else {
        image = toolItem.getUserData( "image" );
      }     
      toolItem.setIcon( image );    
    },
    
    _registerMouseListeners : function( toolItem ) {
      toolItem.addEventListener( "mouseover",
                                 org.eclipse.swt.ToolItemUtil._onMouseOver );
      toolItem.addEventListener( "mouseout", 
                                 org.eclipse.swt.ToolItemUtil._onMouseOut );
    },

    checkSelected : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var check = evt.getTarget();
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( check );
        var req = org.eclipse.swt.Request.getInstance();
        req.addParameter( id + ".selection", check.getChecked() );
      }
    },

    checkSelectedAction : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        org.eclipse.swt.ToolItemUtil.checkSelected( evt );
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( evt.getTarget() );
        org.eclipse.swt.EventUtil.doWidgetSelected( id, 0, 0, 0, 0 );
      }
    },
    
    radioSelected : function( evt ) {
      var radioManager = evt.getTarget();
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var req = org.eclipse.swt.Request.getInstance();
      var radioButtons = radioManager.getItems();
      for( var i=0; i<radioButtons.length; i++ ) {
        var selected = radioButtons[ i ] == radioManager.getSelected();
        var id = widgetManager.findIdByWidget( radioButtons[ i ] );
        req.addParameter( id + ".selection", selected );
      }
    },

    radioSelectedAction : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        org.eclipse.swt.ToolItemUtil.radioSelected( evt );
        var radioManager = evt.getTarget();
        var radio = radioManager.getSelected();
        if( radio != null ) {
          var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
          var id = widgetManager.findIdByWidget( radio );
          org.eclipse.swt.EventUtil.doWidgetSelected( id, 0, 0, 0, 0 );
        }
      }
    }

  }
});
