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

package org.eclipse.rap.rwt.internal.widgets;

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.graphics.*;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.W4TContext;
import com.w4t.engine.service.ContextProvider;
import com.w4t.util.browser.Mozilla;


public final class WidgetLCAUtil {
  
  private static final String PARAM_X = "bounds.x";
  private static final String PARAM_Y = "bounds.y";
  private static final String PARAM_WIDTH = "bounds.width";
  private static final String PARAM_HEIGHT = "bounds.height";
  
  private static final String PROP_TOOL_TIP_TEXT = "toolTip";

  private WidgetLCAUtil() {
    // prevent instantiation
  }
  
  /////////////////////////////////////////////
  // Methods to preserve common property values
  
  public static void preserveToolTipText( final Widget widget, 
                                          final String toolTip ) 
  {
    String text = toolTip == null ? "" : toolTip;
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( WidgetLCAUtil.PROP_TOOL_TIP_TEXT, text );
  }
  
  ////////////////////////////////////////////////////
  // Methods to determine changes of widget properties
  
  /**
   * <p>Determines whether the property of the given <code>widget</code> has
   * changed in comparison to its 'preserved' value and thus 'something' needs 
   * to be rendered in order to reflect the changes on the client side.</p>
   * <p>If there is no preserved value, <code>null</code> will be assumed.</p>
   * @param widget the widget whose property is to be compared, must not be 
   * <code>null</code>.
   * @param property the name of the property under which the preserved value 
   * can be looked up. Must not be <code>null</code>.
   * @param newValue the value that is compared to the preserved value
   */
  public static boolean hasChanged( final Widget widget, 
                                    final String property, 
                                    final Object newValue ) 
  {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    Object oldValue = adapter.getPreserved( property );
    return !WidgetLCAUtil.equals( oldValue, newValue );
  }

  /**
   * <p>Determines whether the property of the given <code>widget</code> has
   * changed in comparison to its 'preserved' value.</p>
   * <p>In case it is the first time that the widget is rendered (it is not yet
   * present on the client side) <code>true</code> is only returned if the 
   * <code>newValue</code> differs from the <code>defaultValue</code>. Otherwise 
   * the decision is delegated to {@link hasChanged(Widget,String,Object)
   * <code>hasChanged(Widget,String,Object)</code>}.</p>
   * @param widget the widget whose property is to be compared, must not be 
   * <code>null</code>.
   * @param property the name of the property under which the preserved value 
   * can be looked up. Must not be <code>null</code>.
   * @param newValue the value that is compared to the preserved value
   * @param defaultValue the default value 
   */
  public static boolean hasChanged( final Widget widget, 
                                    final String property, 
                                    final Object newValue, 
                                    final Object defaultValue ) 
  {
    boolean result;
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    if( adapter.isInitialized() ) {
      result = hasChanged( widget, property, newValue );
    } else {
      result = !WidgetLCAUtil.equals( newValue, defaultValue );
    }
    return result;
  }

  ///////////////////////////////////////////
  // Methods to read request parameter values
  
  /**
   * <p>Returns the value of the given widget's property. The value is read out
   * from the request and may be null if no value for the given property
   * was submitted.</p>
   * 
   * @param widget the widget to which the given property belongs.
   * @param propertyName the name of the widget-property that should be
   *                     read from the request.
   *                     TODO: [fappel] create a clear specification how
   *                                    property names should look like,
   *                                    in particular properties that are
   *                                    non primitive with their own props.
   */
  public static String readPropertyValue( final Widget widget, 
                                          final String propertyName ) 
  {
    HttpServletRequest request = ContextProvider.getRequest();
    StringBuffer key = new StringBuffer();
    key.append( WidgetUtil.getId( widget ) );
    key.append( "." );
    key.append( propertyName );
    return request.getParameter( key.toString() );
  }

  public static boolean wasEventSent( final Widget widget,
                                      final String eventName ) 
  {
    HttpServletRequest request = ContextProvider.getRequest();
    String widgetId = request.getParameter( eventName );
    return WidgetUtil.getId( widget ).equals( widgetId );
  }
  
  public static Rectangle readBounds( final Widget widget, 
                                      final Rectangle defValue ) 
  {
    return readBounds( WidgetUtil.getId( widget ), defValue );
  }

  public static Rectangle readBounds( final String widgetId, 
                                      final Rectangle defValue ) 
  {
    int x = readBoundsX( widgetId, defValue.x );
    int y = readBoundsY( widgetId, defValue.y );
    int width = readBoundsWidth( widgetId, defValue.width );
    int height = readBoundsHeight( widgetId, defValue.height );
    return new Rectangle( x, y, width, height );
  }
  
  /////////////////////////////////////////////////////////
  // Methods to write JavaScript code for widget properties
  
  public static void writeBounds( final Widget widget, 
                                  final Control parent, 
                                  final Rectangle bounds, 
                                  final boolean clip ) 
    throws IOException 
  {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    // TODO [rh] replace code below with WidgetUtil.hasChanged
    Rectangle oldBounds = ( Rectangle )adapter.getPreserved( Props.BOUNDS );
    Rectangle newBounds = bounds;
    if( !adapter.isInitialized() || !newBounds.equals( oldBounds ) ) {
      
      // the RWT coordinates for client area differ in some cases to
      // the widget realisation of qooxdoo
      if( parent != null ) {
        AbstractWidgetLCA parentLCA = WidgetUtil.getLCA( parent );
        newBounds = parentLCA.adjustCoordinates( newBounds ); 
      }
      
      JSWriter writer = JSWriter.getWriterFor( widget );
      int[] args = new int[]{
        newBounds.x, newBounds.width, newBounds.y, newBounds.height
      };
      
      writer.set( "space", args );
      if( !WidgetUtil.getAdapter( widget ).isInitialized() ) {
        writer.set( "minWidth", 0 );
        writer.set( "minHeight", 0 );
      }
      if( clip ) {
        writer.set( "clipHeight", args[ 3 ] );
        writer.set( "clipWidth", args[ 1 ] );
      }
    }
  }

