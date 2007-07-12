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

package org.eclipse.swt.lifecycle;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.internal.graphics.IColor;
import org.eclipse.swt.internal.widgets.buttonkit.ButtonLCA;
import org.eclipse.swt.widgets.*;

import com.w4t.HtmlResponseWriter;
import com.w4t.engine.service.ContextProvider;
import com.w4t.engine.service.IServiceStateInfo;

/**
 * TODO [rh] JavaDoc
 * <p></p>
 * <p>Note that the JavaScript code that is rendered relies on the client-side
 * <code>org.eclipse.swt.WidgetManager</code> to be present. </p>
 */
// TODO [rh] decide whether method arguments are to be checked (not-null, etc)
//      this also applies for other 'SPI's.
public final class JSWriter {

  public static JSVar WIDGET_MANAGER_REF = new JSVar( "wm" );
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

  public static JSWriter getWriterForResetHandler() {
    return new JSWriter( null );
  }

  private JSWriter( final Widget widget ) {
    this.widget = widget;
  }

  public void newWidget( final String className ) throws IOException {
    newWidget( className, null );
  }

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

  public void setParent( final String parentId ) throws IOException {
    call( WIDGET_MANAGER_REF, "setParent", new Object[] { widget, parentId } );
  }

  public void set( final String jsProperty, final String value )
    throws IOException
  {
    call( getSetterName( jsProperty ), new Object[] { value } );
  }

  public void set( final String jsProperty, final int value )
    throws IOException
  {
    set( jsProperty, new int[] { value } );
  }

  public void set( final String jsProperty, final float value )
    throws IOException
  {
    set( jsProperty, new float[] { value } );
  }

  public void set( final String jsProperty, final boolean value )
    throws IOException
  {
    set( jsProperty, new boolean[] { value } );
  }

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

  public void set( final String jsProperty, final Object value )
    throws IOException
  {
    set( jsProperty, new Object[] { value } );
  }

  public void set( final String jsProperty, final Object[] values )
    throws IOException
  {
    call( widget, getSetterName( jsProperty ), values );
  }


  public void set( final String[] jsPropertyChain, final Object[] values )
    throws IOException
  {
    call( widget, createPropertyChain( jsPropertyChain, false ), values );
  }

  public void set( final String javaProperty,
                   final String jsProperty,
                   final Object newValue )
    throws IOException
  {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    if(    !adapter.isInitialized()
        || WidgetLCAUtil.hasChanged( widget, javaProperty, newValue ) )
    {
      set( jsProperty, newValue );
    }
  }

  public void set( final String javaProperty,
                   final String jsProperty,
                   final Object newValue,
                   final Object defValue )
    throws IOException
  {
    if( WidgetLCAUtil.hasChanged( widget, javaProperty, newValue, defValue ) ) {
      set( jsProperty, new Object[] { newValue } );
    }
  }

  public void reset( final String jsProperty ) throws IOException {
    call( widget, getResetterName( jsProperty ), null );
  }

  public void reset( final String[] jsPropertyChain ) throws IOException {
    call( widget, createPropertyChain( jsPropertyChain, true ), null );
  }

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


  public void call( final String function, final Object[] args )
    throws IOException
  {
    call( widget, function, args );
  }

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

  public void dispose() throws IOException {
    ensureWidgetManager();
    String widgetId = WidgetUtil.getId( widget );
    if( widget instanceof Control ) {
      ControlLCAUtil.resetActivateListener( ( Control )widget );
    }
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
    return   !( widget instanceof Shell )&& widget instanceof Control
           ? Boolean.TRUE
           : Boolean.FALSE;
  }

  private String getTypePoolId( final Widget widget ) throws IOException {
    AbstractWidgetLCA lca = WidgetUtil.getLCA( widget );
    return lca.getTypePoolId( widget );
  }

  private String getJSParentId( final Widget widget ) {
    String result = "";
    if( !(widget instanceof Shell ) && widget instanceof Control ) {
      Control control = ( Control )widget;
      IWidgetAdapter adapter = WidgetUtil.getAdapter( control );
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
    if(    widget != null
        && stateInfo.getAttribute( HAS_WINDOW_MANAGER ) == null )
    {
      write( "var {0} = org.eclipse.swt.WidgetManager.getInstance();",
             WIDGET_MANAGER_REF );
      stateInfo.setAttribute( HAS_WINDOW_MANAGER, Boolean.TRUE );
    }
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
                                         final boolean useCurrentWidgetRef ) {
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
