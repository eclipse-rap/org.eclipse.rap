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
 * This class contains static functions for checkbox.
 */
qx.OO.defineClass( "org.eclipse.rap.rwt.CheckUtil" );

/**
 * Fires a widgetSelected event if the list item wasn't laready selected.
 */
org.eclipse.rap.rwt.CheckUtil.widgetSelected = function( evt ) {
    var check = evt.getTarget();
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var chkId = widgetManager.findIdByWidget( check );
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addParameter( chkId + ".selectedItem", check.getChecked() );
    
    var left = check.getLeft();
    var top = check.getTop();
    var width = check.getWidth();
    var height = check.getHeight();
    org.eclipse.rap.rwt.EventUtil.doWidgetSelected( chkId, 
                                                    left, 
                                                    top, 
                                                    width,
                                                    height );
};


