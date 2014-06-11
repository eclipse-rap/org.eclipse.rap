/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import org.eclipse.rap.rwt.internal.lifecycle.WidgetAdapter;
import org.eclipse.swt.internal.SerializableCompatibility;
import org.eclipse.swt.widgets.Widget;


/*
 * This class is used to transport widget parent between widget constructor and the actual widget
 * adapter, created lazily in Widget#getAdapter. This reduce the memory footprint of "virtual"
 * items, which are created, but not rendered immediately (virtual Nebula GridItem ).
 */
public class ParentHolderAdapter implements WidgetAdapter, SerializableCompatibility {

  private final Widget parent;

  public ParentHolderAdapter( Widget parent ) {
    this.parent = parent;
  }

  public Widget getParent() {
    return parent;
  }

  public String getId() {
    throw new UnsupportedOperationException();
  }

  public boolean isInitialized() {
    throw new UnsupportedOperationException();
  }

  public void preserve( String propertyName, Object value ) {
    throw new UnsupportedOperationException();
  }

  public Object getPreserved( String propertyName ) {
    throw new UnsupportedOperationException();
  }

  public void markDisposed( Widget widget ) {
    throw new UnsupportedOperationException();
  }

}
