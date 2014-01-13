
package org.crocodile.fitbit;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;


/**
 * Scribe FitBit API. Taken from:
 * https://github.com/apakulov/scribe-java/commit/b9fe95d376a584cf7efcd109a75ef42dd3471193
 * 
 * @author lord
 *
 */
public class FitbitApi extends DefaultApi10a
{
    private static final String AUTHORIZATION_URL = "http://www.fitbit.com/oauth/authorize?oauth_token=%s";

    @Override
    public String getRequestTokenEndpoint()
    {
        return "http://api.fitbit.com/oauth/request_token";
    }

    @Override
    public String getAccessTokenEndpoint()
    {
        return "http://api.fitbit.com/oauth/access_token";
    }

    @Override
    public String getAuthorizationUrl(Token requestToken)
    {
        return String.format(AUTHORIZATION_URL, requestToken.getToken());
    }

}
