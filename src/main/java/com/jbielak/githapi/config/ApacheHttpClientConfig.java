package com.jbielak.githapi.config;

import java.util.concurrent.TimeUnit;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HeaderIterator;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class ApacheHttpClientConfig {

    private static final Logger LOG = LoggerFactory.getLogger(ApacheHttpClientConfig.class);

    @Value("${http_client.max_route_connections}")
    private int MAX_ROUTE_CONNECTIONS;

    @Value("${http_client.max_total_connections}")
    private int MAX_TOTAL_CONNECTIONS;

    @Value("${http_client.default_keep_alive_time}")
    private int DEFAULT_KEEP_ALIVE_TIME;

    @Value("${http_client.connection_timeout}")
    private int CONNECTION_TIMEOUT;

    @Value("${http_client.request_timeout}")
    private int REQUEST_TIMEOUT;

    @Value("${http_client.socket_timeout}")
    private int SOCKET_TIMEOUT;

    @Value("${http_client.idle_connection_wait_time}")
    private int IDLE_CONNECTION_WAIT_TIME;

    private static final int IDLE_CONNECTIONS_MONITOR_FIXED_DELAY_MS = 20000;
    private static final String IDLE_CONNECTIONS_MONITOR_THREAD_PREFIX = "IdleMonitor";
    private static final int IDLE_CONNECTIONS_MONITOR_THREADS = 5;
    private static final int MILLISECONDS = 1000;


    @Bean
    public PoolingHttpClientConnectionManager poolingConnectionManager() {
        PoolingHttpClientConnectionManager poolingConnectionManager =
                new PoolingHttpClientConnectionManager();

        poolingConnectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        poolingConnectionManager.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);

        LOG.info("HttpClient-PoolingHttpClientConnectionManager configured.");
        LOG.info("Max total connections: {}", MAX_TOTAL_CONNECTIONS);
        LOG.info("Max route connections: {}", MAX_ROUTE_CONNECTIONS);

        return poolingConnectionManager;
    }

    @Bean
    public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
        return (httpResponse, httpContext) -> {
            HeaderIterator headerIterator =
                    httpResponse.headerIterator(HTTP.CONN_KEEP_ALIVE);
            HeaderElementIterator elementIterator =
                    new BasicHeaderElementIterator(headerIterator);

            while (elementIterator.hasNext()) {
                HeaderElement element = elementIterator.nextElement();
                String param = element.getName();
                String value = element.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    return Long.parseLong(value) * MILLISECONDS;
                }
            }

            LOG.debug("HttpClient-ConnectionKeepAliveStrategy configured. " +
                            "Default keep alive time: {} s",
                    DEFAULT_KEEP_ALIVE_TIME);

            return DEFAULT_KEEP_ALIVE_TIME * MILLISECONDS;
        };
    }

    @Bean
    public Runnable idleConnectionMonitor(PoolingHttpClientConnectionManager pool) {
        return new Runnable() {
            @Override
            @Scheduled(fixedDelay = IDLE_CONNECTIONS_MONITOR_FIXED_DELAY_MS)
            public void run() {
                if (pool != null) {
                    pool.closeExpiredConnections();
                    pool.closeIdleConnections(IDLE_CONNECTION_WAIT_TIME * MILLISECONDS,
                            TimeUnit.MILLISECONDS);

                    LOG.info("Idle Connections Monitor: Closing expired and idle connections");
                }
            }
        };
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix(IDLE_CONNECTIONS_MONITOR_THREAD_PREFIX);
        scheduler.setPoolSize(IDLE_CONNECTIONS_MONITOR_THREADS);
        return scheduler;
    }

    @Bean
    public CloseableHttpClient httpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(CONNECTION_TIMEOUT * MILLISECONDS)
                .setConnectionRequestTimeout(REQUEST_TIMEOUT * MILLISECONDS)
                .setSocketTimeout(SOCKET_TIMEOUT * MILLISECONDS)
                .build();

        LOG.info("HttpClient-RequestConfig configured.");
        LOG.info("Connection timeout: {} s", CONNECTION_TIMEOUT);
        LOG.info("Connection request timeout: {} s", REQUEST_TIMEOUT);
        LOG.info("Socket timeout: {} s", SOCKET_TIMEOUT);

        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(poolingConnectionManager())
                .setKeepAliveStrategy(connectionKeepAliveStrategy())
                .build();
    }
}
