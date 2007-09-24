/*******************************************************************************
 * Copyright (c) 2002-2007 Innoopract Informationssysteme GmbH.
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
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.rwt.internal.lifecycle.CurrentPhase;
import org.eclipse.rwt.internal.lifecycle.HtmlResponseWriter;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.internal.graphics.IColor;
import org.eclipse.swt.internal.widgets.WidgetAdapter;
import org.eclipse.swt.internal.widgets.buttonkit.ButtonLCA;
import org.eclipse.swt.widgets.*;


/**
 * TODO [rh] JavaDoc
 * <p></p>
 * <p>Note that the JavaScript code that is rendered relies on the client-side
 * <code>org.eclipse.swt.WidgetManager</code> to be present. </p>
 * 
 * @see AbstractWidgetLCA
 * @see ControlLCAUtil
 * @see WidgetLCAUtil
 */
// TODO [rh] decide whether method arguments are to be checked (not-null, etc)
//      this also applies for other 'SPI's.
public final class JSWriter {

  /**
   * A reference to the current widget manager on the client side.
   */
  public static JSVar WIDGET_MANAGER_REF = new JSVar( "wm" );
  
  /**
   * Reference to the widget of this JSWriter instance.
   */
  public static JSVar WIDGET_REF = new JSVar( "w" );

  private static final Object[] NULL_PARAMETER = new Object[] { null };
  private static final String NEW_WIDGET_PATTERN
    =   "var w = wm.newWidget( \"{0}\", \"{1}\", {2}, "
      + "{3,number,#}, ''{4}''{5} );";
  private static final Pattern DOUBLE_QUOTE_PATTERN
    = Pattern.compile( "(\"|\\\\)" );
  private static final Pattern NEWLINE_PATTERN
    = Pattern.compile( "\\r\\n|\\r|\\n" );
  private static final String NEWLINE_ESCAPE = "\\\\n";

  private static final JSVar TARGET_REF = new JSVar( "t" );

  private static final String WRITER_MAP
    = JSWriter.class.getName() + "#map";
  private static final String HAS_WINDOW_MANAGER
    = JSWriter.class.getName() + "#hasWindowManager";
  private static final String CURRENT_WIDGET_REF
    = JSWriter.class.getName() + "#currentWidgetRef";
  private static final String FORMAT_EMPTY = "";

  private final Widget widget;

  /**
   * Returns an instance of {@link JSWriter} for a specified
   * widget. Only this writer can modify attributes and call methods
   * on the client-side representation of the widget.
   * 
   * @param widget the widget for the requested {@link JSWriter}
   * @return the corresponding {@link JSWriter}
   */
  public static JSWriter getWriterFor( final Widget widget ) {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    JSWriter result;
    Map map = ( Map )stateInfo.getAttribute( WRITER_MAP );
    if( map == null ) {
      map = new HashMap();
      stateInfo.setAttribute( WRITER_MAP, map );
    }
    if( map.containsKey( widget ) ) {
      result = ( JSWriter )map.get( widget );
    } else {
      result = new JSWriter( widget );
      map.put( widget, result );
    }
    return result;
  }

  /**
   * Returns the {@link JSWriter} instance used to reset
   * a widgets attributes in order to take part in the
   * pooling mechanism.
   * 
   * @return the {@link JSWriter} instance
   */
  public static JSWriter getWriterForResetHandler() {
    return new JSWriter( null );
  }

  private JSWriter( final Widget iwidgetdget ) {
    this.widget = iwidgetdget;
  }

  /**
   * Creates a new widget on the client-side by creating an instance of the
   * corresponding javascript class definition. This is normally done in
   * the <code>renderInitialization</code> method of the widgets life-cycle
   * adapter (LCA).
   * 
   * @param className the javascript class to initiate
   * @throws IOException
   * @see AbstractWidgetLCA#renderInitialization
   */
  public void newWidget( final String className ) throws IOException {
    newWidget( className, null );
  }

