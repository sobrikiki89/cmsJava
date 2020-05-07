package module.upload.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.core.service.impl.AbstractServiceImpl;
import module.upload.model.UploadedFile;
import module.upload.service.UploadedFileService;

@Service
public class UploadedFileServiceImpl extends AbstractServiceImpl implements UploadedFileService {

	@Autowired
	private SessionFactory sessionFactory;

	@Transactional
	public List<UploadedFile> saveAttachment(List<UploadedFile> filesList) {
		Session session = sessionFactory.getCurrentSession();
		List<UploadedFile> files = new ArrayList<UploadedFile>();
		for (UploadedFile attachment : filesList) {
			UploadedFile file = new UploadedFile();
			file.setName(attachment.getName());
			file.setFileCategory(attachment.getFileCategory());
			file.setMimeType(attachment.getMimeType());
			file.setFileSize(attachment.getFileSize());
			file.setContent(attachment.getContent());
			if (attachment.getId() == null) {
				session.persist(file);
			} else {
				session.merge(file);
			}
			files.add(file);
		}
		return files;
	}

	@Transactional
	public UploadedFile getUploadedFileById(Long objId) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("SELECT o FROM " + UploadedFile.class.getName() + " o " + " WHERE o.id = :objId")
				.setParameter("objId", objId);
		UploadedFile file = (UploadedFile) q.uniqueResult();
		return file;
	}

	@Transactional
	public UploadedFile saveSingleAttachment(UploadedFile filesList) {
		Session session = sessionFactory.getCurrentSession();
		UploadedFile file = new UploadedFile();
		file.setName(filesList.getName());
		file.setFileCategory(filesList.getFileCategory());
		file.setMimeType(filesList.getMimeType());
		file.setFileSize(filesList.getFileSize());
		file.setContent(filesList.getContent());
		if (filesList.getId() == null) {
			session.persist(file);
		} else {
			session.merge(file);
		}
		return file;
	}
}
