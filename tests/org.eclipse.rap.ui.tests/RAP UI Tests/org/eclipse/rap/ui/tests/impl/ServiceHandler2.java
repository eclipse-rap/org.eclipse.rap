package org.eclipse.rap.ui.tests.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.service.IServiceHandler;
import org.eclipse.rap.ui.tests.ServiceHandlerExtensionTest;


public class ServiceHandler2 implements IServiceHandler {

  public void service( HttpServletRequest request, HttpServletResponse response ) {
    ServiceHandlerExtensionTest.log = this.getClass().getName();
  }
}
