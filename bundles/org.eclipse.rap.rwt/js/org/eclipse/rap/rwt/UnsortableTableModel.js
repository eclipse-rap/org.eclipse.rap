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

qx.OO.defineClass(
  "org.eclipse.rap.rwt.UnsortableTableModel", 
  qx.ui.table.SimpleTableModel,
  function() 
{
  qx.ui.table.SimpleTableModel(this);
});


// overridden
qx.Proto.isColumnSortable = function(columnIndex) {
  return false;
};