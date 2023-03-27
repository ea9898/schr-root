package moscow.ptnl.app.last.anthropometry.validator;

import moscow.ptnl.app.error.CustomErrorReason;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.Optional;

@Component
public class LastAnthropometryValidator {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

    private static final String SCHEMA_PATH = "json.schema/LastAnthropometry.json";

    private final Schema schema;

    public LastAnthropometryValidator() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(SCHEMA_PATH)) {
            Objects.requireNonNull(inputStream);
            JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
            this.schema = SchemaLoader.load(rawSchema);
        } catch (Exception e) {
            LOG.error("Ошибка парсинга схемы", e);
            throw new IllegalStateException(e);
        }
    }

    public Optional<String> validate(String json) {
        String msg;

        try {
            JSONObject object = new JSONObject(json);
//            schema.validate(object);

            return Optional.empty();
        } catch (ValidationException validationException) {
            msg = CustomErrorReason.INCORRECT_FORMAT_ESU_MESSAGE.format(String.join("; ", validationException.getAllMessages()));
        } catch (JSONException validationException) {
            msg = CustomErrorReason.INCORRECT_FORMAT_ESU_MESSAGE.format(String.join("; ", validationException.getMessage()));
        } catch (Exception e) {
            msg = CustomErrorReason.UNEXPECTED_ERROR.format(e);
        }
        return Optional.of(msg);
    }
}
