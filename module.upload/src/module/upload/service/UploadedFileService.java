package module.upload.service;

import java.util.List;

import module.upload.model.UploadedFile;

public interface UploadedFileService {

	public List<UploadedFile> saveAttachment(List<UploadedFile> filesList);

	public UploadedFile saveSingleAttachment(UploadedFile filesList);
	
	public UploadedFile getUploadedFileById(Long objId);
}
