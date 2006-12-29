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
import com.w4t.Adaptable;
import com.w4t.W4TContext;
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
