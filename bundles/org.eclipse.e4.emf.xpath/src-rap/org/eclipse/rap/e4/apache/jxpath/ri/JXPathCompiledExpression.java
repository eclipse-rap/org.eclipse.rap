/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.rap.e4.apache.jxpath.ri;

import java.util.Iterator;

import org.eclipse.rap.e4.apache.jxpath.CompiledExpression;
import org.eclipse.rap.e4.apache.jxpath.JXPathContext;
import org.eclipse.rap.e4.apache.jxpath.Pointer;
import org.eclipse.rap.e4.apache.jxpath.ri.compiler.Expression;

/**
 * RI of CompiledExpression.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 652845 $ $Date: 2008-05-02 12:46:46 -0500 (Fri, 02 May 2008) $
 */
public class JXPathCompiledExpression implements CompiledExpression {

    private String xpath;
    private Expression expression;

    /**
     * Create a new JXPathCompiledExpression.
     * @param xpath source
     * @param expression compiled
     */
    public JXPathCompiledExpression(String xpath, Expression expression) {
        this.xpath = xpath;
        this.expression = expression;
    }

    /**
     * Get the source expression.
     * @return String
     */
    protected String getXPath() {
        return xpath;
    }

    /**
     * Get the compiled expression.
     * @return Expression
     */
    protected Expression getExpression() {
        return expression;
    }

    public String toString() {
        return xpath;
    }

    public Object getValue(JXPathContext context) {
        return ((JXPathContextReferenceImpl) context).
                    getValue(xpath, expression);
    }

    public Object getValue(JXPathContext context, Class requiredType) {
        return ((JXPathContextReferenceImpl) context).
                    getValue(xpath, expression, requiredType);
    }

    public void setValue(JXPathContext context, Object value) {
        ((JXPathContextReferenceImpl) context).
                    setValue(xpath, expression, value);
    }

    public Pointer createPath(JXPathContext context) {
        return ((JXPathContextReferenceImpl) context).
                    createPath(xpath, expression);
    }

    public Pointer createPathAndSetValue(JXPathContext context, Object value) {
        return ((JXPathContextReferenceImpl) context).
                    createPathAndSetValue(xpath, expression, value);
    }

    public Iterator iterate(JXPathContext context) {
        return ((JXPathContextReferenceImpl) context).
                    iterate(xpath, expression);
    }

    public Pointer getPointer(JXPathContext context, String xpath) {
        return ((JXPathContextReferenceImpl) context).
                    getPointer(xpath, expression);
    }

    public Iterator iteratePointers(JXPathContext context) {
        return ((JXPathContextReferenceImpl) context).
                    iteratePointers(xpath, expression);
    }

    public void removePath(JXPathContext context) {
        ((JXPathContextReferenceImpl) context).removePath(xpath, expression);
    }

    public void removeAll(JXPathContext context) {
        ((JXPathContextReferenceImpl) context).removeAll(xpath, expression);
    }
}
