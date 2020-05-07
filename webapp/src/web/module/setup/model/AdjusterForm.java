package web.module.setup.model;

import java.util.List;

import module.setup.dto.ContactDTO;
import module.setup.dto.AdjusterDTO;
import module.setup.dto.StateDTO;
import web.core.model.AbstractForm;

public class AdjusterForm extends AbstractForm {

	private static final long serialVersionUID = 1L;
	
	private Long[] selected;
	
	private List<StateDTO> states;
		
	private AdjusterDTO AdjusterDTO;
	
	private ContactDTO contactDTO;

	public Long[] getSelected() {
		return selected;
	}

	public void setSelected(Long[] selected) {
		this.selected = selected;
	}

	public List<StateDTO> getStates() {
		return states;
	}

	public void setStates(List<StateDTO> states) {
		this.states = states;
	}

	public AdjusterDTO getAdjusterDTO() {
		return AdjusterDTO;
	}

	public void setAdjusterDTO(AdjusterDTO AdjusterDTO) {
		this.AdjusterDTO = AdjusterDTO;
	}

	public ContactDTO getContactDTO() {
		return contactDTO;
	}

	public void setContactDTO(ContactDTO contactDTO) {
		this.contactDTO = contactDTO;
	}

}
