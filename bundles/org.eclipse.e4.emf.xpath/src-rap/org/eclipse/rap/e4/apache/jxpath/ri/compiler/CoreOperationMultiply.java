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
import org.eclipse.rap.e4.apache.jxpath.ri.InfoSetUtil;

/**
 * Implementation of {@link Expression} for the operation "*".
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 652845 $ $Date: 2008-05-02 12:46:46 -0500 (Fri, 02 May 2008) $
 */
public class CoreOperationMultiply extends CoreOperation {

    /**
     * Create a new CoreOperationMultiply.
     * @param arg1 factor 1
     * @param arg2 factor 2
     */
    public CoreOperationMultiply(Expression arg1, Expression arg2) {
        super(new Expression[] { arg1, arg2 });
    }

    public Object computeValue(EvalContext context) {
        double l = InfoSetUtil.doubleValue(args[0].computeValue(context));
        double r = InfoSetUtil.doubleValue(args[1].computeValue(context));
        return new Double(l * r);
    }

    protected int getPrecedence() {
        return MULTIPLY_PRECEDENCE;
    }

    protected boolean isSymmetric() {
        return true;
    }

    public String getSymbol() {
        return "*";
    }
}
