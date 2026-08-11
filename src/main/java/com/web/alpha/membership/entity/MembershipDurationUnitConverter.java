package com.web.alpha.membership.entity;

import com.web.alpha.membership.enums.MembershipDurationUnit;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class MembershipDurationUnitConverter implements AttributeConverter<MembershipDurationUnit, Short> {

    @Override
    public Short convertToDatabaseColumn(MembershipDurationUnit attribute) {
        return attribute == null ? null : (short) attribute.getCode();
    }

    @Override
    public MembershipDurationUnit convertToEntityAttribute(Short dbData) {
        return dbData == null ? null : MembershipDurationUnit.fromCode(dbData.intValue());
    }
}
