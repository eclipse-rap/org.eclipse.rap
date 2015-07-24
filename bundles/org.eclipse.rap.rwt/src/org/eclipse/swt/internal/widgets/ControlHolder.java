/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.internal.SerializableCompatibility;
import org.eclipse.swt.widgets.Control;


public final class ControlHolder implements SerializableCompatibility {

  private final List<Control> controls;

  public ControlHolder() {
    controls = new ArrayList<>();
  }

  public int size() {
    return controls.size();
  }

  public Control[] getControls() {
    return controls.toArray( new Control[ controls.size() ] );
  }

  public void add( Control control ) {
    controls.add( control );
  }

  public void add( Control control, int index ) {
    controls.add( index, control );
  }

  public void remove( Control control ) {
    controls.remove( control );
  }

  public int indexOf( Control control ) {
    return controls.indexOf( control );
  }


  public boolean contains( Control control ) {
    return controls.contains( control );
  }

}
