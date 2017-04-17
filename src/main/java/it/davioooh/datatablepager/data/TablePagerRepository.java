package it.davioooh.datatablepager.data;

import java.util.List;

import it.davioooh.datatablepager.PaginationCriteria;
import it.davioooh.datatablepager.model.User;

/**
 * This interface is used to retrieve data to paginate. Classes implementing
 * {@code TablePagerRepository} should also implement filter and ordering logic.
 * 
 * @author davioooh
 *
 */
public interface TablePagerRepository<T> {

	/**
	 * Used to get the total count of the entries before filtering.
	 * 
	 * @return the total count of the entries.
	 * @throws TablePagerRepositoryException
	 */
	long countTotalEntries() throws TablePagerRepositoryException;

	/**
	 * Used to get the number of total filtered result according to provided
	 * search criteria declared in {@code PaginationCriteria}}
	 * 
	 * @param pCriteria
	 *            pagination criteria.
	 * @return the count of filter entries.
	 * @throws TablePagerRepositoryException
	 */
	long countFilteredEntries(PaginationCriteria pCriteria) throws TablePagerRepositoryException;

	/**
	 * Used to select and filter the entries for a single page. It provides the
	 * entries filtered by search keys and sorted by ordering criteria declared
	 * in {@code PaginationCriteria}}
	 * 
	 * @param pCriteria
	 *            pagination criteria.
	 * @return filter and ordered entities.
	 * @throws TablePagerRepositoryException
	 */
	List<User> findPageEntries(PaginationCriteria pCriteria) throws TablePagerRepositoryException;

}
