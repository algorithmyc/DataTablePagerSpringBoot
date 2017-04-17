package it.davioooh.datatablepager.repository;

import java.util.Arrays;
import java.util.List;

import it.davioooh.datatablepager.PaginationCriteria;
import it.davioooh.datatablepager.data.TablePagerRepository;
import it.davioooh.datatablepager.data.TablePagerRepositoryException;
import it.davioooh.datatablepager.model.User;

public class UserTableRepository<T> implements TablePagerRepository<T> {

	private static final List<User> TEST_DATA = Arrays.asList(new User(1, "Lisa", 20), new User(2, "Tom", 31),
			new User(3, "David", 38), new User(4, "Marco", 23), new User(5, "Jenny", 15));

	@Override
	public long countTotalEntries() throws TablePagerRepositoryException {
		// TODO Auto-generated method stub
		return TEST_DATA.size();
	}

	@Override
	public long countFilteredEntries(PaginationCriteria pCriteria) throws TablePagerRepositoryException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<User> findPageEntries(PaginationCriteria pCriteria) throws TablePagerRepositoryException {
		// TODO Auto-generated method stub
		return TEST_DATA;
	}

}
