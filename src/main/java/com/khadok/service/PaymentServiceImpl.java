package com.khadok.service;

import com.khadok.model.Order;
import com.khadok.response.PaymentResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Value;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService{

    @Value("${strip.api.key}")
    private  String stripeSecretKey;
    @Override
    public PaymentResponse createPaymentLink(Order order) throws StripeException {
        Stripe.apiKey=stripeSecretKey;

        SessionCreateParams params = SessionCreateParams.builder().addPaymentMethodType(
                SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/payment/success/"+order.getId())
                .setCancelUrl("http://localhost:3000/payment/fail")
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L).setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount((long) order.getTotalPrice()+126*100)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("khadok online food ordering").
                                        build())
                                .build()
                        )
                        .build()
                ).build();

        Session session = Session.create(params);

        PaymentResponse response = new PaymentResponse();
        response.setPayment_url(session.getUrl());


        return response;
    }
}
