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
package org.eclipse.rap.e4.apache.jxpath.util;

import org.eclipse.rap.e4.apache.jxpath.BasicNodeSet;
import org.eclipse.rap.e4.apache.jxpath.ExtendedKeyManager;
import org.eclipse.rap.e4.apache.jxpath.JXPathContext;
import org.eclipse.rap.e4.apache.jxpath.KeyManager;
import org.eclipse.rap.e4.apache.jxpath.NodeSet;
import org.eclipse.rap.e4.apache.jxpath.Pointer;
import org.eclipse.rap.e4.apache.jxpath.ri.InfoSetUtil;

/**
 * Utility class.
 *
 * @author Matt Benson
 * @since JXPath 1.3
 * @version $Revision: 652845 $ $Date: 2008-05-02 12:46:46 -0500 (Fri, 02 May 2008) $
 */
public class KeyManagerUtils {
    /**
     * Adapt KeyManager to implement ExtendedKeyManager.
     */
    private static class SingleNodeExtendedKeyManager implements
            ExtendedKeyManager {
        private KeyManager delegate;

        /**
         * Create a new SingleNodeExtendedKeyManager.
         * @param delegate KeyManager to wrap
         */
        public SingleNodeExtendedKeyManager(KeyManager delegate) {
            this.delegate = delegate;
        }

        public NodeSet getNodeSetByKey(JXPathContext context, String key,
                Object value) {
            Pointer pointer = delegate.getPointerByKey(context, key, InfoSetUtil.stringValue(value));
            BasicNodeSet result = new BasicNodeSet();
            result.add(pointer);
            return result;
        }

        public Pointer getPointerByKey(JXPathContext context, String keyName,
                String keyValue) {
            return delegate.getPointerByKey(context, keyName, keyValue);
        }
    }

    /**
     * Get an ExtendedKeyManager from the specified KeyManager.
     * @param keyManager to adapt, if necessary
     * @return <code>keyManager</code> if it implements ExtendedKeyManager
     *         or a basic single-result ExtendedKeyManager that delegates to
     *         <code>keyManager</code>.
     */
    public static ExtendedKeyManager getExtendedKeyManager(KeyManager keyManager) {
        return keyManager instanceof ExtendedKeyManager ? (ExtendedKeyManager) keyManager
                : new SingleNodeExtendedKeyManager(keyManager);
    }
}
