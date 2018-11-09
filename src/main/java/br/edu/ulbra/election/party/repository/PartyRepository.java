package br.edu.ulbra.election.party.repository;

import org.springframework.data.repository.CrudRepository;

import br.edu.ulbra.election.party.model.Party;

public interface PartyRepository extends CrudRepository<Party, Long> {
	Party findByName(String partyname);
	Party findByCode(String partycode);
	Party findByNumber(Integer partynumber);
	
}