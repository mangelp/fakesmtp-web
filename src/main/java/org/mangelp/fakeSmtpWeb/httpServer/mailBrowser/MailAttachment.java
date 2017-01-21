package org.mangelp.fakeSmtpWeb.httpServer.mailBrowser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailAttachment implements DataSource {
	
	Logger logger = LoggerFactory.getLogger(MailAttachment.class);
	
	private DataSource wrappedSource = null;
	private long size = -1;
	private int id = -1;
	
	public long getSize() {
		return this.getSize(true);
	}
	
	public long getSize(boolean calculate) {
		if (calculate && size == -1) {
			this.size = this.getAttachmentSize();
		}
		
		return this.size;
	}
	
	public void setSize(long size) {
		this.size = size;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public DataSource getWrappedSource() {
		return wrappedSource;
	}
	
	public void setWrappedSource(DataSource wrappedSource) {
		this.wrappedSource = wrappedSource;
	}
	
	public MailAttachment(int index, DataSource wrappedSource) {
		this.wrappedSource = wrappedSource;
	}
	
	/**
	 * Gets the size of an attachment in bytes
	 * 
	 * This operation requires the attachment to be read in order to be able to
	 * calculate the size.
	 * 
	 * @param id
	 * @return
	 */
	protected long getAttachmentSize() {

		InputStream in = null;
		long total = 0;

		try {
			in = wrappedSource.getInputStream();

			int chunk = 512;
			int readBytes = 0;

			do {
				byte[] data = new byte[chunk];
				readBytes = in.read(data, 0, chunk);
				data = null;

				if (readBytes == -1) {
					break;
				}

				// Sum the bytes read and check the availability of data for the
				// next chunk.
				total += readBytes;
				chunk = in.available();
			} while (readBytes >= 0);
		} catch (Throwable t) {
			total = -1;
			logger.warn("Failed to get attachment size", t);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.warn("Failed to close stream", e);
				}
			}
		}

		return total;
	}
	
	public String getTitle() {
		return this.getTitle(false);
	}

	/**
	 * Gets the title of an attachment by its position.
	 * 
	 * The flag withSize controls whether the attachment size will be calculated or not. Attachment sizes require
	 * the attachment to be read in full.
	 * 
	 * @param id
	 * @return
	 */
	public String getTitle(boolean withSize) {
		String title = wrappedSource.getName() + ", " + wrappedSource.getContentType();
		long size = this.getSize(withSize);

		if (withSize && size > 0) {
			title += " " + Math.round(((float)size)/(1024*1024)) + " KB";
		}

		return title;
	}

	/* (non-Javadoc)
	 * @see javax.activation.DataSource#getContentType()
	 */
	public String getContentType() {
		return wrappedSource.getContentType();
	}

	/* (non-Javadoc)
	 * @see javax.activation.DataSource#getInputStream()
	 */
	public InputStream getInputStream() {
		try {
			return wrappedSource.getInputStream();
		} catch (IOException e) {
			logger.error("Failed to get attachment input stream", e);
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.activation.DataSource#getName()
	 */
	public String getName() {
		return wrappedSource.getName();
	}

	/* (non-Javadoc)
	 * @see javax.activation.DataSource#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException {
		return wrappedSource.getOutputStream();
	}
	
	
}
