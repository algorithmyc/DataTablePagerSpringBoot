package it.davioooh.datatablepager.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.reflect.TypeToken;

import it.davioooh.datatablepager.PaginationCriteria;
import it.davioooh.datatablepager.PaginationCriteria.Column;
import it.davioooh.datatablepager.PaginationCriteria.OrderingCriteria;
import it.davioooh.datatablepager.model.User;

/**
 * Abstract class implementing basic ordering and filtering based on JPA.
 * 
 * @author davioooh
 *
 */
public abstract class AbstractJpaTablePagerRepository<T> implements TablePagerRepository<T> {

	private EntityManager entityManager;
	private TypeToken<T> entityType;
	private String keyName;
	private String entityName;

	@SuppressWarnings("serial")
	public AbstractJpaTablePagerRepository(EntityManager entityManager) {
		this.entityManager = entityManager;
		this.entityType = new TypeToken<T>(getClass()) {
		};
	}

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public void setEntityType(TypeToken<T> entityType) {
		this.entityType = entityType;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	@Override
	public long countTotalEntries() throws TablePagerRepositoryException {
		String key = Strings.isNullOrEmpty(getKeyName()) ? "id" : getKeyName();
		String entity = Strings.isNullOrEmpty(entityName) ? getEntityType().getSimpleName() : entityName;

		Query qr = entityManager.createQuery(String.format("select count(e.%s) from %s e", key, entity));
		try {
			return (Long) qr.getSingleResult();
		} catch (NoResultException | NonUniqueResultException e) {
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> findPageEntries(PaginationCriteria pCriteria) throws TablePagerRepositoryException {
		String entity = Strings.isNullOrEmpty(entityName) ? getEntityType().getSimpleName() : entityName;
		StringBuilder queryBuilder = new StringBuilder(String.format("from %s e where (1 = 1)", entity));

		setSearchConditions(pCriteria, queryBuilder);
		setOrderingCriteria(pCriteria, queryBuilder);

		TypedQuery<T> qr = entityManager.createQuery(queryBuilder.toString(), getEntityType());
		return (List<User>) qr.setFirstResult(pCriteria.getStart()).setMaxResults(pCriteria.getLength()).getResultList();
	}

	@Override
	public long countFilteredEntries(PaginationCriteria pCriteria) throws TablePagerRepositoryException {
		String key = Strings.isNullOrEmpty(getKeyName()) ? "id" : getKeyName();
		String entity = Strings.isNullOrEmpty(entityName) ? getEntityType().getSimpleName() : entityName;
		StringBuilder queryBuilder = new StringBuilder(
				String.format("select count(e.%s) from %s e where (1 = 1)", key, entity));

		setSearchConditions(pCriteria, queryBuilder);

		Query qr = entityManager.createQuery(queryBuilder.toString());
		try {
			return (Long) qr.getSingleResult();
		} catch (NoResultException | NonUniqueResultException e) {
			return 0;
		}
	}

	//

	protected void setSearchConditions(PaginationCriteria pCriteria, StringBuilder queryBuilder) {
		// search
		Iterable<Column> searchableCols = Iterables.filter(pCriteria.getColumns(), Column.IS_SEARCHABLE);
		List<String> sList = new ArrayList<String>();
		if (!Strings.isNullOrEmpty(pCriteria.getSearch().getValue())) {
			for (Column col : searchableCols) {
				sList.add(String.format("e.%s like '%%%s%%'", col.getData(), pCriteria.getSearch().getValue()));
			}
		}
		for (Column col : searchableCols) {
			if (!Strings.isNullOrEmpty(col.getSearch().getValue())) {
				sList.add(String.format("e.%s like '%%%s%%'", col.getData(), col.getSearch().getValue()));
			}
		}
		if (sList.size() > 0) {
			queryBuilder.append(" and (" + Joiner.on(" or ").skipNulls().join(sList) + ")");
		}
	}

	protected void setOrderingCriteria(PaginationCriteria pCriteria, StringBuilder queryBuilder) {
		// sort
		List<String> oList = new ArrayList<String>();
		for (OrderingCriteria oc : pCriteria.getOrder()) {
			Column col = pCriteria.getColumns().get(oc.getColumn());
			if (col.isOrderable()) {
				oList.add(String.format("e.%s %s", col.getData(), oc.getDir()));
			}
		}
		queryBuilder.append(" order by " + Joiner.on(",").skipNulls().join(oList));
	}

	@SuppressWarnings("unchecked")
	protected Class<T> getEntityType() {
		return (Class<T>) entityType.getRawType();
	}

	protected EntityManager getEntityManager() {
		return entityManager;
	}
}
