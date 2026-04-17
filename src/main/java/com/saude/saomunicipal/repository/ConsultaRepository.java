package com.saude.saomunicipal.repository;

import com.saude.saomunicipal.entity.Consulta;
import com.saude.saomunicipal.entity.StatusConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    List<Consulta> findByPacienteId(Long pacienteId);

    List<Consulta> findByProfissionalEspecialidadeContainingIgnoreCase(String especialidade);

    List<Consulta> findByPacienteIdAndStatus(Long pacienteId, StatusConsulta status);

    boolean existsByProfissionalIdAndDataHoraAndStatusNot(
            Long profissionalId,
            LocalDateTime dataHora,
            StatusConsulta status
    );

    boolean existsByPacienteIdAndDataHoraAndStatusNot(
            Long pacienteId,
            LocalDateTime dataHora,
            StatusConsulta status
    );
}