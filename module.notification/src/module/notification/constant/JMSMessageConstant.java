package module.notification.constant;

public class JMSMessageConstant {

	public static final String SEQ_JMS = "JMS_REF_NUMBER";

	public static final String SEQ_JMS_FORMAT = "%05d";

	public static final String CONTENT_TYPE_XML = "xml";

	public static final String CONTENT_ROOT_NODE = "Message";

	public static final String CONTENT_HEADER_NODE = "Header";

	public static final String CONTENT_BODY_NODE = "Body";

	public static final String HEADER_CONTENT_TYPE = "contentType";

	public static final String HEADER_SOURCE_ID = "source.id";

	public static final String HEADER_PROTOCOL = "source.protocol";

	public static final String HEADER_EVENT_TYPE = "event.type";

	public static final String HEADER_EVENT_DATETME = "event.dateTime";

	public static final String HEADER_TRASACTION_CODE = "transaction.code";

	public static final String HEADER_TRASACTION_TYPE = "transaction.type";

	public static final String HEADER_TRANSACTION_STATUS = "transaction.status";

	public static final String MESSAGE_HEADER_REF_NO = "refNo";

	public static final String MESSAGE_HEADER_USER_ID = "userId";
	
	public static final String MESSAGE_HEADER_NOTIFICATION_TYPE = "notificationType";

	public static final String MESSAGE_HEADER_TRANSACTION_CODE = "transactionCode";
	
	public static final String MESSAGE_HEADER_TRANSACTION_TYPE = "transactionType";
	
	public static final String EVENT_TYPE_NOTIFICATION = "notification";

	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static final String INTERNAL_XML = "XML";
	
	private JMSMessageConstant() {
	}
}