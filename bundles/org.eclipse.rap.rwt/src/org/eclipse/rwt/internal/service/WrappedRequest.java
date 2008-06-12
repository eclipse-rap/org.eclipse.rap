/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.eclipse.rwt.internal.util.ParamCheck;

final class WrappedRequest extends HttpServletRequestWrapper {

  private final Map parameterMap;
  
  WrappedRequest( final HttpServletRequest request, final Map paramMap ) {
    super( request );
    ParamCheck.notNull( paramMap, "paramMap" );
    this.parameterMap = new HashMap( paramMap );
    Enumeration parameterNames = request.getParameterNames();
    while( parameterNames.hasMoreElements() ) {
      String name = ( String )parameterNames.nextElement();
      parameterMap.put( name, request.getParameterValues( name ) );
    }
  }
  
  public String getParameter( final String name ) {
    String[] value = ( String[] )parameterMap.get( name );
    String result = null;
    if( value != null ) {
      result = value[ 0 ];
    }
    return result;
  }
  
  public String[] getParameterValues( final String name ) {
    String[] values = ( String[] )parameterMap.get( name );
    String[] result = null;
    if( values != null ) {
      result = new String[ values.length ];
      System.arraycopy( values, 0, result, 0, values.length );
    }
    return result;
  }
  
  public Enumeration getParameterNames() {
    final Iterator iterator = parameterMap.keySet().iterator();
    return new Enumeration() {
      public boolean hasMoreElements() {
        return iterator.hasNext();
      }
      public Object nextElement() {
        return iterator.next();
      }      
    };
  }
  
  public Map getParameterMap() {
    return Collections.unmodifiableMap( parameterMap );
  }
}