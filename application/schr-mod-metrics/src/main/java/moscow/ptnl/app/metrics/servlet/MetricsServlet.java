package moscow.ptnl.app.metrics.servlet;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Модифицированная версия сервлета.
 * Модификация (добавлены геттеры и сеттеры) позволяет инициализировать метрики 
 * после инициализации сервлета.
 * 
 * https://github.com/prometheus/client_java/blob/master/simpleclient_servlet/src/main/java/io/prometheus/client/exporter/MetricsServlet.java
 * @author m.kachalov
 */
public class MetricsServlet extends HttpServlet {
        
    private PrometheusMeterRegistry meterRegistry;

    public MetricsServlet(PrometheusMeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * @return the registry
     */
    public CollectorRegistry getRegistry() {
        return meterRegistry.getPrometheusRegistry();
    }

    /**
     * @param meterRegistry the registry to set
     */
    public void setMeterRegistry(PrometheusMeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType(TextFormat.CONTENT_TYPE_004);

        try (Writer writer = resp.getWriter()) {
            if (getRegistry() != null) {
                TextFormat.write004(writer, getRegistry().filteredMetricFamilySamples(parse(req)));
            }
            writer.flush();
        }
    }

    private Set<String> parse(HttpServletRequest req) {
        String[] includedParam = req.getParameterValues("name[]");
        if (includedParam == null) {
            return Collections.emptySet();
        } else {
            return new HashSet<>(Arrays.asList(includedParam));
        }
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
    
}
