//This is the server for Turnin
//NOTE: activation.jar and mail.jar must be added to:
//      Linux: /usr/java/jdkX.X.X/jre/lib/ext
//      OS X: /System/Library/Frameworks/JavaVM.framework/Versions/X.X.X/Home/lib/ext
//
import java.net.*;
import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

public class TurninServer {
	public final static String VERSION = "4.1";
	public final static int PORT = 4040;
	static Hashtable submittals = new Hashtable();

public static void main (String[] args) {
	ServerSocket serverSocket = null;
	boolean listening = true;
	RandomAccessFile log = null;

//Read previous submittals from the log file
//Set up Hashtable of those so that users can be
//  warned about multiple submittals.
	try {
		serverSocket = new ServerSocket(PORT);
	} catch (IOException e) {
		System.out.println("Could not listen on port: " + PORT + ", " + e);
		System.exit(1);
	}
	
	try {
		log = new RandomAccessFile("Turnin.log","rw");
		String line;
		while((line=log.readLine()) != null) {
			StringTokenizer t = new StringTokenizer(line," ");
			t.nextToken();    //receipt#
			t.nextToken();    //"for"
			String userCourseLab = t.nextToken() + "\t" + t.nextToken() + "\t" + t.nextToken();
			if (submittals.containsKey(userCourseLab)) {
				int num = ((Integer)(submittals.get(userCourseLab))).intValue();
				num++;
				submittals.put(userCourseLab,new Integer(num));
			} else
				submittals.put(userCourseLab,new Integer(1));
		}
		log.seek(log.length());
	} catch (IOException e) {
		System.out.println("Could not open log file");
		System.exit(1);
	}

//Listen for a connection and create a thread to handle it
	while (listening) {
		Socket clientSocket = null;
		try {
			clientSocket = serverSocket.accept();
		} catch (IOException e) {
			System.out.println("Accept failed: " + PORT + ", " + e);
			continue;
		}
		new TurninServerThread(clientSocket,log,submittals).start();
	}
	
	try {
		serverSocket.close();
	} catch (IOException e) {
		System.out.println("Could not close server socket. " + e);
	}

}
}
class TurninServerThread extends Thread{
	Socket socket = null;
	RandomAccessFile log;
	DataInputStream is;
	DataOutputStream os;
	Hashtable submittals;

TurninServerThread(Socket socket,RandomAccessFile log, Hashtable submittals) {
//	super("TurninServerThread");
	this.socket = socket;
	this.log = log;
	this.submittals = submittals;
}
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
private String generateReceipt(String user) {
	GregorianCalendar today = new GregorianCalendar();
	int month = today.get(Calendar.MONTH);
	int day = today.get(Calendar.DAY_OF_MONTH);
	int hour = today.get(Calendar.HOUR_OF_DAY);
	int minute = today.get(Calendar.MINUTE);
	int second = today.get(Calendar.SECOND);
	int number = month + 12 * ((day - 1) + 31 * (hour + 24 * (minute + 60 * second)));
	user = user.toLowerCase();
	int j = 0;
	int sum = 0;
	for (int i=0; i < user.length(); i++) {
		if (j % 5 == 0) {
			number += sum;
			sum = 0;
		}
		int ndx = "abcdefghijklmnopqrstuvwxyz".indexOf(user.charAt(i));
		if (ndx >= 0) {
			sum = sum * 26 + ndx;
			j++;
		}
	}
	number += sum;
	String receipt = "";
	for (int i=0; i < 6; i++) {
		receipt += "ACEFGHJKLMNPRSTUVWXYZ".charAt(number % 21);
		number /= 21;
	}
	return receipt;
}
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
private String getLabFile(String course) throws FileNotFoundException, IOException {
	String line;
	BufferedReader in;
	StringTokenizer tok;
	in = new BufferedReader(new FileReader("courses.txt"));
	while ((line = in.readLine()) != null) {
		if (line.startsWith(course)) {
			tok = new StringTokenizer(line, " \t\n\r");
			tok.nextToken();	//first token is course id
			in.close();
			return tok.nextToken(); //second token is file name for labs
		}
	}
	in.close();
	return line;
}
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
private String getUserFile(String course) throws FileNotFoundException, IOException {
	String line;
	BufferedReader in;
	StringTokenizer tok;
	in = new BufferedReader(new FileReader("courses.txt"));
	while ((line = in.readLine()) != null) {
		if (line.startsWith(course)) {
			tok = new StringTokenizer(line, " \t\n\r");
			tok.nextToken();	//first token is course id
			tok.nextToken();	//second token is file name for labs
			in.close();
			return tok.nextToken(); //third token is file name for users
		}
	}
	in.close();
	return line;
}
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
private String getMailAddr(String course) throws FileNotFoundException, IOException {
	String line;
	BufferedReader in;
	StringTokenizer tok;
	in = new BufferedReader(new FileReader("courses.txt"));
	while ((line = in.readLine()) != null) {
		if (line.startsWith(course)) {
			tok = new StringTokenizer(line, " \t\n\r");
			tok.nextToken();	//first token is course id
			tok.nextToken();	//second token is file name for labs
			tok.nextToken();	//third token is file name for users
			in.close();
			return tok.nextToken(); //fourth token is e-mail address
		}
	}
	in.close();
	return line;
}
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
private String getSMTPhost(String course) throws FileNotFoundException, IOException {
	String line;
	BufferedReader in;
	StringTokenizer tok;
	in = new BufferedReader(new FileReader("courses.txt"));
	while ((line = in.readLine()) != null) {
		if (line.startsWith(course)) {
			tok = new StringTokenizer(line, " \t\n\r");
			tok.nextToken();	//first token is course id
			tok.nextToken();	//second token is file name for labs
			tok.nextToken();	//third token is file name for users
			tok.nextToken();	//fourth token is e-mail address
			in.close();
			return tok.nextToken(); //fifth token is SMTP host
		}
	}
	in.close();
	return line;
}
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
private String getValidLabs(String course) throws FileNotFoundException, IOException {
	GregorianCalendar today = new GregorianCalendar();
	BufferedReader in = new BufferedReader(new FileReader(getLabFile(course)));
	String line;
	String labList = "";
	int year1,month1,day1,hour1=0,minute1=0;
	int year2,month2,day2,hour2=23,minute2=59;
	while ((line = in.readLine()) != null) {
		line = line.trim();
		if (!(line.equals(""))) {	//ignore blank lines
			StringTokenizer tok = new StringTokenizer(line, " ,/-\t\n\r");
			String lab = tok.nextToken();
			year1 = Integer.parseInt(tok.nextToken());
			month1 = Integer.parseInt(tok.nextToken()) - 1;
			day1 = Integer.parseInt(tok.nextToken());
			String time = tok.nextToken();
			int timeSep = time.indexOf(":");
			if (timeSep >= 0) {
				hour1 = Integer.parseInt(time.substring(0,timeSep));
				minute1 = Integer.parseInt(time.substring(timeSep+1));
				year2 = Integer.parseInt(tok.nextToken());
			} else {
				year2 = Integer.parseInt(time);
			}
			month2 = Integer.parseInt(tok.nextToken()) - 1;
			day2 = Integer.parseInt(tok.nextToken());
			if (tok.hasMoreTokens()) {
				time = tok.nextToken();
				timeSep = time.indexOf(":");
				if (timeSep >= 0) {
					hour2 = Integer.parseInt(time.substring(0,timeSep));
					minute2 = Integer.parseInt(time.substring(timeSep+1));
				}
			}
//System.out.println(lab + " " + (new GregorianCalendar(year1,month1,day1,hour1,minute1)).getTime() + 
//	" " + today.getTime() + " " + (new GregorianCalendar(year2,month2,day2,hour2,minute2)).getTime());
			if (today.after(new GregorianCalendar(year1,month1,day1,hour1,minute1)) &&
				 today.before(new GregorianCalendar(year2,month2,day2,hour2,minute2)))
				labList += lab + '\t';
		}
	}
	in.close();
	return labList;
}
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
//NOTE: This assumes lab names are distinct
private String getLabAttributes(String courseLab) throws FileNotFoundException, IOException {
	int ndx = courseLab.indexOf("\t");
	String course = courseLab.substring(0,ndx);
	String lab = courseLab.substring(ndx+1);
	BufferedReader in = new BufferedReader(new FileReader(getLabFile(course)));
	String line;
	String attributes = "";
	while ((line = in.readLine()) != null) {
		line = line.trim();
		if (!(line.equals(""))) {	//ignore blank lines
			StringTokenizer tok = new StringTokenizer(line, " \t\n\r");
			String labx = tok.nextToken();
			if (labx.equals(lab)) {
				String token = tok.nextToken();	//start date
				token = tok.nextToken();
				if (token.indexOf(":") >= 0) {	//is it a time spec?
					token = tok.nextToken();	//now we have end date
				}
				if (tok.hasMoreTokens()) {
					token = tok.nextToken();
					if (token.indexOf(":") < 0) {	//not time, must be attr
						attributes = token + " ";
					}
					while (tok.hasMoreTokens()) {
						attributes += tok.nextToken() + " ";	//collect all attr
					}
				}
			}
		}
	}
	in.close();
	return attributes;
}
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
private String getValidUsers(String course) throws FileNotFoundException, IOException {
	BufferedReader in = new BufferedReader(new FileReader(getUserFile(course)));
	String userList = "";
	String line;
	while ((line = in.readLine()) != null) {
		line = line.trim();
		if (!(line.equals(""))) {	//ignore blank lines
			userList += line + '\t';
		}
	}
	in.close();
	return userList;
}
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
private String countSubmittal(String userCourseLab) throws FileNotFoundException, IOException {
	if (submittals.containsKey(userCourseLab))
		return ((Integer)(submittals.get(userCourseLab))).toString();
	return "0";
}
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
private String acceptSubmittal(String userCourseLab) throws FileNotFoundException, IOException {
	StringTokenizer tok = new StringTokenizer(userCourseLab,"\t");
	String user = tok.nextToken();
	String course = tok.nextToken();
	String lab = tok.nextToken();
	String fileName = tok.nextToken();
	long len = Long.valueOf(tok.nextToken()).longValue();
	if (("\t" + getValidUsers(course).toLowerCase()).indexOf("\t" + user + "\t") < 0)
		return("500 invalid user for " + course);
	if (("\t" + getValidLabs(course)).indexOf("\t" + lab + "\t") < 0)
		return("500 Lab no longer accepted");
	if (len > Integer.MAX_VALUE)
		return("500 File too large");
	os.writeBytes("200 send it\r\n");
	os.flush();
	
	byte[] ba = new byte[(int)len];
	is.readFully(ba);

	String receipt = generateReceipt(user);
	
	String toAddr = getMailAddr(course);
	String smtpHost = getSMTPhost(course);
	String ccAddress = null;
	if (getLabAttributes(course + "\t" + lab).toLowerCase().indexOf("ccstudent") >= 0)
		ccAddress = user;
	try {
		Mailer.mailIt(toAddr, ccAddress, user, smtpHost, fileName, course, lab, receipt, ba, false);
	} catch (MessagingException mex) {
		mex.printStackTrace();
		Exception ex = null;
		if ((ex = mex.getNextException()) != null) {
			ex.printStackTrace();
		}
		return "500 MessagingException - Contact your instructor";
	}

	try {
		log.writeBytes(receipt + " for " + user + " " + course + " " + lab + " on " + new Date() + " from " + socket.getInetAddress() + "\n");
	} catch (IOException e) {
		System.out.println("Error writing to log file: " + e);
	}

//update the submittals Hashtable (not thread safe, but who cares)
	String submittalsKey = user + "\t" + course + "\t" + lab;
	if (submittals.containsKey(submittalsKey)) {
		int num = ((Integer)(submittals.get(submittalsKey))).intValue();
		num++;
		submittals.put(submittalsKey,new Integer(num));
	} else
		submittals.put(submittalsKey,new Integer(1));
	
	return "200 " + receipt;

}
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
private String processInput(String inputLine) {
/* The client can send the following requests:
		COMMAND       PARAM              RESULT
		version       none               string in the form "x.x"
		"x.x"courses  none               list of valid courses; eg, COSC1203-055
		labs          course             list of labs for that course
		users         course             list of users enrolled in that course  **DELETED**
		mail          course             e-mail address to which a submittal should be sent
		receipt       user               encoded timestamp & user
		count         user\tcourse\tlab  number of previous submittals
		submit        user\tcourse\tlab\tfileName  receipt
		Bye           none               close this socket
	the parameter must be separated from the command by a single space.
	The list of courses is in a file named "courses.txt" which contains lines with fields
		course	 lab_file_name	 user_file_name	 mail_address
	The file containing lab info has one line per lab in the following format:
		lab name	 first acceptable date	  first non-acceptable date
	Dates are in the form yyyy/mm/dd hh:mm
	   where the time defaults to 23:59 if omitted
	A lab name must not contain blanks.
	The file of users has one name per line.
*/
	BufferedReader in;
	String line;
	StringTokenizer tok;
	String outputLine = "";
	try {
		if (inputLine.equals("version")) {
			outputLine = TurninServer.VERSION + '\t';
		}
		if (inputLine.equals(TurninServer.VERSION + "courses")) {
			in = new BufferedReader(new FileReader("courses.txt"));
			while ((line = in.readLine()) != null) {
				if (line.trim().equals("")) continue;
				if (line.charAt(0) == '#') continue;
				tok = new StringTokenizer(line, " \t\n\r");
				outputLine += tok.nextToken() + '\t';	//first token is course id
			}
			in.close();
		} else if (inputLine.startsWith("labs")) {
			outputLine = getValidLabs(inputLine.substring(5));
		} else if (inputLine.startsWith("attributes")) {
			outputLine = getLabAttributes(inputLine.substring(11));
//		} else if (inputLine.startsWith("users")) {
//			outputLine = getValidUsers(inputLine.substring(6));
		} else if (inputLine.startsWith("mail")) {
			outputLine = getMailAddr(inputLine.substring(5)) + '\t';
		} else if (inputLine.startsWith("receipt")) {
			outputLine = generateReceipt(inputLine.substring(8)) + '\t';
		} else if (inputLine.startsWith("count")) {
			outputLine = countSubmittal(inputLine.substring(6)) + '\t';
		} else if (inputLine.startsWith("submit")) {
			outputLine = acceptSubmittal(inputLine.substring(7)) + '\t';
		} else if (inputLine.equals("Bye")) {
			outputLine = "Bye\t";
		}
	} catch (IOException e) { System.out.println("error " + getName() + " " + new Date() + e); }
	if (outputLine.length() > 0)
		outputLine = outputLine.substring(0,outputLine.length()-1);
	System.out.flush();
	return outputLine;
}
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
public void run() {
	try {
		System.out.println("start " + getName() + " " + new Date() + " from " + socket.getInetAddress());
		System.out.flush();
		is = new DataInputStream(socket.getInputStream());
		os = new DataOutputStream(socket.getOutputStream());

		String inputLine, outputLine;

		while ((inputLine = is.readLine()) != null) {
//System.out.println("Command = " + inputLine);
			outputLine = processInput(inputLine);
//System.out.println("   Response = " + outputLine);
			if (outputLine.equals("Bye"))
				break;
			os.writeBytes(outputLine + "\r\n");
			os.flush();
		}
		os.close();
		is.close();
		socket.close();
		System.out.println("stop  " + getName() + " " + new Date());
		System.out.flush();
	} catch (IOException e) { System.out.println("abort " + getName() + " " + new Date() + " for " + e); System.out.flush(); }
}
}

