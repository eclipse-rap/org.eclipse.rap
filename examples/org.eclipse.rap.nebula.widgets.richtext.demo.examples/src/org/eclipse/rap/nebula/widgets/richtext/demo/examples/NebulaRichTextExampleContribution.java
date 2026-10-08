/*******************************************************************************
 * Copyright (c) 2016 EclipseSource and others.
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
package org.eclipse.rap.nebula.widgets.richtext.demo.examples;

import org.eclipse.rap.examples.IExampleContribution;
import org.eclipse.rap.examples.IExamplePage;


public class NebulaRichTextExampleContribution implements IExampleContribution {

  @Override
  public String getId() {
    return "richtext";
  }

  @Override
  public String getTitle() {
    return "Rich Text Editor";
  }

  @Override
  public IExamplePage createPage() {
    return new NebulaRichTextExamplePage();
  }

}
