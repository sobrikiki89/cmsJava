package module.notification.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.google.common.base.MoreObjects;

import app.core.exception.BaseRuntimeException;
import app.core.service.SequenceService;
import app.core.service.XMLDocumentService;
import module.notification.constant.JMSMessageConstant;
import module.notification.object.JMSMessage;
import module.notification.object.JMSMessageContent;
import module.notification.service.JMSMessageService;

@Component
public class JMSMessageServiceImpl implements JMSMessageService {

	private static final Logger LOGGER = LoggerFactory.getLogger(JMSMessageServiceImpl.class);

	private XMLDocumentService xmlDocumentService;

	private SequenceService sequenceService;

	@Produce
	private ProducerTemplate template;

	@Autowired
	public JMSMessageServiceImpl(XMLDocumentService xmlDocumentService, SequenceService sequenceService) {
		this.xmlDocumentService = xmlDocumentService;
		this.sequenceService = sequenceService;
	}

	@Override
	public JMSMessage createMessage(HttpServletRequest request, JMSMessageContent jmsMessageContent) {

		if (jmsMessageContent == null) {
			throw new BaseRuntimeException("JMS Message Content is required.");
		}

		if (jmsMessageContent.getTransaction() == null) {
			throw new BaseRuntimeException("JMS Message Transaction is required.");
		}

		if (jmsMessageContent.getNotificationType() == null) {
			throw new BaseRuntimeException("JMS Message Notification Type is required.");
		}

		JMSMessage message = new JMSMessage();
		message = constructHeader(message, request, jmsMessageContent);
		message = constructBody(message, jmsMessageContent);

		return message;
	}

	public void send(String uri, final JMSMessage message) {
		final Exchange response = template.send(uri, new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				exchange.getIn().setHeaders(message.getHeader());
				exchange.getIn().setBody(message.getMergedBody());
			}
		});
		if (response.isFailed()) {
			throw new BaseRuntimeException("JMS Message Transaction is required.", response.getException());
		}
	}

	protected JMSMessage constructHeader(JMSMessage message, HttpServletRequest request,
			JMSMessageContent jmsMessageContent) {

		message.addHeader(JMSMessageConstant.HEADER_CONTENT_TYPE, JMSMessageConstant.CONTENT_TYPE_XML);
		message.addHeader(JMSMessageConstant.HEADER_PROTOCOL, request.getProtocol());

		// Source protocol segment
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = headerNames.nextElement();

			String jmsKey = JMSMessageConstant.HEADER_PROTOCOL + "." + key;
			String value = request.getHeader(key);
			message.addHeader(jmsKey, value);
		}

		// Source channel segment
		message.addHeader(JMSMessageConstant.HEADER_SOURCE_ID, jmsMessageContent.getSrcId());

		// Event segment
		message.addHeader(JMSMessageConstant.HEADER_EVENT_TYPE, JMSMessageConstant.EVENT_TYPE_NOTIFICATION);
		message.addHeader(JMSMessageConstant.HEADER_EVENT_DATETME, convertDateTime(jmsMessageContent.getEventTime()));

		// Transaction segment
		message.addHeader(JMSMessageConstant.HEADER_TRASACTION_CODE, jmsMessageContent.getTransaction().getCode());
		message.addHeader(JMSMessageConstant.HEADER_TRASACTION_TYPE, jmsMessageContent.getTransaction().getType());
		message.addHeader(JMSMessageConstant.HEADER_TRANSACTION_STATUS, jmsMessageContent.getTransactionStatus());

		return message;
	}

	protected JMSMessage constructBody(JMSMessage message, JMSMessageContent jmsMessageContent) {

		Document xml = xmlDocumentService.createXML();
		Element root = xml.createElement(JMSMessageConstant.CONTENT_ROOT_NODE);

		// Message Header
		Element header = xml.createElement(JMSMessageConstant.CONTENT_HEADER_NODE);
		header.appendChild(xmlDocumentService.createField(xml, JMSMessageConstant.MESSAGE_HEADER_USER_ID,
				jmsMessageContent.getUserId()));
		header.appendChild(xmlDocumentService.createField(xml, JMSMessageConstant.MESSAGE_HEADER_NOTIFICATION_TYPE,
				jmsMessageContent.getNotificationType().toString()));
		header.appendChild(xmlDocumentService.createField(xml, JMSMessageConstant.MESSAGE_HEADER_TRANSACTION_CODE,
				jmsMessageContent.getTransaction().getCode()));
		header.appendChild(xmlDocumentService.createField(xml, JMSMessageConstant.MESSAGE_HEADER_TRANSACTION_TYPE,
				jmsMessageContent.getTransaction().getType()));
		header.appendChild(xmlDocumentService.createField(xml, JMSMessageConstant.MESSAGE_HEADER_REF_NO, sequenceService
				.getNextSequence(JMSMessageConstant.SEQ_JMS, JMSMessageConstant.SEQ_JMS_FORMAT, new Date())));
		root.appendChild(header);

		// Message Body
		root.appendChild(createNode(xml, JMSMessageConstant.CONTENT_BODY_NODE, jmsMessageContent.getMessageBody()));

		xml.appendChild(root);
		message.setBody(xmlDocumentService.toString(xml));

		return message;
	}

	protected Node createNode(Document xml, String nodeName, Map<String, Object> childs) {
		Node body = xml.createElement(nodeName);

		for (Node node : constructMessageBodyNode(xml, childs)) {
			body.appendChild(node);
		}
		return body;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected List<Node> constructMessageBodyNode(Document xml, Map<String, Object> body) {
		List<Node> nodes = new ArrayList<>();
		if (body != null) {
			for (Map.Entry<String, Object> entry : body.entrySet()) {
				Object value = entry.getValue();

				if (value == null) {
					LOGGER.warn("[{}] key value is null.", entry.getKey());
					nodes.add(xmlDocumentService.createField(xml, entry.getKey(), ""));
					continue;
				}

				if (value instanceof List) {
					for (Map map : (List<Map>) value) {
						nodes.add(createNode(xml, entry.getKey(), map));
					}
				} else if (value instanceof Map) {
					nodes.add(createNode(xml, entry.getKey(), (Map) value));
				} else if (value instanceof String) {
					String nodeValue = MoreObjects.firstNonNull((String) value, StringUtils.EMPTY);
					nodes.add(xmlDocumentService.createField(xml, entry.getKey(), nodeValue));
				} else {
					throw new BaseRuntimeException("Unsupported message data type. [" + entry.getKey() + "]");
				}
			}
		}
		return nodes;
	}

	private static String convertDateTime(Timestamp timestamp) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern(JMSMessageConstant.DATE_TIME_FORMAT);
		if (timestamp != null) {
			return formatter.print(LocalDateTime.fromDateFields(timestamp));
		} else {
			return formatter.print(LocalDateTime.now());
		}
	}
}