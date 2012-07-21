/*******************************************************************************
 * Copyright (c) 2011, 2012 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.osgi.internal;

import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.osgi.framework.*;
import org.osgi.service.http.HttpService;


class Matcher {

  private final ServiceReference<HttpService> httpServiceReference;
  private final ServiceReference<ApplicationConfiguration> configurationReference;

  Matcher( ServiceReference<HttpService> httpServiceReference,
           ServiceReference<ApplicationConfiguration> configurationReference )
  {
    this.httpServiceReference = httpServiceReference;
    this.configurationReference = configurationReference;
  }

  public boolean matches() {
    return matchesHttpService() && matchesConfigurator();
  }

  private boolean matchesHttpService() {
    return matchesTarget( configurationReference, httpServiceReference, HttpService.class );
  }

  private boolean matchesConfigurator() {
    Class<ApplicationConfiguration> targetType = ApplicationConfiguration.class;
    return matchesTarget( httpServiceReference, configurationReference, targetType );
  }

  private boolean matchesTarget( ServiceReference<?> serviceReference,
                                 ServiceReference<?> targetReference,
                                 Class<?> targetType )
  {
    boolean result = targetReference != null;
    String filterExpression = getFilterExpression( serviceReference, targetType );
    if( filterExpression != null ) {
      Filter filter = createFilter( filterExpression );
      result = filter.match( targetReference );
    }
    return result;
  }

  private String getFilterExpression( ServiceReference<?> serviceReference, Class targetType ) {
    String result = null;
    if( serviceReference != null ) {
      String targetKey = createTargetKey( targetType );
      result = ( String )serviceReference.getProperty( targetKey );
    }
    return result;
  }

  private Filter createFilter( String filterExpression ) {
    Filter result = null;
    try {
      result = FrameworkUtil.createFilter( filterExpression );
    } catch( InvalidSyntaxException ise ) {
      throw new IllegalArgumentException( ise );
    }
    return result;
  }

  static String createTargetKey( Class targetType ) {
    StringBuilder result = new StringBuilder();
    result.append( targetType.getSimpleName().substring( 0, 1 ).toLowerCase() );
    result.append( targetType.getSimpleName().substring( 1 ) );
    result.append( ".target" );
    return result.toString();
  }
}
