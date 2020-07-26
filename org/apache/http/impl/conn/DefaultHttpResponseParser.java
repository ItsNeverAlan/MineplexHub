package org.apache.http.impl.conn;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ProtocolException;
import org.apache.http.StatusLine;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.impl.io.AbstractMessageParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.LineParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.params.HttpParams;
import org.apache.http.util.CharArrayBuffer;









































@ThreadSafe
public class DefaultHttpResponseParser
  extends AbstractMessageParser<HttpResponse>
{
  private final Log log = LogFactory.getLog(getClass());
  

  private final HttpResponseFactory responseFactory;
  
  private final CharArrayBuffer lineBuf;
  

  public DefaultHttpResponseParser(SessionInputBuffer buffer, LineParser parser, HttpResponseFactory responseFactory, HttpParams params)
  {
    super(buffer, parser, params);
    if (responseFactory == null) {
      throw new IllegalArgumentException("Response factory may not be null");
    }
    
    this.responseFactory = responseFactory;
    this.lineBuf = new CharArrayBuffer(128);
  }
  

  protected HttpResponse parseHead(SessionInputBuffer sessionBuffer)
    throws IOException, HttpException
  {
    int count = 0;
    ParserCursor cursor = null;
    for (;;)
    {
      this.lineBuf.clear();
      int i = sessionBuffer.readLine(this.lineBuf);
      if ((i == -1) && (count == 0))
      {
        throw new NoHttpResponseException("The target server failed to respond");
      }
      cursor = new ParserCursor(0, this.lineBuf.length());
      if (this.lineParser.hasProtocolVersion(this.lineBuf, cursor)) {
        break;
      }
      if ((i == -1) || (reject(this.lineBuf, count)))
      {
        throw new ProtocolException("The server failed to respond with a valid HTTP response");
      }
      
      if (this.log.isDebugEnabled()) {
        this.log.debug("Garbage in response: " + this.lineBuf.toString());
      }
      count++;
    }
    
    StatusLine statusline = this.lineParser.parseStatusLine(this.lineBuf, cursor);
    return this.responseFactory.newHttpResponse(statusline, null);
  }
  
  protected boolean reject(CharArrayBuffer line, int count) {
    return false;
  }
}
