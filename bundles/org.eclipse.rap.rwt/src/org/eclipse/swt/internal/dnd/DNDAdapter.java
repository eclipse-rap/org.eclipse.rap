/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.dnd;

import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.service.IServiceStore;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Control;


public final class DNDAdapter implements IDNDAdapter {

  private static final String CANCEL = DNDAdapter.class.getName() + "#cancel";
  private static final String DETAIL_CHANGED_VALUE
    = DNDAdapter.class.getName() + "#detailChangedValue";
  private static final String DETAIL_CHANGED_CONTROL
    = DNDAdapter.class.getName() + "#detailChangedControl";
  private static final String FEEDBACK_CHANGED_VALUE
    = DNDAdapter.class.getName() + "#feedbackChangedValue";
  private static final String FEEDBACK_CHANGED_CONTROL
    = DNDAdapter.class.getName() + "#feedbackChangedControl";
  private static final String DATATYPE_CHANGED_VALUE
    = DNDAdapter.class.getName() + "#dataTypeChangedValue";
  private static final String DATATYPE_CHANGED_CONTROL
    = DNDAdapter.class.getName() + "#dataTypeChangedControl";

  public void cancel() {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    serviceStore.setAttribute( CANCEL, Boolean.TRUE );
    cancelDetailChanged();
    cancelFeedbackChanged();
    cancelDataTypeChanged();
  }

  public boolean isCanceled() {
    return ContextProvider.getServiceStore().getAttribute( CANCEL ) != null;
  }

  public void setDetailChanged( Control control, int detail ) {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    serviceStore.setAttribute( DETAIL_CHANGED_VALUE, new Integer( detail ) );
    serviceStore.setAttribute( DETAIL_CHANGED_CONTROL, control );
  }

  public void cancelDetailChanged() {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    serviceStore.setAttribute( DETAIL_CHANGED_VALUE, null );
    serviceStore.setAttribute( DETAIL_CHANGED_CONTROL, null );
  }

  public boolean hasDetailChanged() {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    Object value = serviceStore.getAttribute( DETAIL_CHANGED_VALUE );
    return value != null;
  }

  public int getDetailChangedValue() {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    Integer value = ( Integer )serviceStore.getAttribute( DETAIL_CHANGED_VALUE );
    return value.intValue();
  }

  public Control getDetailChangedControl() {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    return ( Control )serviceStore.getAttribute( DETAIL_CHANGED_CONTROL );
  }

  public void setFeedbackChanged( Control control, int feedback ) {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    serviceStore.setAttribute( FEEDBACK_CHANGED_VALUE, new Integer( feedback ) );
    serviceStore.setAttribute( FEEDBACK_CHANGED_CONTROL, control );
  }

  public void cancelFeedbackChanged() {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    serviceStore.setAttribute( FEEDBACK_CHANGED_VALUE, null );
    serviceStore.setAttribute( FEEDBACK_CHANGED_CONTROL, null );
  }

  public boolean hasFeedbackChanged() {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    Object value = serviceStore.getAttribute( FEEDBACK_CHANGED_VALUE );
    return value != null;
  }

  public int getFeedbackChangedValue() {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    Integer value = ( Integer )serviceStore.getAttribute( FEEDBACK_CHANGED_VALUE );
    return value.intValue();
  }

  public Control getFeedbackChangedControl() {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    return ( Control )serviceStore.getAttribute( FEEDBACK_CHANGED_CONTROL );
  }

  public void setDataTypeChanged( Control control, TransferData dataType ) {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    serviceStore.setAttribute( DATATYPE_CHANGED_VALUE, dataType );
    serviceStore.setAttribute( DATATYPE_CHANGED_CONTROL, control );
  }

  public void cancelDataTypeChanged() {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    serviceStore.setAttribute( DATATYPE_CHANGED_VALUE, null );
    serviceStore.setAttribute( DATATYPE_CHANGED_CONTROL, null );
  }

  public boolean hasDataTypeChanged() {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    Object value = serviceStore.getAttribute( DATATYPE_CHANGED_VALUE );
    return value != null;
  }

  public TransferData getDataTypeChangedValue() {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    return ( TransferData )serviceStore.getAttribute( DATATYPE_CHANGED_VALUE );
  }

  public Control getDataTypeChangedControl() {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    return ( Control )serviceStore.getAttribute( DATATYPE_CHANGED_CONTROL );
  }
}