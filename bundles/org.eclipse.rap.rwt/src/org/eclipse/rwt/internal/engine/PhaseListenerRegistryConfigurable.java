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

import javax.servlet.ServletContext;

import org.eclipse.rwt.internal.lifecycle.CurrentPhase;
import org.eclipse.rwt.internal.textsize.MeasurementListener;
import org.eclipse.rwt.internal.util.ClassInstantiationException;
import org.eclipse.rwt.internal.util.ClassUtil;
import org.eclipse.rwt.lifecycle.PhaseListener;


class PhaseListenerRegistryConfigurable implements Configurable {
  static final String[] DEFAULT_PHASE_LISTENERS = new String[] {
    CurrentPhase.Listener.class.getName(),
    MeasurementListener.class.getName()
  };
  private final ServletContext servletContext;

  PhaseListenerRegistryConfigurable( ServletContext servletContext ) {
    this.servletContext = servletContext;
  }

  public void configure( ApplicationContext context ) {
    String[] listenerNames = getPhaseListenerNames();
    for( int i = 0; i < listenerNames.length; i++ ) {
      String className = listenerNames[ i ].trim();
      registerPhaseListener( context, className );
    }
  }

  public void reset( ApplicationContext context ) {
    context.getPhaseListenerRegistry().removeAll();
  }
  
  String[] getPhaseListenerNames() {
    String[] result = DEFAULT_PHASE_LISTENERS;
    if( getInitParameter() != null ) {
      result = getInitParameter().split( RWTServletContextListener.PARAMETER_SEPARATOR );
    }
    return result;
  }
  
  private void registerPhaseListener( ApplicationContext context, String className ) {
    PhaseListener phaseListener = createPhaseListener( className );
    context.getPhaseListenerRegistry().add( phaseListener );
  }

  private PhaseListener createPhaseListener( String className ) {
    PhaseListener result;
    try {
      result = ( PhaseListener )ClassUtil.newInstance( getClass().getClassLoader(), className );
    } catch( ClassInstantiationException cie ) {
      String pattern = "Unable to create a phase listener instance of ''{0}''.";
      String msg = MessageFormat.format( pattern, new Object[] { className } );
      throw new IllegalArgumentException( msg );
    }
    return result;
  }

  private String getInitParameter() {
    return servletContext.getInitParameter( RWTServletContextListener.PHASE_LISTENERS_PARAM );
  }
}