  /**
   * Creates a new widget on the client-side by creating an instance of the
   * corresponding javascript class definition. This is normally done in
   * the <code>renderInitialization</code> method of the widgets life-cycle
   * adapter (LCA). All arguments passed to this function will be transmitted
   * to the client and used to call the constructor of the javascript widget.
   * 
   * @param className the javascript class to initiate
   * @param args the arguments for the widgets constructor on the client-side
   * 
   * @throws IOException
   * @see AbstractWidgetLCA#renderInitialization
   */
  public void newWidget( final String className, final Object[] args )
    throws IOException
  {
    ensureWidgetManager();
    String typePoolId = getTypePoolId( widget );
    Object[] args1 = new Object[] {
      WidgetUtil.getId( widget ),
      getJSParentId( widget ),
      useSetParent(),
      typePoolId == null ? null : new Integer( typePoolId.hashCode() ),
      className,
      createParamList( ", '", args, "'", false )
    };
    String out = MessageFormat.format( NEW_WIDGET_PATTERN, args1 );
    getWriter().write( out );
    setCurrentWidgetRef( widget );
    if( widget instanceof Shell ) {
      call( "addToDocument", null );
    }
  }

  /**
   * Explicitly sets the parent of the client-side widget.
   * 
   * @param parentId the widget id of the parent
   * @throws IOException
   * @see WidgetUtil
   */
  public void setParent( final String parentId ) throws IOException {
    call( WIDGET_MANAGER_REF, "setParent", new Object[] { widget, parentId } );
  }

  /**
   * Sets the specified property of the client-side widget to a new value.
   * 
   * @param jsProperty the attribute to change
   * @param value the new value
   * 
   * @throws IOException
   * @see AbstractWidgetLCA
   */
  public void set( final String jsProperty, final String value )
    throws IOException
  {
    call( getSetterName( jsProperty ), new Object[] { value } );
  }

  /**
   * Sets the specified property of the client-side widget to a new value.
   * 
   * @param jsProperty the attribute to change
   * @param value the new value
   * 
   * @throws IOException
   * @see AbstractWidgetLCA
   */
  public void set( final String jsProperty, final int value )
    throws IOException
  {
    set( jsProperty, new int[] { value } );
  }

  /**
   * Sets the specified property of the client-side widget to a new value.
   * 
   * @param jsProperty the attribute to change
   * @param value the new value
   * 
   * @throws IOException
   * @see AbstractWidgetLCA
   */
  public void set( final String jsProperty, final float value )
    throws IOException
  {
    set( jsProperty, new float[] { value } );
  }

  /**
   * Sets the specified property of the client-side widget to a new value.
   * 
   * @param jsProperty the attribute to change
   * @param value the new value
   * 
   * @throws IOException
   * @see AbstractWidgetLCA
   */
  public void set( final String jsProperty, final boolean value )
    throws IOException
  {
    set( jsProperty, new boolean[] { value } );
  }

  /**
   * Sets the specified property of the client-side widget to a new value.
   * 
   * @param jsProperty the attribute to change
   * @param values the new values
   * 
   * @throws IOException
   * @see AbstractWidgetLCA
   */
  public void set( final String jsProperty, final int[] values )
    throws IOException
  {
    String functionName = getSetterName( jsProperty );
    Integer[] integers = new Integer[ values.length ];
    for( int i = 0; i < values.length; i++ ) {
      integers[ i ] = new Integer( values[ i ] );
    }
    call( widget, functionName, integers );
  }

  /**
   * Sets the specified property of the client-side widget to a new value.
   * 
   * @param jsProperty the attribute to change
   * @param values the new values
   * 
   * @throws IOException
   * @see AbstractWidgetLCA
   */
  public void set( final String jsProperty, final float[] values )
  throws IOException
  {
    String functionName = getSetterName( jsProperty );
    Float[] floats = new Float[ values.length ];
    for( int i = 0; i < values.length; i++ ) {
      floats[ i ] = new Float( values[ i ] );
    }
    call( widget, functionName, floats );
  }

  /**
   * Sets the specified property of the client-side widget to a new value.
   * 
   * @param jsProperty the attribute to change
   * @param values the new values
   * 
   * @throws IOException
   * @see AbstractWidgetLCA
   */
  public void set( final String jsProperty, final boolean[] values )
    throws IOException
  {
    ensureWidgetRef();
    Boolean[] parameters = new Boolean[ values.length ];
    for( int i = 0; i < values.length; i++ ) {
      parameters[ i ] = Boolean.valueOf( values[ i ] );
    }
    String pattern = createSetPatternForPrimitives( values.length,
                                                    FORMAT_EMPTY );
    writeProperty( pattern, jsProperty, parameters );
  }

