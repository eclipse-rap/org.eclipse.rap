/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.browser.browserkit;

import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.jsonToJava;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.eclipse.swt.internal.browser.browserkit.BrowserLCA.EXECUTED_FUNCTION_ERROR;
import static org.eclipse.swt.internal.browser.browserkit.BrowserLCA.EXECUTED_FUNCTION_NAME;
import static org.eclipse.swt.internal.browser.browserkit.BrowserLCA.EXECUTED_FUNCTION_RESULT;
import static org.eclipse.swt.internal.events.EventTypes.PROGRESS_CHANGED;
import static org.eclipse.swt.internal.events.EventTypes.PROGRESS_COMPLETED;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.protocol.ControlOperationHandler;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.ServiceStore;
import org.eclipse.rap.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.internal.widgets.IBrowserAdapter;
import org.eclipse.swt.widgets.Event;


public class BrowserOperationHandler extends ControlOperationHandler<Browser> {

  private static final String METHOD_EXECUTE_FUNCTION = "executeFunction";
  private static final String PARAM_NAME = "name";
  private static final String PARAM_ARGUMENTS = "arguments";
  private static final String METHOD_EVALUATION_SUCCEEDED = "evaluationSucceeded";
  private static final String METHOD_EVALUATION_FAILED = "evaluationFailed";
  private static final String PARAM_RESULT = "result";
  private static final String EVENT_PROGRESS = "Progress";

  public BrowserOperationHandler( Browser browser ) {
    super( browser );
  }

  @Override
  public void handleCall( Browser browser, String method, JsonObject properties ) {
    if( METHOD_EXECUTE_FUNCTION.equals( method ) ) {
      handleCallExecuteFunction( browser, properties );
    } else if( METHOD_EVALUATION_SUCCEEDED.equals( method ) ) {
      handleCallEvaluationSucceeded( browser, properties );
    } else if( METHOD_EVALUATION_FAILED.equals( method ) ) {
      handleCallEvaluationFailed( browser, properties );
    }
  }

  @Override
  public void handleNotify( Browser browser, String eventName, JsonObject properties ) {
    if( EVENT_PROGRESS.equals( eventName ) ) {
      handleNotifyProgress( browser, properties );
    } else {
      super.handleNotify( browser, eventName, properties );
    }
  }

  /*
   * PROTOCOL CALL executeFunction
   *
   * @param name (string) the name of the function
   * @param arguments ([object]) array with function arguments
   */
  public void handleCallExecuteFunction( final Browser browser, JsonObject properties ) {
    String name = properties.get( PARAM_NAME ).asString();
    final Object[] arguments = ( Object[] )jsonToJava( properties.get( PARAM_ARGUMENTS ) );
    BrowserFunction[] functions = getAdapter( browser ).getBrowserFunctions();
    boolean found = false;
    for( int i = 0; i < functions.length && !found; i++ ) {
      final BrowserFunction current = functions[ i ];
      if( current.getName().equals( name ) ) {
        ProcessActionRunner.add( new Runnable() {
          public void run() {
            try {
              Object executedFunctionResult = current.function( arguments );
              setExecutedFunctionResult( browser, executedFunctionResult );
            } catch( Exception exception ) {
              setExecutedFunctionError( browser, exception.getMessage() );
            }
            setExecutedFunctionName( browser, current.getName() );
          }
        } );
        found = true;
      }
    }
  }

  /*
   * PROTOCOL CALL evaluationSucceeded
   *
   * @param result ([object]) array with one element that contains evaluation result
   */
  public void handleCallEvaluationSucceeded( Browser browser, JsonObject properties ) {
    Object evalValue = jsonToJava( properties.get( PARAM_RESULT ) );
    Object result = null;
    if( evalValue != null && evalValue instanceof Object[] ) {
      result = ( ( Object[] )evalValue )[ 0 ];
    }
    getAdapter( browser ).setExecuteResult( true, result );
  }

  /*
   * PROTOCOL CALL evaluationFailed
   *
   */
  public void handleCallEvaluationFailed( Browser browser, JsonObject properties ) {
    getAdapter( browser ).setExecuteResult( false, null );
  }

  /*
   * PROTOCOL NOTIFY Progress
   *
   */
  public void handleNotifyProgress( Browser browser, JsonObject properties ) {
    browser.notifyListeners( PROGRESS_CHANGED, new Event() );
    browser.notifyListeners( PROGRESS_COMPLETED, new Event() );
  }

  private static void setExecutedFunctionResult( Browser browser, Object result ) {
    ServiceStore serviceStore = ContextProvider.getServiceStore();
    serviceStore.setAttribute( EXECUTED_FUNCTION_RESULT + getId( browser ), result );
  }

  private static void setExecutedFunctionError( Browser browser, String error ) {
    ServiceStore serviceStore = ContextProvider.getServiceStore();
    serviceStore.setAttribute( EXECUTED_FUNCTION_ERROR + getId( browser ), error );
  }

  private static void setExecutedFunctionName( Browser browser, String name ) {
    ServiceStore serviceStore = ContextProvider.getServiceStore();
    serviceStore.setAttribute( EXECUTED_FUNCTION_NAME + getId( browser ), name );
  }

  private IBrowserAdapter getAdapter( Browser browser ) {
    return browser.getAdapter( IBrowserAdapter.class );
  }

}
