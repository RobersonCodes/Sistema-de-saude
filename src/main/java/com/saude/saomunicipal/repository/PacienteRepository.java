package com.saude.saomunicipal.repository;

import com.saude.saomunicipal.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PacienteRepository extends
        JpaRepository<Paciente, Long>,
        JpaSpecificationExecutor<Paciente> {

    boolean existsByCpf(String cpf);

    Optional<Paciente> findByCpf(String cpf);

    Optional<Paciente> findByCns(String cns);

    Optional<Paciente> findByCpfOrCns(String cpf, String cns);
}