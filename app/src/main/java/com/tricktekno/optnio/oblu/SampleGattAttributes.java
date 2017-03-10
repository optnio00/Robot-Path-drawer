/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tricktekno.optnio.oblu;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    /**
     * UART characteristics
     */
    public static final String SERVER_UART = "0003cdd0-0000-1000-8000-00805f9b0131";//sevice
    public static final String SERVER_UART_tx = "0003cdd1-0000-1000-8000-00805f9b0131";//characterristic
    public static final String SERVER_UART_rx = "0003cdd2-0000-1000-8000-00805f9b0131";//characterristic static



    static {
        // Sample Services.
        attributes.put(SERVER_UART, "UART Service");
        attributes.put(SERVER_UART_rx, "UART rx Service");
        attributes.put(SERVER_UART_tx, "UART tx Service");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
