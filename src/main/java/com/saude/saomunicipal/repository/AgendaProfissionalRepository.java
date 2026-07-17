package com.saude.saomunicipal.repository;

import com.saude.saomunicipal.entity.AgendaProfissional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgendaProfissionalRepository extends JpaRepository<AgendaProfissional, Long> {

    List<AgendaProfissional> findByProfissionalIdAndData(Long profissionalId, LocalDate data);

    List<AgendaProfissional> findByProfissionalIdAndDataAndDisponivelTrue(Long profissionalId, LocalDate data);

    List<AgendaProfissional> findByUnidadeIdAndDataAndDisponivelTrue(Long unidadeId, LocalDate data);

    boolean existsByProfissionalIdAndDataAndHoraInicio(Long profissionalId, LocalDate data, LocalTime horaInicio);

    /**
     * Busca o slot com lock pessimista para reserva atômica: a transação que
     * chama este método bloqueia a linha até o commit, forçando chamadas
     * concorrentes para o mesmo horário a esperar e reavaliar
     * {@code disponivel} em vez de agendar em cima uma da outra.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AgendaProfissional a "
            + "where a.profissional.id = :profissionalId "
            + "and a.data = :data "
            + "and a.horaInicio = :horaInicio")
    Optional<AgendaProfissional> buscarParaReserva(
            Long profissionalId,
            LocalDate data,
            LocalTime horaInicio
    );
}