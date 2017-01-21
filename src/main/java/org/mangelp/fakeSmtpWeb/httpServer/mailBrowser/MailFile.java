/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 */

package org.mangelp.fakeSmtpWeb.httpServer.mailBrowser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.commons.cli.ParseException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.util.MimeMessageParser;
import org.jsoup.Jsoup;

/**
 * Wrapper around a MIME-mail file that is parsed from a real on-disk file when
 * the object is instanced.
 *
 * @author mangelp
 *
 */
public class MailFile {

	private Date creationDate;
	private long size;
	private String id = null;
	private String fileName = null;
	private File file = null;
	private String subject = null;
	private String from = null;
	private String replyTo = null;
	private String htmlContent = null;
	private String plainContent = null;
	private int order;
	private String[] to = ArrayUtils.EMPTY_STRING_ARRAY;
	private String[] cc = ArrayUtils.EMPTY_STRING_ARRAY;
	private String[] bcc = ArrayUtils.EMPTY_STRING_ARRAY;
	private List<DataSource> attachments = new ArrayList<DataSource>();

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return this.creationDate;
	}

	/**
	 * @param creationDate
	 *            the creationDate to set
	 */
	protected void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the size in bytes
	 */
	public long getSize() {
		return this.size;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	protected void setSize(long size) {
		this.size = size;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	protected void setFile(File file) {
		this.file = file;
		String fileName = StringUtils.trimToEmpty(file.getName());
		fileName = Base64.encodeBase64String(fileName.getBytes());

		this.fileName = fileName;
	}

	public File getFile() {
		return this.file;
	}

	public String getFileName() {
		return this.fileName;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return this.subject;
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	protected void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the from
	 */
	public String getFrom() {
		return this.from;
	}

	/**
	 * @param from
	 *            the from to set
	 */
	protected void setFrom(String from) {
		this.from = from;
	}

	/**
	 *
	 * @return
	 */
	public String getReplyTo() {
		return this.replyTo;
	}

	/**
	 *
	 * @param replyTo
	 */
	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

	/**
	 * @return the to
	 */
	public String[] getTo() {
		return this.to;
	}

	/**
	 * @param to
	 *            the to to set
	 */
	protected void setTo(String[] to) {
		this.to = to;
	}

	/**
	 * @return the cc
	 */
	public String[] getCc() {
		return this.cc;
	}

	/**
	 * @param cc
	 *            the cc to set
	 */
	protected void setCc(String[] cc) {
		this.cc = cc;
	}

	/**
	 * @return the bcc
	 */
	public String[] getBcc() {
		return this.bcc;
	}

	/**
	 * @param bcc
	 *            the bcc to set
	 */
	protected void setBcc(String[] bcc) {
		this.bcc = bcc;
	}

	/**
	 * @return the attachment titles
	 */
	public String[] getAttachmentTitles() {
		String[] titles = new String[this.attachments.size()];

		for (int i = 0; i < titles.length; i++) {
			DataSource attachmentData = this.attachments.get(i);
			titles[i] = attachmentData.getName() + ", " + attachmentData.getContentType();
		}

		return titles;
	}

	public List<DataSource> getAttachments() {
		return this.attachments;
	}

	protected void setAttachments(List<DataSource> attachments) {
		this.attachments = attachments;
	}

	/**
	 * @return the htmlContent
	 */
	public String getHtmlContent() {
		return this.htmlContent;
	}

	/**
	 * @param htmlContent
	 *            the htmlContent to set
	 */
	protected void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	/**
	 * @return the plainContent
	 */
	public String getPlainContent() {
		return this.plainContent;
	}

	/**
	 * @param plainContent
	 *            the plainContent to set
	 */
	protected void setPlainContent(String plainContent) {
		this.plainContent = plainContent;
	}

	public int getOrder() {
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	/**
	 *
	 * @param file
	 * @throws MessagingException
	 * @throws ParseException
	 * @throws IOException
	 */
	public MailFile(File file) throws MessagingException, ParseException, IOException {
		this.setFile(file);
		this.getAttributes();
		this.parseEmail();
	}

	protected void getAttributes() throws IOException {
		BasicFileAttributes attr = Files.readAttributes(this.getFile().toPath(), BasicFileAttributes.class);

		this.setCreationDate(new Date(attr.creationTime().toMillis()));
		this.setSize(attr.size());
	}

	/**
	 * Parse the file on disk using a MimeMessageParser and set all the instance
	 * properties we will be using.
	 *
	 * @throws FileNotFoundException
	 * @throws MessagingException
	 * @throws ParseException
	 * @throws IOException
	 */
	protected void parseEmail() throws FileNotFoundException, MessagingException, ParseException, IOException {

		InputStream inputStream = new BufferedInputStream(new FileInputStream(this.getFile()));

		try {
			final Session session = Session.getDefaultInstance(new Properties());

			MimeMessage message = new MimeMessage(session, inputStream);
			MimeMessageParser mimeParser = new MimeMessageParser(message);

			mimeParser.parse();

			this.setSubject(mimeParser.getSubject());
			this.setFrom(mimeParser.getFrom());
			this.setReplyTo(mimeParser.getReplyTo());

			ArrayList<String> toList = new ArrayList<String>();
			for (Address emailAddress : mimeParser.getTo()) {
				toList.add(emailAddress.toString());
			}

			this.setTo(toList.toArray(this.getTo()));

			ArrayList<String> ccList = new ArrayList<String>();
			for (Address emailAddress : mimeParser.getCc()) {
				ccList.add(emailAddress.toString());
			}

			this.setCc(ccList.toArray(this.getCc()));

			ArrayList<String> bccList = new ArrayList<String>();
			for (Address emailAddress : mimeParser.getBcc()) {
				bccList.add(emailAddress.toString());
			}

			this.setBcc(bccList.toArray(this.getBcc()));

			if (mimeParser.hasAttachments()) {
				ArrayList<String> attachments = new ArrayList<String>(mimeParser.getAttachmentList().size());

				for (DataSource ds : mimeParser.getAttachmentList()) {
					attachments.add(ds.getName() + " " + ds.getContentType());
				}
			}

			if (mimeParser.hasHtmlContent()) {
				this.setHtmlContent(mimeParser.getHtmlContent());
			}

			if (mimeParser.hasPlainContent()) {
				this.setPlainContent(mimeParser.getPlainContent());
			}

		} catch (Exception e) {
			throw new ParseException("Failed to parse file " + this.getFile().toString() + ": " + e.getMessage());
		}

		this.setId(DigestUtils.sha1Hex(inputStream));

		inputStream.close();
	}

	/**
	 * Gets one or more email addresses by its type (from, to, cc, bcc and
	 * reply-to).
	 *
	 * @param type
	 * @return
	 */
	public String getEmailAddressesAsString(String type) {
		String[] addresses = null;

		if (StringUtils.equalsIgnoreCase("from", type)) {
			addresses = new String[] { this.getFrom() };
		} else if (StringUtils.equalsIgnoreCase("reply-to", type)) {
			addresses = new String[] { this.getReplyTo() };
		} else if (StringUtils.equalsIgnoreCase("to", type)) {
			addresses = this.getTo();
		} else if (StringUtils.equalsIgnoreCase("cc", type)) {
			addresses = this.getCc();
		} else if (StringUtils.equalsIgnoreCase("bcc", type)) {
			addresses = this.getBcc();
		}

		return StringUtils.join(addresses, ", ");
	}

	/**
	 * Gets email size with the given units
	 *
	 * @param units
	 * @return
	 */
	public String getSize(String units) {
		if (StringUtils.isBlank(units)) {
			return this.size + " b";
		} else if (units.length() > 1) {
			throw new IllegalArgumentException("Invalid units: " + units + ". Valid units are: k, m, b");
		}

		return this.getSize(units.charAt(0));
	}

	/**
	 * Gets email size with the given units
	 *
	 * @param units
	 * @return
	 */
	public String getSize(char units) {
		if (StringUtils.isBlank(units + "")) {
			return this.size + " b";
		}

		switch (Character.toLowerCase(units)) {
		case 'k':
			return (Math.round(((float) this.size) / 1024)) + " KB";
		case 'm':
			return (Math.round(((float) this.size) / (1024 * 1024))) + " MB";
		case 'b':
			return this.size + " B";
		default:
			throw new IllegalArgumentException("Invalid units: " + units + ". Valid units are: k, m, b");
		}
	}

	/**
	 * Get creation date as an string given the date format
	 *
	 * @param pattern
	 * @return
	 */
	public String getCreationDate(String pattern) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

		return dateFormat.format(this.getCreationDate());
	}

	/**
	 * Gets the email body.
	 *
	 * First checks the plain email format and returns it first, if not present
	 * checks the HTML format and if present removes all the HTML tags and
	 * returns it.
	 *
	 * Whitespace is trimmed from the result and an String instance is always
	 * returned (even if empty).
	 *
	 * @return
	 */
	public String getContent() {
		String content = null;

		if (!StringUtils.isBlank(this.getPlainContent())) {
			content = this.getPlainContent();
		} else if (!StringUtils.isBlank(this.getHtmlContent())) {
			content = Jsoup.parse(this.getHtmlContent()).text();
		}

		content = StringUtils.trimToEmpty(content);

		return content;
	}

	/**
	 * Returns the content but reduced to a maximum of size-4 characters.
	 *
	 * If the content text size is over size-4 characters then is cut down to
	 * that length and an ellipsis is appended to it.
	 *
	 * @param size
	 * @return
	 */
	public String getExcerpt(int size) {
		String content = this.getContent();

		if (content.length() > (size - 4)) {
			content = StringUtils.substring(content, 0, size - 4) + " ...";
		}

		return content;
	}
}
