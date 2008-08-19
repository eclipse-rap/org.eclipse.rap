/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.lifecycle;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.lifecycle.CommonPatterns;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;


/**
 * Utility class that provides a number of useful static methods to support the
 * implementation of widget life cycle adapters.
 *
 * @see ControlLCAUtil
 * @since 1.0
 */
public final class WidgetLCAUtil {

  private static final String JS_PROP_HEIGHT = "height";
  private static final String JS_PROP_WIDTH = "width";
  private static final String JS_PROP_CLIP_WIDTH = "clipWidth";
  private static final String JS_PROP_CLIP_HEIGHT = "clipHeight";
  private static final String PARAM_X = "bounds.x";
  private static final String PARAM_Y = "bounds.y";
  private static final String PARAM_WIDTH = "bounds.width";
  private static final String PARAM_HEIGHT = "bounds.height";

  private static final String PROP_TOOL_TIP_TEXT = "toolTip";
  private static final String PROP_FONT = "font";
  private static final String PROP_FOREGROUND = "foreground";
  private static final String PROP_BACKGROUND = "background";
  private static final String PROP_BACKGROUND_TRANSPARENCY = "backgroundTrans";
  private static final String PROP_ENABLED = "enabled";
  private static final String PROP_VARIANT = "variant";

  private static final String JS_PROP_SPACE = "space";
  private static final String JS_PROP_CONTEXT_MENU = "contextMenu";

  private static final String JS_FUNC_SET_TOOL_TIP = "setToolTip";

  private static final Pattern HTML_ESCAPE_PATTERN
    = Pattern.compile( "&|<|>|\\\"" );
  private static final Pattern FONT_NAME_FILTER_PATTERN
    = Pattern.compile( "\"|\\\\" );

  //////////////////////////////////////////////////////////////////////////////
  // TODO [fappel]: Experimental - profiler seems to indicate that buffering
  //                improves performance - still under investigation.
  private final static Map parsedFonts = new HashMap();
  //////////////////////////////////////////////////////////////////////////////

  private WidgetLCAUtil() {
    // prevent instantiation
  }

  /////////////////////////////////////////////
  // Methods to preserve common property values

