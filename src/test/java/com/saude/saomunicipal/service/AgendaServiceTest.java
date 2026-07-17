package com.saude.saomunicipal.service;

import com.saude.saomunicipal.dto.AgendaRequestDTO;
import com.saude.saomunicipal.entity.ProfissionalSaude;
import com.saude.saomunicipal.entity.UnidadeSaude;
import com.saude.saomunicipal.exception.BusinessException;
import com.saude.saomunicipal.repository.ProfissionalSaudeRepository;
import com.saude.saomunicipal.repository.UnidadeSaudeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class AgendaServiceTest {

    @Autowired
    private AgendaService agendaService;

    @Autowired
    private UnidadeSaudeRepository unidadeRepository;

    @Autowired
    private ProfissionalSaudeRepository profissionalRepository;

    private UnidadeSaude unidade;
    private ProfissionalSaude profissional;

    @BeforeEach
    void setUp() {
        unidade = unidadeRepository.save(UnidadeSaude.builder()
                .nome("UBS Central")
                .tipo("UBS")
                .bairro("Centro")
                .endereco("Rua Principal, 100")
                .ativa(true)
                .build());

        profissional = profissionalRepository.save(ProfissionalSaude.builder()
                .nome("Dr. João")
                .cargo("Médico")
                .especialidade("Clínico Geral")
                .ativo(true)
                .unidade(unidade)
                .build());
    }

    @Test
    void naoPermiteCadastrarHorarioDuplicadoParaOMesmoProfissional() {
        AgendaRequestDTO dto = new AgendaRequestDTO(
                LocalDate.now().plusDays(1), LocalTime.of(10, 0), profissional.getId(), unidade.getId()
        );

        agendaService.cadastrar(dto);

        assertThatThrownBy(() -> agendaService.cadastrar(dto))
                .isInstanceOf(BusinessException.class);
    }
}
