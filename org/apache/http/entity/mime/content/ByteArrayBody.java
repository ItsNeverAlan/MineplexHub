package org.apache.http.entity.mime.content;

import java.io.IOException;
import java.io.OutputStream;















































public class ByteArrayBody
  extends AbstractContentBody
{
  private final byte[] data;
  private final String filename;
  
  public ByteArrayBody(byte[] data, String mimeType, String filename)
  {
    super(mimeType);
    if (data == null) {
      throw new IllegalArgumentException("byte[] may not be null");
    }
    this.data = data;
    this.filename = filename;
  }
  





  public ByteArrayBody(byte[] data, String filename)
  {
    this(data, "application/octet-stream", filename);
  }
  
  public String getFilename() {
    return this.filename;
  }
  
  public void writeTo(OutputStream out) throws IOException {
    out.write(this.data);
  }
  
  public String getCharset() {
    return null;
  }
  
  public String getTransferEncoding() {
    return "binary";
  }
  
  public long getContentLength() {
    return this.data.length;
  }
}
