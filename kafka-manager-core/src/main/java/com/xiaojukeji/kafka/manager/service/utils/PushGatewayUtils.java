package com.xiaojukeji.kafka.manager.service.utils;

import io.prometheus.client.exporter.PushGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PushGatewayUtils {
    private static PushGateway pushGateway;
    @Value(value = "${prometheus.pushgateway}")
    private static String address = "10.210.14.45:9091";

    public  PushGateway getPushGateway() {
        if (pushGateway == null) {
            synchronized (PushGatewayUtils.class) {
                if (pushGateway == null) {
                    pushGateway = new PushGateway(address);
                }
            }
        }
        return pushGateway;
    }
}
