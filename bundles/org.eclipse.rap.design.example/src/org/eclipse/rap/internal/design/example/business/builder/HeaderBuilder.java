/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.internal.design.example.business.builder;

import org.eclipse.rap.internal.design.example.business.layoutsets.HeaderInitializer;
import org.eclipse.rap.ui.interactiondesign.layout.ElementBuilder;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;


public class HeaderBuilder extends ElementBuilder {

  private Image left;
  private Image leftBg;
  private Image logo;
  private Image right;
  private Image rightBg;
  private Image wave;
  private FormData fdLogo;
  private Control toolBar;
  private Composite leftArea;
  private Composite waveArea;

  public HeaderBuilder( Composite parent, String layoutSetId ) {
    super( parent, layoutSetId );
    initLayoutData();
  }

  private void initLayoutData() {
    LayoutSet set = getLayoutSet();
    // images
    left = createImage( set.getImagePath( HeaderInitializer.LEFT ) );
    leftBg = createImage( set.getImagePath( HeaderInitializer.LEFT_BG ) );
    logo = createImage( set.getImagePath( HeaderInitializer.LOGO ) );
    right = createImage( set.getImagePath( HeaderInitializer.RIGHT ) );
    rightBg = createImage( set.getImagePath( HeaderInitializer.RIGHT_BG ) );
    wave = createImage( set.getImagePath( HeaderInitializer.WAVE ) );
    // positions
    fdLogo = set.getPosition( HeaderInitializer.LOGO_POSITION );
  }

  public void addControl( Control control, Object layoutData ) {
    toolBar = control;
    toolBar.setLayoutData( layoutData );
  }

  public void addControl( Control control, String positionId ) {
  }

  public void addImage( Image image, Object layoutData ) {
  }

  public void addImage( Image image, String positionId ) {
  }

  public void build() {
    getParent().setLayout( new FormLayout() );
    getParent().setBackgroundMode( SWT.INHERIT_FORCE );
    
    // left border
    Label leftLabel = new Label( getParent(), SWT.NONE );
    leftLabel.setImage( left );
    FormData fdLeftLabel = new FormData();
    leftLabel.setLayoutData( fdLeftLabel );
    fdLeftLabel.left = new FormAttachment( 0, 0 );
    fdLeftLabel.top = new FormAttachment( 0, 0 );
    fdLeftLabel.width = left.getBounds().width;
    fdLeftLabel.height = left.getBounds().height;
    
    leftArea = new Composite( getParent(), SWT.NONE );
    leftArea.setLayout( new FormLayout() );
    leftArea.setBackgroundImage( leftBg );
    FormData fdLeftArea = new FormData();
    leftArea.setLayoutData( fdLeftArea );
    fdLeftArea.left = new FormAttachment( leftLabel );
    fdLeftArea.top = new FormAttachment( 0, 0 );
    fdLeftArea.height = leftBg.getBounds().height;
    
    waveArea = new Composite( getParent(), SWT.NONE );
    waveArea.setLayout( new FormLayout() );
    waveArea.setBackgroundImage( wave );
    FormData fdWaveArea = new FormData();
    waveArea.setLayoutData( fdWaveArea );
    fdWaveArea.left = new FormAttachment( leftArea );
    fdWaveArea.top = new FormAttachment( 0, 0 );
    fdWaveArea.width = wave.getBounds().width;
    fdWaveArea.height = wave.getBounds().height;
    
    
    Label rightLabel = new Label( getParent(), SWT.NONE );
    rightLabel.setImage( right );
    FormData fdRightLabel = new FormData();
    rightLabel.setLayoutData( fdRightLabel );
    fdRightLabel.right = new FormAttachment( 100, -1 );
    fdRightLabel.top = new FormAttachment( 0, 0 );
    fdRightLabel.width = right.getBounds().width;
    fdRightLabel.height = right.getBounds().height;
    
    Composite logoArea = new Composite( getParent(), SWT.NONE );
    logoArea.setLayout( new FormLayout() );
    logoArea.setBackgroundImage( rightBg );
    FormData fdLogoArea = new FormData();
    logoArea.setLayoutData( fdLogoArea );
    fdLogoArea.right = new FormAttachment( rightLabel );
    fdLogoArea.top = new FormAttachment( 0, 0 );
    fdLogoArea.height = rightBg.getBounds().height;
    fdLogoArea.width = logo.getBounds().width + 220;
    
    Label logoLabel = new Label( logoArea, SWT.NONE );
    logoLabel.setImage( logo ); 
    logoLabel.setLayoutData( fdLogo );
    fdLogo.height = logo.getBounds().height;
    fdLogo.width = logo.getBounds().width;
    fdLeftArea.right = new FormAttachment( logoArea );

  }

  public void dispose() {
  }

  public Control getControl() {
    return leftArea;
  }

  public Point getSize() {
    return leftArea.getSize();
  }
  
  public Object getAdapter( Class adapter ) {
    Object result = null;
    if( adapter == Composite.class ) {
      result = waveArea;
    }
    return result;
  }
}
