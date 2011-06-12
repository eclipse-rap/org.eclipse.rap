/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.engine.configurables;

import java.text.MessageFormat;

import javax.servlet.ServletContext;

import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.engine.*;
import org.eclipse.rwt.internal.util.ClassInstantiationException;
import org.eclipse.rwt.internal.util.ClassUtil;

public class BrandingManagerConfigurable implements Configurable {
  public static final String BRANDINGS_PARAM = "org.eclipse.rwt.brandings";

  private final ServletContext servletContext;

  public BrandingManagerConfigurable( ServletContext servletContext ) {
    this.servletContext = servletContext;
  }

  public void configure( ApplicationContext context ) {
    if( hasBrandingsConfigured() ) {
      registerBrandings( context );
    }
  }

  public void reset( ApplicationContext context ) {
    context.getBrandingManager().deregisterAll();
  }

  private void registerBrandings( ApplicationContext context ) {
    String param = servletContext.getInitParameter( BrandingManagerConfigurable.BRANDINGS_PARAM );
    String[] brandings = param.split( RWTServletContextListener.PARAMETER_SEPARATOR );
    for( int i = 0; i < brandings.length; i++ ) {
      registerBranding( context, brandings[ i ].trim() );
    }
  }

  private boolean hasBrandingsConfigured() {
    return null != servletContext.getInitParameter( BrandingManagerConfigurable.BRANDINGS_PARAM );
  }

  private void registerBranding( ApplicationContext context, String className ) {
    AbstractBranding branding = createBranding( className );
    context.getBrandingManager().register( branding );
  }
  
  private AbstractBranding createBranding( String className ) {
    AbstractBranding result;
    try {
      result = ( AbstractBranding )ClassUtil.newInstance( getClass().getClassLoader(), className );
    } catch( ClassInstantiationException cie ) {
      String text = "Unable to create an branding instance of ''{0}''.";
      String msg = MessageFormat.format( text, new Object[] { className } );
      throw new IllegalArgumentException( msg );
    }
    return result;
  }
}