package com.saude.saomunicipal.integration.sus.client;

import com.saude.saomunicipal.integration.sus.dto.ConsultaSusDTO;
import com.saude.saomunicipal.integration.sus.dto.SusIntegrationResponseDTO;

public interface SusIntegrationClient {

    SusIntegrationResponseDTO enviarConsulta(ConsultaSusDTO consulta);
}