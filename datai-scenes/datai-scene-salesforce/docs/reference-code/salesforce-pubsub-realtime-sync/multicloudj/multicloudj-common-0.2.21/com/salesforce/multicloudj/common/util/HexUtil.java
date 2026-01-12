package com.salesforce.multicloudj.common.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class HexUtil {
  public static byte[] convertToBytes(String hash) {
    if (hash == null)
      return new byte[0]; 
    try {
      return Hex.decodeHex(hash);
    } catch (DecoderException e) {
      return new byte[0];
    } 
  }
}
