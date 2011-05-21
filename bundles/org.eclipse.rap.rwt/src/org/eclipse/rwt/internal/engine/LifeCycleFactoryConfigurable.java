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

import org.eclipse.rwt.internal.ConfigurationReader;
import org.eclipse.rwt.internal.lifecycle.LifeCycleFactory;


public class LifeCycleFactoryConfigurable implements Configurable {

  public void configure( ApplicationContext context ) {
    LifeCycleFactory lifeCycleFactory = context.getLifeCycleFactory();
    ConfigurationReader configurationReader = context.getConfigurationReader();
    lifeCycleFactory.setConfigurationReader( configurationReader );
  }

  public void reset( ApplicationContext context ) {
    context.getLifeCycleFactory().setConfigurationReader( null );
  }
}