  /**
   * Sets the specified property of the client-side widget to a new value.
   * 
   * @param jsProperty the attribute to change
   * @param value the new value
   * 
   * @throws IOException
   * @see AbstractWidgetLCA
   */
  public void set( final String jsProperty, final Object value )
    throws IOException
  {
    set( jsProperty, new Object[] { value } );
  }

  /**
   * Sets the specified property of the client-side widget to a new value.
   * 
   * @param jsProperty the attribute to change
   * @param values the new values
   * 
   * @throws IOException
   * @see AbstractWidgetLCA
   */
  public void set( final String jsProperty, final Object[] values )
    throws IOException
  {
    call( widget, getSetterName( jsProperty ), values );
  }


  /**
   * Sets the specified properties of the client-side widget to new values.
   * 
   * @param jsPropertyChain the attributes to change
   * @param values the new values
   * 
   * @throws IOException
   * @see AbstractWidgetLCA
   */
  public void set( final String[] jsPropertyChain, final Object[] values )
    throws IOException
  {
    call( widget, createPropertyChain( jsPropertyChain, false ), values );
  }

  /**
   * Sets the specified properties of the client-side widget to a new value.
   * Uses a specified key to determinate if this value is already
   * preserved and only sets the new value if it has changed since the last
   * request. 
   * 
   * @param javaProperty the key to use on the server-side
   * @param jsProperty the attribute to change
   * @param newValue the new values
   * 
   * @return if the value was already preserved
   * 
   * @throws IOException
   * @see AbstractWidgetLCA
   * @see AbstractWidgetLCA#preserveValues(Widget)
   */
  public boolean set( final String javaProperty,
                      final String jsProperty,
                      final Object newValue )
    throws IOException
  {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    boolean changed
      =  !adapter.isInitialized()
      || WidgetLCAUtil.hasChanged( widget, javaProperty, newValue );
    if( changed ) {
      set( jsProperty, newValue );
    }
    return changed;
  }

  /**
   * Sets the specified properties of the client-side widget to a new value.
   * Uses a specified key to determinate if this value is already
   * preserved and only sets the new value if it has changed since the last
   * request. If the widget is rendered (and not yet present on the client side)
   * <code>true</code> is only returned if the <code>newValue</code> differs
   * from the <code>defaultValue</code>.
   * 
   * @param javaProperty the key to use on the server-side
   * @param jsProperty the attributes to change
   * @param newValue the new value
   * @param defValue the default value
   * 
   * @return if the value has changed and <code>true</code> if the value is not yet
   *         preserved and <code>newValue</code> differs
   *         from the <code>defaultValue</code>.
   * @throws IOException
   * @see AbstractWidgetLCA
   * @see AbstractWidgetLCA#preserveValues(Widget)
   */
  public boolean set( final String javaProperty,
                      final String jsProperty,
                      final Object newValue,
                      final Object defValue )
    throws IOException
  {
    boolean changed
      = WidgetLCAUtil.hasChanged( widget, javaProperty, newValue, defValue );
    if( changed ) {
      set( jsProperty, new Object[] { newValue } );
    }
    return changed;
  }

  /**
   * Resets the specified javascript property to its initial value.
   * 
   * @param jsProperty the javascript property to reset
   * 
   * @throws IOException
   */
  public void reset( final String jsProperty ) throws IOException {
    call( widget, getResetterName( jsProperty ), null );
  }

  /**
   * Resets the specified javascript properties to their initial values.
   * 
   * @param jsPropertyChain the javascript properties to reset
   * 
   * @throws IOException
   */
  public void reset( final String[] jsPropertyChain ) throws IOException {
    call( widget, createPropertyChain( jsPropertyChain, true ), null );
  }

  /**
   * This will add a listener to an object specified by the property of
   * the widget. The listener has to be a javascript function which accepts
   * exact one parameter - an <code>qx.event.type.Event</code> object.
   * 
   * @param property the property of the widget to what the listener should be added
   * @param eventType the type of the event
   * @param listener reference to the listener function
   * 
   * @throws IOException
   */
  public void addListener( final String property,
                           final String eventType,
                           final String listener )
    throws IOException
  {
    ensureWidgetRef();
    if( property == null ) {
      // TODO [rh] HACK to allow 'instance' listener instead of static listener
      //      functions
      if( listener.startsWith( "this." ) ) {
        String thisListener = listener.substring( 5 );
        String code = "w.addEventListener( \"{0}\", w.{1}, w );";
        write( code, eventType, thisListener );
      } else {
        String code = "w.addEventListener( \"{0}\", {1} );";
        write( code, eventType, listener );
      }
    } else {
      String code = "w.{0}().addEventListener( \"{1}\", {2} );";
      write( code, getGetterName( property ), eventType, listener );
    }
  }

