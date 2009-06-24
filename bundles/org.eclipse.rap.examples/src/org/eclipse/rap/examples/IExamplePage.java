/*******************************************************************************
 * Copyright (c) 2008, 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples;

import org.eclipse.swt.widgets.Composite;


public interface IExamplePage {

  /**
   * Creates the example page.
   * 
   * @param parent
   */
  public void createControl( Composite parent );
}
