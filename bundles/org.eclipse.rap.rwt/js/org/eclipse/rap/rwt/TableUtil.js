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

qx.OO.defineClass( "org.eclipse.rap.rwt.TableUtil" );

org.eclipse.rap.rwt.TableUtil.initColors = function( table ) {
  var colors = {};
  colors.bgcolEven = "white";
  table.getDataRowRenderer().setRowColors( colors );
}

org.eclipse.rap.rwt.TableUtil.columnWidthChanged = function( evt ) {
  var index = evt.getData().col;
  var width = evt.getData().newWidth;
  var req = org.eclipse.rap.rwt.Request.getInstance();
  var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var id = wm.findIdByWidget( evt.getTarget().table );
  req.addParameter( id + ".columnWidth_" + index, width );  
};

org.eclipse.rap.rwt.TableUtil.selectionChanged = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var tableModel = evt.getTarget().table.getTableModel();
    var rowCount = tableModel.getRowCount();
    var selection = "";
    for( i = 0; i < rowCount; i++ ) {
      if( evt.getTarget().isSelectedIndex( i ) ) {
        selection += i + ",";
      }
    }
    var req = org.eclipse.rap.rwt.Request.getInstance();
    var id = wm.findIdByWidget( evt.getTarget().table );
    req.addParameter( id + ".selection", selection );
  }
};

org.eclipse.rap.rwt.TableUtil.selectionChangedAction = function( evt ) {
  org.eclipse.rap.rwt.TableUtil.selectionChanged( evt );
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = widgetManager.findIdByWidget( evt.getTarget().table );
    org.eclipse.rap.rwt.EventUtil.doWidgetSelected( id, 0, 0, 0, 0 );
  }
};

org.eclipse.rap.rwt.TableUtil.enablementChanged = function( evt ) {
  var items = this.getChildren();
  for( var i = 0; i < items.length; i++ ) {
    var item = items[ i ];
    item.setEnabled( propValue );
    this.debug( "CHILD: " + item );
  }
}