  /**
   * This will add a listener to the widget of this {@link JSWriter}. The
   * listener has to be a javascript function which accepts exact one
   * parameter - an <code>qx.event.type.Event</code> object.
   * 
   * @param eventType the type of the event
   * @param listener reference to the listener function
   * 
   * @throws IOException
   */
  public void addListener( final String eventType, final String listener )
    throws IOException
  {
    addListener( null, eventType, listener );
  }


  public void updateListener( final String property,
                              final JSListenerInfo info,
                              final String javaListener,
                              final boolean hasListeners )
    throws IOException
  {
    if( info.getJSListenerType() == JSListenerType.ACTION ) {
      updateActionListener( property, info, javaListener, hasListeners );
    } else {
      updateStateAndActionListener( property,
                                    info,
                                    javaListener,
                                    hasListeners );
    }
  }

  public void updateListener( final JSListenerInfo info,
                              final String javaListener,
                              final boolean hasListeners )
    throws IOException
  {
    updateListener( null, info, javaListener, hasListeners );
  }

  public void removeListener( final String eventType, final String listener )
    throws IOException
  {
    removeListener( null, eventType, listener );
  }

  public void removeListener( final String property,
                              final String eventType,
                              final String listener )
    throws IOException
  {
    ensureWidgetRef();
    if( property == null ) {
      // TODO [rh] HACK to allow 'instance' listener instead of static listener
      //      functions
      if( listener.startsWith( "this." ) ) {
        String thisListener = listener.substring( 5 );
        String code = "w.removeEventListener( \"{0}\", w.{1}, w );";
        write( code, eventType, thisListener );
      } else {
        String code = "w.removeEventListener( \"{0}\", {1} );";
        write( code, eventType, listener );
      }
    } else {
      String code = "w.{0}().removeEventListener( \"{1}\", {2} );";
      write( code, getGetterName( property ), eventType, listener );
    }
  }


  /**
   * Calls a specific function of the widget on the client-side.
   * 
   * @param function the function name
   * @param args the arguments for the function
   * 
   * @throws IOException
   */
  public void call( final String function, final Object[] args )
    throws IOException
  {
    call( widget, function, args );
  }

  /**
   * Calls a specific function of a widget on the client-side.
   * 
   * @param target the widget on which the function should be called
   * @param function the function name
   * @param args the arguments for the function
   * 
   * @throws IOException
   */
  public void call( final Widget target,
                    final String function,
                    final Object[] args )
    throws IOException
  {
    ensureWidgetManager();
    JSVar refVariable;
    if( target == widget ) {
      ensureWidgetRef();
      refVariable = WIDGET_REF;
    } else {
      refVariable = TARGET_REF;
      write( "var {0} = {1};", refVariable, createFindWidgetById( target ) );
    }
    String params = createParamList( args );
    write( "{0}.{1}({2});", refVariable, function, params );
  }

  /**
   * Calls a specific function of a widget on the client-side.
   * 
   * @param target the widget on which the function should be called
   * @param function the function name
   * @param args the arguments for the function
   * 
   * @throws IOException
   */
  public void call( final JSVar target,
                    final String function,
                    final Object[] args )
    throws IOException
  {
    ensureWidgetManager();
    String params = createParamList( args );
    write( "{0}.{1}({2});", target, function, params );
  }

  public void startCall( final JSVar target,
                         final String function,
                         final Object[] args )
    throws IOException
  {
    ensureWidgetManager();
    String params = createParamList( " ", args, "", false );
    write( "{0}.{1}({2}", target, function, params );
  }

  public void endCall( final Object[] args ) throws IOException {
    getWriter().write( createParamList( "", args, "", false )  );
    getWriter().write( " );" );
  }

  // TODO [rh] should we name this method 'call' and make it a static method?
  public void callStatic( final String function, final Object[] args )
    throws IOException
  {
    ensureWidgetManager();
    String params = createParamList( args );
    write( "{0}({1});", function, params );
  }

