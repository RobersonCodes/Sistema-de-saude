package com.saude.saomunicipal.integration.sus.mapper;

import com.saude.saomunicipal.entity.Consulta;
import com.saude.saomunicipal.entity.Paciente;
import com.saude.saomunicipal.entity.ProfissionalSaude;
import com.saude.saomunicipal.entity.UnidadeSaude;
import com.saude.saomunicipal.integration.sus.dto.ConsultaSusDTO;
import com.saude.saomunicipal.integration.sus.dto.EstabelecimentoSusDTO;
import com.saude.saomunicipal.integration.sus.dto.PacienteSusDTO;
import com.saude.saomunicipal.integration.sus.dto.ProfissionalSusDTO;
import org.springframework.stereotype.Component;

@Component
public class SusIntegrationMapper {

    public ConsultaSusDTO toConsultaSusDTO(Consulta consulta) {
        return new ConsultaSusDTO(
                consulta.getId(),
                consulta.getDataHora(),
                consulta.getStatus().name(),
                consulta.getObservacoes(),
                toPacienteSusDTO(consulta.getPaciente()),
                toProfissionalSusDTO(consulta.getProfissional()),
                toEstabelecimentoSusDTO(consulta.getUnidade())
        );
    }

    public PacienteSusDTO toPacienteSusDTO(Paciente paciente) {
        return new PacienteSusDTO(
                paciente.getId(),
                paciente.getNomeCompleto(),
                paciente.getCpf(),
                paciente.getCns(),
                paciente.getDataNascimento(),
                paciente.getSexo(),
                paciente.getTelefone(),
                paciente.getEmail(),
                paciente.getEndereco(),
                paciente.getUnidade() != null ? paciente.getUnidade().getId() : null
        );
    }

    public ProfissionalSusDTO toProfissionalSusDTO(ProfissionalSaude profissional) {
        return new ProfissionalSusDTO(
                profissional.getId(),
                profissional.getNome(),
                profissional.getCargo(),
                profissional.getEspecialidade(),
                profissional.getTelefone(),
                null,
                null,
                null,
                profissional.getUnidade() != null ? profissional.getUnidade().getId() : null
        );
    }

    public EstabelecimentoSusDTO toEstabelecimentoSusDTO(UnidadeSaude unidade) {
        return new EstabelecimentoSusDTO(
                unidade.getId(),
                unidade.getNome(),
                unidade.getTipo(),
                null,
                unidade.getBairro(),
                unidade.getEndereco(),
                unidade.getTelefone()
        );
    }
}