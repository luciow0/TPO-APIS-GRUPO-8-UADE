package com.uade.tpo.marketplace.service;

import java.util.List;

import com.uade.tpo.marketplace.Enum.RolResena;
import com.uade.tpo.marketplace.dto.ResenaPendienteResponse;
import com.uade.tpo.marketplace.dto.ResenaPublicaResponse;
import com.uade.tpo.marketplace.dto.ResenaRequest;
import com.uade.tpo.marketplace.dto.ResenaResponse;
import com.uade.tpo.marketplace.dto.ResenasDeReservaResponse;
import com.uade.tpo.marketplace.exception.ReservaNotFoundException;

public interface ResenaService {

    ResenaResponse crear(String emailAutor, ResenaRequest request) throws ReservaNotFoundException;

    ResenaResponse editar(String emailAutor, Long idResena, ResenaRequest request);

    List<ResenaPendienteResponse> pendientes(String email);

    List<ResenaResponse> mias(String email);

    ResenasDeReservaResponse deReserva(String email, Long idReserva) throws ReservaNotFoundException;

    List<ResenaPublicaResponse> publicadasDeUsuario(Long idUsuario, RolResena rol);
}
