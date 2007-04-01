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

package org.eclipse.rap.rwt.events;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.widgets.Item;
import org.eclipse.rap.rwt.widgets.Widget;
import com.w4t.Adaptable;


/**
 * TODO[rh] JavaDoc
 * <p></p>
 */
public class SelectionEvent extends RWTEvent {

  public static final int WIDGET_SELECTED = 0;
  public static final int WIDGET_DEFAULT_SELECTED = 1;
  
  private static final Class LISTENER = SelectionListener.class;
  
  public int x;
  public int y;
  public int width;
  public int height;
  public String text;
  public boolean doit;
  public Item item;
  public int detail;
  
  public SelectionEvent( final Widget widget,
                         final Item item,
                         final int id )
  {
    this( widget, item, id, new Rectangle( 0, 0, 0, 0 ), null, true, RWT.NONE );
  }

  public SelectionEvent( final Widget widget,
                         final Item item,
                         final int id,
                         final Rectangle bounds,
                         final String text,
                         final boolean doit,
                         final int detail )
  {
    super( widget, id );
    this.x = bounds.x;
    this.y = bounds.y;
    this.width = bounds.width;
    this.height = bounds.height;
    this.text = text;
    this.doit = doit;
    this.item = item;
    this.detail = detail;
  }

  protected void dispatchToObserver( final Object listener ) {
    switch( getID() ) {
      case WIDGET_SELECTED:
        ( ( SelectionListener )listener ).widgetSelected( this );
      break;
      case WIDGET_DEFAULT_SELECTED:
        ( ( SelectionListener )listener ).widgetDefaultSelected( this );        
        break;
      default:
        throw new IllegalStateException( "Invalid event handler type." );
    }
  }

  protected Class getListenerType() {
    return LISTENER;
  }
  
  public static boolean hasListener( final Adaptable adaptable ) {
    return hasListener( adaptable, LISTENER );
  }
  
  public static void addListener( final Adaptable adaptable,
                                  final SelectionListener listener )
  {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( final Adaptable adaptable,
                                     final SelectionListener listener )
  {
    removeListener( adaptable, LISTENER, listener );
  }

  public static Object[] getListeners( final Adaptable adaptable ) {
    return getListener( adaptable, LISTENER );
  }
  
  public String toString() {
    String string = super.toString ();
    return   string.substring( 0, string.length() - 1 ) // remove trailing '}'
           + " item="
           + item
           + " detail="
           + detail
           + " x="
           + x
           + " y="
           + y
           + " width="
           + width
           + " height="
           + height
           // + " stateMask=" + stateMask
           + " text=" + text
           + " doit="
           + doit
           + "}";
  }
}