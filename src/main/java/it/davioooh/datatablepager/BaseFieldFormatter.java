package it.davioooh.datatablepager;

/**
 * Basic implementation of the {@code FieldFormatter}.
 * 
 * @author davioooh
 *
 */
public class BaseFieldFormatter implements FieldFormatter {

	@Override
	public String format(Object fieldValue) {
		return fieldValue.toString();
	}

}
