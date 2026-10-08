/*******************************************************************************
 * Copyright (c) 2014, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import org.eclipse.rap.rwt.internal.lifecycle.RemoteAdapter;
import org.eclipse.swt.internal.SerializableCompatibility;
import org.eclipse.swt.widgets.Widget;


/*
 * This class is used to transport widget parent between widget constructor and the actual widget
 * adapter, created lazily in Widget#getAdapter. This reduce the memory footprint of "virtual"
 * items, which are created, but not rendered immediately (virtual Nebula GridItem ).
 */
public class ParentHolderRemoteAdapter implements RemoteAdapter, SerializableCompatibility {

  private final Widget parent;

  public ParentHolderRemoteAdapter( Widget parent ) {
    this.parent = parent;
  }

  @Override
  public Widget getParent() {
    return parent;
  }

  @Override
  public String getId() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isInitialized() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void preserve( String propertyName, Object value ) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object getPreserved( String propertyName ) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void markDisposed( Widget widget ) {
    throw new UnsupportedOperationException();
  }

}
