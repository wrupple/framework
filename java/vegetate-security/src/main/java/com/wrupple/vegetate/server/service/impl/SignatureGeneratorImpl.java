package com.wrupple.vegetate.server.service.impl;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.crypto.hash.format.ModularCryptFormat;
import org.apache.shiro.util.ByteSource;

import com.wrupple.muba.catalogs.server.services.ObjectMapper;
import com.wrupple.muba.catalogs.shared.services.SignatureGenerator;

/**
 * 
 * <a href="https://github.com/vibornoff/asmcrypto.js"> uses asmcrypto.js</a>
 * @author japi
 *
 */
public class SignatureGeneratorImpl implements SignatureGenerator {

	private static final char delimiter = ModularCryptFormat.TOKEN_DELIMITER.charAt(0);

	private final RandomNumberGenerator saltGenerator;
	
	// TODO use timestamp to sign and prevent replay attacks, use UTC time to
	// avoid DST issues. also send a request id ·nonce" field?
	private final String publicKey, privateKey;

	private final long stakeHolder;

	private final ObjectMapper mapper;

	private final long timeThresholdMillis;

	public SignatureGeneratorImpl(RandomNumberGenerator saltGenerator, String publicKey, String privateKey, long stakeHolder,ObjectMapper mapper,long timeThresholdMillis) {
		if (publicKey == null || privateKey == null) {
			throw new IllegalArgumentException();
		}
		this.timeThresholdMillis=timeThresholdMillis;
		this.stakeHolder = stakeHolder;
		this.publicKey = publicKey;
		this.privateKey = privateKey;
		this.saltGenerator = saltGenerator;
		this.mapper=mapper;
	}

	@Override
	public String generateSignature(String message,String encodedSalt) {

		if(encodedSalt==null){
			ByteSource salt = saltGenerator.nextBytes();
			return defaultEncode(message, privateKey, salt, delimiter);
		}else{
			return defaultEncode(message, privateKey, encodedSalt, delimiter);
		}
		

		
	}

	@Override
	public String getPublicKey() {
		return publicKey;
	}

	@Override
	public boolean doSignatureMatch(String signature, String message,String storedSalt) {
		String storedPasswordHash64String;
		if(storedSalt==null){
			int saltEndIndex = signature.indexOf(delimiter);
			storedPasswordHash64String = signature.substring(saltEndIndex + 1, signature.length());
			storedSalt = signature.substring(0, saltEndIndex);
		}else{
			storedPasswordHash64String=signature;
		}
		
		ByteSource salt = ByteSource.Util.bytes(Base64.decode(storedSalt));

		String hashedPassword = getPassword(message, privateKey, salt);
		boolean equality = storedPasswordHash64String.equals(hashedPassword);
		return equality;
	}
	
	public static void main(String... args){
		//BECHugCuleMbeptfZeGsCQ==$R6YYAs2jXL3YszniTLyqZg84On5oZ8H+HrqHcR7WCTg=
		//SignatureGeneratorImpl signer = new SignatureGeneratorImpl(new SecureRandomNumberGenerator(), "11", "22", 1l);
		//System.out.println(signer.generateSignature("33"));
		
		long start = System.currentTimeMillis();
		ByteSource salt = ByteSource.Util.bytes(Base64.decode("BECHugCuleMbeptfZeGsCQ=="));

		String hashedPassword = getPassword("33","22", salt);
		
		System.out.println(hashedPassword);
		System.out.println(System.currentTimeMillis()-start);
	}

	@Override
	public long getStakeHolder() {
		return stakeHolder;
	}
	private static String defaultEncode(String message, String key, String encodedSalt, char delimiter) {
		
		String hashedPassword = getPassword(message, key,encodedSalt.length()>8 ? ByteSource.Util.bytes(Base64.decode(encodedSalt)): ByteSource.Util.bytes(encodedSalt));

		return hashedPassword;
	}
	private static String defaultEncode(String message, String key, ByteSource salt, char delimiter) {

		String encodedSalt = salt.toBase64();
		String hashedPassword = getPassword(message, key, salt);
		StringBuilder regreso = new StringBuilder(encodedSalt.length() + hashedPassword.length() + 1);
		regreso.append(encodedSalt);
		regreso.append(delimiter);
		regreso.append(hashedPassword);

		return regreso.toString();
	}

	private static String getPassword(String message, String key, ByteSource salt) {
		char[] lockedMessage = new char[message.length() + key.length()];
		append(lockedMessage, message, key);
		//DefaultPasswordService.DEFAULT_HASH_ITERATIONS
		Hash hash = new SimpleHash(DefaultPasswordService.DEFAULT_HASH_ALGORITHM, lockedMessage, salt, 1);
		return hash.toBase64();
	}

	private static void append(char[] lockedMessage, String... ss) {
		char[] temp;
		int count = 0;
		for (String s : ss) {
			temp = s.toCharArray();
			for (int i = 0; i < temp.length; i++) {
				lockedMessage[count] = temp[i];
				count++;
			}
		}
	}

	public static String hash(byte[] privateKey, String stringToSign) throws NoSuchAlgorithmException, InvalidKeyException {
		// Get an hmac_sha1 key from the raw key bytes
		SecretKeySpec signingKey = new SecretKeySpec(privateKey, "HmacSHA1");

		// Get an hmac_sha1 Mac instance and initialize with the signing key
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(signingKey);

		// Compute the hmac on input data bytes
		byte[] rawHmac = mac.doFinal(stringToSign.getBytes());

		// Convert raw bytes to Hex
		return Base64.encodeToString(rawHmac);
	}

	@Override
	public Date getTimestamp() {
		return new Date();
	}

	@Override
	public void setTimestamp(Date d) {
		
	}

	@Override
	public String getSerializedTimestamp() {
		return mapper.formatDate(getTimestamp());
	}

	@Override
	public String random(String timestamp) {
		ByteSource salt = saltGenerator.nextBytes();
		ByteSource saltier = ByteSource.Util.bytes(timestamp);
		int origLenght = salt.getBytes().length;
		byte[] saltierBytes = saltier.getBytes();
		//FIXME array size overFlow
		byte[] arr = Arrays.copyOf(salt.getBytes(), salt.getBytes().length+saltierBytes.length);
		
		for(int i =  0; i < saltierBytes.length; i++){
			arr[origLenght+i]=saltierBytes[i];
		}
		
		return ByteSource.Util.bytes(arr).toBase64();
	}

	@Override
	public boolean isInTimestampThreshold(Date timestamp) {
		if(timestamp==null){
			return true;
		}else{
			return timeThresholdMillis<=(System.currentTimeMillis()-timestamp.getTime());
		}
		
	}

}
