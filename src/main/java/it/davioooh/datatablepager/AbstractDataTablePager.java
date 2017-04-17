package it.davioooh.datatablepager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.reflect.TypeToken;

import it.davioooh.datatablepager.PaginationCriteria.Column;
import it.davioooh.datatablepager.data.TablePagerRepository;
import it.davioooh.datatablepager.model.User;

/**
 * Abstract and generic implementation of {@code DataTablePager}. Provides basic
 * logic to process pagination criteria and to generate a data page structure
 * compliant to DataTable plugin specs.
 * 
 * @author davioooh
 *
 */
public abstract class AbstractDataTablePager<T> implements DataTablePager {

	private static final FieldFormatter BASE_FORMATTER = new BaseFieldFormatter();

	private TablePagerRepository<T> repository;
	private TypeToken<T> entityType;
	private Map<String, FieldFormatter> fieldFormatters;

	@SuppressWarnings("serial")
	public AbstractDataTablePager(TablePagerRepository<T> repo) {
		this.repository = repo;
		this.entityType = new TypeToken<T>(getClass()) {
		};
		fieldFormatters = new HashMap<>();
	}

	@Override
	public TablePage getPage(PaginationCriteria pCriteria) throws TablePagerException {
		TablePage tPage = new TablePage();
		tPage.setDraw(pCriteria.getDraw());
		tPage.setRecordsTotal(repository.countTotalEntries());
		List<User> entries = repository.findPageEntries(pCriteria);
		tPage.setRecordsFiltered(entries.size());
		tPage.setData(formatOutputData(pCriteria.getColumns(), entries));
		return tPage;
	}

	public void setFieldFormatter(String field, FieldFormatter formatter) {
		fieldFormatters.put(field, formatter);
	}

	public FieldFormatter getFieldFormatter(String field) {
		return fieldFormatters.get(field);
	}

	//

	/**
	 * Converts retrieved data in plain tabular format.
	 * 
	 * @param columns columns declared in pagination criteria.
	 * @param entries retrieved data.
	 * @return data as a list of maps.
	 * @throws TablePagerException
	 */
	protected List<Map<String, String>> formatOutputData(List<Column> columns, List<User> entries)
			throws TablePagerException {
		List<Field> fields = new ArrayList<>();
		for (Column col : columns) {
			try {
				fields.add(getDeclaredField(col.getData()));
			} catch (Exception e) {
				throw new TablePagerException("Error parsing table columns", e);
			}
		}
		List<Map<String, String>> outputData = new ArrayList<Map<String, String>>();
		for (User o : entries) {
			Map<String, String> recordVals = new TreeMap<>();
			for (Field field : fields) {
				try {
					field.setAccessible(true);
					FieldFormatter f = fieldFormatters.containsKey(field.getName())
							? fieldFormatters.get(field.getName()) : BASE_FORMATTER;
					recordVals.put(field.getName(), f.format(field.get(o)));
				} catch (Exception e) {
					throw new TablePagerException("Error generating output data", e);
				}
			}
			outputData.add(recordVals);
		}
		return outputData;
	}

	/**
	 * Uses reflection to extract required values from objects.
	 * @param field name of the object field.
	 * @return the declared field as {@code Field}.
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	protected Field getDeclaredField(String field) throws NoSuchFieldException, SecurityException {
		Field fld = getEntityType().getDeclaredField(field);
		if (fld == null) {
			if (getEntityType().getSuperclass() != null) {
				fld = getDeclaredField(field);
			}
		}
		return fld;
	}

	/**
	 * Returns the type of the entities retrieved by the repository.
	 * 
	 * @return the class of the entity.
	 */
	@SuppressWarnings("unchecked")
	protected Class<T> getEntityType() {
		return (Class<T>) entityType.getRawType();
	}

	/**
	 * Returns the repository used to retrieve, sort and filter the data.
	 * 
	 * @return the repository,
	 */
	protected TablePagerRepository<T> getRepository() {
		return repository;
	}
}
