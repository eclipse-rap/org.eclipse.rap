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
package org.eclipse.rwt.internal.engine;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletContext;

import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.util.ClassInstantiationException;
import org.eclipse.rwt.internal.util.ClassUtil;

class BrandingManagerConfigurable implements Configurable {
  private static final String BRANDINGS
    = BrandingManagerConfigurable.class.getName() + "#BRANDINGS";

  private final ServletContext servletContext;

  BrandingManagerConfigurable( ServletContext servletContext ) {
    this.servletContext = servletContext;
  }

  public void configure( ApplicationContext context ) {
    if( hasBrandingsConfigured() ) {
      registerBrandings( context );
    }
  }

  public void reset( ApplicationContext context ) {
    Iterator brandings = getBufferedBrandings().iterator();
    while( brandings.hasNext() ) {
      AbstractBranding branding = ( AbstractBranding )brandings.next();
      context.getBrandingManager().deregister( branding );
    }
    removeBufferedBrandings();
  }

  private void registerBrandings( ApplicationContext context ) {
    String param = servletContext.getInitParameter( RWTServletContextListener.BRANDINGS_PARAM );
    String[] brandings = param.split( RWTServletContextListener.PARAMETER_SEPARATOR );
    for( int i = 0; i < brandings.length; i++ ) {
      registerBranding( context, brandings[ i ].trim() );
    }
  }

  private boolean hasBrandingsConfigured() {
    return null != servletContext.getInitParameter( RWTServletContextListener.BRANDINGS_PARAM );
  }

  private void registerBranding( ApplicationContext context, String className ) {
    AbstractBranding branding = createBranding( className );
    context.getBrandingManager().register( branding );
    getBufferedBrandings().add( branding );
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

  private Set getBufferedBrandings() {
    return RWTServletContextListener.getBuffer( BRANDINGS, servletContext );
  }
  
  private void removeBufferedBrandings() {
    RWTServletContextListener.removeBuffer( BRANDINGS, servletContext );
  }
}