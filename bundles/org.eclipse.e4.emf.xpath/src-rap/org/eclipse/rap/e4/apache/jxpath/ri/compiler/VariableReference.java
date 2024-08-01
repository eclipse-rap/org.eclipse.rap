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
package org.eclipse.rap.e4.apache.jxpath.ri.compiler;

import org.eclipse.rap.e4.apache.jxpath.ri.EvalContext;
import org.eclipse.rap.e4.apache.jxpath.ri.QName;

/**
 * An element of the compile tree holding a variable reference.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 652845 $ $Date: 2008-05-02 12:46:46 -0500 (Fri, 02 May 2008) $
 */
public class VariableReference extends Expression {

    private QName varName;

    /**
     * Create a new VariableReference.
     * @param varName variable name
     */
    public VariableReference(QName varName) {
        this.varName = varName;
    }

    /**
     * Get the variable name.
     * @return QName
     */
    public QName getVariableName() {
        return varName;
    }

    public String toString() {
        return "$" + varName;
    }

    public boolean isContextDependent() {
        return false;
    }

    public boolean computeContextDependent() {
        return false;
    }

    public Object compute(EvalContext context) {
        return computeValue(context);
    }

    /**
     * Returns the value of the variable.
     * @param context EvalContext against which to compute the variable's value.
     * @return Object
     */
    public Object computeValue(EvalContext context) {
        return context.getRootContext().getVariableContext(varName);
    }
}
