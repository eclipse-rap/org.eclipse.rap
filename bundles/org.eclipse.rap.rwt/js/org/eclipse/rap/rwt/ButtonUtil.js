/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
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
qx.OO.defineClass( "org.eclipse.rap.rwt.ButtonUtil" );

/**
 * Registers the given button at the RadioManager of the first sibling 
 * radio button. If there is not sibing radio button, a new RadioManager
 * is created.
 */
org.eclipse.rap.rwt.ButtonUtil.registerRadioButton = function( button ) {
  var radioManager = null;
  var parent = button.getParent();
  var siblings = parent.getChildren();
  for( var i = 0; radioManager == null && i < siblings.length; i++ ) {
    if( siblings[ i ] != button && siblings[ i ].classname == button.classname )
    {
      radioManager = siblings[ i ].getManager();
    }
  }
  if( radioManager == null ) {
    radioManager = new qx.manager.selection.RadioManager();
  }
  radioManager.add( button );
}

/**
 * Removes the given button from its RadioManager and disposes of the
 * RadioManager if there are no more radio buttons that use this 
 * RadioManager.
 */
org.eclipse.rap.rwt.ButtonUtil.unregisterRadioButton = function( button ) {
  var radioManager = button.getManager();
  if( radioManager != null ) {
    radioManager.remove( button );
    if( radioManager.getItems().length == 0 ) {
      radioManager.dispose();
    }
  }
}

org.eclipse.rap.rwt.ButtonUtil.radioSelected = function( evt ) {
    var radioManager = evt.getTarget();
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var req = org.eclipse.rap.rwt.Request.getInstance();
    var radioButtons = radioManager.getItems();
    for( var i = 0; i < radioButtons.length; i++ ) {
      var selected = radioButtons[ i ] == radioManager.getSelected();
      var id = widgetManager.findIdByWidget( radioButtons[ i ] );
      req.addParameter( id + ".selection", selected );
    }
}

org.eclipse.rap.rwt.ButtonUtil.radioSelectedAction = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    org.eclipse.rap.rwt.ButtonUtil.radioSelected( evt );
    var radioManager = evt.getTarget();
    var radio = radioManager.getSelected();
    if( radio != null ) {
      var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( radio );
      org.eclipse.rap.rwt.EventUtil.doWidgetSelected( id, 0, 0, 0, 0 );
    }
  }
}

org.eclipse.rap.rwt.ButtonUtil.checkSelected = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var check = evt.getTarget();
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = widgetManager.findIdByWidget( check );
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addParameter( id + ".selection", check.getChecked() );
  }
}

org.eclipse.rap.rwt.ButtonUtil.checkSelectedAction = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    org.eclipse.rap.rwt.ButtonUtil.checkSelected( evt );
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = widgetManager.findIdByWidget( evt.getTarget() );
    org.eclipse.rap.rwt.EventUtil.doWidgetSelected( id, 0, 0, 0, 0 );
  }
}

/* Called when a TOGGLE button is executed */
org.eclipse.rap.rwt.ButtonUtil.onToggleExecute = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var button = evt.getTarget();
    var checked = !button.hasState( "checked" );
    button.setState( "checked", checked );
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = widgetManager.findIdByWidget( button );
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addParameter( id + ".selection", checked );
  }
}
