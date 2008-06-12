/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
/**
 * This class contains static functions for radio buttons and check boxes.
 */
qx.Class.define( "org.eclipse.swt.ButtonUtil", {

  statics : {
    
    setLabelMode : function( button ) {
      // Note: called directly after creating the menuItem instance, therefore
      // it is not necessary to check getLabelObject and/or preserve its label
      button.setLabel( "(empty)" );
      button.getLabelObject().setMode( "html" );
      button.getLabelObject().setAppearance( "label-graytext" );
      button.setLabel( "" );
    },
    
    /**
     * Registers the given button at the RadioManager of the first sibling 
     * radio button. If there is not sibing radio button, a new RadioManager
     * is created.
     */
    registerRadioButton : function( button ) {
      var radioManager = null;
      var parent = button.getParent();
      var siblings = parent.getChildren();
      for( var i = 0; radioManager == null && i < siblings.length; i++ ) {
        if(    siblings[ i ] != button 
            && siblings[ i ].classname == button.classname )
        {
          radioManager = siblings[ i ].getManager();
        }
      }
      if( radioManager == null ) {
        radioManager = new qx.ui.selection.RadioManager();
      }
      radioManager.add( button );
    },

    /**
     * Removes the given button from its RadioManager and disposes of the
     * RadioManager if there are no more radio buttons that use this 
     * RadioManager.
     */
    unregisterRadioButton : function( button ) {
      var radioManager = button.getManager();
      if( radioManager != null ) {
        radioManager.remove( button );
        if( radioManager.getItems().length == 0 ) {
          radioManager.dispose();
        }
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
        org.eclipse.swt.ButtonUtil.radioSelected( evt );
        var radioManager = evt.getTarget();
        var radio = radioManager.getSelected();
        if( radio != null ) {
          var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
          var id = widgetManager.findIdByWidget( radio );
          org.eclipse.swt.EventUtil.doWidgetSelected( id, 0, 0, 0, 0 );
        }
      }
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
        org.eclipse.swt.ButtonUtil.checkSelected( evt );
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( evt.getTarget() );
        org.eclipse.swt.EventUtil.doWidgetSelected( id, 0, 0, 0, 0 );
      }
    },

    /* Called when a TOGGLE button is executed */
    onToggleExecute : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var button = evt.getTarget();
        var checked = !button.hasState( "checked" );
        if( checked ) {
          button.addState( "checked" );
        } else {
          button.removeState( "checked" );
        }
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( button );
        var req = org.eclipse.swt.Request.getInstance();
        req.addParameter( id + ".selection", checked );
      }
    }
  }
});