  public void callFieldAssignment( final JSVar target,
                                   final String field,
                                   final String value )
    throws IOException
  {
    write( "{0}.{1} = {2};", target, field, value );
  }

  /**
   * Dispose is used to dispose the widget of this {@link JSWriter} on the client
   * side. As todays browser have several memory issues this will only dispose
   * the widget if there are no pooling informations available.
   * 
   * @throws IOException
   * 
   * @see AbstractWidgetLCA#getTypePoolId(Widget)
   * @see AbstractWidgetLCA#createResetHandlerCalls(String)
   */
  public void dispose() throws IOException {
    ensureWidgetManager();
    String widgetId = WidgetUtil.getId( widget );
    if( widget instanceof Control ) {
      ControlLCAUtil.resetActivateListener( ( Control )widget );
    }
    // TODO [rh] why can't setDefaultButton( null ) not be written in ButtonLCA?
    if( widget instanceof Button && ButtonLCA.isDefault( ( Button )widget ) ) {
      Button button = ( Button )widget;
      call( button.getShell(), "setDefaultButton", NULL_PARAMETER );
    }
    call( WIDGET_MANAGER_REF, "dispose", new Object[] { widgetId } );
  }


  ////////////////////////////////////////////////////////////////
  // helping methods for client side listener addition and removal

  private void updateActionListener( final String property,
                                     final JSListenerInfo info,
                                     final String javaListener,
                                     final boolean hasListeners )
    throws IOException
  {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    if( adapter.isInitialized() ) {
      Boolean hadListeners = ( Boolean )adapter.getPreserved( javaListener );
      if( hadListeners == null || Boolean.FALSE.equals( hadListeners ) ) {
        if( hasListeners ) {
          addListener( property, info.getEventType(), info.getJSListener() );
        }
      } else if( !hasListeners ) {
        removeListener( property, info.getEventType(), info.getJSListener() );
      }
    } else {
      if( hasListeners ) {
        addListener( property, info.getEventType(), info.getJSListener() );
      }
    }
  }

  private void updateStateAndActionListener( final String property,
                                             final JSListenerInfo info,
                                             final String javaListener,
                                             final boolean hasListeners )
    throws IOException
  {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    if( adapter.isInitialized() ) {
      Boolean hadListeners = ( Boolean )adapter.getPreserved( javaListener );
      if( hadListeners == null || Boolean.FALSE.equals( hadListeners ) ) {
        if( hasListeners ) {
          removeListener( property, info.getEventType(), info.getJSListener() );
          addListener( property,
                       info.getEventType(),
                       createJsActionListener( info ) );
        }
      } else {
        if( !hasListeners ) {
          removeListener( property,
                          info.getEventType(),
                          createJsActionListener( info ) );
          addListener( property, info.getEventType(), info.getJSListener() );
        }
      }
    } else {
      if( hasListeners ) {
        addListener( property,
                     info.getEventType(),
                     createJsActionListener( info ) );
      } else {
        addListener( property, info.getEventType(), info.getJSListener() );
      }
    }
  }

  private String createJsActionListener( final JSListenerInfo info ) {
    StringBuffer buffer = new StringBuffer();
    buffer.append( info.getJSListener() );
    buffer.append( "Action" );
    return buffer.toString();
  }


  /////////////////////////////////////////////////////////////////////
  // Helping methods for JavaScript WidgetManager and Widget references


  private Boolean useSetParent() {
    return   !( widget instanceof Shell ) && widget instanceof Control
           ? Boolean.TRUE
           : Boolean.FALSE;
  }

  private String getTypePoolId( final Widget widget ) throws IOException {
    AbstractWidgetLCA lca = WidgetUtil.getLCA( widget );
    return lca.getTypePoolId( widget );
  }

  private String getJSParentId( final Widget widget ) {
    String result = "";
    if( !( widget instanceof Shell ) && widget instanceof Control ) {
      Control control = ( Control )widget;
      WidgetAdapter adapter = ( WidgetAdapter )WidgetUtil.getAdapter( control );
      if( adapter.getJSParent() == null ) {
        result = WidgetUtil.getId( control.getParent() );
      } else {
        result = adapter.getJSParent();
      }
    }
    return result;
  }

