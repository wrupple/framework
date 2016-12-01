package com.wrupple.vegetate.server.service.impl;

import javax.inject.Provider;

import org.apache.shiro.crypto.RandomNumberGenerator;

public class SignatureGeneratorProvider extends SignatureGeneratorImpl implements Provider<SignatureGeneratorImpl> {

	public SignatureGeneratorProvider(RandomNumberGenerator saltGenerator, String publicKey, String privateKey,long stakeHolder) {
		super(saltGenerator, publicKey, privateKey,stakeHolder);
	}

	@Override
	public SignatureGeneratorImpl get() {
		return this;
	}

}
