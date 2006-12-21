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

package org.eclipse.rap.rwt.lifecycle;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.rap.rwt.internal.widgets.IWidgetAdapter;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.HtmlResponseWriter;
import com.w4t.engine.service.ContextProvider;
import com.w4t.engine.service.IServiceStateInfo;

/**
 * TODO [rh] JavaDoc
 * <p></p>
 * <p>Note that the JavaScript code that is rendered relies on the client-side 
 * <code>org.eclipse.rap.rwt.WidgetManager</code> to be present. </p> 
 */
// TODO [rh] decide whether method arguments are to be checked (not-null, etc)
//      this also applies for other 'SPI's. 
public final class JSWriter {
  
  public static JSVar WIDGET_MANAGER_REF = new JSVar( "wm" );
  public static JSVar WIDGET_REF = new JSVar( "w" );
  
  private static final JSVar TARGET_REF = new JSVar( "t" );
  private static final String WRITER_MAP = JSWriter.class.getName() + "Map";
  private static final String HAS_WINDOW_MANAGER 
    = JSWriter.class.getName() + ".hasWindowManager";
  private static final String FORMAT_EMPTY = "";
  
  private final Widget widget;
  private boolean hasWidgetRef;
  
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
    String paramList = createParamList( args );
    String code 
      = "var w = new {0}({1});"
      + "{2}.add( w, \"{3}\" );";
    String widgetId = WidgetUtil.getId( widget );
    write( code, className, paramList, WIDGET_MANAGER_REF, widgetId );
    hasWidgetRef = true;
    if( widget instanceof Shell ) {
      call( "addToDocument", null );
    } else if( widget instanceof Control ){
      setParent( ( Control )widget );
    }
  }

  public void setParent( final Control control ) throws IOException {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( control );
    if( adapter.getJSParent() == null ) {
      setParent( WidgetUtil.getId( control.getParent() ) );
    } else {
      setParent( adapter.getJSParent() );
    }
  }
  
  public void setParent( final String parentId ) throws IOException {
    doSetParent( createFindWidgetById( parentId ) );
  }
  
  public void set( final String jsProperty, final String value ) 
    throws IOException 
  {
    set( null, jsProperty, value );
  }
  
  public void set( final String jsProperty, final int value ) 
    throws IOException 
  {
    set( jsProperty, new int[] { value } );
  }
  
  public void set( final String jsProperty, final boolean value ) 
    throws IOException 
  {
    set( jsProperty, new boolean[] { value } );
  }
  
  public void set( final String jsProperty, final String[] values )
    throws IOException
  {
    ensureWidgetRef();
    String pattern = createSetPatternForStrings( values.length );
    writeProperty( pattern, jsProperty, values );
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
    call( widget, createPropertyChain( jsPropertyChain ), values );
  }

  // TODO [rh] the client side default value (relevant at initial rendering) is 
  //      not yet taken into account
  public void set( final String javaProperty, 
                   final String jsProperty, 
                   final String newValue ) 
    throws IOException 
  {
    if( javaProperty == null ) {
      set( jsProperty, new String[] { newValue } );
    } else {
      IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
      if(    !adapter.isInitialized()
          || WidgetUtil.hasChanged( widget, javaProperty, newValue ) ) 
      {
        set( jsProperty, new String[] { newValue } );
      }
    }
  }
  
  public void addListener( final String property, 
                           final String eventType,
                           final String listener )
    throws IOException
  {
    ensureWidgetRef();
    if( property == null ) {
      String code = "w.addEventListener( \"{0}\", {1} );";
      write( code, eventType, listener );
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
      String code = "w.removeEventListener( \"{0}\", {1} );";
      write( code, eventType, listener );
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
    write( "{0}.{1}({2});", target, function, params.toString() );
  }
  
  // TODO [rh] should we name this call and make it a static method?
  public void callStatic( final String function, final Object[] args ) 
    throws IOException 
  {
    ensureWidgetManager();
    String params = createParamList( args );
    write( "{0}({1});", function, params.toString() );
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
    String jsListenerAction = buffer.toString();
    return jsListenerAction;
  }
  

  /////////////////////////////////////////////////////////////////////
  // Helping methods for JavaScript WidgetManager and Widget references
  
  private void ensureWidgetManager() throws IOException {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    if( stateInfo.getAttribute( HAS_WINDOW_MANAGER ) == null ) {
      write( "var {0} = org.eclipse.rap.rwt.WidgetManager.getInstance();", 
             WIDGET_MANAGER_REF );
      stateInfo.setAttribute( HAS_WINDOW_MANAGER, Boolean.TRUE );
    }
  }

  private void ensureWidgetRef() throws IOException {
    ensureWidgetManager();
    if( !hasWidgetRef ) {
      String code = "var {0} = {1};";
      write( code, WIDGET_REF, createFindWidgetById( widget ) );
      hasWidgetRef = true;
    }
  }
  
  private void doSetParent( final String parentWidgetId ) throws IOException {
    ensureWidgetManager();
    write( "w.setParent( {0} );", parentWidgetId );
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
  
  private String createSetPatternForPrimitives( final int parameterCount,
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
  
  private static String createSetPatternForStrings( final int parameterCount ) {
    StringBuffer buffer = new StringBuffer( "w.set{0}(" );
    for( int i = 0; i < parameterCount; i++ ) {
      buffer.append( " \"{" );
      buffer.append( i + 1  );
      buffer.append( "}\"" );
      if( i + 1 < parameterCount ) {
        buffer.append( "," );
      }
    }
    buffer.append( " );" );
    return buffer.toString();
  }
  
  private String createParamList( final Object[] args ) {
    StringBuffer params = new StringBuffer();
    if( args != null ) {
      for( int i = 0; i < args.length; i++ ) {
        if( i == 0 ) {
          params.append( " " );
        }
        if( args[ i ] instanceof String ) {
          params.append( '"' );
          params.append( args[ i ] );
          params.append( '"' );
        } else if( args[ i ] instanceof Widget ) {
          if( args[ i ] == widget && hasWidgetRef ) {
            params.append( "w" );
          } else {
            params.append( createFindWidgetById( ( Widget )args[ i ] ) );
          }
        } else if( args[ i ] instanceof JSVar ) { 
          params.append( args[ i ] );
        } else if( args[ i ] instanceof Object[] ) { 
          params.append( createArray( ( Object[] )args[ i ] ) );
        } else {
          params.append( args[ i ] );
        }
        if( i == args.length - 1 ) {
          params.append( " " );
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
        buffer.append( ',' );
      }
      buffer.append( " \"" );
      buffer.append( array[ i ] );
      buffer.append( '"' );
      if( i == array.length - 1 ) {
        buffer.append( ' ' );
      }
    }
    buffer.append( ']' );
    return buffer.toString();
  }
  
  private String createPropertyChain( final String[] jsPropertyChain ) {
    StringBuffer buffer = new StringBuffer();
    int last = jsPropertyChain.length - 1;
    for( int i = 0; i < last; i++ ) {
      buffer.append( getGetterName( jsPropertyChain[ i ] ) );
      buffer.append( "()." );
    }
    buffer.append( getSetterName( jsPropertyChain[ last ] ) );
    return buffer.toString();
  }


  /////////////////////////////////////////////////////////
  // Helping methods to write to the actual response writer 
  
  private void write( final String pattern, final Object arg1 ) 
    throws IOException 
  {
    String out = MessageFormat.format( pattern, new Object[] { arg1 } );
    getWriter().write( out );
  }
  
  private void writeProperty( final String pattern, 
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
  
  private static String getSetterName( final String jsProperty ) {
    StringBuffer functionName = new StringBuffer();
    functionName.append( "set" );
    functionName.append( capitalize( jsProperty ) );
    return functionName.toString();
  }
  
  private static String getGetterName( final String jsProperty ) {
    StringBuffer functionName = new StringBuffer();
    functionName.append( "get" );
    functionName.append( capitalize( jsProperty ) );
    return functionName.toString();
  }
  
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
  
  private void write( final String pattern, 
                      final Object arg1, 
                      final Object arg2 ) 
    throws IOException 
  {
    String out = MessageFormat.format( pattern, new Object[] { arg1, arg2 } );
    getWriter().write( out );
  }
  
  private void write( final String pattern, 
                      final Object arg1, 
                      final Object arg2, 
                      final Object arg3 ) 
  throws IOException 
  {
    Object[] args = new Object[] { arg1, arg2, arg3 };
    String out = MessageFormat.format( pattern, args );
    getWriter().write( out );
  }
  
  private void write( final String pattern, 
                      final Object arg1, 
                      final Object arg2, 
                      final Object arg3, 
                      final Object arg4 ) 
  throws IOException 
  {
    Object[] args = new Object[] { arg1, arg2, arg3, arg4 };
    String out = MessageFormat.format( pattern, args );
    getWriter().write( out );
  }
  
  private static HtmlResponseWriter getWriter() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    return stateInfo.getResponseWriter();
  }
}
