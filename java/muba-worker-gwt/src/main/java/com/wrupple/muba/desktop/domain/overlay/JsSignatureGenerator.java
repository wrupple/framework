package com.wrupple.muba.desktop.domain.overlay;

import com.google.gwt.core.client.JavaScriptObject;
import com.wrupple.vegetate.shared.services.SignatureGenerator;

public final class JsSignatureGenerator extends JavaScriptObject implements SignatureGenerator {
	private static final char delimiter = '$';
	/*
	 * SHowcase:
	 * 
	 * http://www.jokecamp.com/blog/examples-of-creating-base64-hashes-using-hmac-sha256-in-different-languages/#js
	 * 
	 * reasonable implementation for single iteration, slow for multiple rounds
	 * http://caligatio.github.io/jsSHA/
	 * 
	 * fastest accordig to google: 
	 * https://github.com/vibornoff/asmcrypto.js
	 * 
	 * fastest according to some benchmark, but over-complex
	 * https://github.com/digitalbazaar/forge
	 * 
	 * 
	 * 
	 */
	protected  JsSignatureGenerator() {
	}
	@Override
	public boolean doSignatureMatch(String signature, String message) {
		int saltEndIndex = signature.indexOf(delimiter);
		int warPasswordStoreSize = signature.length();
		String storedPasswordHash64String = signature.substring(saltEndIndex + 1, warPasswordStoreSize);
		String storedSalt = signature.substring(0, saltEndIndex);

		String hashedPassword = getPassword(message, getPrivateKey(), storedSalt,500000);
		boolean equality = storedPasswordHash64String.equals(hashedPassword);
		return equality;
	}
	@Override
	public String generateSignature(String message) {
		
		String salt = getRandomBase64String();

		return defaultEncode(message, getPrivateKey(), salt, delimiter);
		
	}
	
//FIXME asmcrypto provides some functionality to produce random bytes but its undocumented
	private static native String getRandomBase64String() /*-{
			var chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
			var string_length = 24;
			var randomstring = '';
			var rnum;
			for (var i=0; i<string_length; i++) {
				rnum = Math.floor(Math.random() * chars.length);
				randomstring += chars.substring(rnum,rnum+1);
			}
			return randomstring;
		}
	}-*/;
	
	
	
	
	private static String defaultEncode(String message, String key, String encodedSalt, char delimiter) {

		String hashedPassword = getPassword(message, key, encodedSalt,500000);
		StringBuilder regreso = new StringBuilder(encodedSalt.length() + hashedPassword.length() + 1);
		regreso.append(encodedSalt);
		regreso.append(delimiter);
		regreso.append(hashedPassword);

		return regreso.toString();
	}

	private static native String getPassword(String message, String privateKey,  String salt,int iterations) /*-{
		var lockedMessage = salt+message+privateKey;
		var digest = $wnd.asmCrypto.SHA256;
		var hashed =digest.bytes(lockedMessage);
		iterations--;
		var last = iterations-1;
		for(var i = 0; i < iterations; i++){
			if(i==last){
				hashed=digest.base64(hashed);
			}else{
				hashed=digest.bytes(hashed);
			}  
		}
		return hashed;
	}-*/;


	@Override
	public long getStakeHolder() {
		return Long.parseLong(getStakeHolderString());
	}
	
	@Override
	public native String getPublicKey() /*-{
		return this.publicKey;
	}-*/;
	
	public native String getPrivateKey() /*-{
		return this.privateKey;
	}-*/;

	
	public  static native String getStakeHolderString()/*-{
		return this.stakeHolder;
	}-*/;
	
	public  static native JsSignatureGenerator create(String stakeHolderr,String publicKeyy, String privateKeyy)/*-{
		return {stakeHolder:stakeHolderr,publicKey:publicKeyy,privateKey:privateKeyy};
	}-*/;

}
