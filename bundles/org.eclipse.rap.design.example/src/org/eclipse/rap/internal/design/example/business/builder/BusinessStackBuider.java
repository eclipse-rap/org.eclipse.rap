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

import org.eclipse.rap.internal.design.example.business.layoutsets.StackInitializer;
import org.eclipse.rap.internal.design.example.business.stacks.ViewStackPresentation;
import org.eclipse.rap.ui.interactiondesign.layout.ElementBuilder;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;


public class BusinessStackBuider extends ElementBuilder {


  private Image tabInactiveBgActive;
  private Composite content;
  private Image borderBottom;
  private Image borderTop;
  private Image borderLeft;
  private Image borderRight;
  private Composite tabBar;

  public BusinessStackBuider( Composite parent, String layoutSetId ) {
    super( parent, layoutSetId );
    init();
  }

  private void init() {
    tabInactiveBgActive 
      = createImageById( StackInitializer.TAB_INACTIVE_BG_ACTIVE );
    borderBottom = createImageById( StackInitializer.BORDER_BOTTOM );
    borderTop = createImageById( StackInitializer.BORDER_TOP );
    borderLeft = createImageById( StackInitializer.BORDER_LEFT );
    borderRight = createImageById( StackInitializer.BORDER_RIGHT );
    
  }
  
  private Image createImageById( final String id ) {
    LayoutSet set = getLayoutSet();
    return createImage( set.getImagePath( id ) );
  }

  public void addControl( Control control, Object layoutData ) {
  }

  public void addControl( Control control, String positionId ) {
  }

  public void addImage( Image image, Object layoutData ) {
  }

  public void addImage( Image image, String positionId ) {
  }

  public void build() {
    getParent().setLayout( new FillLayout() );    
    Composite stack = createFrame();
    stack.setLayout( new FormLayout() );
    
    tabBar = new Composite( stack, SWT.NONE );
    tabBar.setLayout( new FormLayout() );
    tabBar.setBackgroundImage( tabInactiveBgActive );
    FormData fdTabBar = new FormData();
    tabBar.setLayoutData( fdTabBar );
    fdTabBar.top = new FormAttachment( 0 );
    fdTabBar.left = new FormAttachment( 0 );
    fdTabBar.right = new FormAttachment( 100 );
    fdTabBar.height = tabInactiveBgActive.getBounds().height;
    
    content = new Composite( stack, SWT.NONE );
    FormData fdContent = new FormData();
    content.setLayoutData( fdContent );
    fdContent.top = new FormAttachment( tabBar );
    fdContent.left = new FormAttachment( 0 );
    fdContent.right = new FormAttachment( 100 );
    fdContent.bottom = new FormAttachment( 100 );

    
  }

  private Composite createFrame() {
    Composite frameComp = new Composite( getParent(), SWT.NONE );
    frameComp.setData( WidgetUtil.CUSTOM_VARIANT, "compGray" );
    frameComp.setLayout( new FormLayout() );
    
    Label left = new Label( frameComp, SWT.NONE );
    left.setData( WidgetUtil.CUSTOM_VARIANT, "stackBorder" );
    left.setBackgroundImage( borderLeft );
    FormData fdLeft = new FormData();
    left.setLayoutData( fdLeft );
    fdLeft.top = new FormAttachment( 0, borderTop.getBounds().height - 1 );
    fdLeft.bottom 
      = new FormAttachment( 100, - borderBottom.getBounds().height + 1 );
    fdLeft.left = new FormAttachment( 0 );
    fdLeft.width = borderLeft.getBounds().width;
    
    Label right = new Label( frameComp, SWT.NONE );
    right.setData( WidgetUtil.CUSTOM_VARIANT, "stackBorder" );
    right.setBackgroundImage( borderRight );
    FormData fdRight = new FormData();
    right.setLayoutData( fdRight );
    fdRight.top = new FormAttachment( 0, borderTop.getBounds().height - 1 );
    fdRight.bottom 
      = new FormAttachment( 100, - borderBottom.getBounds().height + 1 );
    fdRight.right = new FormAttachment( 100 );
    fdRight.width = borderRight.getBounds().width;
    
    Label top = new Label( frameComp, SWT.NONE );
    top.setData( WidgetUtil.CUSTOM_VARIANT, "stackBorder" );
    top.setBackgroundImage( borderTop );
    FormData fdTop = new FormData();
    top.setLayoutData( fdTop );
    fdTop.top = new FormAttachment( 0 );
    fdTop.left = new FormAttachment( left );
    fdTop.right = new FormAttachment( right );
    fdTop.height = borderTop.getBounds().height;
    
    Label bottom = new Label( frameComp, SWT.NONE );
    bottom.setData( WidgetUtil.CUSTOM_VARIANT, "stackBorder" );
    bottom.setBackgroundImage( borderBottom );
    FormData fdBottom = new FormData();
    bottom.setLayoutData( fdBottom );
    fdBottom.bottom = new FormAttachment( 100 );
    fdBottom.left = new FormAttachment( left );
    fdBottom.right = new FormAttachment( right );
    fdBottom.height = borderBottom.getBounds().height;
    
    Composite result = new Composite( frameComp, SWT.NONE );
    result.setData( WidgetUtil.CUSTOM_VARIANT, "compGray" );
    FormData fdResult = new FormData();
    result.setLayoutData( fdResult );
    fdResult.top = new FormAttachment( top );
    fdResult.left = new FormAttachment( left );
    fdResult.right = new FormAttachment( right );
    fdResult.bottom = new FormAttachment( bottom );

    return result;
  }

  public void dispose() {
  }

  public Control getControl() {
    return content;
  }

  public Point getSize() {
    Point result = null;
    if( content != null ) {
      result = content.getSize();
    }
    return result;
  }
  
  public Object getAdapter( Class adapter ) {
    Object result = null;
    if( adapter == ViewStackPresentation.class ) {
      result = tabBar;
    }
    return result;
  }
}
