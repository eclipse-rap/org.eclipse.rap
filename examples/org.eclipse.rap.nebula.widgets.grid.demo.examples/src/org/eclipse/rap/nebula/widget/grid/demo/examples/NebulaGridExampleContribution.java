/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.nebula.widget.grid.demo.examples;

import org.eclipse.rap.examples.IExampleContribution;
import org.eclipse.rap.examples.IExamplePage;


public class NebulaGridExampleContribution implements IExampleContribution {

  public String getId() {
    return "nebula-grid";
  }

  public String getTitle() {
    return "Nebula Grid";
  }

  public IExamplePage createPage() {
    return new NebulaGridExamplePage();
  }

}