  private void ensureWidgetManager() throws IOException {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    if(    currentPhaseIsRender()
        && widget != null
        && stateInfo.getAttribute( HAS_WINDOW_MANAGER ) == null )
    {
      write( "var {0} = org.eclipse.swt.WidgetManager.getInstance();",
             WIDGET_MANAGER_REF );
      stateInfo.setAttribute( HAS_WINDOW_MANAGER, Boolean.TRUE );
    }
  }

  // TODO [fappel]: FontSizeCalculation causes problems with widget manager
  //                in IE. See FontSizeCalculationHandler#createFontParam.
  //                Untill a better solution is found this hack is needed.
  private boolean currentPhaseIsRender() {
    return CurrentPhase.get() != PhaseId.PROCESS_ACTION
        && CurrentPhase.get() != PhaseId.PREPARE_UI_ROOT
        && CurrentPhase.get() != PhaseId.READ_DATA;
  }

  private void ensureWidgetRef() throws IOException {
    ensureWidgetManager();
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    Object currentWidgetRef = stateInfo.getAttribute( CURRENT_WIDGET_REF );
    if( widget != currentWidgetRef && widget != null ) {
      String code = "var {0} = {1};";
      write( code, WIDGET_REF, createFindWidgetById( widget ) );
      setCurrentWidgetRef( widget );
    }
  }

  private static void setCurrentWidgetRef( final Widget widget ) {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    stateInfo.setAttribute( CURRENT_WIDGET_REF, widget );
  }

