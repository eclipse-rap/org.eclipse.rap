/*******************************************************************************
 * Copyright (c) 2011, 2015 Frank Appel and others.
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

  private static boolean matchesTarget( ServiceReference<?> serviceReference,
                                        ServiceReference<?> targetReference,
                                        Class<?> targetType )
  {
    String filterExpression = getFilterExpression( serviceReference, targetType );
    if( filterExpression != null ) {
      Filter filter = createFilter( filterExpression );
      return filter.match( targetReference );
    }
    return targetReference != null;
  }

  private static String getFilterExpression( ServiceReference<?> serviceReference,
                                             Class<?> targetType )
  {
    if( serviceReference != null ) {
      return ( String )serviceReference.getProperty( createTargetKey( targetType ) );
    }
    return null;
  }

  private static Filter createFilter( String filterExpression ) {
    try {
      return FrameworkUtil.createFilter( filterExpression );
    } catch( InvalidSyntaxException ise ) {
      throw new IllegalArgumentException( ise );
    }
  }

  static String createTargetKey( Class<?> targetType ) {
    return new StringBuilder()
      .append( targetType.getSimpleName().substring( 0, 1 ).toLowerCase() )
      .append( targetType.getSimpleName().substring( 1 ) )
      .append( ".target" )
      .toString();
  }

}