  /**
   * Preserves the value of the property <code>toolTipText</code> of the
   * specified widget.
   *
   * @param widget the widget whose toolTip property to preserve
   * @param toolTip the value to preserve
   * @see #writeToolTip(Widget, String)
   */
  public static void preserveToolTipText( final Widget widget,
                                          final String toolTip )
  {
    String text = toolTip == null ? "" : toolTip;
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_TOOL_TIP_TEXT, text );
  }

  /**
   * Preserves the value of the property <code>font</code> of the specified
   * widget.
   *
   * @param widget the widget whose font property to preserve
   * @param font the value to preserve
   * @see #writeFont(Widget, Font)
   */
  public static void preserveFont( final Widget widget, final Font font ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_FONT, font );
  }

  /**
   * Preserves the value of the property <code>foreground</code> of the
   * specified widget.
   *
   * @param widget the widget whose foreground property to preserve
   * @param foreground the value to preserve
   * @see #writeForeground(Widget, Color)
   */
  public static void preserveForeground( final Widget widget,
                                         final Color foreground )
  {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_FOREGROUND, foreground );
  }

  /**
   * Preserves the value of the property <code>background</code> of the
   * specified widget.
   *
   * @param widget the widget whose background property to preserve
   * @param background the value to preserve
   * @see #writeBackground(Widget, Color)
   */
  public static void preserveBackground( final Widget widget,
                                         final Color background )
  {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_BACKGROUND, background );
  }

  /**
   * Preserves the value of the property <code>background</code> of the
   * specified widget.
   *
   * @param widget the widget whose background property to preserve
   * @param background the background color to preserve
   * @param transparency the background transparency to preserve
   * @see #writeBackground(Widget, Color, boolean)
   */
  public static void preserveBackground( final Widget widget,
                                         final Color background,
                                         final boolean transparency )
  {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_BACKGROUND, background );
    adapter.preserve( PROP_BACKGROUND_TRANSPARENCY,
                      Boolean.valueOf( transparency ) );
  }

  /**
   * Preserves the value of the property <code>enabled</code> of the specified
   * widget.
   *
   * @param widget the widget whose enabled property to preserve
   * @param enabled the value to preserve
   * @see #writeEnabled(Widget, boolean)
   */
  public static void preserveEnabled( final Widget widget,
                                      final boolean enabled )
  {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_ENABLED, Boolean.valueOf( enabled ) );
  }

  /**
   * Preserves the value of the custom variant of the specified
   * widget.
   *
   * @param widget the widget whose custom variant to preserve
   * @see #writeCustomVariant(Widget)
   */
  public static void preserveCustomVariant( final Widget widget ) {
    String variant = WidgetUtil.getVariant( widget );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_VARIANT, variant );
  }

  ////////////////////////////////////////////////////
  // Methods to determine changes of widget properties

  /**
   * Determines whether the property of the given widget has changed during the
   * processing of the current request and thus the changes must be rendered in
   * the response. This is done by comparing the current value with the
   * preserved value.
   * <p>
   * If there is no preserved value, <code>null</code> is assumed.
   * </p>
   *
   * @param widget the widget whose property is to be compared, must not be
   *            <code>null</code>.
   * @param property the name of the property under which the preserved value
   *            can be looked up. Must not be <code>null</code>.
   * @param newValue the value to compare the preserved value with
   * @return <code>true</code> if the property has changed, <code>false</code>
   *         otherwise
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
   * Determines whether the property of the given widget has changed during the
   * processing of the current request and thus the changes must be rendered in
   * the response. This is done by comparing the current value with the
   * preserved value.
   * <p>
   * In case it is the first time that the widget is rendered (it is not yet
   * present on the client side) <code>true</code> is only returned if the
   * <code>newValue</code> differs from the <code>defaultValue</code>.
   * Otherwise the decision is delegated to
   * {@link #hasChanged(Widget,String,Object)}.
   * </p>
   *
   * @param widget the widget whose property is to be compared, must not be
   *            <code>null</code>.
   * @param property the name of the property under which the preserved value
   *            can be looked up. Must not be <code>null</code>.
   * @param newValue the value that is compared to the preserved value
   * @param defaultValue the default value
   * @return <code>true</code> if the property has changed or if the widget is
   *         not yet initialized and the property is at its default value,
   *         <code>false</code> otherwise
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
      result = !equals( newValue, defaultValue );
    }
    return result;
  }

  ///////////////////////////////////////////
  // Methods to read request parameter values

  /**
   * Reads the value of the specified property for the specified widget from the
   * request that is currently processed. If this property is not submitted for
   * the given widget, <code>null</code> is returned.
   *
   * @param widget the widget whose property to read
   * @param propertyName the name of the property to read
   * @return the value read from the request or <code>null</code> if no value
   *         was submitted for the given property
   */
  // TODO: [fappel] create a clear specification how property names should look
  //                like, in particular properties that are non primitive with
  //                their own props.
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

  /**
   * Determines whether an event with the specified name was submitted for the
   * specified widget within the current request.
   *
   * @param widget the widget that should receive the event
   * @param eventName the name of the event to check for
   * @return <code>true</code> if the event was sent for the widget, false
   *         otherwise.
   */
  public static boolean wasEventSent( final Widget widget,
                                      final String eventName )
  {
    HttpServletRequest request = ContextProvider.getRequest();
    String widgetId = request.getParameter( eventName );
    return WidgetUtil.getId( widget ).equals( widgetId );
  }

  /**
   * Reads the bounds of the specified widget from the current request. If the
   * bounds of this widget was not sent with the current request, the specified
   * default is returned.
   *
   * @param widget the widget whose bounds to read
   * @param defValue the default bounds
   * @return the bounds as read from the request or the default bounds if no
   *         bounds were passed within the current request
   */
  public static Rectangle readBounds( final Widget widget,
                                      final Rectangle defValue )
  {
    return readBounds( WidgetUtil.getId( widget ), defValue );
  }

  /**
   * Reads the bounds of the widget specified by its id from the current
   * request. If the bounds of this widget was not sent with the current
   * request, the specified default is returned.
   *
   * @param widgetId the widget id of the widget whose bounds to read
   * @param defValue the default bounds
   * @return the bounds as read from the request or the default bounds if no
   *         bounds were passed within the current request
   */
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

  /**
   * Determines whether the bounds of the given widget have changed during the
   * processing of the current request and if so, writes JavaScript code to the
   * response that updates the client-side bounds of the specified widget. For
   * instances of {@link Control}, use the method
   * {@link ControlLCAUtil#writeBounds(Control)} instead.
   *
   * @param widget the widget whose bounds to write
   * @param parent the parent of the widget or <code>null</code> if the widget
   *            does not have a parent
   * @param bounds the new bounds of the widget
   * @throws IOException
   */
  public static void writeBounds( final Widget widget,
                                  final Control parent,
                                  final Rectangle bounds )
    throws IOException
  {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    // TODO [rh] replace code below with WidgetUtil.hasChanged
    Rectangle oldBounds = ( Rectangle )adapter.getPreserved( Props.BOUNDS );
    Rectangle newBounds = bounds;
    if( !adapter.isInitialized() || !newBounds.equals( oldBounds ) ) {
      // the SWT coordinates for the client area differ in some cases from
      // the widget realization of qooxdoo
      if( parent != null ) {
        AbstractWidgetLCA parentLCA = WidgetUtil.getLCA( parent );
        newBounds = parentLCA.adjustCoordinates( widget, newBounds );
      }
      JSWriter writer = JSWriter.getWriterFor( widget );
      // Note [rst] Children of ScrolledComposites must not render their x and y
      //            coordinates as the content of SCs is scrolled automatically
      //            by the client according to the position of the scroll bars.
      //            Setting negative values breaks the layout on the client.
      // Note [rst] Children of ToolBars must not not render their x and y
      //            coordinates as this is interpreted as offset from the left
      //            neighbor on the client. I guess the reason is that the
      //            client-side ToolBar has a layout set already which
      //            interferes with setting positions.
      if( parent instanceof ScrolledComposite || parent instanceof ToolBar ) {
        writer.set( JS_PROP_WIDTH, newBounds.width );
        writer.set( JS_PROP_HEIGHT, newBounds.height );
      } else {
        int[] args = new int[] {
          newBounds.x, newBounds.width, newBounds.y, newBounds.height
        };
        writer.set( JS_PROP_SPACE, args );
      }
    }
  }

  /**
   * Writes JavaScript code to the response that resets the bounds of a widget.
   * This method is intended to be used by implementations of the method
   * {@link AbstractWidgetLCA#createResetHandlerCalls(String)}.
   *
   * @throws IOException
   */
  public static void resetBounds() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.reset( JS_PROP_CLIP_WIDTH );
    writer.reset( JS_PROP_CLIP_HEIGHT );
    writer.set( JS_PROP_SPACE, new int[] { 0, 0, 0, 0 } );
  }


  /**
   * Determines whether the property <code>menu</code> of the given widget has
   * changed during the processing of the current request and if so, writes
   * JavaScript code to the response that updates the client-side menu property
   * of the specified widget. For instances of {@link Control}, use the method
   * {@link ControlLCAUtil#writeMenu(Control)} instead.
   *
   * @param widget the widget whose menu property to set
   * @param menu the new value of the property
   * @throws IOException
   */
  public static void writeMenu( final Widget widget, final Menu menu )
    throws IOException
  {
    if( WidgetLCAUtil.hasChanged( widget, Props.MENU, menu, null ) ) {
      JSWriter writer = JSWriter.getWriterFor( widget );
      writer.set( JS_PROP_CONTEXT_MENU, menu );
      if( menu == null ) {
        writer.removeListener( JSConst.QX_EVENT_CONTEXTMENU,
                               JSConst.JS_CONTEXT_MENU );
      } else {
        writer.addListener( JSConst.QX_EVENT_CONTEXTMENU,
                            JSConst.JS_CONTEXT_MENU );
      }
    }
  }

  /**
   * Writes JavaScript code to the response that resets the property
   * <code>menu</code> of a widget. This method is intended to be used by
   * implementations of the method
   * {@link AbstractWidgetLCA#createResetHandlerCalls(String)}.
   *
   * @throws IOException
   */
  public static void resetMenu() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.reset( JS_PROP_CONTEXT_MENU );
    writer.removeListener( JSConst.QX_EVENT_CONTEXTMENU,
                           JSConst.JS_CONTEXT_MENU );
  }

  /**
   * Determines whether the property <code>toolTip</code> of the given widget
   * has changed during the processing of the current request and if so, writes
   * JavaScript code to the response that updates the client-side toolTip
   * property of the specified widget. For instances of {@link Control}, use
   * the method {@link ControlLCAUtil#writeToolTip(Control)} instead.
   *
   * @param widget the widget whose toolTip property to set
   * @param toolTip the new value of the property
   * @throws IOException
   * @see #preserveToolTipText(Widget, String)
   */
  public static void writeToolTip( final Widget widget, final String toolTip )
    throws IOException
  {
    String text = toolTip == null ? "" : toolTip;
    if( hasChanged( widget, WidgetLCAUtil.PROP_TOOL_TIP_TEXT, text, "" ) ) {
      JSWriter writer = JSWriter.getWriterFor( widget );
      // Under Windows, ampersand characters are not correctly displayed:
      // https://bugs.eclipse.org/bugs/show_bug.cgi?id=188271
      // However, it is correct not to escape mnemonics in tool tips
      text = escapeText( text, false );
      text = replaceNewLines( text, "<br/>" );
      Object[] args = new Object[] { widget, text };
      writer.call( JSWriter.WIDGET_MANAGER_REF, JS_FUNC_SET_TOOL_TIP, args );
    }
  }

  /**
   * Writes JavaScript code to the response that resets the property
   * <code>toolTip</code> of a widget. This method is intended to be used by
   * implementations of the method
   * {@link AbstractWidgetLCA#createResetHandlerCalls(String)}.
   *
   * @throws IOException
   */
  public static void resetToolTip() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.call( JSWriter.WIDGET_MANAGER_REF,
                 JS_FUNC_SET_TOOL_TIP,
                 new Object[] { JSWriter.WIDGET_REF } );
  }


  /////////////////////////////////////////////////
  // write-methods used by other ...LCAUtil classes

  /**
   * Determines whether the property <code>image</code> of the given widget
   * has changed during the processing of the current request and if so, writes
   * JavaScript code to the response that updates the client-side image property
   * of the specified widget.
   *
   * @param widget the widget whose image property to set
   * @param image the new value of the property
   * @throws IOException
   */
  public static void writeImage( final Widget widget, final Image image )
    throws IOException
  {
    writeImage( widget, Props.IMAGE, JSConst.QX_FIELD_ICON, image );
  }

  /**
   * Determines whether the specified image property of the given widget has
   * changed during the processing of the current request and if so, writes
   * JavaScript code to the response that updates the specified client-side
   * property of the specified widget.
   *
   * @param widget the widget whose property to set
   * @param javaProperty the key of the preserved value to compare the new value
   *            with
   * @param jsProperty the name of the JavaScript property to set
   * @param image the new value of the property
   * @throws IOException
   */
  public static void writeImage( final Widget widget,
                                 final String javaProperty,
                                 final String jsProperty,
                                 final Image image )
    throws IOException
  {
    if( WidgetLCAUtil.hasChanged( widget, javaProperty, image, null ) ) {
      writeImage( widget, jsProperty, image );
    }
  }

  /**
   * Writes JavaScript code to the response that sets the specified JavaScript
   * property of the specified widget to the specified image.
   *
   * @param widget the widget whose property to set
   * @param jsProperty the name of the JavaScript property to set
   * @param image the new value of the property
   * @throws IOException
   */
  public static void writeImage( final Widget widget,
                                 final String jsProperty,
                                 final Image image )
    throws IOException
  {
    String path = image == null ? null : ResourceFactory.getImagePath( image );
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.set( jsProperty, path );
  }

  /**
   * Determines whether the property <code>font</code> of the given widget has
   * changed during the processing of the current request and if so, writes
   * JavaScript code to the response that updates the client-side font property
   * of the specified widget. For instances of {@link Control}, use the method
   * {@link ControlLCAUtil#writeFont(Control)} instead.
   *
   * @param widget the widget whose font property to set
   * @param font the new value of the property
   * @throws IOException
   * @see #preserveFont(Widget, Font)
   */
  public static void writeFont( final Widget widget, final Font font )
    throws IOException
  {
    writeFont( widget, font, false );
  }

  private static void writeFont( final Widget widget,
                                 final Font font,
                                 final boolean force )
    throws IOException
  {
    if(    force
        || WidgetLCAUtil.hasChanged( widget, PROP_FONT, font, null ) )
    {
      JSWriter writer = JSWriter.getWriterFor( widget );
      if( font != null ) {
        FontData fontData = font.getFontData()[ 0 ];
        String[] names = parseFontName( fontData.getName() );
        Object[] args = new Object[]{
          widget,
          names,
          new Integer( fontData.getHeight() ),
          Boolean.valueOf( ( fontData.getStyle() & SWT.BOLD ) != 0 ),
          Boolean.valueOf( ( fontData.getStyle() & SWT.ITALIC ) != 0 )
        };
        writer.call( JSWriter.WIDGET_MANAGER_REF, "setFont", args );
      } else {
        writer.reset( JSConst.QX_FIELD_FONT );
      }
    }
  }

  /**
   * Writes JavaScript code to the response that resets the property
   * <code>font</code> of a widget. This method is intended to be used by
   * implementations of the method
   * {@link AbstractWidgetLCA#createResetHandlerCalls(String)}.
   *
   * @throws IOException
   */
  public static void resetFont() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.reset( JSConst.QX_FIELD_FONT );
  }

  /**
   * Determines whether the property <code>foreground</code> of the given
   * widget has changed during the processing of the current request and if so,
   * writes JavaScript code to the response that updates the client-side
   * foreground property of the specified widget. For instances of
   * {@link Control}, use the method
   * {@link ControlLCAUtil#writeForeground(Control)} instead.
   *
   * @param widget the widget whose foreground property to set
   * @param newColor the new value of the property
   * @throws IOException
   * @see #preserveForeground(Widget, Color)
   */
  public static void writeForeground( final Widget widget,
                                      final Color newColor )
    throws IOException
  {
    if( WidgetLCAUtil.hasChanged( widget, PROP_FOREGROUND, newColor, null ) ) {
      JSWriter writer = JSWriter.getWriterFor( widget );
      if( newColor != null ) {
        writer.set( JSConst.QX_FIELD_COLOR, newColor );
      } else {
        writer.reset( JSConst.QX_FIELD_COLOR );
      }
    }
  }

  /**
   * Writes JavaScript code to the response that resets the property
   * <code>foreground</code> of a widget. This method is intended to be used
   * by implementations of the method
   * {@link AbstractWidgetLCA#createResetHandlerCalls(String)}.
   *
   * @throws IOException
   */
  public static void resetForeground() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.reset( "textColor" );
    String listener = "org.eclipse.swt.WidgetManager._onAppearSetForeground";
    writer.removeListener( "appear", listener );
  }

  /**
   * Determines whether the property <code>background</code> of the given
   * widget has changed during the processing of the current request and if so,
   * writes JavaScript code to the response that updates the client-side
   * background property of the specified widget. For instances of
   * {@link Control}, use the method
   * {@link ControlLCAUtil#writeBackground(Control)} instead.
   *
   * @param widget the widget whose background property to set
   * @param newColor the new value of the property
   * @throws IOException
   * @see #preserveBackground(Widget, Color)
   */
  public static void writeBackground( final Widget widget,
                                      final Color newColor )
    throws IOException
  {
    writeBackground( widget, newColor, false );
  }

  /**
   * Determines whether the property <code>background</code> of the given
   * widget has changed during the processing of the current request and if so,
   * writes JavaScript code to the response that updates the client-side
   * background property of the specified widget. For instances of
   * {@link Control}, use the method
   * {@link ControlLCAUtil#writeBackground(Control)} instead.
   *
   * @param widget the widget whose background property to set
   * @param background the new background color
   * @param transparency the new background transparency, if <code>true</code>,
   *            the <code>background</code> parameter is ignored
   * @throws IOException
   * @see {@link #preserveBackground(Widget, Color, boolean)}
   */
  public static void writeBackground( final Widget widget,
                                      final Color background,
                                      final boolean transparency )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( widget );
    boolean changed = WidgetLCAUtil.hasChanged( widget,
                                                PROP_BACKGROUND_TRANSPARENCY,
                                                Boolean.valueOf( transparency ),
                                                Boolean.FALSE );
    if( !changed && !transparency ) {
      changed
        = WidgetLCAUtil.hasChanged( widget, PROP_BACKGROUND, background, null );
    }
    if( changed ) {
      if( transparency ) {
        writer.set( JSConst.QX_FIELD_BG_COLOR, ( Object )null );
      } else if( background != null ) {
        writer.set( JSConst.QX_FIELD_BG_COLOR, background );
      } else {
        writer.reset( JSConst.QX_FIELD_BG_COLOR );
      }
    }
  }

  /**
   * Writes JavaScript code to the response that reset the property
   * <code>background</code> of a widget. This method is intended to be used
   * by implementations of the method
   * {@link AbstractWidgetLCA#createResetHandlerCalls(String)}.
   *
   * @throws IOException
   */
  public static void resetBackground() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.reset( JSConst.QX_FIELD_BG_COLOR );
  }

  /**
   * Determines whether the property <code>enabled</code> of the given widget
   * has changed during the processing of the current request and if so, writes
   * JavaScript code to the response that updates the client-side enabled
   * property of the specified widget. For instances of {@link Control}, use
   * the method {@link ControlLCAUtil#writeEnabled(Control)} instead.
   *
   * @param widget the widget whose enabled property to set
   * @param enabled the new value of the property
   * @throws IOException
   * @see #preserveEnabled(Widget, boolean)
   */
  public static void writeEnabled( final Widget widget, final boolean enabled )
    throws IOException
  {
    Boolean newValue = Boolean.valueOf( enabled );
    JSWriter writer = JSWriter.getWriterFor( widget );
    Boolean defValue = Boolean.TRUE;
    writer.set( Props.ENABLED, JSConst.QX_FIELD_ENABLED, newValue, defValue );
  }


  /**
   * Writes JavaScript code to the response that resets the property
   * <code>enabled</code> of a widget. This method is intended to be used by
   * implementations of the method
   * {@link AbstractWidgetLCA#createResetHandlerCalls(String)}.
   *
   * @throws IOException
   */
  public static void resetEnabled() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    // TODO [fappel]: check whether to use reset
    writer.set( JSConst.QX_FIELD_ENABLED, true );
  }

  /**
   * Replaces all newline characters in the specified input string with the
   * given replacement string.
   *
   * @param input the string to process
   * @param replacement the string to replace line feeds with
   * @return a new string with all line feeds replaced
   * @since 1.1
   */
  public static String replaceNewLines( final String input,
                                        final String replacement )
  {
    return CommonPatterns.replaceNewLines( input, replacement );
  }

  /**
   * Determines whether the custom variant of the given widget
   * has changed during the processing of the current request and if so, writes
   * JavaScript code to the response that updates the client-side variant.
   *
   * @param widget the widget whose custom variant to write
   * @throws IOException
   */
  public static void writeCustomVariant( final Widget widget )
    throws IOException
  {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    String oldValue = ( String )adapter.getPreserved( PROP_VARIANT );
    String newValue = WidgetUtil.getVariant( widget );
    if( WidgetLCAUtil.hasChanged( widget, PROP_VARIANT, newValue, null ) ) {
      JSWriter writer = JSWriter.getWriterFor( widget );
      Object[] args = new Object[] { "variant_" + oldValue };
      if( oldValue != null ) {
        writer.call( JSConst.QX_FUNC_REMOVE_STATE, args );
      }
      if( newValue != null ) {
        args = new Object[] { "variant_" + newValue };
        writer.call( JSConst.QX_FUNC_ADD_STATE, args );
      }
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

  ///////////////////////////////////////
  // Helping method to test for equality

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

  ////////////////////////////////////
  // Helping method to split font name

  static String[] parseFontName( final String name ) {
    synchronized( parsedFonts ) {
      String[] result = ( String[] )parsedFonts.get( name );
      if( result == null ) {
        result = name.split( "," );
        for( int i = 0; i < result.length; i++ ) {
          result[ i ] = result[ i ].trim();
          Matcher matcher = FONT_NAME_FILTER_PATTERN.matcher( result[ i ] );
          result[ i ] = matcher.replaceAll( "" );
        }
        parsedFonts.put( name, result );
      }
      return result;
    }
  }

  //////////////////////////////////////
  // Escaping of reserved XML characters

  /**
   * Replaces all occurrences of the characters <code>&lt;</code>,
   * <code>&gt;</code>, <code>&amp;</code>, and <code>&quot;</code> with
   * their corresponding HTML entities. This function is used for rendering
   * texts to the client. When the parameter mnemonic is set to
   * <code>true</code>, this method handles ampersand characters in the text
   * as mnemonics in the same manner as SWT does.
   * <p>
   * <strong>Note:</strong> In contrast to SWT, the characters following an
   * ampersand are currently not underlined, as RAP doesn't support key events
   * yet.
   * </p>
   *
   * @param text the input text
   * @param mnemonics if <code>true</code>, the function is mnemonic aware,
   *            otherwise all ampersand characters are directly rendered.
   * @return the resulting text
   */
  // Note [rst]: Single quotes are not escaped as the entity &apos; is not
  //             defined in HTML 4. They should be handled by this method once
  //             we produce XHTML output.
  public static String escapeText( final String text, final boolean mnemonics )
  {
//    int mnemonicPos = -1;
    int offset = 0;
    boolean insertAmp = false;
    StringBuffer sb = new StringBuffer();
    Matcher matcher = HTML_ESCAPE_PATTERN.matcher( text );
    while( matcher.find() ) {
      int index = matcher.start();
      char ch = text.charAt( index );
      if( ch == '&' ) {
        if( !mnemonics || insertAmp ) {
          insertAmp = false;
          matcher.appendReplacement( sb, "&amp;" );
          offset += 4;
        } else {
          if( index + 1 < text.length() && text.charAt( index + 1 ) == '&' ) {
            insertAmp = true;
          } else {
//            mnemonicPos = index + offset;
          }
          matcher.appendReplacement( sb, "" );
          offset -= 1;
        }
      } else if( ch == '<' ) {
        matcher.appendReplacement( sb, "&lt;" );
        offset += 3;
      } else if( ch == '>' ) {
        matcher.appendReplacement( sb, "&gt;" );
        offset += 3;
      } else if( ch == '"' ) {
        matcher.appendReplacement( sb, "&quot;" );
        offset += 5;
      }
    }
    matcher.appendTail( sb );
//    if( mnemonics && underline && mnemonicPos != -1 ) {
//      sb.insert( mnemonicPos + 1, "</u>" );
//      sb.insert( mnemonicPos, "<u>" );
//    }
    // truncate at zeros
    String result = sb.toString();
    int index = result.indexOf( 0 );
    if( index != -1 ) {
      result = result.substring( 0, index );
    }
    return result;
  }
}
