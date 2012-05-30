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
package org.eclipse.rap.rwt.osgi;


/**
 * A reference to an application started by the ApplicationLauncher.
 *
 * @since 1.5
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ApplicationReference {

  /**
   * Stops the running application. If the application has been stopped already, this method does
   * nothing.
   */
  void stopApplication();

}
