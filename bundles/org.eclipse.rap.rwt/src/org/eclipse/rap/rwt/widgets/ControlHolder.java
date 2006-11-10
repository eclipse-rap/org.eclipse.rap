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

import java.util.List;
import com.w4t.ParamCheck;

class ControlHolder {

  private final List controls;
  
  static interface IControlHolderAdapter {
  }

  ControlHolder() {
    controls = new SlimList();
  }

  int size() {
    return controls.size();
  }

  Control[] getControls() {
    Control[] result = new Control[ controls.size() ];
    controls.toArray( result );
    return result;
  }

  void add( final Control control ) {
    ParamCheck.notNull( control, "control" );
    if( controls.contains( control ) ) {
      throw new IllegalArgumentException( "The control was already added." );
    }
    controls.add( control );
  }

  void remove( final Control control ) {
    ParamCheck.notNull( control, "control" );
    if( !controls.contains( control ) ) {
      String msg = "The control was not added to this control holder.";
      throw new IllegalArgumentException( msg );
    }
    controls.remove( control );
  }

  static Control[] getControls( final Composite composite ) {
    return getControlHolder( composite ).getControls();
  }

  static void addControl( final Composite composite, final Control control ) {
    if( control.getParent() != composite ) {
      throw new IllegalArgumentException( "The control has the wrong parent" );
    }
    getControlHolder( composite ).add( control );
  }

  static void removeControl( final Composite composite, final Control control )
  {
    if( control.getParent() != composite ) {
      throw new IllegalArgumentException( "The control has the wrong parent" );
    }
    getControlHolder( composite ).remove( control );
  }

  // ////////////////
  // helping methods
  private static ControlHolder getControlHolder( final Composite composite ) {
    return ( ControlHolder )composite.getAdapter( IControlHolderAdapter.class );
  }
}
