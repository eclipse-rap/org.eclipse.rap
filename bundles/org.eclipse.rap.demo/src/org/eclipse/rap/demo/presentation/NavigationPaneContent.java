// Created on 08.02.2008
package org.eclipse.rap.demo.presentation;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


public abstract class NavigationPaneContent {
  private Control control;
  private Object selector;
  
  public abstract void createControl( Composite parent );
  public abstract String getLabel();

  public ISelectionProvider getSelectionProvider() {
    return null;
  }
  
  public boolean isSelectionProvider() {
    return false;
  }
  
  final void setControl( final Control control ) {
    this.control = control;
  }
  
  final Control getControl() {
    return control;
  }
  
  final Object getSelector() {
    return selector;
  }
  
  final void setSelector( final Object selector ) {
    this.selector = selector;
  }
}