/*
 * @(#)sendfile.java	1.8 00/05/24
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

class Mailer {

	public static void mailIt(String to, String ccAddress, String user, String smtp, String fileName,
    						  String course, String lab, String receipt, byte[] bytes, boolean debug)
    						  throws MessagingException {

		String msgText1 = "Receipt# = " + receipt + "\n";
		String subject = course + " " + lab + " " + user;

		// create some properties and get the default Session
		Properties props = System.getProperties();
		props.put("mail.smtp.host", smtp);

		Session session = Session.getDefaultInstance(props, null);
		session.setDebug(debug);

		// create a message
		MimeMessage msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress("Turnin@cs.tcu.edu"));
		InternetAddress[] replyAddress = {new InternetAddress(user)};
		msg.setReplyTo(replyAddress);
		InternetAddress[] address = {new InternetAddress(to)};
		msg.setRecipients(Message.RecipientType.TO, address);
		if (ccAddress != null)
			msg.setRecipients(Message.RecipientType.CC, replyAddress);
		msg.setSubject(subject);

		// create and fill the first message part
		MimeBodyPart mbp1 = new MimeBodyPart();
		mbp1.setText(msgText1);

		// create the second message part
		MimeBodyPart mbp2 = new MimeBodyPart();

		// attach the file to the message
		ByteArrayDataSource bads = new ByteArrayDataSource(bytes, null, fileName);
		mbp2.setDataHandler(new DataHandler(bads));
		mbp2.setFileName(bads.getName());

		// create the Multipart and its parts to it
		Multipart mp = new MimeMultipart();
		mp.addBodyPart(mbp1);
		mp.addBodyPart(mbp2);

		// add the Multipart to the message
		msg.setContent(mp);

		// set the Date: header
		msg.setSentDate(new Date());

		// send the message
		Transport.send(msg);

	}
}

class ByteArrayDataSource implements DataSource {

	static MimetypesFileTypeMap typeMap = new MimetypesFileTypeMap();

	byte[] bytes;
	String contentType, name;

	ByteArrayDataSource(byte[] bytes, String contentType, String name) {
		this.bytes = bytes;
		if(contentType == null)
			this.contentType = typeMap.getContentType(name);
		else
			this.contentType = contentType;
		this.name = name;
	}

	public String getContentType() {
		return contentType;
	}

	public InputStream getInputStream() {
		return new ByteArrayInputStream(bytes,0,bytes.length);
	}

	public String getName() {
		return name;
	}

	public OutputStream getOutputStream() throws IOException {
		throw new FileNotFoundException();
	}
}