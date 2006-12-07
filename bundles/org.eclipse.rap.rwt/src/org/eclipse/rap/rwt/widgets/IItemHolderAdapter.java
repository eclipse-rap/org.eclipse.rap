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

package org.eclipse.rap.rwt.widgets;

// TODO [rh] had to make this interface public, since otherwise not visible
//      from ...custom-package. Is OK?, should this be moved to another package?
public interface IItemHolderAdapter {

  void add( Item item );

  void remove( Item item );

  Item[] getItems();
}