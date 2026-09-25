package com.uade.tpo.marketplace.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import com.uade.tpo.marketplace.entity.Pago;
import com.uade.tpo.marketplace.exception.MercadoPagoException;

@Service
public class MercadoPagoService {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoService.class);

    private final String publicUrl;
    private final PreferenceClient preferenceClient = new PreferenceClient();
    private final PaymentClient paymentClient = new PaymentClient();

    public MercadoPagoService(
            @Value("${mercadopago.access-token:}") String accessToken,
            @Value("${app.public-url:}") String publicUrl) {

        MercadoPagoConfig.setAccessToken(accessToken);
        this.publicUrl = publicUrl;
    }

    public Preference crearPreferencia(Pago pago) throws MercadoPagoException {

        PreferenceItemRequest item = PreferenceItemRequest.builder()
                .id(String.valueOf(pago.getReserva().getIdReserva()))
                .title("Reserva #" + pago.getReserva().getIdReserva())
                .quantity(1)
                .currencyId("ARS")
                .unitPrice(pago.getMonto())
                .build();

        // Mercado redirige al front.
        String urlRetorno = publicUrl + "/pagos/mercadopago/retorno";

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(urlRetorno)
                .pending(urlRetorno)
                .failure(urlRetorno)
                .build();

        PreferenceRequest request = PreferenceRequest.builder()
                .items(List.of(item))
                .backUrls(backUrls)
                .autoReturn("approved")
                .externalReference(String.valueOf(pago.getIdPago()))
                .build();

        try {
            return preferenceClient.create(request);
        } catch (MPApiException e) {
            log.error("Mercado Pago rechazo la preferencia: {}", e.getApiResponse().getContent());
            throw new MercadoPagoException();
        } catch (MPException e) {
            log.error("Error al crear la preferencia en Mercado Pago", e);
            throw new MercadoPagoException();
        }
    }

    public Preference obtenerPreferencia(String preferenceId) throws MercadoPagoException {
        try {
            return preferenceClient.get(preferenceId);
        } catch (MPApiException e) {
            log.error("Mercado Pago rechazo la consulta de preferencia: {}", e.getApiResponse().getContent());
            throw new MercadoPagoException();
        } catch (MPException e) {
            log.error("Error al consultar la preferencia en Mercado Pago", e);
            throw new MercadoPagoException();
        }
    }

    public Payment obtenerPago(Long paymentId) throws MercadoPagoException {
        try {
            return paymentClient.get(paymentId);
        } catch (MPApiException e) {
            log.error("Mercado Pago rechazo la consulta del pago {}: {}", paymentId, e.getApiResponse().getContent());
            throw new MercadoPagoException();
        } catch (MPException e) {
            log.error("Error al consultar el pago {} en Mercado Pago", paymentId, e);
            throw new MercadoPagoException();
        }
    }
}
