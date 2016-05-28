package com.wrupple.muba.catalogs.server.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;

import javax.inject.Provider;

import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.vegetate.domain.VegetatePeer;
import com.wrupple.vegetate.domain.structure.HasTimestamp;
import com.wrupple.vegetate.server.services.ObjectMapper;
import com.wrupple.vegetate.server.services.impl.AbstractVegetateChannel;
import com.wrupple.vegetate.shared.services.PeerManager;
import com.wrupple.vegetate.shared.services.SignatureGenerator;

public abstract class AbstractCatalogVegetateChannel<R> extends AbstractVegetateChannel<CatalogActionRequestImpl, R> {
	
	protected final Provider<? extends SignatureGenerator> signatureGeneratorProvider;
	protected final SignatureGenerator signatureGenerator;
	
	public AbstractCatalogVegetateChannel(String host, String vegetateUrlBase, ObjectMapper mapper, CatalogServiceManifest serviceManifest, Provider<? extends SignatureGenerator> signatureGeneratorProvider) {
		super(host, vegetateUrlBase, serviceManifest, mapper);
		this.signatureGeneratorProvider=signatureGeneratorProvider;
		signatureGenerator=null;
	}
	
	public AbstractCatalogVegetateChannel(String host, String vegetateUrlBase, ObjectMapper mapper, CatalogServiceManifest serviceManifest,SignatureGenerator signatureGenerator) {
		super(host, vegetateUrlBase, serviceManifest, mapper);
		this.signatureGenerator=signatureGenerator;
		signatureGeneratorProvider=null;
	}
	
	

	static String convertStreamToString(InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}

	@Override
	protected void writeObject(CatalogActionRequestImpl object, OutputStreamWriter writer) throws IOException  {
		writer.write("0=");
		if(signatureGeneratorProvider==null&& signatureGenerator==null){
			mapper.writeValue(writer, object);
		}else{
			SignatureGenerator signer = signatureGenerator==null? signatureGeneratorProvider.get() : signatureGenerator;

			
			
			String message = mapper.writeValueAsString(object) ;
			String timestamp= signer.getSerializedTimestamp();
			String salt = signer.random(timestamp);
			String signature = signer.generateSignature(message,salt);
			writer.write(URLEncoder.encode(message, encoding));
			writer.write("&");
			writer.write(VegetatePeer.PUBLIC_KEY);
			writer.write("=");
			writer.write(URLEncoder.encode(signer.getPublicKey(), encoding));
			writer.write("&");
			writer.write(HasTimestamp.FIELD);
			writer.write("=");
			
			writer.write(URLEncoder.encode(timestamp, encoding));
			writer.write("&");
			writer.write(PeerManager.REQUEST_SALT);
			writer.write("=");
			writer.write(salt);
			writer.write("&");
			writer.write(PeerManager.ACCESS_TOKEN);
			writer.write("=");
			writer.write(URLEncoder.encode(signature, encoding));
		}
	}

	

}
