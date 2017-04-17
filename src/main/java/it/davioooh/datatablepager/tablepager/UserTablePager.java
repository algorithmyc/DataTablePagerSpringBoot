package it.davioooh.datatablepager.tablepager;

import it.davioooh.datatablepager.AbstractDataTablePager;
import it.davioooh.datatablepager.data.TablePagerRepository;
import it.davioooh.datatablepager.model.User;

public class UserTablePager extends AbstractDataTablePager {
	public UserTablePager(TablePagerRepository<User> repo) {
		super(repo);
	}
}
