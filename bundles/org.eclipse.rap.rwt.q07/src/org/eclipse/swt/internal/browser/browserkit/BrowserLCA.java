/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.browser.browserkit;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.internal.widgets.IBrowserAdapter;
import org.eclipse.swt.widgets.Widget;

public final class BrowserLCA extends AbstractWidgetLCA {

  static final String BLANK_HTML = "<html><script></script></html>";

  private static final String QX_TYPE = "org.eclipse.swt.browser.Browser";
  private static final String QX_FIELD_SOURCE = "source";

  private static final String PARAM_EXECUTE_RESULT = "executeResult";
  static final String PARAM_EXECUTE_FUNCTION = "executeFunction";
  static final String PARAM_EXECUTE_ARGUMENTS = "executeArguments";

  static final String EXECUTED_FUNCTION_NAME
    = Browser.class.getName() + "#executedFunctionName.";
  static final String EXECUTED_FUNCTION_RESULT
    = Browser.class.getName() + "#executedFunctionResult.";
  static final String EXECUTED_FUNCTION_ERROR
    = Browser.class.getName() + "#executedFunctionError.";
  private static final String FUNCTIONS_TO_CREATE
    = Browser.class.getName() + "#functionsToCreate.";
  private static final String FUNCTIONS_TO_DESTROY
    = Browser.class.getName() + "#functionsToDestroy.";

  private static final String PROP_URL = "url";
  private static final String PROP_TEXT = "text";


  public void preserveValues( final Widget widget ) {
    Browser browser = ( Browser )widget;
    ControlLCAUtil.preserveValues( browser );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( browser );
    adapter.preserve( PROP_URL, browser.getUrl() );
    adapter.preserve( PROP_TEXT, getText( browser ) );
    WidgetLCAUtil.preserveCustomVariant( browser );
  }

