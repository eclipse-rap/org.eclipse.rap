/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.servlet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.rap.ui.resources.IResource;
import org.eclipse.ui.internal.WorkbenchPlugin;


class ResourceReader {

  private static final String ID_RESOURCES = "org.eclipse.rap.ui.resources";

  public static List<IResource> readResources() {
    DependentResource[] resources = readResourcesFromExtensions();
    DependentResource[] sortedResources = sortResources( resources );
    return createResourceList( sortedResources );
  }

  private static DependentResource[] readResourcesFromExtensions() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint point = registry.getExtensionPoint( ID_RESOURCES );
    IConfigurationElement[] elements = point.getConfigurationElements();
    DependentResource[] resources = new DependentResource[ elements.length ];
    for( int i = 0; i < elements.length; i++ ) {
      try {
        IResource resource = ( IResource )elements[ i ].createExecutableExtension( "class" );
        String resourceId = elements[ i ].getAttribute( "id" );
        IConfigurationElement[] dependsOn = elements[ i ].getChildren( "dependsOn" );
        List<String> resourceDependencies = new ArrayList<String>();
        for( int j = 0; j < dependsOn.length; j++ ) {
          String dependency = dependsOn[ j ].getAttribute( "resourceId" );
          resourceDependencies.add( dependency );
        }
        resources[ i ] = new DependentResource( resource, resourceId, resourceDependencies );
      } catch( CoreException ce ) {
        WorkbenchPlugin.getDefault().getLog().log( ce.getStatus() );
      }
    }
    return resources;
  }

  private static DependentResource[] sortResources( DependentResource[] resources ) {
    DependentResource[] result = new DependentResource[ resources.length ];
    List<String> sortedResourceIds = new ArrayList<String>();
    List<DependentResource> deferredResources = new ArrayList<DependentResource>();
    int index = 0;
    for( int i = 0; i < resources.length; i++ ) {
      DependentResource resource = resources[ i ];
      if( resource != null ) {
        resource.dependencies.removeAll( sortedResourceIds );
        boolean checkDeferredResources = false;
        if( resource.dependencies.isEmpty() ) {
          result[ index++ ] = resource;
          sortedResourceIds.add( resource.id );
          checkDeferredResources = true;
        } else {
          deferredResources.add( resource );
        }
        while( checkDeferredResources ) {
          checkDeferredResources = false;
          Iterator<DependentResource> iterator = deferredResources.iterator();
          while( iterator.hasNext() ) {
            DependentResource deferredResource = iterator.next();
            deferredResource.dependencies.removeAll( sortedResourceIds );
            if( deferredResource.dependencies.isEmpty() ) {
              result[ index++ ] = deferredResource;
              sortedResourceIds.add( deferredResource.id );
              iterator.remove();
              checkDeferredResources = true;
            }
          }
        }
      }
    }
    if( deferredResources.size() != 0 ) {
      String pluginId = WorkbenchPlugin.getDefault().getBundle().getSymbolicName();
      String message = "Dependencies could not be resolved for " + deferredResources;
      WorkbenchPlugin.getDefault().getLog().log( new Status( IStatus.ERROR, pluginId, message ) );
    }
    return result;
  }

  private static List<IResource> createResourceList( DependentResource[] sortedResources ) {
    List<IResource> result = new ArrayList<IResource>();
    for( DependentResource dependentResource : sortedResources ) {
      result.add( dependentResource.resource );
    }
    return result;
  }

  private static final class DependentResource {

    public final IResource resource;
    public final String id;
    public final List<String> dependencies;

    public DependentResource( IResource resource, String id, List<String> dependencies ) {
      this.resource = resource;
      this.id = id;
      this.dependencies = dependencies;
    }

    @Override
    public String toString() {
      return id != null ? id : resource.getClass().getName();
    }

  }

}
