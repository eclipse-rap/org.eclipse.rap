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

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.widgets.IDisplayAdapter;
import com.w4t.*;
import com.w4t.engine.service.ContextProvider;

/**
 * TODO [rh] JavaDoc
 */
public class Display implements Adaptable {

  private static final String DISPLAY_ID = "org.eclipse.rap.rwt.display";

  public static Display getCurrent() {
    return ( Display )ContextProvider.getSession().getAttribute( DISPLAY_ID );
  }
  
  private final List shells;
  private Rectangle bounds = new Rectangle( 0, 0, 0, 0 );
  private IDisplayAdapter accessAdapter;

  public Display() {
    HttpSession session = ContextProvider.getSession();
    if( getCurrent() != null ) {
      String msg = "Currently only one display per session is supported.";
      throw new IllegalStateException( msg );
    }
    session.setAttribute( DISPLAY_ID, this );
    shells = new ArrayList();
  }

  public Composite[] getShells() {
    Composite[] result = new Composite[ shells.size() ];
    shells.toArray( result );
    return result;
  }

  public Rectangle getBounds() {
    return new Rectangle( bounds );
  }

  
  public Rectangle map( final Control from,
                        final Control to,
                        final Rectangle rectangle )
  {
    ParamCheck.notNull( rectangle, "rectangle" );
    return map( from, 
                to, 
                rectangle.x, 
                rectangle.y, 
                rectangle.width, 
                rectangle.height );
  }
  
  public Rectangle map( final Control from, 
                        final Control to, 
                        final int x, 
                        final int y, 
                        final int width, 
                        final int height )
  {
    int newX = x;
    int newY = y;
    if( from != null ) {
      Control currentFrom = from;
      do {
        Rectangle bounds = currentFrom.getBounds();
        newX += bounds.x;
        newY += bounds.y;
        currentFrom = currentFrom.getParent();
      } while( currentFrom != null );
    }
    
    if( to != null ) {
      Control currentTo = to;
      do {
        Rectangle bounds = currentTo.getBounds();
        newX -= bounds.x;
        newY -= bounds.y;
        currentTo = currentTo.getParent();
      } while( currentTo != null );
    }
    return new Rectangle( newX, newY, width, height );
  }
  
  // TODO [rh] This is preliminary!
  public void dispose() {
    ContextProvider.getSession().removeAttribute( DISPLAY_ID );
  }

  public Object getAdapter( final Class adapter ) {
    Object result = null;
    if( adapter == IDisplayAdapter.class ) {
      if( accessAdapter == null ) {
        accessAdapter = new DisplayAdapter();
      }
      result = accessAdapter;
    } else {
      result = W4TContext.getAdapterManager().getAdapter( this, adapter );  
    }
    return result;
  }

  final void addShell( final Composite shell ) {
    shells.add( shell );
  }

  final void removeShell( final Composite shell ) {
    shells.remove( shell );
  }
  
  ////////////////
  // Inner classes

  private final class DisplayAdapter implements IDisplayAdapter {

    public void setBounds( final Rectangle bounds ) {
      if( bounds == null ) {
        RWT.error( RWT.ERROR_NULL_ARGUMENT );
      }
      Display.this.bounds = new Rectangle( bounds );
    }
  }
}
