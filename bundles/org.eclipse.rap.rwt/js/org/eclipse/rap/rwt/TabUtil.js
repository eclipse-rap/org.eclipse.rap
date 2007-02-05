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

qx.OO.defineClass( "org.eclipse.rap.rwt.TabUtil" );

org.eclipse.rap.rwt.TabUtil.createTabItem = function( id, parent ) {
  var tabButton = new qx.ui.pageview.tabview.Button();
  var tabView
    = org.eclipse.rap.rwt.WidgetManager.getInstance().findWidgetById( parent );
  tabView.getBar().add( tabButton );
  tabButton.tabView = tabView;
  var tabViewPage = new qx.ui.pageview.tabview.Page( tabButton );
  tabView.getPane().add( tabViewPage );
  
  org.eclipse.rap.rwt.WidgetManager.getInstance().add( tabButton, id );
  org.eclipse.rap.rwt.WidgetManager.getInstance().add( tabViewPage, id + "pg" );
};

org.eclipse.rap.rwt.TabUtil.tabSelected = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var req = org.eclipse.rap.rwt.Request.getInstance();
    var id = widgetManager.findIdByWidget( evt.getTarget() );
    req.addParameter( id + ".checked", evt.getTarget().getChecked() );
  }
};

org.eclipse.rap.rwt.TabUtil.tabSelectedAction = function( evt ) {
  org.eclipse.rap.rwt.TabUtil.tabSelected( evt );
  if( !org_eclipse_rap_rwt_EventUtil_suspend && evt.getTarget().getChecked() ) {
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = widgetManager.findIdByWidget( evt.getTarget().tabView );
    org.eclipse.rap.rwt.EventUtil.doWidgetSelected( id, 0, 0, 0, 0 );
  }
};