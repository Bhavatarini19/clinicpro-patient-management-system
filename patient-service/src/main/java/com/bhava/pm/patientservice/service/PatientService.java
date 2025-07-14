package com.bhava.pm.patientservice.service;

import com.bhava.pm.patientservice.dto.PatientRequestDTO;
import com.bhava.pm.patientservice.dto.PatientResponseDTO;
import com.bhava.pm.patientservice.exception.EmailAlreadyExistsException;
import com.bhava.pm.patientservice.exception.PatientNotFoundException;
import com.bhava.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.bhava.pm.patientservice.kafka.KafkaProducer;
import com.bhava.pm.patientservice.mapper.PatientMapper;
import com.bhava.pm.patientservice.model.Patient;
import com.bhava.pm.patientservice.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private static final Logger log = LoggerFactory.getLogger(PatientService.class);
    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient, KafkaProducer kafkaProducer)
    {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }


    public List<PatientResponseDTO> getPatients()
    {
        List<Patient> patients = patientRepository.findAll();

//        List<PatientResponseDTO> patientResponseDTOS = patients.stream().map(patient-> PatientMapper.toDTO(patient)).toList();
        List<PatientResponseDTO> patientResponseDTOS = patients.stream().map(PatientMapper::toDTO).toList();

        return patientResponseDTOS;
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO)
    {
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail()))
        {
            throw new EmailAlreadyExistsException("A patient with this email already exists " + patientRequestDTO.getEmail());
        }
        Patient newPatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));
        billingServiceGrpcClient.createBillingAccount(newPatient.getId().toString(), newPatient.getName(), newPatient.getEmail());
        kafkaProducer.sendMessage(newPatient);
        return PatientMapper.toDTO(newPatient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO)
    {
        Patient patient = patientRepository.findById(id).orElseThrow(()-> new PatientNotFoundException("Patient not found with ID: "+ id));

        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id))
        {
            throw new EmailAlreadyExistsException("A patient with this email already exists " + patientRequestDTO.getEmail());
        }

        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);

        return PatientMapper.toDTO(updatedPatient);
    }

    public void deletePatient(UUID id)
    {
        patientRepository.deleteById(id);
    }
}
