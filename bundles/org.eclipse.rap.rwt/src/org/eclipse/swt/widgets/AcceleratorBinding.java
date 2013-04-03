/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;


class AcceleratorBinding implements Listener {

  private final MenuItem menuItem;

  private int accelerator;

  AcceleratorBinding( MenuItem menuItem ) {
    this.menuItem = menuItem;
  }

  public void handleEvent( Event event ) {
    if( isRelevantEvent( event ) && menuItem.isEnabled() ) {
      menuItem.handleAcceleratorActivation();
      event.type = SWT.NONE;
    }
  }

  int getAccelerator() {
    return accelerator;
  }

  void setAccelerator( int accelerator ) {
    if( accelerator != this.accelerator ) {
      int oldAccelerator = this.accelerator;
      this.accelerator = accelerator;
      if( ( menuItem.style & SWT.SEPARATOR ) == 0 ) {
        updateDisplayActiveKeys( oldAccelerator, accelerator );
        updateDisplayFilter( oldAccelerator, accelerator );
      }
    }
  }

  void release() {
    setAccelerator( 0 );
  }

  private boolean isRelevantEvent( Event event ) {
    boolean result = false;
    if( event.type == SWT.KeyDown ) {
      if( ( accelerator & SWT.MODIFIER_MASK ) == event.stateMask ) {
        char key = Character.toUpperCase( ( char )( accelerator & SWT.KEY_MASK ) );
        char character = Character.toUpperCase( event.character );
        if( key == character ) {
          result = true;
        }
      }
    }
    return result;
  }

  private void updateDisplayFilter( int oldAccelerator, int newAccelerator ) {
    if( oldAccelerator == 0 && newAccelerator != 0 ) {
      menuItem.display.addFilter( SWT.KeyDown, this );
    } else if( oldAccelerator != 0 && newAccelerator == 0 ) {
      menuItem.display.removeFilter( SWT.KeyDown, this );
    }
  }

  private void updateDisplayActiveKeys( int oldAccelerator, int newAccelerator ) {
    updateDisplayActiveKeys( RWT.ACTIVE_KEYS, oldAccelerator, newAccelerator );
    updateDisplayActiveKeys( RWT.CANCEL_KEYS, oldAccelerator, newAccelerator );
  }

  private void updateDisplayActiveKeys( String keysType, int oldAccelerator, int newAccelerator ) {
    String[] oldActiveKeys = ( String[] )menuItem.display.getData( keysType );
    if( oldActiveKeys == null ) {
      oldActiveKeys = new String[ 0 ];
    }
    ArrayList<String> activeKeys = new ArrayList<String>( Arrays.asList( oldActiveKeys ) );
    if( oldAccelerator != 0 ) {
      activeKeys.remove( acceleratorAsString( oldAccelerator ) );
    }
    if( newAccelerator != 0 ) {
      activeKeys.add( acceleratorAsString( newAccelerator ) );
    }
    menuItem.display.setData( keysType, activeKeys.toArray( new String[ 0 ] ) );
  }

  private static String acceleratorAsString( int accelerator ) {
    String result = "";
    if( ( accelerator & SWT.ALT ) != 0 ) {
      result += "ALT+";
    }
    if( ( accelerator & SWT.CTRL ) != 0 ) {
      result += "CTRL+";
    }
    if( ( accelerator & SWT.SHIFT ) != 0 ) {
      result += "SHIFT+";
    }
    char key = ( char )( accelerator & SWT.KEY_MASK );
    result += Character.toString( Character.toUpperCase( key ) );
    return result;
  }

}
