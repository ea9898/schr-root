package moscow.ptnl.app.test.erp;

import moscow.ptnl.app.ecppis.validator.ErpChangePatientPoliciesValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author sevgeniy
 */
//@SpringBootTest(classes = {
//        ErpChangePatientPoliciesValidator.class
//})
@Disabled //Валидация в ErpChangePatientPoliciesValidator отключена по задаче SCHREGISTER-40
public class ServiceTest {

    @Autowired
    private ErpChangePatientPoliciesValidator validator;

    @Test
    public void erpChangePatientPoliciesD() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-D.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isEmpty());
    }

    @Test
    public void erpChangePatientPoliciesY() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-Y.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isEmpty());
    }

    @Test
    public void erpChangePatientPoliciesIdIncorrect() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-id-incorrect.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/id: expected type: Number, found: String", validate.get());
    }

    @Test
    public void erpChangePatientPoliciesIdIsNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-id-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/emiasId: expected type: Number, found: String; #/id: expected type: Number, found: Null", validate.get());
    }

    @Test
    public void erpChangePatientPoliciesIdNotFound() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-id-not-found.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/emiasId: expected type: Number, found: String; #: required key [id] not found", validate.get());
    }

    @Test
    public void erpChangePatientPoliciesOperationDateIncorrect() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-operation-date-incorrect.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/operationDate: expected type: String, found: Integer; #/emiasId: expected type: Number, found: String", validate.get());
    }

    @Test
    public void erpChangePatientPoliciesOperationDateIsNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-operation-date-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/operationDate: expected type: String, found: Null; #/emiasId: expected type: Number, found: String", validate.get());
    }

    @Test
    public void erpChangePatientPoliciesOperationDateNotFound() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-operation-date-not-found.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/emiasId: expected type: Number, found: String; #: required key [operationDate] not found", validate.get());
    }

    @Test
    public void erpChangePatientPoliciesEmiasIdIncorrect() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-emiasId-incorrect.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/emiasId: expected type: Number, found: String", validate.get());
    }

    @Test
    public void erpChangePatientPoliciesEmiasIdIsNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-emiasId-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/emiasId: expected type: Number, found: Null", validate.get());
    }

