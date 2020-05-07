package module.notification.listener.converter.impl;

import static module.notification.listener.helper.ContentTemplateHelper.replaceContent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import app.core.service.XMLDocumentService;
import module.notification.listener.converter.NotificationConverter;
import module.notification.object.Email;

@Component("ClaimAcknowledgmentConverter")
public class ClaimAcknowledgmentConverter implements NotificationConverter {

	private XMLDocumentService xmlDocumentService;

	@Autowired
	public ClaimAcknowledgmentConverter(XMLDocumentService xmlDocumentService) {
		this.xmlDocumentService = xmlDocumentService;
	}

	@Override
	public Email convertAlertEmail(Email mail, Document xml) {
		return convertNotificationEmail(mail, xml);
	}

	@Override
	public Email convertNotificationEmail(Email mail, Document xml) {
		String subject = mail.getSubject();
		subject = replaceContent(subject, "${subject}", xmlDocumentService.getField(xml, "subject"));
		mail.setSubject(subject);

		String template = mail.getMessage();
		template = replaceContent(template, "${claimNo}", xmlDocumentService.getField(xml, "claimNo"));
		template = replaceContent(template, "${companyName}", xmlDocumentService.getField(xml, "companyName"));
		template = replaceContent(template, "${policyNo}", xmlDocumentService.getField(xml, "policyNo"));
		template = replaceContent(template, "${lossDate}", xmlDocumentService.getField(xml, "lossDate"));
		template = replaceContent(template, "${estimatedLossAmount}",
				xmlDocumentService.getField(xml, "estimatedLossAmount"));
		template = replaceContent(template, "${lossDescription}", xmlDocumentService.getField(xml, "lossDescription"));
		template = replaceContent(template, "${insuredContactNo}",
				xmlDocumentService.getField(xml, "insuredContactNo"));
		template = replaceContent(template, "${remark}", xmlDocumentService.getField(xml, "remark"));

		mail.setMessage(template);
		return mail;
	}
}
