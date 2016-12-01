package com.wrupple.vegetate.server.chain.command;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;

import com.wrupple.base.server.domain.OAuthServiceManifest;
import com.wrupple.base.server.domain.UserAuthenticationContext;
import com.wrupple.muba.catalogs.domain.VegetateAuthenticationToken;

public abstract class AbstractOAuthHandler implements Command {

	protected final OAuthServiceManifest manifest;
	protected final String realm;
	
	
	public AbstractOAuthHandler(OAuthServiceManifest manifest,String realm) {
		super();
		this.manifest = manifest;
		this.realm=realm;
	}

	@Override
	public boolean execute(Context c) throws Exception {
		UserAuthenticationContext context = (UserAuthenticationContext) c;
		String action = (String) context.get(VegetateAuthenticationToken.ACTION_PARAMETER);
		HttpServletRequest request = context.getRequest().getServletContext().getRequest();
		HttpServletResponse response =  context.getRequest().getServletContext().getResponse();
		if (action == null && !isUserSignedIn(context)) {
			// auth request
				
				String callbackURL = manifest.getCallbackUrl(request,realm); 
				
				String oAuthUrl = prepareOAuthRequest(callbackURL,context,action,request,response);
				
				//THE URL FROM WHERE THE USER ISSUED THE LOGIN REQUEST
				String desktopCallbackParam = context.getCallback();
				if (desktopCallbackParam != null) {
					request.getSession().setAttribute(OAuthServiceManifest.OAUTH_REQUEST_TOKEN, desktopCallbackParam);
				}
				response.sendRedirect(oAuthUrl);
			
		} else {
			// auth callback
			
			Subject currentUser = SecurityUtils.getSubject();
			AuthenticationToken authenticationToken = getAuthenticationToken(context,action,request,response);
			currentUser.login(authenticationToken);
			
			String desktopCallbackParam = (String) request.getSession().getAttribute(OAuthServiceManifest.OAUTH_REQUEST_TOKEN);
			request.getSession().removeAttribute(OAuthServiceManifest.OAUTH_REQUEST_TOKEN);
			if (desktopCallbackParam == null) {
				response.sendRedirect(manifest.getDefaultSuccessUrl(request));
			} else {
				response.sendRedirect(desktopCallbackParam);
			}
		}
		return CONTINUE_PROCESSING;
	}



	protected abstract boolean isUserSignedIn(UserAuthenticationContext context) ;

	protected abstract AuthenticationToken getAuthenticationToken(UserAuthenticationContext context, String action, HttpServletRequest request,
			HttpServletResponse response) throws ServletException;


	/**
	 * @param callbackURL
	 * @param context
	 * @param action
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException 3rd party oAuth url
	 */
	protected abstract  String prepareOAuthRequest(String callbackURL, UserAuthenticationContext context, String action, HttpServletRequest request, HttpServletResponse response) throws ServletException; 

}
