/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.provisional.uitest;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.internal.widgets.WidgetAdapter;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;
import org.eclipse.swt.widgets.*;

/**
 * <p><strong>Note:</strong> This class is provisional and subject to
 * change without further notice.</p>
 * 
 * @since 1.1
 */
public final class UITests {

  private static final String ACTIVATED 
    = UITests.class.getName() + "#activated";
  private static final String NEW_WIDGETS
    = UITests.class.getName() + "#newWidgets";

  /**
   * Activates UI testing support for the given <code>display.</code>. For all
   * widgets that are newly rendered to the client, the HTML id attribute
   * is set.
   *  
   * <p>In conjunction with <code>overrideId</code>, each widget can be
   * assigned a custom, more human-readable, identifier.</p>
   * 
   * @see IWidgetAdapter#getId()
   * @see #overrideId(Widget, String)
   * @see #isActivated()
   */
  public static void activate( final Display display ) {
    RWT.getLifeCycle().addPhaseListener( new PhaseListener() {

      private static final long serialVersionUID = 1L;

      public void beforePhase( final PhaseEvent event ) {
        if( display == Display.getCurrent() ) {
          Widget[] newWidets = collectNewWidgets( display );
          setNewWidgets( newWidets );
        }
      }

      public void afterPhase( final PhaseEvent event ) {
        if( display == Display.getCurrent() ) {
          Widget[] newWidgets = getNewWidgets();
          for( int i = 0; i < newWidgets.length; i++ ) {
            writeId( newWidgets[ i ] );
          }
        }
      }

      public PhaseId getPhaseId() {
        return PhaseId.RENDER;
      }
    } );
    RWT.getSessionStore().setAttribute( ACTIVATED, Boolean.TRUE );
  }
  
  /**
   * This method returns whether UI testing is activated for the current 
   * session.
   * 
   * @return returns <code>true</code> if and only if <code>activate(Display)
   * </code> was called before; <code>false</code> otherwise.
   * 
   * @see #activate(Display)
   */
  public static boolean isActivated() {
    Object attribute = RWT.getSessionStore().getAttribute( ACTIVATED );
    return attribute != null && attribute.equals( Boolean.TRUE );
  }

  /**
   * By default, the widget-id returned by {@link IWidgetAdapter#getId()} is 
   * an automatically generated value that is session-wide unique. If and only
   * if UI tests are {@link #activate(Display) activated}, this method 
   * overrides the id for the given <code>widget</code> with the specified 
   * <code>id</code>.
   * The <code>id</code> must only contain characters that are valid according 
   * to the <a href="http://www.w3.org/TR/html401/types.html#type-cdata">W3C
   * recommendation for id and name attributes</a>.
   * 
   * <p>It is the clients' responsibility to choose a unique id. Assigning an
   * id that is used by another widget will lead to undeterministic behavior.
   * </p>
   * 
   * @param widget the widget whose id should be changed
   * @param id the new id to be used, must not be <code>null</code>
   * 
   * @throws NullPointerException if <code>id</code> is <code>null</code>
   * @throws IllegalArgumentException if the <code>id</code> contains illegal
   *   characters
   * 
   * @see #activate(Display)
   * @see #isActivated()
   */
  public static void overrideId( final Widget widget, final String id ) {
    ParamCheck.notNull( id, "id" );
    if( isActivated() ) {
      if( !isValidId( id ) ) {
        String text = "The widget identifier contains illegal characters: {0}";
        String msg = MessageFormat.format( text, new Object[] { id } );
        throw new IllegalArgumentException( msg ) ;
      }
      WidgetAdapter adapter = ( WidgetAdapter )WidgetUtil.getAdapter( widget );
      adapter.overrideId( id );
    }
  }

  //////////////////
  // Helping methods
  
  private static Widget[] collectNewWidgets( final Display display ) {
    final List newWidgets = new ArrayList();
    AllWidgetTreeVisitor visitor = new AllWidgetTreeVisitor() {
      public boolean doVisit( final Widget widget ) {
        Object adapter = widget.getAdapter( IWidgetAdapter.class );
        IWidgetAdapter widgetAdapter = ( IWidgetAdapter )adapter;
        if( !widgetAdapter.isInitialized() ) {
          newWidgets.add( widget );
        }
        return true;
      }
    };
    Shell[] shells = display.getShells();
    for( int i = 0; i < shells.length; i++ ) {
      WidgetTreeVisitor.accept( shells[ i ], visitor );
    }
    Widget[] result = new Widget[ newWidgets.size() ];
    newWidgets.toArray( result );
    return result;
  }

  private static void setNewWidgets( final Widget[] newWidets ) {
    RWT.getServiceStore().setAttribute( NEW_WIDGETS, newWidets );
  }

  private static Widget[] getNewWidgets() {
    return ( Widget[] )RWT.getServiceStore().getAttribute( NEW_WIDGETS );
  }

  private static void writeId( final Widget widget ) {
    String id = WidgetUtil.getId( widget );
    JSWriter writer = JSWriter.getWriterFor( widget );
    Object[] args = new Object[] { widget, id };
    try {
      writer.call( JSWriter.WIDGET_MANAGER_REF, "setHtmlId", args );
    } catch( IOException e ) {
      // TODO [rh] proper exception handling
      throw new RuntimeException( e );
    }
  }

  private static boolean isValidId( final String id ) {
    // see http://www.w3.org/TR/html401/types.html#type-cdata (id and name)
    // for what characters are allowed
    boolean result = id.length() > 0 && Character.isLetter( id.charAt ( 0 ) );
    for( int i = 1; result && i < id.length(); i++ ) {
      char ch = id.charAt( i );
      result &= Character.isLetter( ch ) 
             || isNumber( ch )
             || ch == '.'
             || ch == '_'
             || ch == ':';
    }
    return result;
  }

  private static boolean isNumber( final char ch ) {
    return ( ch >= '0' && ch <= '9' );
  }
  
  private UITests() {
    // prevent instantiation
  }
}