//    @Test // TODO если не передаем поле emiasId то ошибка не вылетает т.к поле не обязательное
//    public void erpChangePatientPoliciesEmiasIdNotFound() {
//        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-emiasId-not-found.json");
//        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
//                .lines()
//                .collect(Collectors.joining("\n"));
//        Optional<String> validate = validator.validate(json);
//        Assertions.assertTrue(validate.isPresent());
//        Assertions.assertEquals("", validate.get());
//    }

    @Test
    public void erpChangePatientPoliciesUklErpIncorrect() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-uklerp-incorrect.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/uklErp: expected type: String, found: Integer", validate.get());
    }

    @Test
    public void erpChangePatientPoliciesUklErpIsNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-uklerp-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/uklErp: expected type: String, found: Null", validate.get());
    }

    @Test
    public void erpChangePatientPoliciesUklErpNotFound() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-uklerp-not-found.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #: required key [uklErp] not found", validate.get());
    }

    @Test
    public void erpChangePatientPoliciesPatientRecStatusIncorrect() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-patientrec-status-incorrect.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/patientRecStatus: 1 is not a valid enum value", validate.get());
    }

    @Test
    public void erpChangePatientPoliciesPatientRecStatusIsNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-patientrec-status-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/patientRecStatus: null is not a valid enum value", validate.get());
    }

    @Test
    public void erpChangePatientPoliciesPatientRecStatusNotFound() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-patientrec-status-not-found.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #: required key [patientRecStatus] not found", validate.get());
    }

    @Test
    public void erpChangePatientPoliciesEntityNameIncorrect() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-entity-name-incorrect.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityName: expected type: String, found: Integer", validate.get());
    }

    @Test
    public void erpChangePatientPoliciesEntityNameIsNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-entity-name-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityName: expected type: String, found: Null", validate.get());
    }

    @Test
    public void erpChangePatientPoliciesEntityNameNotFound() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-entity-name-not-found.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #: required key [entityName] not found", validate.get());
    }

    @Test
    public void erpChangePatientPoliciesPatientTypeIncorrect() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-patient-type-incorrect.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/patientType: 1 is not a valid enum value", validate.get());
    }

    @Test
    public void erpChangePatientPoliciesPatientTypeIsNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-patient-type-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/patientType: null is not a valid enum value", validate.get());
    }

    @Test
    public void erpChangePatientPoliciesPatientTypeIsNotFound() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-patient-type-not-found.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #: required key [patientType] not found", validate.get());
    }

    @Test
    public void erpChangePatientPolicyNumberIncorrectValue() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-number-incorrect-value.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/1/value/value: expected type: String, found: Integer", validate.get());
    }

    @Test
    public void erpChangePatientPolicyNumberValueIsNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-number-value-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/1/value/value: expected type: String, found: Null", validate.get());
    }

    @Test
    public void erpChangePatientPolicyNotFound() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-number-not-found.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/1: required key [name] not found; #/entityData/0/attributes/1: required key [type] not found", validate.get());
    }

    @Test
    public void erpChangePatientPolicyOMSTypeIncorrectType() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-OMS-type-incorrect-type.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/0/type: expected type: String, found: Integer", validate.get());
    }

    @Test
    public void erpChangePatientPolicyOMSTypeIsNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-OMS-type-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/0/value/value: expected type: String, found: Null", validate.get());
    }

    @Test
    public void erpChangePatientPolicyOMSTypeIncorrectValue() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-OMS-type-incorrect-value.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/0/value/value: expected type: String, found: Integer", validate.get());
    }

    @Test
    public void erpChangePatientPolicyOMSTypeValueIsNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-OMS-type-value-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/0/value/value: expected type: String, found: Null", validate.get());
    }

    @Test
    public void erpChangePatientPolicyOMSTypeIncorrectCode() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-OMS-type-incorrect-code.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/0/value/code: expected type: String, found: Integer", validate.get());
    }

    @Test
    public void erpChangePatientPolicyOMSTypeCodeIsNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-OMS-type-code-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/0/value/code: expected type: String, found: Null", validate.get());
    }

    @Test
    public void erpChangePatientPolicyOMSTypeNotFound() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-OMS-type-not-found.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/0: required key [name] not found; #/entityData/0/attributes/0: required key [type] not found", validate.get());
    }

    @Test
    public void erpChangePatientPolicyBeginDateIncorrectType() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-begin-date-incorrect-type-begin-date.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/2/type: expected type: String, found: Integer", validate.get());
    }

    @Test
    public void erpChangePatientPolicyBeginDateIncorrectValue() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-begin-date-incorrect-value.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/2/value/value: expected type: String, found: Integer", validate.get());
    }

    @Test
    public void erpChangePatientPolicyBeginDateValueIsNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-begin-date-value-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/2/value/value: expected type: String, found: Null", validate.get());
    }

    @Test
    public void erpChangePatientPolicyBeginDateNotFound() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-begin-date-not-found.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/2: required key [name] not found; #/entityData/0/attributes/2: required key [type] not found", validate.get());
    }

    @Test
    public void erpChangePatientPolicyEndDateIncorrectType() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-end-date-incorrect-type.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/3/type: expected type: String, found: Integer", validate.get());
    }

    @Test
    public void erpChangePatientPolicyEndDateIncorrectValue() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-end-date-incorrect-value.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/3/value/value: expected type: String, found: Integer", validate.get());
    }

    @Test
    public void erpChangePatientPolicyEndDateValueNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-end-date-value-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/3/value/value: expected type: String, found: Null", validate.get());
    }

    @Test
    public void erpChangePatientPolicyEndDateNotFound() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-end-date-not-found.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/3: required key [name] not found; #/entityData/0/attributes/3: required key [type] not found", validate.get());
    }

    @Test
    public void erpChangePatientPolicySMOIncorrectTypeId() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-smo-incorrect-type-id.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/4/value/id: expected type: Number, found: String", validate.get());
    }

    @Test
    public void erpChangePatientPolicySMOIdIsNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-smo-id-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/4/value/id: expected type: Number, found: Null", validate.get());
    }

    @Test
    public void erpChangePatientPolicySMOIncorrectCode() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-smo-incorrect-code.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/4/value/code: expected type: String, found: Integer", validate.get());
    }

    @Test
    public void erpChangePatientPolicySMOCodeIsNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-smo-code-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/4/value/code: expected type: String, found: Null", validate.get());
    }

    @Test
    public void erpChangePatientPolicySMOIncorrectValue() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-smo-incorrect-value.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/4/value/value: expected type: String, found: Integer", validate.get());
    }

    @Test
    public void erpChangePatientPolicySMOValueIsNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-smo-value-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/4/value/value: expected type: String, found: Null", validate.get());
    }

    @Test
    public void erpChangePatientPolicySMONotFound() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-smo-not-found.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/4: required key [name] not found; #/entityData/0/attributes/4: required key [type] not found", validate.get());
    }

    @Test
    public void erpChangePatientPolicyIsExternalSMOIncorrectValue() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-external-smo-incorrect-value.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/5/value/value: expected type: String, found: Integer", validate.get());
    }

    @Test
    public void erpChangePatientPolicyIsExternalSMOValueIsNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-external-smo-value-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/5/value/value: expected type: String, found: Null", validate.get());
    }

    @Test
    public void erpChangePatientPolicyIsExternalSMONotFound() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-external-smo-not-found.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/5: required key [name] not found; #/entityData/0/attributes/5: required key [type] not found", validate.get());
    }

    @Test
    public void erpChangePatientPolicyInsuranceTerritoryNameIncorrectValue() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-insurance-territory-name-incorrect-value.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/6/value/value: expected type: String, found: Integer", validate.get());
    }

    @Test
    public void erpChangePatientPolicyInsuranceTerritoryNameValueIsNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-insurance-territory-name-value-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/6/value/value: expected type: String, found: Null", validate.get());
    }

    @Test
    public void erpChangePatientPolicyInsuranceTerritoryNameNotFound() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-insurance-territory-name-not-found.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/6: required key [name] not found; #/entityData/0/attributes/6: required key [type] not found", validate.get());
    }

    @Test
    public void erpChangePatientPolicyInsuranceTerritoryCodeIncorrectValue() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-insurance-territory-code-incorrect-value.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/7/value/value: expected type: String, found: Integer", validate.get());
    }

    @Test
    public void erpChangePatientPolicyInsuranceTerritoryCodeValueIsNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-insurance-territory-code-value-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/7/value/value: expected type: String, found: Null", validate.get());
    }

    @Test
    public void erpChangePatientPolicyInsuranceTerritoryCodeNotFound() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-insurance-territory-code-not-found.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/7: required key [name] not found; #/entityData/0/attributes/7: required key [type] not found", validate.get());
    }

    @Test
    public void erpChangePatientPolicySMOOGRNIncorrectValue() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-smoogrn-incorrect-value.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/8/value/value: expected type: String, found: Integer", validate.get());
    }

    @Test
    public void erpChangePatientPolicySMOOGRNValueIsNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-smoogrn-value-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/8/value/value: expected type: String, found: Null", validate.get());
    }

    @Test
    public void erpChangePatientPolicySMOOGRNNotFound() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-smoogrn-not-found.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/8: required key [name] not found; #/entityData/0/attributes/8: required key [type] not found", validate.get());
    }

    @Test
    public void erpChangePatientPolicySMOBeginDateIncorrectValue() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-smo-begin-date-incorrect-value.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/9/value/value: expected type: String, found: Integer", validate.get());
    }

    @Test
    public void erpChangePatientPolicySMOBeginDateValueIsNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-smo-begin-date-value-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/9/value/value: expected type: String, found: Null", validate.get());
    }

    @Test
    public void erpChangePatientPolicySMOBeginDateNotFound() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-smo-begin-date-not-found.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/9: required key [name] not found; #/entityData/0/attributes/9: required key [type] not found", validate.get());
    }

    @Test
    public void erpChangePatientPolicySMOEndDateIncorrectValue() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-smo-end-date-incorrect-value.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/10/value/value: expected type: String, found: Integer", validate.get());
    }

    @Test
    public void erpChangePatientPolicySMOEndDateValueIsNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-smo-end-date-value-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/10/value/value: expected type: String, found: Null", validate.get());
    }

    @Test
    public void erpChangePatientPolicySMOEndDateNotFound() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-smo-end-date-not-found.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/10: required key [name] not found; #/entityData/0/attributes/10: required key [type] not found", validate.get());
    }

    @Test
    public void erpChangePatientPolicyStatusIncorrectCode() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-status-incorrect-code.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/11/value/code: expected type: String, found: Integer", validate.get());
    }

    @Test
    public void erpChangePatientPolicyStatusCodeIsNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-status-code-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/11/value/code: expected type: String, found: Null", validate.get());
    }

    @Test
    public void erpChangePatientPolicyStatusIncorrectValue() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-status-incorrect-value.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/11/value/value: expected type: String, found: Integer", validate.get());
    }

    @Test
    public void erpChangePatientPolicyStatusValueIsNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-status-value-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/11/value/value: expected type: String, found: Null", validate.get());
    }

    @Test
    public void erpChangePatientPolicyStatusNotFound() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-status-not-found.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/11: required key [name] not found; #/entityData/0/attributes/11: required key [type] not found", validate.get());
    }

    @Test
    public void erpChangePatientPolicyErzlStatusIncorrectCode() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-erzl-status-incorrect-code.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/12/value/code: expected type: String, found: Integer", validate.get());
    }

    @Test
    public void erpChangePatientPolicyErzlStatusCodeIsNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-erzl-status-code-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/12/value/code: expected type: String, found: Null", validate.get());
    }

    @Test
    public void erpChangePatientPolicyErzlStatusIncorrectValue() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-erzl-status-incorrect-value.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/12/value/value: expected type: String, found: Integer", validate.get());
    }

    @Test
    public void erpChangePatientPolicyErzlStatustValueIsNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-erzl-status-value-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/12/value/value: expected type: String, found: Null", validate.get());
    }

    @Test
    public void erpChangePatientPolicyErzlStatustNotFound() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-erzl-status-not-found.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/12: required key [name] not found; #/entityData/0/attributes/12: required key [type] not found", validate.get());
    }

    @Test
    public void erpChangePatientPolicyChangeDateIncorrectValue() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-change-date-incorrect-value.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/13/value/value: expected type: String, found: Integer", validate.get());
    }

    @Test
    public void erpChangePatientPolicyChangeDateValueIsNull() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-change-date-value-null.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/13/value/value: expected type: String, found: Null", validate.get());
    }

    @Test
    public void erpChangePatientPolicyChangeDateNotFound() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/erp-change-patient-policies-policy-change-date-not-found.json");
        String json = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Optional<String> validate = validator.validate(json);
        Assertions.assertTrue(validate.isPresent());
        Assertions.assertEquals("SCHR_101 - Некорректный формат сообщения ЕСУ: #/entityData/0/attributes/13: required key [name] not found; #/entityData/0/attributes/13: required key [type] not found", validate.get());
    }
}
