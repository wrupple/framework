package com.wrupple.vegetate.shared.services;

import java.util.Date;

import com.wrupple.vegetate.domain.structure.HasTimestamp;

public interface SignatureGenerator extends HasTimestamp{

	/**
	 * 
	 * Uses a private key to digest/encode a message
	 * 
	 * @param message unencoded message
	 * @param salt could just be an incremental count, in adition to the timestamp makes for unique signatures
	 * @return
	 */
	String generateSignature(String message,String salt);

	String getPublicKey();

	/**
	 * @param signature encoded signature
	 * @param message   unencoded message
	 * @param salt  could just be an incremental count, in adition to the timestamp makes for unique signatures
	 * @return
	 */
	boolean doSignatureMatch(String signature, String message,String salt);

	long getStakeHolder();

	String getSerializedTimestamp();

	String random(String timestamp);

	boolean isInTimestampThreshold(Date timestamp);

}
