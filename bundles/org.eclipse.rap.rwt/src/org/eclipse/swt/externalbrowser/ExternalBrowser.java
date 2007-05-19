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

package org.eclipse.swt.externalbrowser;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.w4t.HtmlResponseWriter;
import com.w4t.W4TContext;
import com.w4t.engine.lifecycle.*;
import com.w4t.engine.service.ContextProvider;
import com.w4t.engine.service.IServiceStateInfo;

/**
 * This is preliminary API and subject to change without notice.
 */
// TODO [rh] JavaDoc
// TODO [rh] Consider renaming this package to ...additional or ...extra or 
//      ...rwt or similar, as a future place for holding additional non-SWT 
//      widgets like Upload and alike
public final class ExternalBrowser {

  public static final int LOCATION_BAR = 1 << 1;
  public static final int NAVIGATION_BAR = 1 << 2;
  public static final int STATUS = 1 << 3;

  private static final String OPEN 
    = "org.eclipse.swt.externalbrowser.Util.open( \"{0}\", \"{1}\", \"{2}\" );";
  private static final String CLOSE
    = "org.eclipse.swt.externalbrowser.Util.close( \"{0}\" );";

  public static void open( final String id, final String url, final int style ) 
  {
    if( id == null || url == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( id.length() == 0 ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    executeJS( getOpenJS( id, url, style ) );
  }
  
  public static void close( final String id ) {
    if( id == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( id.length() == 0 ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    executeJS( getCloseJS( id ) );
  }

  ///////////////////////////////
  // JavaScript code 'generation'
  
  private static String getOpenJS( final String id, 
                                   final String url, 
                                   final int style ) 
  {
    String[] args = new String[] { escapeId( id ), url, getFeatures( style ) };
    return MessageFormat.format( OPEN, args );
  }
  
  private static String getCloseJS( final String id ) {
    return MessageFormat.format( CLOSE, new String[] { escapeId( id ) } );
  }
  
  static String escapeId( final String id ) {
    String result = id;
    result = result.replaceAll( "_", "_0" );
    result = result.replaceAll( ".", "_" );
    return result;
  }
  
  private static String getFeatures( final int style ) {
    StringBuffer result = new StringBuffer();
    appendFeature( result, "dependent", true );
    appendFeature( result, "scrollbars", true );
    appendFeature( result, "resizable", true );
    appendFeature( result, "status", ( style & STATUS ) != 0 );
    appendFeature( result, "location", ( style & LOCATION_BAR ) != 0 );
    boolean navigation = ( style & NAVIGATION_BAR ) != 0;
    appendFeature( result, "toolbar", navigation );
    appendFeature( result, "menubar", navigation );
    return result.toString();
  }
  
  private static void appendFeature( final StringBuffer features, 
                                     final String feature, 
                                     final boolean enable ) 
  {
    if( features.length() > 0 ) {
      features.append( "," );
    }
    features.append( feature );
    features.append( "=" );
    features.append( enable ? 1 : 0 );
  }

  private static void executeJS( final String code ) {
    final Display display = Display.getCurrent();
    W4TContext.getLifeCycle().addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;

      public void beforePhase( final PhaseEvent event ) {
      }

      public void afterPhase( final PhaseEvent event ) {
        if( display == Display.getCurrent() ) {
          IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
          HtmlResponseWriter writer = stateInfo.getResponseWriter();
          try {
            writer.write( code, 0, code.length() );
          } catch( IOException e ) {
            // [rh] exception handling
            e.printStackTrace();
          }
          W4TContext.getLifeCycle().removePhaseListener( this );
        }
      }
      
      public PhaseId getPhaseId() {
        return PhaseId.RENDER;
      }
    } );
  }
  
  private ExternalBrowser() {
    // prevent instantiation
  }
}
