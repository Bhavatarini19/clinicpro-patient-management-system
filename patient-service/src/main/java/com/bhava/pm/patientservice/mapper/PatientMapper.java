package com.bhava.pm.patientservice.mapper;

import com.bhava.pm.patientservice.dto.PatientRequestDTO;
import com.bhava.pm.patientservice.dto.PatientResponseDTO;
import com.bhava.pm.patientservice.model.Patient;

import java.time.LocalDate;

public class PatientMapper {

    public static PatientResponseDTO toDTO(Patient patient)
    {
        PatientResponseDTO prDTO = new PatientResponseDTO();
        prDTO.setId(patient.getId().toString());
        prDTO.setName(patient.getName());
        prDTO.setEmail(patient.getEmail());
        prDTO.setAddress(patient.getAddress());
        prDTO.setDateOfBirth(patient.getDateOfBirth().toString());
        return prDTO;
    }

    public static Patient toModel(PatientRequestDTO patientRequestDTO)
    {
        Patient patient = new Patient();
        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        patient.setRegisteredDate(LocalDate.parse(patientRequestDTO.getRegisteredDate()));
        return patient;
    }

}
