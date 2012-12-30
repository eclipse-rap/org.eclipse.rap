/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.eclipse.rap.rwt.internal.util.ParamCheck;


final class WrappedRequest extends HttpServletRequestWrapper {

  private final Map<String, String[]> parameterMap;

  WrappedRequest( HttpServletRequest request, Map<String, String[]> paramMap ) {
    super( request );
    ParamCheck.notNull( paramMap, "paramMap" );
    this.parameterMap = new HashMap<String, String[]>( paramMap );
    Enumeration parameterNames = request.getParameterNames();
    while( parameterNames.hasMoreElements() ) {
      String name = ( String )parameterNames.nextElement();
      parameterMap.put( name, request.getParameterValues( name ) );
    }
  }

  public String getParameter( String name ) {
    String[] value = parameterMap.get( name );
    String result = null;
    if( value != null ) {
      result = value[ 0 ];
    }
    return result;
  }

  public String[] getParameterValues( String name ) {
    String[] values = parameterMap.get( name );
    String[] result = null;
    if( values != null ) {
      result = new String[ values.length ];
      System.arraycopy( values, 0, result, 0, values.length );
    }
    return result;
  }

  public Enumeration<String> getParameterNames() {
    final Iterator iterator = parameterMap.keySet().iterator();
    return new Enumeration<String>() {
      public boolean hasMoreElements() {
        return iterator.hasNext();
      }
      public String nextElement() {
        return ( String )iterator.next();
      }
    };
  }

  public Map<String, String[]> getParameterMap() {
    return Collections.unmodifiableMap( parameterMap );
  }

}
