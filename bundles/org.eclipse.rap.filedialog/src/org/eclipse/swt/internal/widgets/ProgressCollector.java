/*******************************************************************************
 * Copyright (c) 2013, 2015 EclipseSource and others.
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
package org.eclipse.swt.internal.widgets;

import static org.eclipse.swt.internal.widgets.LayoutUtil.createGridLayout;
import static org.eclipse.swt.internal.widgets.LayoutUtil.createHorizontalFillData;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;


public class ProgressCollector extends Composite {

  private ProgressBar progressBar;
  private final List<String> completedFiles;
  private final List<Exception> uploadExceptions;

  public ProgressCollector( Composite parent ) {
    super( parent, SWT.NONE );
    setLayout( createGridLayout( 2, 0, 5 ) );
    createChildren();
    completedFiles = new ArrayList<>();
    uploadExceptions = new ArrayList<>();
  }

  public String[] getCompletedFileNames() {
    return completedFiles.toArray( new String[ 0 ] );
  }

  public List<Exception> getUploadExceptions() {
    return new ArrayList<>(uploadExceptions);
  }

  private void createChildren() {
    progressBar = new ProgressBar( this, SWT.HORIZONTAL );
    progressBar.setLayoutData( createHorizontalFillData() );
  }

  void updateProgress( int percent ) {
    if( !isDisposed() ) {
      progressBar.setSelection( percent );
      progressBar.setToolTipText( percent + "%" );
    }
  }

  void updateCompletedFiles( List<String> fileNames ) {
    if( !isDisposed() ) {
      completedFiles.addAll( fileNames );
    }
  }

  void addException( Exception exception ) {
    if( !isDisposed() ) {
      uploadExceptions.add( exception );
    }
  }

  void resetToolTip() {
    if( !isDisposed() ) {
      progressBar.setToolTipText( null );
    }
  }

}
