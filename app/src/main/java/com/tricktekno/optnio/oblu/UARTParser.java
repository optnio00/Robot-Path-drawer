package com.tricktekno.optnio.oblu;
/*
 * Copyright Cypress Semiconductor Corporation, 2014-2015 All rights reserved.
 * 
 * This software, associated documentation and materials ("Software") is
 * owned by Cypress Semiconductor Corporation ("Cypress") and is
 * protected by and subject to worldwide patent protection (UnitedStates and foreign), United States copyright laws and international
 * treaty provisions. Therefore, unless otherwise specified in a separate license agreement between you and Cypress, this Software
 * must be treated like any other copyrighted material. Reproduction,
 * modification, translation, compilation, or representation of this
 * Software in any other form (e.g., paper, magnetic, optical, silicon)
 * is prohibited without Cypress's express written permission.
 * 
 * Disclaimer: THIS SOFTWARE IS PROVIDED AS-IS, WITH NO WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO,
 * NONINFRINGEMENT, IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE. Cypress reserves the right to make changes
 * to the Software without notice. Cypress does not assume any liability
 * arising out of the application or use of Software or any product or
 * circuit described in the Software. Cypress does not authorize its
 * products for use as critical components in any products where a
 * malfunction or failure may reasonably be expected to result in
 * significant injury or death ("High Risk Product"). By including
 * Cypress's product in a High Risk Product, the manufacturer of such
 * system or application assumes all risk of such use and in doing so
 * indemnifies Cypress against all liability.
 * 
 * Use of this Software may be limited by and subject to the applicable
 * Cypress software license agreement.
 * 
 * 
 */

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;
import android.util.Log;

import java.util.Locale;
import java.util.Objects;

/**
 * Parser class for parsing the data related to UART Profile
 */
public abstract class UARTParser {

    private final static String TAG = UARTParser.class.getSimpleName();
    private static int[] header;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static byte[] getsensorsdata(
            BluetoothGattCharacteristic characteristic) {
       // Logger.e( "BEGIN mConnectedThread swdr");
        int i = 0,counter=0;
        int bytes;
        byte[] buffer = new byte[1024];
        String TXDATA = null;
        buffer = characteristic.getValue();

        //bytes = buffer.length;
        if (buffer != null && buffer.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(buffer.length);
            for (byte byteChar : buffer)
                stringBuilder.append(String.format("%02X ", byteChar));
             TXDATA = String.valueOf(stringBuilder.toString().trim());
        }

        if (Objects.equals("A0 34 00 D4", TXDATA)){//A0 22 00 C2 A0 32 00 D2
           // Logger.e( "swdr "+TXDATA);
            for(int j=0;j<4;j++)
            {
                header[j]=buffer[i++]& 0xFF;
                Log.e(TAG,"ak- " + header[j]);
            }
            return buffer;
        }
        else if (Objects.equals("A0 22 00 C2 A0 32 00 D2", TXDATA)){//A0 22 00 C2 A0 32 00 D2
             Log.e( TAG,"swdr "+TXDATA);
            for(int j=0;j<18;j++)
            {
                buffer[j]= (byte) (header[j++]& 0xFF);
             //   Logger.e("ak- "+ header[j]);
            }
            return buffer;
        }
        else if (Objects.equals("A0 22 00 C2", TXDATA)){//A0 22 00 C2
            Log.e(TAG,"swdr "+TXDATA);
            for(int j=0;j<18;j++)
            {
                buffer[j]= (byte) (header[j++]& 0xFF);
            //    Logger.e("ak- "+ header[j]);
            }
            return buffer;
        }
        return  buffer;
    }

    public static String byte2HexStr(byte[] paramArrayOfByte, int paramInt)
    {
        StringBuilder localStringBuilder1 = new StringBuilder("");
        int i = 0;
        for (;;)
        {
            if (i >= paramInt)
            {
                String str1 = localStringBuilder1.toString().trim();
                Locale localLocale = Locale.US;
                return str1.toUpperCase(localLocale);
            }
            String str2 = Integer.toHexString(paramArrayOfByte[i] & 0xFF);
            if (str2.length() == 1) {
                str2 = "0" + str2;
            }
            StringBuilder localStringBuilder2 = localStringBuilder1.append(str2);
            StringBuilder localStringBuilder3 = localStringBuilder1.append(" ");
            i += 1;
        }
    }
}
