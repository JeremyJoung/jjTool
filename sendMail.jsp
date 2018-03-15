<%@page import="javax.mail.*,java.util.*,javax.activation.*,javax.mail.internet.*,javax.mail.PasswordAuthentication" %>

<%!
/**
@param toList	(String[][]) Receiver data. [[mail, name, receiveType(BB BCC)], ...]
*/

String sendMail(String host, String[] sendData , String subject, String mess, String to)
{	return sendMail(host, sendData , subject, mess, new String[][]{{to}});	}
String sendMail(String host, String[] sendData , String subject, String mess, String[] to)
{	return sendMail(host, sendData , subject, mess, new String[][]{to});	}
String sendMail(String host, String[] sendData , String subject, String mess, String[][] toList)
{
	String username = "", password = "", port="25";
	String sender = sendData[0], sendName = sendData.length>1?sendData[1]:sendData[0];
	javax.mail.Authenticator auth = null;

	if(toList.length==0)	return "null receive";
	if(sendData.length>3)
	{
		username = sendData[2];
		password = sendData[3];
		auth=new SMTPAuthenticator(sendData[2],sendData[3]);
	}
	if(sendData.length>4)
		port = sendData[4];
	boolean sessionDebug = false;

	try
	{
	  // 設定所要用的Mail 伺服器和所使用的傳送協定
	  java.util.Properties props = System.getProperties();
	  props.put("mail.transport.protocol", "smtp"); //設定所使用的protocol為SMTP(Small Mail Transfer Protocol)
	  props.put("mail.smtp.host", host);
	if(sendData.length>3)
	{
		props.put("mail.smtp.auth","true");
		props.put("mail.user", username);
		props.put("mail.password", password);
		if(sendData.length>4)
			props.put("mail.smtp.port", port);
	}
	
	// 產生新的Session 服務
//	javax.mail.Session mailSession = javax.mail.Session.getDefaultInstance(props, auth); 
	javax.mail.Session mailSession = auth==null?javax.mail.Session.getInstance(props):javax.mail.Session.getInstance(props, auth); 
	mailSession.setDebug(sessionDebug);
	Message msg = new MimeMessage(mailSession);
	
	msg.setFrom(new InternetAddress(sender, sendName)); // 設定傳送郵件的發信人
	
	ArrayList to = new ArrayList(), cc = new ArrayList(), bcc = new ArrayList();

	for(int i=0;i<toList.length;i++)
	{
		if(toList[i]==null || toList[i][0].indexOf("@")==-1) continue;
		InternetAddress ia = (toList[i].length>1 ? new InternetAddress(toList[i][0], toList[i][1]) : new InternetAddress(toList[i][0]));
		
		if(toList[i].length<=2 )	to.add(ia);
		else if(toList[i][2].toUpperCase().equals("CC"))	cc.add(ia);
		else if(toList[i][2].toUpperCase().equals("BCC"))	bcc.add(ia);
		else	to.add(ia);
	}

	if(to.size()>0) msg.setRecipients(Message.RecipientType.TO, (InternetAddress[])to.toArray(new InternetAddress[to.size()]));
	if(cc.size()>0) msg.setRecipients(Message.RecipientType.CC, (InternetAddress[])cc.toArray(new InternetAddress[cc.size()]));
	if(bcc.size()>0) msg.setRecipients(Message.RecipientType.BCC, (InternetAddress[])bcc.toArray(new InternetAddress[bcc.size()]));
	


	  msg.setSubject(subject);
	  msg.setSentDate(new Date());
	  msg.setText(mess);

	  Transport.send(msg);
	}
	catch (Exception mex) 
	{
		return "mail Err: "+mex;
	}

	return null;
}

	class SMTPAuthenticator extends javax.mail.Authenticator {
		private String SMTP_AUTH_PWD;
		private String SMTP_AUTH_USER;
		public SMTPAuthenticator(String SMTP_AUTH_USER,String SMTP_AUTH_PWD)
		{
			super();
			this.SMTP_AUTH_USER = SMTP_AUTH_USER;
			this.SMTP_AUTH_PWD = SMTP_AUTH_PWD;
		}
        public PasswordAuthentication getPasswordAuthentication() {
           String username = SMTP_AUTH_USER;
           String password = SMTP_AUTH_PWD;
           System.out.println(SMTP_AUTH_USER+";"+SMTP_AUTH_PWD);
           return new PasswordAuthentication(username, password);
        }
    }

%>