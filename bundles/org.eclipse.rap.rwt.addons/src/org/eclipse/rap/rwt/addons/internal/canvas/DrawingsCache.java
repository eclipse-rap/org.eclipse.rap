/*******************************************************************************
 * Copyright (c) 2025 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.addons.internal.canvas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class DrawingsCache implements Serializable {

  private final List<String> cachedDrawings;
  private final List<String> removedDrawings;
  private final Object lock = new Object();

  public DrawingsCache() {
    cachedDrawings = new ArrayList<String>();
    removedDrawings = new ArrayList<String>();
  }

  public void clear() {
    synchronized( lock ) {
      cachedDrawings.clear();
      removedDrawings.clear();
    }
  }

  public void clearCachedDrawings() {
    synchronized( lock ) {
      cachedDrawings.clear();
    }
  }

  public boolean hasUndo() {
    return !cachedDrawings.isEmpty();
  }

  public void undo() {
    synchronized( lock ) {
      int lastObjectIndex = cachedDrawings.size() - 1;
      String lastObject = cachedDrawings.remove( lastObjectIndex );
      removedDrawings.add( lastObject );
    }
  }

  public boolean hasRedo() {
    return !removedDrawings.isEmpty();
  }

  public void redo() {
    synchronized( lock ) {
      int lastObjectIndex = removedDrawings.size() - 1;
      String lastObject = removedDrawings.remove( lastObjectIndex );
      cachedDrawings.add( lastObject );
    }
  }

  public void clearRemoved() {
    synchronized( lock ) {
      removedDrawings.clear();
    }
  }

  public void cache( String drawings ) {
    synchronized( lock ) {
      if( !cachedDrawings.contains( drawings ) ) {
        cachedDrawings.add( drawings );
      }
    }
  }

  public void cacheAll( List<String> drawings ) {
    synchronized( lock ) {
      cachedDrawings.addAll( drawings );
    }
  }

  public void clearRemovedDrawings() {
    synchronized( lock ) {
      removedDrawings.clear();
    }
  }

  public void addAllRemoved( List<String> removedDrawings ) {
    synchronized( lock ) {
      this.removedDrawings.addAll( removedDrawings );
    }
  }

  public List<String> getCachedDrawings() {
    return new ArrayList<String>( cachedDrawings );
  }

  public List<String> getRemovedDrawings() {
    return new ArrayList<String>( removedDrawings );
  }

}
