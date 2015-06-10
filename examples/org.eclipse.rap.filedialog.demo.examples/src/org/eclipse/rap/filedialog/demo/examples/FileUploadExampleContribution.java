/*******************************************************************************
 * Copyright (c) 2011, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
