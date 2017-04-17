package it.davioooh.datatablepager;

/**
 * Used to provide a custom representation for a table field value.
 * 
 * @author davioooh
 *
 */
public interface FieldFormatter {
	String format(Object fieldValue);
}
