/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.widgets;

import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.SerializableCompatibility;
import org.eclipse.swt.internal.widgets.SlimList;


/*
 * Holds the child controls of Composites
 */
final class ControlHolder implements IControlHolderAdapter, SerializableCompatibility {

  private final List<Control> controls;
  
  ControlHolder() {
    controls = new SlimList<Control>();
  }

  public int size() {
    return controls.size();
  }

  public Control[] getControls() {
    return controls.toArray( new Control[ controls.size() ] );
  }

  public void add( Control control ) {
    add( control, controls.size() );
  }
  
  public void add( Control control, int index ) {
    if( control == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( controls.contains( control ) ) {
      throw new IllegalArgumentException( "The control was already added." );
    }
    controls.add( index, control );
  }

  public void remove( Control control ) {
    if( control == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( !controls.contains( control ) ) {
      String msg = "The control was not added to this control holder.";
      throw new IllegalArgumentException( msg );
    }
    controls.remove( control );
  }
  
  public int indexOf( Control control ) {
    if( control == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( !controls.contains( control ) ) {
      String msg = "The control was not added to this control holder.";
      throw new IllegalArgumentException( msg );
    }
    return controls.indexOf( control );
  }
  

  boolean contains( Control control ) {
    if( control == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return controls.contains( control );
  }
  
  static int size( Composite composite ) {
    return getControlHolder( composite ).size();
  }

  static void addControl( Composite composite, Control control ) {
    if( control.getParent() != composite ) {
      throw new IllegalArgumentException( "The control has the wrong parent" );
    }
    getControlHolder( composite ).add( control );
  }

  static void addControl( Composite composite, Control control, int index ) {
    if( control.getParent() != composite ) {
      throw new IllegalArgumentException( "The control has the wrong parent" );
    }
    getControlHolder( composite ).add( control, index );
  }
  
  static void removeControl( Composite composite, Control control ) {
    if( control.getParent() != composite ) {
      throw new IllegalArgumentException( "The control has the wrong parent" );
    }
    getControlHolder( composite ).remove( control );
  }

  static int indexOf( Composite composite, Control control ) {
    if( control.getParent() != composite ) {
      throw new IllegalArgumentException( "The control has the wrong parent" );
    }
    return getControlHolder( composite ).indexOf( control );
  }
  
  // ////////////////
  // helping methods
  
  private static IControlHolderAdapter getControlHolder( Composite composite ) {
    return composite.getAdapter( IControlHolderAdapter.class );
  }
}
