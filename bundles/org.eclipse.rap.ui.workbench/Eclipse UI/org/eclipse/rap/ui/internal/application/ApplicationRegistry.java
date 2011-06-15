/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.application;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.*;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.rap.ui.internal.servlet.EntryPointExtension;
import org.eclipse.rwt.engine.Context;
import org.eclipse.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.osgi.framework.Bundle;

/*
 * Registers all available applications as entrypoints.
 */
public final class ApplicationRegistry {

  private static final String RUN
    = "run"; //$NON-NLS-1$
  private static final String PI_RUNTIME
    = "org.eclipse.core.runtime"; //$NON-NLS-1$
  private static final String PT_APPLICATIONS
    = "applications"; //$NON-NLS-1$
  private static final String PT_APP_VISIBLE
    = "visible"; //$NON-NLS-1$

  private static Map appEntrypointMapping = new HashMap();

  public static IApplication getApplication() {
    IApplication application = null;
    String currentEntryPoint = EntryPointManager.getCurrentEntryPoint();
    Class clazz = ( Class )appEntrypointMapping.get( currentEntryPoint );
    try {
      application = ( IApplication )clazz.newInstance();
    } catch( final InstantiationException e ) {
      e.printStackTrace();
    } catch( final IllegalAccessException e ) {
      e.printStackTrace();
    }
    return application;
  }

  private static void registerApplication( IExtension extension, Context context ) {
    IConfigurationElement configElement
      = extension.getConfigurationElements()[0];
    String contributorName = configElement.getContributor().getName();
    IConfigurationElement[] runElement = configElement.getChildren( RUN );
    String className = runElement[ 0 ].getAttribute( "class" ); //$NON-NLS-1$
    String applicationId = extension.getUniqueIdentifier();
    // [if] Use full qualified applicationParameter, see bug 321360
    String applicationParameter = extension.getUniqueIdentifier();
    String isVisible = configElement.getAttribute( PT_APP_VISIBLE );
    try {
      // ignore invisible applications
      if( isVisible == null || Boolean.valueOf( isVisible ).booleanValue() ) {
        Bundle bundle = Platform.getBundle( contributorName );
        Class clazz = bundle.loadClass( className );
        appEntrypointMapping.put( applicationParameter, clazz );
        context.addEntryPoint( applicationParameter, EntrypointApplicationWrapper.class );
        EntryPointExtension.bind( applicationId, applicationParameter );
      }
    } catch( final ClassNotFoundException e ) {
      String text =   "Could not register application ''{0}'' " //$NON-NLS-1$
                    + "with request startup parameter ''{1}''."; //$NON-NLS-1$
      Object[] params = new Object[]{ className, applicationParameter };
      String msg = MessageFormat.format( text, params );
      IStatus status = new Status( IStatus.ERROR, contributorName,
                                         IStatus.OK, msg, e );
      WorkbenchPlugin.getDefault().getLog().log( status );
    }
  }

  public static void registerApplicationEntryPoints( Context context ) {
    IExtension[] elements = getApplicationExtensions();
    for( int i = 0; i < elements.length; i++ ) {
      registerApplication( elements[ i ], context );
    }
  }

  private static IExtension[] getApplicationExtensions() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    String extensionPointId = PI_RUNTIME + '.' + PT_APPLICATIONS;
    IExtensionPoint extensionPoint = registry.getExtensionPoint( extensionPointId );
    return extensionPoint.getExtensions();
  }

  private ApplicationRegistry() {
    // prevent instantiation
  }
}