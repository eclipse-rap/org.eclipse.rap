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
 * Default RWT widget theme
 */
qx.OO.defineClass(
  "org.eclipse.rap.rwt.WidgetTheme", 
  qx.renderer.theme.WidgetTheme,
  function() {
    qx.renderer.theme.WidgetTheme.call( this, "RAP" );
  }
);
 
qx.Settings.setDefault("imageUri", 
                       qx.Settings.getValueOfClass("qx.manager.object.AliasManager", "resourceUri") + "/widget/rap");

qx.Class.getInstance = qx.lang.Function.returnInstance;

qx.manager.object.ImageManager.getInstance().registerWidgetTheme( qx.Class );
