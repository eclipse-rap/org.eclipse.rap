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
 * This class contains static functions for combo.
 */
qx.OO.defineClass( "org.eclipse.rap.rwt.ButtonUtil" );

/**
 * Creates a radioButton, registered to a RadioManager.
 */
org.eclipse.rap.rwt.ButtonUtil.createRadioButton
  = function( id , parent , selected )
{
  var radio = new qx.ui.form.RadioButton();
  if( !parent.radioManager ){
    parent.radioManager = new qx.manager.selection.RadioManager();
  }
  parent.radioManager.add ( radio );
  if ( selected ) {
    parent.radioManager.setSelected ( radio ) ;
  }
  org.eclipse.rap.rwt.WidgetManager.getInstance().add ( radio, id );
  radio.setParent( parent );
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