  public void readData( final Widget widget ) {
    Browser browser = ( Browser )widget;
    String value
      = WidgetLCAUtil.readPropertyValue( browser, PARAM_EXECUTE_RESULT );
    if( value != null ) {
      boolean executeResult = Boolean.valueOf( value ).booleanValue();
      getAdapter( browser ).setExecuteResult( executeResult );
    }
    executeFunction( browser );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    Browser browser = ( Browser )widget;
    JSWriter writer = JSWriter.getWriterFor( browser );
    writer.newWidget( QX_TYPE );
    ControlLCAUtil.writeStyleFlags( browser );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Browser browser = ( Browser )widget;
    // TODO [rh] though implemented in DefaultAppearanceTheme, setting border
    //      does not work
    ControlLCAUtil.writeChanges( browser );
    destroyBrowserFunctions( browser );
    writeUrl( browser );
    createBrowserFunctions( browser );
    writeExecute( browser );
    writeFunctionResult( browser );
    WidgetLCAUtil.writeCustomVariant( browser );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  private static void writeUrl( final Browser browser )
    throws IOException
  {
    if( hasUrlChanged( browser ) ) {
      JSWriter writer = JSWriter.getWriterFor( browser );
      writer.set( QX_FIELD_SOURCE, getUrl( browser ) );
    }
  }

  static boolean hasUrlChanged( final Browser browser ) {
    boolean initialized = WidgetUtil.getAdapter( browser ).isInitialized();
    return    !initialized
           || WidgetLCAUtil.hasChanged( browser, PROP_TEXT, getText( browser ) )
           || WidgetLCAUtil.hasChanged( browser, PROP_URL, browser.getUrl() );
  }

  static String getUrl( final Browser browser ) throws IOException {
    String text = getText( browser );
    String url = browser.getUrl();
    String result;
    if( !"".equals( text.trim() ) ) {
      result = registerHtml( text );
    } else if( !"".equals( url.trim() ) ) {
      result = url;
    } else {
      result = registerHtml( BLANK_HTML );
    }
    return result;
  }

  private static void writeExecute( final Browser browser ) throws IOException {
    IBrowserAdapter adapter = getAdapter( browser );
    String executeScript = adapter.getExecuteScript();
    if( executeScript != null ) {
      JSWriter writer = JSWriter.getWriterFor( browser );
      writer.call( "execute", new Object[] { executeScript } );
    }
  }

  private static String registerHtml( final String html ) throws IOException {
    String name = createUrlFromHtml( html );
    byte[] bytes = html.getBytes( "UTF-8" );
    InputStream inputStream = new ByteArrayInputStream( bytes );
    ResourceManager.getInstance().register( name, inputStream );
    return ResourceManager.getInstance().getLocation( name );
  }

  private static String createUrlFromHtml( final String html ) {
    StringBuffer result = new StringBuffer();
    result.append( "org.eclipse.swt.browser/text" );
    result.append( String.valueOf( html.hashCode() ) );
    result.append( ".html" );
    return result.toString();
  }

  private static String getText( final Browser browser ) {
    Object adapter = browser.getAdapter( IBrowserAdapter.class );
    IBrowserAdapter browserAdapter = ( IBrowserAdapter )adapter;
    return browserAdapter.getText();
  }

  private static IBrowserAdapter getAdapter( final Browser browser ) {
    return ( IBrowserAdapter )browser.getAdapter( IBrowserAdapter.class );
  }

  //////////////////////////////////////
  // Helping methods for BrowserFunction

  private static void createBrowserFunctions( final Browser browser )
    throws IOException
  {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    String id = WidgetUtil.getId( browser );
    String[] functions
      = ( String[] )stateInfo.getAttribute( FUNCTIONS_TO_CREATE + id );
    if( functions != null ) {
      for( int i = 0; i < functions.length; i++ ) {
        Object[] args = new Object[]{
          functions[ i ]
        };
        JSWriter writer = JSWriter.getWriterFor( browser );
        writer.call( "createFunction", args );
      }
    }
  }

  private static void destroyBrowserFunctions( final Browser browser )
    throws IOException
  {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    String id = WidgetUtil.getId( browser );
    String[] functions
      = ( String[] )stateInfo.getAttribute( FUNCTIONS_TO_DESTROY + id );
    if( functions != null ) {
      for( int i = 0; i < functions.length; i++ ) {
        Object[] args = new Object[]{
          functions[ i ]
        };
        JSWriter writer = JSWriter.getWriterFor( browser );
        writer.call( "destroyFunction", args );
      }
    }
  }

  private void executeFunction( final Browser browser ) {
    String function
      = WidgetLCAUtil.readPropertyValue( browser, PARAM_EXECUTE_FUNCTION );
    String arguments
      = WidgetLCAUtil.readPropertyValue( browser, PARAM_EXECUTE_ARGUMENTS );
    if( function != null ) {
      IBrowserAdapter adapter = getAdapter( browser );
      BrowserFunction[] functions = adapter.getBrowserFunctions();
      boolean found = false;
      for( int i = 0; i < functions.length && !found; i++ ) {
        final BrowserFunction current = functions[ i ];
        if( current.getName().equals( function ) ) {
          final Object[] args = parseArguments( arguments );
          ProcessActionRunner.add( new Runnable() {
            public void run() {
              try {
                setExecutedFunctionName( browser, current.getName() );
                Object executedFunctionResult = current.function( args );
                setExecutedFunctionResult( browser, executedFunctionResult );
              } catch( Exception e ) {
                setExecutedFunctionError( browser, e.getMessage() );
              }
            }
          } );
          found = true;
        }
      }
    }
  }

  private static void writeFunctionResult( final Browser browser )
    throws IOException
  {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    String id = WidgetUtil.getId( browser );
    String function
      = ( String )stateInfo.getAttribute( EXECUTED_FUNCTION_NAME + id );
    if( function != null ) {
      Object result
        = stateInfo.getAttribute( EXECUTED_FUNCTION_RESULT + id );
      if( result != null ) {
        result = new JSVar( toJson( result, true ) );
      }
      String error
        = ( String )stateInfo.getAttribute( EXECUTED_FUNCTION_ERROR + id );
      Object[] args = new Object[]{
        result,
        error
      };
      JSWriter writer = JSWriter.getWriterFor( browser );
      writer.call( "setFunctionResult", args );
    }
  }

  private static void setExecutedFunctionName( final Browser browser,
                                               final String name )
  {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    String id = WidgetUtil.getId( browser );
    stateInfo.setAttribute( EXECUTED_FUNCTION_NAME + id, name );
  }

  private static void setExecutedFunctionResult( final Browser browser,
                                                 final Object result )
  {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    String id = WidgetUtil.getId( browser );
    stateInfo.setAttribute( EXECUTED_FUNCTION_RESULT + id, result );
  }

  private static void setExecutedFunctionError( final Browser browser,
                                                final String error )
  {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    String id = WidgetUtil.getId( browser );
    stateInfo.setAttribute( EXECUTED_FUNCTION_ERROR + id, error );
  }

  static Object[] parseArguments( final String arguments ) {
    List result = new ArrayList();
    if( arguments.startsWith( "[" ) && arguments.endsWith( "]" ) ) {
      // remove [ ] brackets
      String args = arguments.substring( 1, arguments.length() - 1 );
      int openQuotes = 0;
      int openBrackets = 0;
      String arg;
      StringBuffer argBuff = new StringBuffer();
      char prevChar = ' ';
      for( int i = 0; i < args.length(); i++ ) {
        char ch = args.charAt( i );
        if( ch == ',' && openQuotes == 0 && openBrackets == 0 ) {
          arg = argBuff.toString();
          if( arg.startsWith( "[" ) ) {
            result.add( parseArguments( arg ) );
          } else {
            arg = arg.replaceAll( "\\\\\"", "\"" );
            result.add( withType( arg ) );
          }
          argBuff.setLength( 0 );
        } else {
          if( ch == '"' && prevChar != '\\' ) {
            if( openQuotes == 0 ) {
              openQuotes++;
            } else {
              openQuotes--;
            }
          } else if( ch == '[' && openQuotes == 0 ) {
            openBrackets++;
          } else if( ch == ']'&& openQuotes == 0 ) {
            openBrackets--;
          }
          argBuff.append( ch );
        }
        prevChar = ch;
      }
      // append last segment
      arg = argBuff.toString();
      if( arg.startsWith( "[" ) ) {
        result.add( parseArguments( arg ) );
      } else if( !arg.equals( "" ) ) {
        arg = arg.replaceAll( "\\\\\"", "\"" );
        result.add( withType( arg ) );
      }
    }
    return result.toArray();
  }

  static Object withType( final String argument ) {
    Object result;
    if( argument.equals( "null" ) || argument.equals( "undefined" ) ) {
      result = null;
    } else if( argument.equals( "true" ) || argument.equals( "false" ) ) {
      result = new Boolean( argument );
    } else if( argument.startsWith( "\"" ) ) {
      result = new String( argument.substring( 1, argument.length() - 1 ) );
    } else {
      try {
        result = Double.valueOf( argument );
      } catch( NumberFormatException nfe ) {
        result = argument;
      }
    }
    return result;
  }

  static String toJson( final Object object, boolean deleteLastChar ) {
    StringBuffer result = new StringBuffer();
    if( object == null ) {
      result.append( "null" );
      result.append( "," );
    } else if( object instanceof String ) {
      result.append( "\"" );
      result.append( ( String )object );
      result.append( "\"" );
      result.append( "," );
    } else if( object instanceof Boolean ) {
      result.append( ( ( Boolean )object ).toString() );
      result.append( "," );
    } else if( object instanceof Number ) {
      result.append( ( ( Number )object ).toString() );
      result.append( "," );
    } else if( object.getClass().isArray() ) {
      Object[] array = ( Object[] )object;
      result.append( "[" );
      for( int i = 0; i < array.length; i++ ) {
        result.append( toJson( array[ i ], false ) );
      }
      result.insert( result.length() - 1, "]" );
    }
    if( deleteLastChar ) {
      result.deleteCharAt( result.length() - 1 );
    }
    return result.toString();
  }
}
