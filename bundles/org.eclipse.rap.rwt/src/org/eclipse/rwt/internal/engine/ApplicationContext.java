/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import java.text.MessageFormat;
import java.util.*;

import org.eclipse.rwt.internal.AdapterManager;
import org.eclipse.rwt.internal.ConfigurationReader;
import org.eclipse.rwt.internal.branding.BrandingManager;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.resources.*;
import org.eclipse.rwt.internal.service.ServiceManager;
import org.eclipse.rwt.internal.service.SettingStoreManager;
import org.eclipse.rwt.internal.theme.ThemeManagerHolder;
import org.eclipse.rwt.internal.util.ClassUtil;
import org.eclipse.rwt.internal.util.ParamCheck;


public class ApplicationContext {

  private final Map instances;
  private final Set configurables;
  private boolean activated;
  
  public static interface InstanceTypeFactory {
    Object createInstance();
    Class getInstanceType();
  }

  public ApplicationContext() {
    this( new Class[ 0 ] );
  }

  public ApplicationContext( Class[] instanceTypes ) {
    instances = new HashMap();
    configurables = new HashSet();
    createInstances( instanceTypes );
  }

  Object getInstance( Class instanceType ) {
    checkIsNotActivated();
    ParamCheck.notNull( instanceType, "instanceType" );
    Object result = findInstance( instanceType );
    // do param check here to avoid duplicate map access
    checkRegistered( instanceType, result );
    return result;
  }

  public boolean isActivated() {
    return activated;
  }
  
  public void activate() {
    checkIsActivated();
    activated = true;
    notifyConfigurablesAboutActivation();
    activateActivatables();
  }

  public void deactivate() {
    checkIsNotActivated();
    deactivateActivatables();
    notifyConfigurablesAboutDeactivation();
    activated = false;
  }

  public void addConfigurable( Configurable configurable ) {
    checkIsActivated();
    ParamCheck.notNull( configurable, "configurable" );
    configurables.add( configurable );
  }
  
  public void removeConfigurable( Configurable configurable ) {
    checkIsActivated();
    ParamCheck.notNull( configurable, "configurable" );
    configurables.remove( configurable );
  }
  
  public ConfigurationReader getConfigurationReader() {
    return ( ConfigurationReader )getInstance( ConfigurationReader.class );
  }
  
  public ResourceManagerProvider getResourceManagerProvider() {
    return ( ResourceManagerProvider )getInstance( ResourceManagerProvider.class );
  }
  
  public EntryPointManager getEntryPointManager() {
    return ( EntryPointManager )getInstance( EntryPointManager.class );
  }

  public BrandingManager getBrandingManager() {
    return ( BrandingManager )getInstance( BrandingManager.class );
  }
  
  public SettingStoreManager getSettingStoreManager() {
    return ( SettingStoreManager )getInstance( SettingStoreManager.class );
  }

  public PhaseListenerRegistry getPhaseListenerRegistry() {
    return ( PhaseListenerRegistry )getInstance( PhaseListenerRegistry.class );
  }
  

  public AdapterManager getAdapterManager() {
    return ( AdapterManager )getInstance( AdapterManager.class );
  }
  
  public ResourceRegistry getResourceRegistry() {
    return ( ResourceRegistry )getInstance( ResourceRegistry.class );
  }
  
  public ServiceManager getServiceManager() {
    return ( ServiceManager )getInstance( ServiceManager.class );
  }

  public JSLibraryConcatenator getJSLibraryConcatenator() {
    return ( JSLibraryConcatenator )getInstance( JSLibraryConcatenator.class );
  }

  public ThemeManagerHolder getThemeManager() {
    return ( ThemeManagerHolder )getInstance( ThemeManagerHolder.class );
  }

  public LifeCycleFactory getLifeCycleFactory() {
    return ( LifeCycleFactory )getInstance( LifeCycleFactory.class );
  }

