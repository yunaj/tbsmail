/***
 * jwma Java WebMail
 * Copyright (c) 2000-2003 jwma team
 *
 * jwma is free software; you can distribute and use this source
 * under the terms of the BSD-style license received along with
 * the distribution.
 ***/
package com.hs.mail.webmail.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

/**
 * Class that implements a MimeBodyPartDataSource.
 *
 * It implements <tt>DataSource</tt> for handling
 * with the Mail API (or other JAF aware) classes.
 *
 * @author Dieter Wimberger
 * @version 0.9.7 07/02/2003
 */
public class MimeBodyPartDataSource
    implements DataSource {

  //instance attributes
  private String m_Type;
  private String m_Name;
  private byte[] m_Data;

  /**
   * Constructs a <tt>MimeBodyPartDataSource</tt>
   * instance.
   *
   * @param type the content type of the constructed instance
   *        as <tt>String</tt>.
   * @param name the name of the constructed instance
   *        as <tt>String</tt>.
   * @param data the data of the constructed instance as
   *        <tt>byte[]</tt>.
   *
   * @return the newly constructed <tt>MimeBodyPartDataSource
   *         </tt>.
   */
  public MimeBodyPartDataSource(
      String type, String name, byte[] data) {

    m_Type = type;
    m_Name = name;
    m_Data = data;
  }//constructore

  /**
   * Returns the content type of this instance as
   * <tt>String</tt>.
   * <p>(DataSource implementation)
   *
   * @return the content type as <tt>String</tt>
   */
  public String getContentType() {
    return m_Type;
  }//getContentType

  /**
   * Returns the name of this instance as <tt>String</tt>.
   * <p>(DataSource implementation)
   *
   * @return the name as <tt>String</tt>
   */
  public String getName() {
    return m_Name;
  }//getName

  /**
   * Returns the data of this instance as <tt>
   * InputStream</tt>.
   * <p>(DataSource implementation); wraps the data
   * into a <tt>ByteArrayInputStream</tt>.
   *
   * @return the data of this instance as
   *         <tt>InputStream</tt>.
   * @throws IOException if impossible.
   */
  public InputStream getInputStream()
      throws IOException {

    return new ByteArrayInputStream(m_Data);
  }//getInputStream

  /**
   * Throws an IOException in this implementation, because
   * this instance represents a read-only <tt>DataSource</tt>.
   * <p>(DataSource implementation)
   *
   * @return an <tt>OutputStream</tt> instance for writing
   *         to this <tt>DataSource</tt>.
   * @throws IOException if impossible.
   */
  public OutputStream getOutputStream()
      throws IOException {

    throw new IOException(
        "Not supported."
    );
  }//getOutputStream

}//class MimeBodyPartDataSource
