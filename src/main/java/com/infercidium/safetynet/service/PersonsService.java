package com.infercidium.safetynet.service;

import com.infercidium.safetynet.dto.PersonsDTO;
import com.infercidium.safetynet.model.Persons;
import com.infercidium.safetynet.repository.PersonsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonsService implements PersonsI {

    /**
     * Instantiation of LOGGER in order to inform in console.
     */
    private static final Logger LOGGER
            = LoggerFactory.getLogger(PersonsService.class);

    /**
     * Instantiation of PersonsRepository.
     */
    private final PersonsRepository personsR;
    /**
     * Instantiation of SecondaryTableService.
     */
    private final SecondaryTableService secondaryTableS;

    /**
     * Class constructor.
     * @param personsRe this is PersonsRepository.
     * @param secondaryTableSe this is SecondarytableService.
     */
    public PersonsService(final PersonsRepository personsRe,
                          final SecondaryTableService secondaryTableSe) {
        this.personsR = personsRe;
        this.secondaryTableS = secondaryTableSe;
    }

    //Post, Put, Delete

    /**
     * Post Method Service.
     * @param persons to save.
     * @return persons saved.
     */
    @Override
    public Persons postPerson(final Persons persons) {
        persons.setAddress(secondaryTableS.checkAddress(persons.getAddress()));
        return this.personsR.save(persons);
    }

    /**
     * Edit Method Service.
     * @param firstName to check Persons.
     * @param lastName to check Persons.
     * @param persons to edit.
     */
    @Override
    public void editPerson(final String firstName,
                           final String lastName,
                           final Persons persons) {
        Persons basicPerson = getPersonName(firstName, lastName);
        Persons person = personVerification(basicPerson, persons);
        this.personsR.save(person);
    }

    /**
     * Remove Method Service.
     * @param firstName to check Persons.
     * @param lastName to check Persons.
     */
    @Override
    public void removePerson(final String firstName, final String lastName) {
        Persons person = getPersonName(firstName, lastName);
        this.personsR.delete(person);
    }
    //Get

    /**
     * Get Method Service.
     * @param firstName to check Persons.
     * @param lastName to check Persons.
     * @return persons checked.
     */
    @Override
    public Persons getPersonName(final String firstName,
                                 final String lastName) {
        Persons person = this.personsR
                .findByFirstNameIgnoreCaseAndLastNameIgnoreCase(
                        firstName, lastName);
        if (person != null) {
            return person;
        } else {
            throw new NullPointerException();
        }
    }

    /**
     * GetAll Method Service.
     * @return all Persons.
     */
    @Override
    public List<Persons> getPersons() {
        List<Persons> persons = this.personsR.findAll();
        if (persons.size() != 0) {
            return persons;
        } else {
            throw new NullPointerException();
        }
    }
    //URL lié à Persons

    /**
     * Finds people living at the address.
     * @param address to check Persons.
     * @return list of Persons.
     */
    @Override
    public List<Persons> getPersonsAddress(final String address) {
        LOGGER.debug("List of Person Found");
        return this.personsR.findByAddressAddressIgnoreCase(address);
    }

    /**
     * Find the people living in the city.
     * @param city to check Persons.
     * @return list of Persons.
     */
    @Override
    public List<Persons> getPersonsCity(final String city) {
        LOGGER.debug("List of Person Found");
        return this.personsR.findByCityIgnoreCase(city);
    }

    /**
     * Extract the email from the Persons list in PersonsDTO.
     * @param persons : list of Persons used.
     * @return list of Email.
     */
    @Override
    public List<PersonsDTO> personsToPersonsdtoEmail(
            final List<Persons> persons) {
        List<PersonsDTO> personsDTO = new ArrayList<>();
        for (Persons person : persons) {
            PersonsDTO personsDto = new PersonsDTO();
            personsDto.setEmail(person.getEmail());
            personsDTO.add(personsDto);
        }
        LOGGER.debug("Retrieving emails people");
        return personsDTO;
    }

    //Methods Tiers

    /**
     * Returns values to the mandatory field null.
     * @param basicPerson : Original persons.
     * @param personChanged : Edited Persons.
     * @return Edited Persons verified.
     */
    private Persons personVerification(final Persons basicPerson,
                                       final Persons personChanged) {
        personChanged.setId(basicPerson.getId());
        personChanged.setFirstName(basicPerson.getFirstName());
        personChanged.setLastName(basicPerson.getLastName());
        if (personChanged.getAddress() == null) {
            personChanged.setAddress(basicPerson.getAddress());
        } else {
            personChanged.setAddress(
                    secondaryTableS.checkAddress(
                            personChanged.getAddress()));
        }
        if (personChanged.getCity() == null) {
            personChanged.setCity(basicPerson.getCity());
        }
        if (personChanged.getZip() < 1) {
            personChanged.setZip(basicPerson.getZip());
        }
        if (personChanged.getPhone() == null) {
            personChanged.setPhone(basicPerson.getPhone());
        }
        if (personChanged.getEmail() == null) {
            personChanged.setEmail(basicPerson.getEmail());
        }
        LOGGER.debug("Verification of Person fields");
        return personChanged;
    }

    /**
     * Check if Persons exists in the database.
     * @param firstName to check Persons.
     * @param lastName to check Persons.
     * @return True if exist or False if not.
     */
    @Override
    public boolean personCheck(final String firstName, final String lastName) {
        Persons persons = personsR
                .findByFirstNameIgnoreCaseAndLastNameIgnoreCase(
                        firstName, lastName);
        LOGGER.debug("Verification of the person's existence");
        return persons != null;
    }
}
