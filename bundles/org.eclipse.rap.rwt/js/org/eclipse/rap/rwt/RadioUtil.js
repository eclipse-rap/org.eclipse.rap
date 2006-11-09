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
qx.OO.defineClass( "org.eclipse.rap.rwt.RadioUtil" );

/**
 * Fires a widgetSelected event if the radio button is selected.
 */
org.eclipse.rap.rwt.RadioUtil.widgetSelected = function( evt ) {
    var radioManager = evt.getTarget();
    var radio = radioManager.getSelected();
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var radioId = widgetManager.findIdByWidget( radio );
    var left = radio.getLeft();
    var top = radio.getTop();
    var width = radio.getWidth();
    var height = radio.getHeight();
    org.eclipse.rap.rwt.EventUtil.doWidgetSelected( radioId, 
                                                    left, 
                                                    top, 
                                                    width,
                                                    height );
};

/**
 * Creates a radioButton, registered to a RadioManager.
 */
org.eclipse.rap.rwt.RadioUtil.createRadioButton = function( id , parent , selected ) {
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
};
