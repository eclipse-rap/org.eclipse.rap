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

import org.eclipse.rwt.internal.engine.ApplicationContext;
import org.eclipse.rwt.internal.engine.Configurable;
import org.eclipse.rwt.internal.util.ClassInstantiationException;
import org.eclipse.rwt.internal.util.ClassUtil;
import org.eclipse.rwt.service.ISettingStoreFactory;
import org.eclipse.rwt.service.RWTFileSettingStoreFactory;


public class SettingStoreManagerConfigurable implements Configurable {
  public static final String SETTING_STORE_FACTORY_PARAM = "org.eclipse.rwt.settingStoreFactory";
  
  private final ServletContext servletContext;

  public SettingStoreManagerConfigurable( ServletContext servletContext ) {
    this.servletContext = servletContext;
  }

  public void configure( ApplicationContext context ) {
    ISettingStoreFactory factory = createSettingStoreFactory();
    context.getSettingStoreManager().register( factory );
  }

  public void reset( ApplicationContext context ) {
    context.getSettingStoreManager().deregisterFactory();
  }

  String getFactoryName() {
    String result = RWTFileSettingStoreFactory.class.getName();
    String factoryName = servletContext.getInitParameter( SETTING_STORE_FACTORY_PARAM );
    if( factoryName != null ) {
      result = factoryName;
    }
    return result;
  }

  private ISettingStoreFactory createSettingStoreFactory() {
    ISettingStoreFactory result;
    String factoryName = getFactoryName();
    ClassLoader classLoader = getClass().getClassLoader();
    try {
      result = ( ISettingStoreFactory )ClassUtil.newInstance( classLoader, factoryName ); 
    } catch( ClassInstantiationException cie ) {
      String pattern = "Unable to create a setting store factory instance of ''{0}''.";
      String msg = MessageFormat.format( pattern, new Object[] { factoryName } );
      throw new IllegalArgumentException( msg );
    }
    return result;
  }
}