  private static Widget getCurrentWidgetRef() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    return ( Widget )stateInfo.getAttribute( CURRENT_WIDGET_REF );
  }

  private static String createFindWidgetById( final Widget widget ) {
    return createFindWidgetById( WidgetUtil.getId( widget ) );
  }

  private static String createFindWidgetById( final String id ) {
    return MessageFormat.format( "{0}.findWidgetById( \"{1}\" )",
                                 new Object[] { WIDGET_MANAGER_REF, id } );
  }

  ///////////////////////////////////////////////
  // Helping methods tp construct parameter lists

  private static String createSetPatternForPrimitives( final int parameterCount,
                                                       final String typeAndStyle )
  {
    StringBuffer buffer = new StringBuffer( "w.set{0}(" );
    for( int i = 0; i < parameterCount; i++ ) {
      buffer.append( " {" );
      buffer.append( i + 1  );
      buffer.append( typeAndStyle );
      buffer.append( "}" );
      if( i + 1 < parameterCount ) {
        buffer.append( "," );
      }
    }
    buffer.append( " );" );
    return buffer.toString();
  }

  private static String createParamList( final Object[] args ) {
    return createParamList( " ", args, " ", true );
  }

  private static String createParamList( final String startList,
                                         final Object[] args,
                                         final String endList,
                                         final boolean useCurrentWidgetRef )
  {
    StringBuffer params = new StringBuffer();
    if( args != null ) {
      for( int i = 0; i < args.length; i++ ) {
        if( i == 0 ) {
          params.append( startList );
        }
        if( args[ i ] instanceof String ) {
          params.append( '"' );
          params.append( escapeString( ( String )args[ i ] ) );
          params.append( '"' );
        } else if( args[ i ] instanceof Character ) {
          params.append( '"' );
          params.append( args[ i ] );
          params.append( '"' );
        } else if( args[ i ] instanceof Widget ) {
          if( useCurrentWidgetRef && args[ i ] == getCurrentWidgetRef() ) {
            params.append( "w" );
          } else {
            params.append( createFindWidgetById( ( Widget )args[ i ] ) );
          }
        } else if( args[ i ] instanceof JSVar ) {
          params.append( args[ i ] );
        } else if( args[ i ] instanceof Color ) {
          params.append( '"' );
          params.append( ( ( IColor )args[ i ] ).toColorValue() );
          params.append( '"' );
        } else if( args[ i ] instanceof Object[] ) {
          params.append( createArray( ( Object[] )args[ i ] ) );
        } else {
          params.append( args[ i ] );
        }
        if( i == args.length - 1 ) {
          params.append( endList );
        } else {
          params.append( ", " );
        }
      }
    }
    return params.toString();
  }

  private static String createArray( final Object[] array ) {
    StringBuffer buffer = new StringBuffer();
    buffer.append( '[' );
    for( int i = 0; i < array.length; i++ ) {
      if( i > 0 ) {
        buffer.append( "," );
      }
      if( array[ i ] instanceof String ) {
        buffer.append( " \"" );
        buffer.append( escapeString( array[ i ].toString() ) );
        buffer.append( '"' );
      } else if( array[ i ] instanceof Widget ) {
        buffer.append( createFindWidgetById( ( Widget )array[ i ] ) );
      } else if( array[ i ] instanceof Color ) {
        buffer.append( '"' );
        buffer.append( ( ( IColor )array[ i ] ).toColorValue() );
        buffer.append( '"' );
      } else {
        buffer.append( array[ i ] );
      }
      if( i == array.length - 1 ) {
        buffer.append( ' ' );
      }
    }
    buffer.append( ']' );
    return buffer.toString();
  }

  private String createPropertyChain( final String[] jsPropertyChain,
                                      final boolean forReset )
  {
    StringBuffer buffer = new StringBuffer();
    int last = jsPropertyChain.length - 1;
    for( int i = 0; i < last; i++ ) {
      buffer.append( getGetterName( jsPropertyChain[ i ] ) );
      buffer.append( "()." );
    }
    if( forReset ) {
      buffer.append( getResetterName( jsPropertyChain[ last ] ) );
    } else {
      buffer.append( getSetterName( jsPropertyChain[ last ] ) );
    }
    return buffer.toString();
  }

  ////////////////////////////////////////
  // Helping methods to manipulate strings

  private static String capitalize( final String text ) {
    String result;
    if( Character.isUpperCase( text.charAt( 0 ) ) ) {
      result = text;
    } else {
      StringBuffer buffer = new StringBuffer( text );
      char firstLetter = buffer.charAt( 0 );
      firstLetter = Character.toUpperCase( firstLetter );
      buffer.setCharAt( 0, firstLetter );
      result = buffer.toString();
    }
    return result;
  }

  // TODO [rh] try to unite the various regex patterns
  // TODO [rh] revise how to handle newline characters (\n)
  private static String escapeString( final String input ) {
    Matcher matcher = DOUBLE_QUOTE_PATTERN.matcher( input );
    String result = matcher.replaceAll( "\\\\$1" );
    matcher = NEWLINE_PATTERN.matcher( result );
    result = matcher.replaceAll( NEWLINE_ESCAPE );
    return result;
  }

  private static String getSetterName( final String jsProperty ) {
    StringBuffer functionName = new StringBuffer();
    functionName.append( "set" );
    functionName.append( capitalize( jsProperty ) );
    return functionName.toString();
  }

  private static String getResetterName( final String jsProperty ) {
    StringBuffer functionName = new StringBuffer();
    functionName.append( "reset" );
    functionName.append( capitalize( jsProperty ) );
    return functionName.toString();
  }

  private static String getGetterName( final String jsProperty ) {
    StringBuffer functionName = new StringBuffer();
    functionName.append( "get" );
    functionName.append( capitalize( jsProperty ) );
    return functionName.toString();
  }

  /////////////////////////////////////////////////////////
  // Helping methods to write to the actual response writer

  private static void write( final String pattern, final Object arg1 )
    throws IOException
  {
    String out = MessageFormat.format( pattern, new Object[] { arg1 } );
    getWriter().write( out );
  }

  private static void writeProperty( final String pattern,
                                     final String propertyName,
                                     final Object[] arx )
    throws IOException
  {
    Object[] arguments = new Object[ arx.length + 1 ];
    System.arraycopy( arx, 0, arguments, 1, arx.length );
    arguments[ 0 ] = capitalize( propertyName );
    String out = MessageFormat.format( pattern, arguments );
    getWriter().write( out );
  }

  private static void write( final String pattern,
                             final Object arg1,
                             final Object arg2 )
    throws IOException
  {
    String out = MessageFormat.format( pattern, new Object[] { arg1, arg2 } );
    getWriter().write( out );
  }

  private static void write( final String pattern,
                             final Object arg1,
                             final Object arg2,
                             final Object arg3 )
  throws IOException
  {
    Object[] args = new Object[] { arg1, arg2, arg3 };
    String out = MessageFormat.format( pattern, args );
    getWriter().write( out );
  }

  private static HtmlResponseWriter getWriter() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    return stateInfo.getResponseWriter();
  }
}
