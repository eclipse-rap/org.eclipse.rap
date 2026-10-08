/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
