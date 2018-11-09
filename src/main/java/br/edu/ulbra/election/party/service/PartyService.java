package br.edu.ulbra.election.party.service;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.edu.ulbra.election.party.repository.PartyRepository;
import br.edu.ulbra.election.party.output.v1.GenericOutput;
import br.edu.ulbra.election.party.exception.GenericOutputException;
import br.edu.ulbra.election.party.input.v1.PartyInput;
import br.edu.ulbra.election.party.model.Party;
import br.edu.ulbra.election.party.output.v1.PartyOutput;;

@Service
public class PartyService {

	private final PartyRepository partyRepository;

	private final ModelMapper modelMapper;

	private static final String MESSAGE_INVALID_ID = "Invalid id";
	private static final String MESSAGE_PARTY_NOT_FOUND = "Party not found";

	@Autowired
	public PartyService(PartyRepository partyRepository, ModelMapper modelMapper){
		this.partyRepository = partyRepository;
		this.modelMapper = modelMapper;
	}

	public List<PartyOutput> getAll(){
		Type partyOutputListType = new TypeToken<List<PartyOutput>>(){}.getType();
		return modelMapper.map(partyRepository.findAll(), partyOutputListType);
	}

	public PartyOutput create(PartyInput partyInput) {
		validateInput(partyInput, false);
		Party party = modelMapper.map(partyInput, Party.class);
		party = partyRepository.save(party);
		return modelMapper.map(party, PartyOutput.class);
	}

	public PartyOutput getById(Long partyId){
		if (partyId == null){
			throw new GenericOutputException(MESSAGE_INVALID_ID);
		}

		Party party = partyRepository.findById(partyId).orElse(null);
		if (party == null){
			throw new GenericOutputException(MESSAGE_PARTY_NOT_FOUND);
		}

		return modelMapper.map(party, PartyOutput.class);
	}

	public PartyOutput update(Long partyId, PartyInput partyInput) {
		if (partyId == null){
			throw new GenericOutputException(MESSAGE_INVALID_ID);
		}
		validateInput(partyInput, true);

		Party party = partyRepository.findById(partyId).orElse(null);
		if (party == null){
			throw new GenericOutputException(MESSAGE_PARTY_NOT_FOUND);
		}

		party.setCode(partyInput.getCode());
		party.setName(partyInput.getName());
		party.setNumber(partyInput.getNumber());

		party = partyRepository.save(party);
		return modelMapper.map(party, PartyOutput.class);
	}

	public GenericOutput delete(Long partyId) {
		if (partyId == null){
			throw new GenericOutputException(MESSAGE_INVALID_ID);
		}

		Party party = partyRepository.findById(partyId).orElse(null);
		if (party == null){
			throw new GenericOutputException(MESSAGE_PARTY_NOT_FOUND);
		}

		partyRepository.delete(party);

		return new GenericOutput("Party deleted");
	}


	public Party findByName(String partyname){
		return partyRepository.findByName(partyname);
	}
	public Party findByCode(String partycode){
		return partyRepository.findByCode(partycode);
	}
	public Party findByNumber(Integer partynumber){
		return partyRepository.findByNumber(partynumber);
	}

	private void validateInput(PartyInput partyInput, boolean isUpdate){
		if (StringUtils.isBlank(partyInput.getName())){
			throw new GenericOutputException("Invalid name");
		}
		if (StringUtils.isBlank(partyInput.getCode())){
			throw new GenericOutputException("Invalid Code");
		}
		if (partyInput.getNumber() == null || partyInput.getNumber() == 0){
			throw new GenericOutputException("Invalid Number");
		}


		if (this.findByCode(partyInput.getCode()) != null) {
			throw new GenericOutputException("Codigo ja cadastrado!");
		}
		if (this.findByNumber(partyInput.getNumber()) != null) {
			throw new GenericOutputException("Numero ja cadastrado!");
		}

		if(Integer.toString(partyInput.getNumber()).length() != 2 ) {
			throw new GenericOutputException("Numero teve ter 2 digitos");
		}
		if(partyInput.getName().length() < 5) {
            throw new GenericOutputException("Nome teve ter no minimo 5 caracteres");
        }
	}
}
