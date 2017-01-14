package org.obsplatform.organisation.officeadjustments.serializer;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.obsplatform.infrastructure.core.data.ApiParameterError;
import org.obsplatform.infrastructure.core.data.DataValidatorBuilder;
import org.obsplatform.infrastructure.core.exception.InvalidJsonException;
import org.obsplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.obsplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

/**
 * Deserializer for code JSON to validate API request.
 */
@Component
public class OfficeAdjustmentsCommandFromApiJsonDeserializer {

	/**
	 * The parameters supported for this command.
	 */
	private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("adjustmentDate", "adjustmentCode", "adjustmentType",
				    "amountPaid","remarks", "locale", "dateFormat"));
	
	private final FromJsonHelper fromApiJsonHelper;
	
	@Autowired
	public OfficeAdjustmentsCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
		this.fromApiJsonHelper = fromApiJsonHelper;
	}
	public void validateForCreate(final String json) {
		
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,supportedParameters);
		
		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(
				dataValidationErrors).resource("officeadjustment");
		
		final JsonElement element = fromApiJsonHelper.parse(json);
		
		final LocalDate adjustmentDate = fromApiJsonHelper.extractLocalDateNamed("adjustmentDate", element);
		baseDataValidator.reset().parameter("adjustmentDate").value(adjustmentDate).notBlank();
		
		final String adjustmentCode = fromApiJsonHelper.extractStringNamed("adjustmentCode", element);
		baseDataValidator.reset().parameter("adjustment_code").value(adjustmentCode).notBlank().notExceedingLengthOf(100);
		
		
		final BigDecimal amountPaid = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("amountPaid", element);
		baseDataValidator.reset().parameter("amountPaid").value(amountPaid).notBlank().notExceedingLengthOf(100);
		
		final String adjustmentType = fromApiJsonHelper.extractStringNamed("adjustmentType", element);
		baseDataValidator.reset().parameter("adjustmentType").value(adjustmentType).notBlank().notExceedingLengthOf(100);
		
		throwExceptionIfValidationWarningsExist(dataValidationErrors);
	
	}
	
	private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}
	}
}