  private void createInstances( Class[] instanceTypes ) {
    for( int i = 0; i < instanceTypes.length; i++ ) {
      Object instance = ClassUtil.newInstance( instanceTypes[ i ] );
      bufferInstance( instanceTypes[ i ], instance );
    }
  }

  private Object findInstance( Class instanceType ) {
    return instances.get( instanceType );
  }

  private void bufferInstance( Class instanceType, Object instance ) {
    Object toRegister = createInstanceFromFactory( instance );
    Class registrationType = getTypeFromFactory( instanceType, instance );
    checkInstanceOf( toRegister, registrationType );
    checkAlreadyRegistered( registrationType );
    instances.put( registrationType, toRegister );
  }

  private void checkAlreadyRegistered( Class registrationType ) {
    if( instances.containsKey( registrationType ) ) {
      String pattern = "The instance type ''{0}'' has already been registered.";
      Object[] arguments = new Object[] { registrationType.getName() };
      throwIllegalArgumentException( pattern, arguments );
    }
  }

  private static Object createInstanceFromFactory( Object instance ) {
    Object result = instance;
    if( instance instanceof InstanceTypeFactory ) {
      InstanceTypeFactory factory = ( InstanceTypeFactory )instance;
      result = factory.createInstance();
    }
    return result;
  }

  private static Class getTypeFromFactory( Class instanceType, Object instance ) {
    Class result = instanceType;
    if( instance instanceof InstanceTypeFactory ) {
      InstanceTypeFactory factory = ( InstanceTypeFactory )instance;
      result = factory.getInstanceType();
    }
    return result;
  }

  private static void checkRegistered( Class instanceType, Object instance ) {
    if( instance == null ) {
      String pattern = "Unregistered instance type ''{0}''";
      Object[] arguments = new Object[] { instanceType };
      throwIllegalArgumentException( pattern, arguments );
    }
  }

  private static void checkInstanceOf( Object instance, Class type ) {
    if( !type.isInstance( instance ) ) {
      String pattern = "Instance to register does not match declared type ''{0}''.";
      Object[] arguments = new Object[] { type.getName() };
      throwIllegalArgumentException( pattern, arguments );
    }
  }
  
  private void checkIsNotActivated() {
    if( !activated ) {
      throw new IllegalStateException( "The ApplicationContext has not been activated." );
    }
  }
  
  private void checkIsActivated() {
    if( activated ) {
      throw new IllegalStateException( "The ApplicationContext has already been activated." );
    }
  }

  private static void throwIllegalArgumentException( String pattern, Object[] arx ) {
    String msg = MessageFormat.format( pattern, arx );
    throw new IllegalArgumentException( msg );
  }
  
  private void notifyConfigurablesAboutActivation() {
    Iterator iterator = configurables.iterator();
    while( iterator.hasNext() ) {
      Configurable configurable = ( Configurable )iterator.next();
      configurable.configure( this );
    }
  }
  
  private void notifyConfigurablesAboutDeactivation() {
    Iterator iterator = configurables.iterator();
    while( iterator.hasNext() ) {
      Configurable configurable = ( Configurable )iterator.next();
      configurable.reset( this );
    }
  }
  
  private void activateActivatables() {
    ApplicationContextUtil.runWithInstance( this, new Runnable() {
      public void run() {
        doActivateActivatables();
      }
    } );
  }
  
  private void doActivateActivatables() {
    Iterator iterator = instances.values().iterator();
    while( iterator.hasNext() ) {
      Object instance = iterator.next();
      if( instance instanceof Activatable )  {
        ( ( Activatable )instance ).activate();
      }
    }
  }
  
  private void deactivateActivatables() {
    ApplicationContextUtil.runWithInstance( this, new Runnable() {
      public void run() {
        doDeactivateActivatables();
      }
    } );
  }

  private void doDeactivateActivatables() {
    Iterator iterator = instances.values().iterator();
    while( iterator.hasNext() ) {
      Object instance = iterator.next();
      if( instance instanceof Activatable )  {
        ( ( Activatable )instance ).deactivate();
      }
    }
  }
}