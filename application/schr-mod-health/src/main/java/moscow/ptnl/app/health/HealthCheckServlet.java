package moscow.ptnl.app.health;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Для работы сервлета нужно создать имплементацию интерфейса 
 * @see DatabaseCheckRepository
 * Также в application.properties приложения необходима настройка
 * вида: health.check.tables=ИМЯ_ТАБЛИЦЫ_1,ИМЯ_ТАБЛИЦЫ_2
 * 
 * @author sorlov
 */
public class HealthCheckServlet extends HttpServlet {

    private final HealthCheckService service;
    private final List<String> tablesList;

    public HealthCheckServlet(HealthCheckService service, List<String> tablesList) {
        this.service = service;
        this.tablesList = tablesList;
    }

    public HealthCheckService getService() {
        return service;
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.addDateHeader("Last-Modified", System.currentTimeMillis());
        resp.setContentType(MediaType.TEXT_PLAIN);

        String response;

        try {
            Future<String> databaseHealthChecker = null;

            try {
                databaseHealthChecker = service.databaseHealthCheck(this.tablesList);
                response = databaseHealthChecker.get(30000, TimeUnit.MILLISECONDS);
            }
            finally {
                if (databaseHealthChecker != null) {
                    databaseHealthChecker.cancel(true);
                }
            }

            try (Writer writer = resp.getWriter()) {
                writer.write(response);
                writer.flush();
            }
        }
        catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            try (Writer writer = resp.getWriter()) {
                writer.write(e.toString());
                writer.flush();
            }
        }
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
