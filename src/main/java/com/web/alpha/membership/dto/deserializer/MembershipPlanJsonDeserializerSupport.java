package com.web.alpha.membership.dto.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.web.alpha.membership.enums.MembershipDurationUnit;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Iterator;
import java.util.Set;

abstract  class MembershipPlanJsonDeserializerSupport<T> extends JsonDeserializer<T> {

    protected ObjectNode readObject(
            JsonParser parser,
            DeserializationContext context,
            Class<?> requestType
    )throws IOException {
        JsonNode json = parser.getCodec().readTree(parser);
        if (json == null || !json.isObject()) {
            return context.reportInputMismatch(requestType,"Request Body Must be JSON object");
        }
        return  (ObjectNode) json;
    }

    protected String readText(
            ObjectNode json,
            String fieldName,
            DeserializationContext context,
            Class<?> requestType
    )throws IOException{
        JsonNode value = json.get(fieldName);
        if (value == null || value.isNull()) {
            return  null;
        }
        if (!value.isTextual()){
            return context.reportInputMismatch(requestType,"Field '%s' must be a string\"",fieldName);

        }

        return value.textValue();
    }

    protected LocalDate readDate(
            ObjectNode json,
            String fieldName,
            DeserializationContext context,
            Class<?> requestType
    ) throws IOException {
        String value = readText(json, fieldName, context, requestType);
        if (value == null) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            return context.reportInputMismatch(
                    requestType,
                    "Field '%s' must be a valid date in yyyy-MM-dd format",
                    fieldName
            );
        }
    }

    protected Integer readInteger(
            ObjectNode json,
            String fieldName,
            DeserializationContext context,
            Class<?> requestType
    ) throws IOException {
        JsonNode value = json.get(fieldName);
        if (value == null || value.isNull()) {
            return null;
        }
        if (!value.isIntegralNumber() || !value.canConvertToInt()) {
            return context.reportInputMismatch(requestType, "Field '%s' must be an integer", fieldName);
        }
        return value.intValue();
    }

    protected void rejectUnknownFields(
            ObjectNode json,
            Set<String> allowedFields,
            DeserializationContext context,
            Class<?> requestType
    ) throws IOException {
        Iterator<String> fieldNames = json.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            if (!allowedFields.contains(fieldName)) {
                context.reportInputMismatch(requestType, "Unknown field '%s'", fieldName);
            }
        }
    }

    protected Long readLong(
            ObjectNode json,
            String fieldName,
            DeserializationContext context,
            Class<?> requestType
    ) throws IOException {

        JsonNode value = json.get(fieldName);

        if (value == null || value.isNull()) {
            return null;
        }

        if (!value.isIntegralNumber() || !value.canConvertToLong()) {
            return context.reportInputMismatch(
                    requestType,
                    "Field '%s' must be a valid long integer",
                    fieldName
            );
        }

        return value.longValue();
    }

    protected MembershipDurationUnit readDurationUnit(
            ObjectNode json,
            String fieldName,
            DeserializationContext context,
            Class<?> requestType
    ) throws IOException {
        Integer code = readInteger(json, fieldName, context, requestType);
        if (code == null) {
            return null;
        }
        try {
            return MembershipDurationUnit.fromCode(code);
        } catch (IllegalArgumentException exception) {
            return context.reportInputMismatch(requestType, exception.getMessage());
        }
    }

    protected BigDecimal readDecimal(
            ObjectNode json,
            String fieldName,
            DeserializationContext context,
            Class<?> requestType
    ) throws IOException {

        JsonNode value = json.get(fieldName);

        if (value == null || value.isNull()) {
            return null;
        }

        if (!value.isNumber()) {
            return context.reportInputMismatch(
                    requestType,
                    "Field '%s' must be a valid decimal number",
                    fieldName
            );
        }

        try {
            return value.decimalValue();
        } catch (ArithmeticException exception) {
            return context.reportInputMismatch(
                    requestType,
                    "Field '%s' must be a valid decimal number",
                    fieldName
            );
        }
    }

    protected Double readDouble(
            ObjectNode json,
            String fieldName,
            DeserializationContext context,
            Class<?> requestType
    ) throws IOException {

        JsonNode value = json.get(fieldName);

        if (value == null || value.isNull()) {
            return null;
        }

        if (!value.isNumber()) {
            return context.reportInputMismatch(
                    requestType,
                    "Field '%s' must be a valid number",
                    fieldName
            );
        }

        double result = value.doubleValue();

        if (!Double.isFinite(result)) {
            return context.reportInputMismatch(
                    requestType,
                    "Field '%s' must be a finite double value",
                    fieldName
            );
        }

        return result;
    }

}
