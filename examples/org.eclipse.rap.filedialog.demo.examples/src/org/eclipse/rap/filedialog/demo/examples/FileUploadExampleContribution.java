/*******************************************************************************
 * Copyright (c) 2011, 2015 EclipseSource and others.
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
package org.eclipse.rap.filedialog.demo.examples;

import org.eclipse.rap.examples.IExampleContribution;
import org.eclipse.rap.examples.IExamplePage;


final class FileUploadExampleContribution implements IExampleContribution {

  @Override
  public String getId() {
    return "file-upload";
  }

  @Override
  public String getTitle() {
    return "File Upload";
  }

  @Override
  public IExamplePage createPage() {
    return new FileUploadExamplePage();
  }

}