  public static void writeMenu( final Widget widget, final Menu menu )
    throws IOException
  {
    if( WidgetLCAUtil.hasChanged( widget, Props.MENU, menu, null ) ) {
      JSWriter writer = JSWriter.getWriterFor( widget );
      writer.set( "contextMenu", menu );
      if( menu == null ) {
        writer.removeListener( JSConst.QX_EVENT_CONTEXTMENU, 
                               JSConst.JS_CONTEXT_MENU );
      } else {
        writer.addListener( JSConst.QX_EVENT_CONTEXTMENU, 
                            JSConst.JS_CONTEXT_MENU );
      }
    }
  }

  public static void writeToolTip( final Widget widget, final String toolTip ) 
    throws IOException 
  {
    String text = toolTip == null ? "" : toolTip;
    if( hasChanged( widget, WidgetLCAUtil.PROP_TOOL_TIP_TEXT, text, "" ) ) {
      JSWriter writer = JSWriter.getWriterFor( widget );
      Object[] args = new Object[] { widget, text };
      writer.call( JSWriter.WIDGET_MANAGER_REF, "setToolTip", args );
    }
  }

  /////////////////////////////////////////////////
  // write-methods used by other ...LCAUtil classes
  
  public static void writeImage( final Widget widget, final Image image ) 
    throws IOException 
  {
    writeImage( widget, Props.IMAGE, JSConst.QX_FIELD_ICON, image );
  }

  public static void writeImage( final Widget widget, 
                                 final String javaProperty, 
                                 final String jsProperty, 
                                 final Image image ) 
    throws IOException 
  {
    if( WidgetLCAUtil.hasChanged( widget, javaProperty, image, null ) ) {
      String imagePath = image == null ? "" : Image.getPath( image );
      JSWriter writer = JSWriter.getWriterFor( widget );
      writer.set( jsProperty, imagePath );
    }
  }
  
  static void writeFont( final Widget widget, final Font font )
    throws IOException
  {
    Font systemFont = widget.getDisplay().getSystemFont();
    if( WidgetLCAUtil.hasChanged( widget, Props.FONT, font, systemFont ) ) {
      JSWriter writer = JSWriter.getWriterFor( widget );
      Object[] args = new Object[]{
        widget,
        font.getName(),
        new Integer( font.getSize() ),
        Boolean.valueOf( ( font.getStyle() & RWT.BOLD ) != 0 ),
        Boolean.valueOf( ( font.getStyle() & RWT.ITALIC ) != 0 )
      };
      writer.call( JSWriter.WIDGET_MANAGER_REF, "setFont", args );
    }
  }

  private static String readPropertyValue( final String widgetId, 
                                           final String propertyName ) 
  {
    HttpServletRequest request = ContextProvider.getRequest();
    StringBuffer key = new StringBuffer();
    key.append( widgetId );
    key.append( "." );
    key.append( propertyName );
    return request.getParameter( key.toString() );
  }

  //////////////////////////////////////////////////////////////////
  // Helping methods to read bounds for a widget from request params
  
  private static int readBoundsY( final String widgetId, final int defValue ) {
    String value = readPropertyValue( widgetId, PARAM_Y );
    return readBoundsValue( value, defValue );
  }
  
  private static int readBoundsX( final String widgetId, final int defValue ) {
    String value = readPropertyValue( widgetId, PARAM_X );
    return readBoundsValue( value, defValue );
  }

  private static int readBoundsWidth( final String widgetId, 
                                      final int defValue ) 
  {
    String value = WidgetLCAUtil.readPropertyValue( widgetId, PARAM_WIDTH );
    return readBoundsValue( value, defValue );
  }

  private static int readBoundsHeight( final String widgetId, 
                                       final int defValue ) 
  {
    String value = WidgetLCAUtil.readPropertyValue( widgetId, PARAM_HEIGHT );
    return readBoundsValue( value, defValue );
  }
  
  private static int readBoundsValue( final String value, final int current ) {
    int result;
    if( value != null && !"null".equals( value ) ) {
      result = Integer.parseInt( value );
    } else {
      result = current;
    }
    return result;
  }

  ////////////////////////////////////////
  // Helping methods to test for equality
  
  static boolean equals( final Object object1, final Object object2 ) {
    boolean result;
    if( object1 == object2 ) {
      result = true;
    } else if( object1 == null ) {
      result = false;
    } else if( object1 instanceof boolean[] && object2 instanceof boolean[] ) {
      result = Arrays.equals( ( boolean[] )object1, ( boolean[] )object2 );
    } else if( object1 instanceof int[] && object2 instanceof int[] ) {
      result = Arrays.equals( ( int[] )object1, ( int[] )object2 );
    } else if( object1 instanceof long[] && object2 instanceof long[] ) {
      result = Arrays.equals( ( long[] )object1, ( long[] )object2 );
    } else if( object1 instanceof float[] && object2 instanceof float[] ) {
      result = Arrays.equals( ( float[] )object1, ( float[] )object2 );
    } else if( object1 instanceof double[] && object2 instanceof double[] ) {
      result = Arrays.equals( ( double[] )object1, ( double[] )object2 );
    } else if( object1 instanceof Object[] && object2 instanceof Object[] ) {
      result = Arrays.equals( ( Object[] )object1, ( Object[] )object2 );
    } else {
      result = object1.equals( object2 );
    }
    return result;
  }
}
