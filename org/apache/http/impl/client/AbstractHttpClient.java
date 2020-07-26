package org.apache.http.impl.client;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.annotation.GuardedBy;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.auth.AuthSchemeRegistry;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.BackoffManager;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ConnectionBackoffStrategy;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.RequestDirector;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionManagerFactory;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.cookie.CookieSpecRegistry;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.auth.KerberosSchemeFactory;
import org.apache.http.impl.auth.NTLMSchemeFactory;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.impl.conn.DefaultHttpRoutePlanner;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.impl.cookie.IgnoreSpecFactory;
import org.apache.http.impl.cookie.NetscapeDraftSpecFactory;
import org.apache.http.impl.cookie.RFC2109SpecFactory;
import org.apache.http.impl.cookie.RFC2965SpecFactory;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.DefaultedHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.util.EntityUtils;





















































































































@ThreadSafe
public abstract class AbstractHttpClient
  implements HttpClient
{
  private final Log log = LogFactory.getLog(getClass());
  


  @GuardedBy("this")
  private HttpParams defaultParams;
  


  @GuardedBy("this")
  private HttpRequestExecutor requestExec;
  


  @GuardedBy("this")
  private ClientConnectionManager connManager;
  


  @GuardedBy("this")
  private ConnectionReuseStrategy reuseStrategy;
  

  @GuardedBy("this")
  private ConnectionKeepAliveStrategy keepAliveStrategy;
  

  @GuardedBy("this")
  private CookieSpecRegistry supportedCookieSpecs;
  

  @GuardedBy("this")
  private AuthSchemeRegistry supportedAuthSchemes;
  

  @GuardedBy("this")
  private BasicHttpProcessor mutableProcessor;
  

  @GuardedBy("this")
  private ImmutableHttpProcessor protocolProcessor;
  

  @GuardedBy("this")
  private HttpRequestRetryHandler retryHandler;
  

  @GuardedBy("this")
  private RedirectStrategy redirectStrategy;
  

  @GuardedBy("this")
  private AuthenticationStrategy targetAuthStrategy;
  

  @GuardedBy("this")
  private AuthenticationStrategy proxyAuthStrategy;
  

  @GuardedBy("this")
  private CookieStore cookieStore;
  

  @GuardedBy("this")
  private CredentialsProvider credsProvider;
  

  @GuardedBy("this")
  private HttpRoutePlanner routePlanner;
  

  @GuardedBy("this")
  private UserTokenHandler userTokenHandler;
  

  @GuardedBy("this")
  private ConnectionBackoffStrategy connectionBackoffStrategy;
  

  @GuardedBy("this")
  private BackoffManager backoffManager;
  


  protected AbstractHttpClient(ClientConnectionManager conman, HttpParams params)
  {
    this.defaultParams = params;
    this.connManager = conman;
  }
  

  protected abstract HttpParams createHttpParams();
  

  protected abstract BasicHttpProcessor createHttpProcessor();
  
  protected HttpContext createHttpContext()
  {
    HttpContext context = new BasicHttpContext();
    context.setAttribute("http.scheme-registry", getConnectionManager().getSchemeRegistry());
    

    context.setAttribute("http.authscheme-registry", getAuthSchemes());
    

    context.setAttribute("http.cookiespec-registry", getCookieSpecs());
    

    context.setAttribute("http.cookie-store", getCookieStore());
    

    context.setAttribute("http.auth.credentials-provider", getCredentialsProvider());
    

    return context;
  }
  
  protected ClientConnectionManager createClientConnectionManager()
  {
    SchemeRegistry registry = SchemeRegistryFactory.createDefault();
    
    ClientConnectionManager connManager = null;
    HttpParams params = getParams();
    
    ClientConnectionManagerFactory factory = null;
    
    String className = (String)params.getParameter("http.connection-manager.factory-class-name");
    
    if (className != null) {
      try {
        Class<?> clazz = Class.forName(className);
        factory = (ClientConnectionManagerFactory)clazz.newInstance();
      } catch (ClassNotFoundException ex) {
        throw new IllegalStateException("Invalid class name: " + className);
      } catch (IllegalAccessException ex) {
        throw new IllegalAccessError(ex.getMessage());
      } catch (InstantiationException ex) {
        throw new InstantiationError(ex.getMessage());
      }
    }
    if (factory != null) {
      connManager = factory.newInstance(params, registry);
    } else {
      connManager = new BasicClientConnectionManager(registry);
    }
    
    return connManager;
  }
  
  protected AuthSchemeRegistry createAuthSchemeRegistry()
  {
    AuthSchemeRegistry registry = new AuthSchemeRegistry();
    registry.register("Basic", new BasicSchemeFactory());
    

    registry.register("Digest", new DigestSchemeFactory());
    

    registry.register("NTLM", new NTLMSchemeFactory());
    

    registry.register("negotiate", new SPNegoSchemeFactory());
    

    registry.register("Kerberos", new KerberosSchemeFactory());
    

    return registry;
  }
  
  protected CookieSpecRegistry createCookieSpecRegistry()
  {
    CookieSpecRegistry registry = new CookieSpecRegistry();
    registry.register("best-match", new BestMatchSpecFactory());
    

    registry.register("compatibility", new BrowserCompatSpecFactory());
    

    registry.register("netscape", new NetscapeDraftSpecFactory());
    

    registry.register("rfc2109", new RFC2109SpecFactory());
    

    registry.register("rfc2965", new RFC2965SpecFactory());
    

    registry.register("ignoreCookies", new IgnoreSpecFactory());
    

    return registry;
  }
  
  protected HttpRequestExecutor createRequestExecutor() {
    return new HttpRequestExecutor();
  }
  
  protected ConnectionReuseStrategy createConnectionReuseStrategy() {
    return new DefaultConnectionReuseStrategy();
  }
  
  protected ConnectionKeepAliveStrategy createConnectionKeepAliveStrategy() {
    return new DefaultConnectionKeepAliveStrategy();
  }
  
  protected HttpRequestRetryHandler createHttpRequestRetryHandler() {
    return new DefaultHttpRequestRetryHandler();
  }
  


  @Deprecated
  protected RedirectHandler createRedirectHandler()
  {
    return new DefaultRedirectHandler();
  }
  
  protected AuthenticationStrategy createTargetAuthenticationStrategy() {
    return new TargetAuthenticationStrategy();
  }
  


  @Deprecated
  protected AuthenticationHandler createTargetAuthenticationHandler()
  {
    return new DefaultTargetAuthenticationHandler();
  }
  
  protected AuthenticationStrategy createProxyAuthenticationStrategy() {
    return new ProxyAuthenticationStrategy();
  }
  


  @Deprecated
  protected AuthenticationHandler createProxyAuthenticationHandler()
  {
    return new DefaultProxyAuthenticationHandler();
  }
  
  protected CookieStore createCookieStore() {
    return new BasicCookieStore();
  }
  
  protected CredentialsProvider createCredentialsProvider() {
    return new BasicCredentialsProvider();
  }
  
  protected HttpRoutePlanner createHttpRoutePlanner() {
    return new DefaultHttpRoutePlanner(getConnectionManager().getSchemeRegistry());
  }
  
  protected UserTokenHandler createUserTokenHandler() {
    return new DefaultUserTokenHandler();
  }
  
  public final synchronized HttpParams getParams()
  {
    if (this.defaultParams == null) {
      this.defaultParams = createHttpParams();
    }
    return this.defaultParams;
  }
  





  public synchronized void setParams(HttpParams params)
  {
    this.defaultParams = params;
  }
  
  public final synchronized ClientConnectionManager getConnectionManager()
  {
    if (this.connManager == null) {
      this.connManager = createClientConnectionManager();
    }
    return this.connManager;
  }
  
  public final synchronized HttpRequestExecutor getRequestExecutor()
  {
    if (this.requestExec == null) {
      this.requestExec = createRequestExecutor();
    }
    return this.requestExec;
  }
  
  public final synchronized AuthSchemeRegistry getAuthSchemes()
  {
    if (this.supportedAuthSchemes == null) {
      this.supportedAuthSchemes = createAuthSchemeRegistry();
    }
    return this.supportedAuthSchemes;
  }
  
  public synchronized void setAuthSchemes(AuthSchemeRegistry registry) {
    this.supportedAuthSchemes = registry;
  }
  
  public final synchronized ConnectionBackoffStrategy getConnectionBackoffStrategy() {
    return this.connectionBackoffStrategy;
  }
  
  public synchronized void setConnectionBackoffStrategy(ConnectionBackoffStrategy strategy) {
    this.connectionBackoffStrategy = strategy;
  }
  
  public final synchronized CookieSpecRegistry getCookieSpecs() {
    if (this.supportedCookieSpecs == null) {
      this.supportedCookieSpecs = createCookieSpecRegistry();
    }
    return this.supportedCookieSpecs;
  }
  
  public final synchronized BackoffManager getBackoffManager() {
    return this.backoffManager;
  }
  
  public synchronized void setBackoffManager(BackoffManager manager) {
    this.backoffManager = manager;
  }
  
  public synchronized void setCookieSpecs(CookieSpecRegistry registry) {
    this.supportedCookieSpecs = registry;
  }
  
  public final synchronized ConnectionReuseStrategy getConnectionReuseStrategy() {
    if (this.reuseStrategy == null) {
      this.reuseStrategy = createConnectionReuseStrategy();
    }
    return this.reuseStrategy;
  }
  
  public synchronized void setReuseStrategy(ConnectionReuseStrategy strategy)
  {
    this.reuseStrategy = strategy;
  }
  
  public final synchronized ConnectionKeepAliveStrategy getConnectionKeepAliveStrategy()
  {
    if (this.keepAliveStrategy == null) {
      this.keepAliveStrategy = createConnectionKeepAliveStrategy();
    }
    return this.keepAliveStrategy;
  }
  
  public synchronized void setKeepAliveStrategy(ConnectionKeepAliveStrategy strategy)
  {
    this.keepAliveStrategy = strategy;
  }
  
  public final synchronized HttpRequestRetryHandler getHttpRequestRetryHandler()
  {
    if (this.retryHandler == null) {
      this.retryHandler = createHttpRequestRetryHandler();
    }
    return this.retryHandler;
  }
  
  public synchronized void setHttpRequestRetryHandler(HttpRequestRetryHandler handler) {
    this.retryHandler = handler;
  }
  


  @Deprecated
  public final synchronized RedirectHandler getRedirectHandler()
  {
    return createRedirectHandler();
  }
  


  @Deprecated
  public synchronized void setRedirectHandler(RedirectHandler handler)
  {
    this.redirectStrategy = new DefaultRedirectStrategyAdaptor(handler);
  }
  


  public final synchronized RedirectStrategy getRedirectStrategy()
  {
    if (this.redirectStrategy == null) {
      this.redirectStrategy = new DefaultRedirectStrategy();
    }
    return this.redirectStrategy;
  }
  


  public synchronized void setRedirectStrategy(RedirectStrategy strategy)
  {
    this.redirectStrategy = strategy;
  }
  


  @Deprecated
  public final synchronized AuthenticationHandler getTargetAuthenticationHandler()
  {
    return createTargetAuthenticationHandler();
  }
  


  @Deprecated
  public synchronized void setTargetAuthenticationHandler(AuthenticationHandler handler)
  {
    this.targetAuthStrategy = new AuthenticationStrategyAdaptor(handler);
  }
  


  public final synchronized AuthenticationStrategy getTargetAuthenticationStrategy()
  {
    if (this.targetAuthStrategy == null) {
      this.targetAuthStrategy = createTargetAuthenticationStrategy();
    }
    return this.targetAuthStrategy;
  }
  


  public synchronized void setTargetAuthenticationStrategy(AuthenticationStrategy strategy)
  {
    this.targetAuthStrategy = strategy;
  }
  


  @Deprecated
  public final synchronized AuthenticationHandler getProxyAuthenticationHandler()
  {
    return createProxyAuthenticationHandler();
  }
  


  @Deprecated
  public synchronized void setProxyAuthenticationHandler(AuthenticationHandler handler)
  {
    this.proxyAuthStrategy = new AuthenticationStrategyAdaptor(handler);
  }
  


  public final synchronized AuthenticationStrategy getProxyAuthenticationStrategy()
  {
    if (this.proxyAuthStrategy == null) {
      this.proxyAuthStrategy = createProxyAuthenticationStrategy();
    }
    return this.proxyAuthStrategy;
  }
  


  public synchronized void setProxyAuthenticationStrategy(AuthenticationStrategy strategy)
  {
    this.proxyAuthStrategy = strategy;
  }
  
  public final synchronized CookieStore getCookieStore() {
    if (this.cookieStore == null) {
      this.cookieStore = createCookieStore();
    }
    return this.cookieStore;
  }
  
  public synchronized void setCookieStore(CookieStore cookieStore) {
    this.cookieStore = cookieStore;
  }
  
  public final synchronized CredentialsProvider getCredentialsProvider() {
    if (this.credsProvider == null) {
      this.credsProvider = createCredentialsProvider();
    }
    return this.credsProvider;
  }
  
  public synchronized void setCredentialsProvider(CredentialsProvider credsProvider) {
    this.credsProvider = credsProvider;
  }
  
  public final synchronized HttpRoutePlanner getRoutePlanner() {
    if (this.routePlanner == null) {
      this.routePlanner = createHttpRoutePlanner();
    }
    return this.routePlanner;
  }
  
  public synchronized void setRoutePlanner(HttpRoutePlanner routePlanner) {
    this.routePlanner = routePlanner;
  }
  
  public final synchronized UserTokenHandler getUserTokenHandler() {
    if (this.userTokenHandler == null) {
      this.userTokenHandler = createUserTokenHandler();
    }
    return this.userTokenHandler;
  }
  
  public synchronized void setUserTokenHandler(UserTokenHandler handler) {
    this.userTokenHandler = handler;
  }
  
  protected final synchronized BasicHttpProcessor getHttpProcessor() {
    if (this.mutableProcessor == null) {
      this.mutableProcessor = createHttpProcessor();
    }
    return this.mutableProcessor;
  }
  
  private final synchronized HttpProcessor getProtocolProcessor() {
    if (this.protocolProcessor == null)
    {
      BasicHttpProcessor proc = getHttpProcessor();
      
      int reqc = proc.getRequestInterceptorCount();
      HttpRequestInterceptor[] reqinterceptors = new HttpRequestInterceptor[reqc];
      for (int i = 0; i < reqc; i++) {
        reqinterceptors[i] = proc.getRequestInterceptor(i);
      }
      int resc = proc.getResponseInterceptorCount();
      HttpResponseInterceptor[] resinterceptors = new HttpResponseInterceptor[resc];
      for (int i = 0; i < resc; i++) {
        resinterceptors[i] = proc.getResponseInterceptor(i);
      }
      this.protocolProcessor = new ImmutableHttpProcessor(reqinterceptors, resinterceptors);
    }
    return this.protocolProcessor;
  }
  
  public synchronized int getResponseInterceptorCount() {
    return getHttpProcessor().getResponseInterceptorCount();
  }
  
  public synchronized HttpResponseInterceptor getResponseInterceptor(int index) {
    return getHttpProcessor().getResponseInterceptor(index);
  }
  
  public synchronized HttpRequestInterceptor getRequestInterceptor(int index) {
    return getHttpProcessor().getRequestInterceptor(index);
  }
  
  public synchronized int getRequestInterceptorCount() {
    return getHttpProcessor().getRequestInterceptorCount();
  }
  
  public synchronized void addResponseInterceptor(HttpResponseInterceptor itcp) {
    getHttpProcessor().addInterceptor(itcp);
    this.protocolProcessor = null;
  }
  
  public synchronized void addResponseInterceptor(HttpResponseInterceptor itcp, int index) {
    getHttpProcessor().addInterceptor(itcp, index);
    this.protocolProcessor = null;
  }
  
  public synchronized void clearResponseInterceptors() {
    getHttpProcessor().clearResponseInterceptors();
    this.protocolProcessor = null;
  }
  
  public synchronized void removeResponseInterceptorByClass(Class<? extends HttpResponseInterceptor> clazz) {
    getHttpProcessor().removeResponseInterceptorByClass(clazz);
    this.protocolProcessor = null;
  }
  
  public synchronized void addRequestInterceptor(HttpRequestInterceptor itcp) {
    getHttpProcessor().addInterceptor(itcp);
    this.protocolProcessor = null;
  }
  
  public synchronized void addRequestInterceptor(HttpRequestInterceptor itcp, int index) {
    getHttpProcessor().addInterceptor(itcp, index);
    this.protocolProcessor = null;
  }
  
  public synchronized void clearRequestInterceptors() {
    getHttpProcessor().clearRequestInterceptors();
    this.protocolProcessor = null;
  }
  
  public synchronized void removeRequestInterceptorByClass(Class<? extends HttpRequestInterceptor> clazz) {
    getHttpProcessor().removeRequestInterceptorByClass(clazz);
    this.protocolProcessor = null;
  }
  
  public final HttpResponse execute(HttpUriRequest request)
    throws IOException, ClientProtocolException
  {
    return execute(request, (HttpContext)null);
  }
  










  public final HttpResponse execute(HttpUriRequest request, HttpContext context)
    throws IOException, ClientProtocolException
  {
    if (request == null) {
      throw new IllegalArgumentException("Request must not be null.");
    }
    

    return execute(determineTarget(request), request, context);
  }
  
  private static HttpHost determineTarget(HttpUriRequest request)
    throws ClientProtocolException
  {
    HttpHost target = null;
    
    URI requestURI = request.getURI();
    if (requestURI.isAbsolute()) {
      target = URIUtils.extractHost(requestURI);
      if (target == null) {
        throw new ClientProtocolException("URI does not specify a valid host name: " + requestURI);
      }
    }
    
    return target;
  }
  
  public final HttpResponse execute(HttpHost target, HttpRequest request)
    throws IOException, ClientProtocolException
  {
    return execute(target, request, (HttpContext)null);
  }
  

  public final HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context)
    throws IOException, ClientProtocolException
  {
    if (request == null) {
      throw new IllegalArgumentException("Request must not be null.");
    }
    



    HttpContext execContext = null;
    RequestDirector director = null;
    HttpRoutePlanner routePlanner = null;
    ConnectionBackoffStrategy connectionBackoffStrategy = null;
    BackoffManager backoffManager = null;
    


    synchronized (this)
    {
      HttpContext defaultContext = createHttpContext();
      if (context == null) {
        execContext = defaultContext;
      } else {
        execContext = new DefaultedHttpContext(context, defaultContext);
      }
      
      director = createClientRequestDirector(getRequestExecutor(), getConnectionManager(), getConnectionReuseStrategy(), getConnectionKeepAliveStrategy(), getRoutePlanner(), getProtocolProcessor(), getHttpRequestRetryHandler(), getRedirectStrategy(), getTargetAuthenticationStrategy(), getProxyAuthenticationStrategy(), getUserTokenHandler(), determineParams(request));
      











      routePlanner = getRoutePlanner();
      connectionBackoffStrategy = getConnectionBackoffStrategy();
      backoffManager = getBackoffManager();
    }
    try
    {
      if ((connectionBackoffStrategy != null) && (backoffManager != null)) {
        HttpHost targetForRoute = target != null ? target : (HttpHost)determineParams(request).getParameter("http.default-host");
        

        HttpRoute route = routePlanner.determineRoute(targetForRoute, request, execContext);
        HttpResponse out;
        try
        {
          out = director.execute(target, request, execContext);
        } catch (RuntimeException re) {
          if (connectionBackoffStrategy.shouldBackoff(re)) {
            backoffManager.backOff(route);
          }
          throw re;
        } catch (Exception e) {
          if (connectionBackoffStrategy.shouldBackoff(e)) {
            backoffManager.backOff(route);
          }
          if ((e instanceof HttpException)) throw ((HttpException)e);
          if ((e instanceof IOException)) throw ((IOException)e);
          throw new UndeclaredThrowableException(e);
        }
        if (connectionBackoffStrategy.shouldBackoff(out)) {
          backoffManager.backOff(route);
        } else {
          backoffManager.probe(route);
        }
        return out;
      }
      return director.execute(target, request, execContext);
    }
    catch (HttpException httpException) {
      throw new ClientProtocolException(httpException);
    }
  }
  














  @Deprecated
  protected RequestDirector createClientRequestDirector(HttpRequestExecutor requestExec, ClientConnectionManager conman, ConnectionReuseStrategy reustrat, ConnectionKeepAliveStrategy kastrat, HttpRoutePlanner rouplan, HttpProcessor httpProcessor, HttpRequestRetryHandler retryHandler, RedirectHandler redirectHandler, AuthenticationHandler targetAuthHandler, AuthenticationHandler proxyAuthHandler, UserTokenHandler userTokenHandler, HttpParams params)
  {
    return new DefaultRequestDirector(requestExec, conman, reustrat, kastrat, rouplan, httpProcessor, retryHandler, redirectHandler, targetAuthHandler, proxyAuthHandler, userTokenHandler, params);
  }
  


























  @Deprecated
  protected RequestDirector createClientRequestDirector(HttpRequestExecutor requestExec, ClientConnectionManager conman, ConnectionReuseStrategy reustrat, ConnectionKeepAliveStrategy kastrat, HttpRoutePlanner rouplan, HttpProcessor httpProcessor, HttpRequestRetryHandler retryHandler, RedirectStrategy redirectStrategy, AuthenticationHandler targetAuthHandler, AuthenticationHandler proxyAuthHandler, UserTokenHandler userTokenHandler, HttpParams params)
  {
    return new DefaultRequestDirector(this.log, requestExec, conman, reustrat, kastrat, rouplan, httpProcessor, retryHandler, redirectStrategy, targetAuthHandler, proxyAuthHandler, userTokenHandler, params);
  }
  




























  protected RequestDirector createClientRequestDirector(HttpRequestExecutor requestExec, ClientConnectionManager conman, ConnectionReuseStrategy reustrat, ConnectionKeepAliveStrategy kastrat, HttpRoutePlanner rouplan, HttpProcessor httpProcessor, HttpRequestRetryHandler retryHandler, RedirectStrategy redirectStrategy, AuthenticationStrategy targetAuthStrategy, AuthenticationStrategy proxyAuthStrategy, UserTokenHandler userTokenHandler, HttpParams params)
  {
    return new DefaultRequestDirector(this.log, requestExec, conman, reustrat, kastrat, rouplan, httpProcessor, retryHandler, redirectStrategy, targetAuthStrategy, proxyAuthStrategy, userTokenHandler, params);
  }
  



























  protected HttpParams determineParams(HttpRequest req)
  {
    return new ClientParamsStack(null, getParams(), req.getParams(), null);
  }
  


  public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler)
    throws IOException, ClientProtocolException
  {
    return execute(request, responseHandler, null);
  }
  


  public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context)
    throws IOException, ClientProtocolException
  {
    HttpHost target = determineTarget(request);
    return execute(target, request, responseHandler, context);
  }
  


  public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler)
    throws IOException, ClientProtocolException
  {
    return execute(target, request, responseHandler, null);
  }
  



  public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context)
    throws IOException, ClientProtocolException
  {
    if (responseHandler == null) {
      throw new IllegalArgumentException("Response handler must not be null.");
    }
    

    HttpResponse response = execute(target, request, context);
    T result;
    try
    {
      result = responseHandler.handleResponse(response);
    } catch (Exception t) {
      HttpEntity entity = response.getEntity();
      try {
        EntityUtils.consume(entity);
      }
      catch (Exception t2)
      {
        this.log.warn("Error consuming content after an exception.", t2);
      }
      if ((t instanceof RuntimeException)) {
        throw ((RuntimeException)t);
      }
      if ((t instanceof IOException)) {
        throw ((IOException)t);
      }
      throw new UndeclaredThrowableException(t);
    }
    


    HttpEntity entity = response.getEntity();
    EntityUtils.consume(entity);
    return result;
  }
}
