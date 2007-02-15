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

import java.util.*;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.graphics.*;
import org.eclipse.rap.rwt.internal.widgets.IDisplayAdapter;
import com.w4t.Adaptable;
import com.w4t.W4TContext;
import com.w4t.engine.requests.RequestParams;
import com.w4t.engine.service.ContextProvider;

/**
 * TODO [rh] JavaDoc
 */
public class Display implements Adaptable {

  private static final String DISPLAY_ID = "org.eclipse.rap.rwt.display";
  
  // TODO [rh] this must be in sync with font in DefaultAppearanceTheme.js
  private static final String SYSTEM_FONT_NAME 
    = "\"Segoe UI\", Corbel, Calibri, Tahoma, \"Lucida Sans Unicode\", " 
    + "sans-serif";

  private static final String ICON_PATH = "resource/widget/rap/dialog";
  
  private static final String ERROR_IMAGE_PATH = ICON_PATH + "/error.png";

  private static final String INFO_IMAGE_PATH = ICON_PATH + "/information.png";

  private static final String QUESTION_IMAGE_PATH = ICON_PATH + "/question.png";

  private static final String WARNING_IMAGE_PATH = ICON_PATH + "/warning.png";

  public static Display getCurrent() {
    return ( Display )ContextProvider.getSession().getAttribute( DISPLAY_ID );
  }
  
  private final List shells;
  private final Font systemFont; 
  private Rectangle bounds;
  private Shell activeShell;
  private IDisplayAdapter displayAdapter;

  private Image errorImage;
  private Image infoImage;
  private Image questionImage;
  private Image warningImage;

  public Display() {
    HttpSession session = ContextProvider.getSession();
    if( getCurrent() != null ) {
      String msg = "Currently only one display per session is supported.";
      throw new IllegalStateException( msg );
    }
    session.setAttribute( DISPLAY_ID, this );
    shells = new ArrayList();
    systemFont = Font.getFont( SYSTEM_FONT_NAME, 11, RWT.NORMAL );    
    readInitialBounds();
  }

  public Shell[] getShells() {
    Shell[] result = new Shell[ shells.size() ];
    shells.toArray( result );
    return result;
  }

  public Rectangle getBounds() {
    return new Rectangle( bounds );
  }

  // TODO [rh] preliminary: COLOR_WIDGET_XXX not yet supported
  public Color getSystemColor( final int id ) {
    int pixel = 0x02000000;
    switch( id ) {
      case RWT.COLOR_WHITE:
        pixel = 0x02FFFFFF;
      break;
      case RWT.COLOR_BLACK:
        pixel = 0x02000000;
      break;
      case RWT.COLOR_RED:
        pixel = 0x020000FF;
      break;
      case RWT.COLOR_DARK_RED:
        pixel = 0x02000080;
      break;
      case RWT.COLOR_GREEN:
        pixel = 0x0200FF00;
      break;
      case RWT.COLOR_DARK_GREEN:
        pixel = 0x02008000;
      break;
      case RWT.COLOR_YELLOW:
        pixel = 0x0200FFFF;
      break;
      case RWT.COLOR_DARK_YELLOW:
        pixel = 0x02008080;
      break;
      case RWT.COLOR_BLUE:
        pixel = 0x02FF0000;
      break;
      case RWT.COLOR_DARK_BLUE:
        pixel = 0x02800000;
      break;
      case RWT.COLOR_MAGENTA:
        pixel = 0x02FF00FF;
      break;
      case RWT.COLOR_DARK_MAGENTA:
        pixel = 0x02800080;
      break;
      case RWT.COLOR_CYAN:
        pixel = 0x02FFFF00;
      break;
      case RWT.COLOR_DARK_CYAN:
        pixel = 0x02808000;
      break;
      case RWT.COLOR_GRAY:
        pixel = 0x02C0C0C0;
      break;
      case RWT.COLOR_DARK_GRAY:
        pixel = 0x02808080;
      break;
    }
    return Color.getColor( pixel );
  }
  
  public Font getSystemFont() {
    return systemFont;
  }
  
  public Image getSystemImage( int id ) {
    ClassLoader classLoader = getClass().getClassLoader();
    Image result = null;
    switch( id ) {
      case RWT.ICON_ERROR: {
        if( errorImage == null ) {
          errorImage = Image.find( ERROR_IMAGE_PATH, classLoader );
        }
        result = errorImage;
        break;
      }
      case RWT.ICON_WORKING:
      case RWT.ICON_INFORMATION: {
        if( infoImage == null ) {
          infoImage = Image.find( INFO_IMAGE_PATH, classLoader );
        }
        result = infoImage;
        break;
      }
      case RWT.ICON_QUESTION: {
        if( questionImage == null ) {
          questionImage = Image.find( QUESTION_IMAGE_PATH, classLoader );
        }
        result = questionImage;
        break;
      }
      case RWT.ICON_WARNING: {
        if( warningImage == null ) {
          warningImage = Image.find( WARNING_IMAGE_PATH, classLoader );
        }
        result = warningImage;
        break;
      }
    }
    return result;
  }
  
  public Point map( final Control from, final Control to, final Point point ) {
//    checkDevice ();
    if( point == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    } 
    return map( from, to, point.x, point.y );
  }

  public Point map( final Control from,
                    final Control to,
                    final int x,
                    final int y )
  {
//    checkDevice ();
    Rectangle rectangle = map( from, to, x, y, 0, 0 );
    return new Point( rectangle.x, rectangle.y );
  }
  
  public Rectangle map( final Control from,
                        final Control to,
                        final Rectangle rectangle )
  {
//    checkDevice ();
    if( rectangle == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
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
//    checkDevice ();
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
  
  /////////////////////
  // Adaptable override

  public Object getAdapter( final Class adapter ) {
    Object result = null;
    if( adapter == IDisplayAdapter.class ) {
      if( displayAdapter == null ) {
        displayAdapter = new DisplayAdapter();
      }
      result = displayAdapter;
    } else {
      result = W4TContext.getAdapterManager().getAdapter( this, adapter );  
    }
    return result;
  }

  ///////////////////
  // Shell management
  
  public Shell getActiveShell() {
    return activeShell;
  }
  
  final void setActiveShell( final Shell activeShell ) {
    this.activeShell = activeShell;
  }
  
  final void addShell( final Shell shell ) {
    shells.add( shell );
  }

  final void removeShell( final Shell shell ) {
    shells.remove( shell );
    if( shell == activeShell ) {
      if( shells.size() > 0 ) {
        activeShell = ( Shell )shells.get( 0 );
      } else {
        activeShell = null;
      }
    }
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
    
    public void setActiveShell( final Shell activeShell ) {
      Display.this.setActiveShell( activeShell );
    }
  }
  
  
  //////////////////
  // helping methods
  
  private void readInitialBounds() {
    HttpServletRequest request = ContextProvider.getRequest();
    String widthVal = request.getParameter( RequestParams.AVAILABLE_WIDTH );
    int width = 1024;
    if( widthVal != null ) {
      width = Integer.parseInt( widthVal );
    }
    String height_val = request.getParameter( RequestParams.AVAILABLE_HEIGHT );
    int height = 768;
    if( height_val != null ) {
      height = Integer.parseInt( height_val );
    }
    bounds = new Rectangle( 0, 0, width, height );
  }
}
