package it.davioooh.datatablepager;

/**
 * The main component, used to generate a {@code TablePage}} according to {@code PaginationCriteria}.
 * 
 * @author davioooh
 *
 */
public interface DataTablePager {

	TablePage getPage(PaginationCriteria pCriteria) throws TablePagerException;

}
