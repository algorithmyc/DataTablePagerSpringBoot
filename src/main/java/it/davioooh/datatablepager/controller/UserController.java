package it.davioooh.datatablepager.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import it.davioooh.datatablepager.DataTablePager;
import it.davioooh.datatablepager.PaginationCriteria;
import it.davioooh.datatablepager.TablePage;
import it.davioooh.datatablepager.TablePagerException;
import it.davioooh.datatablepager.model.User;
import it.davioooh.datatablepager.repository.UserTableRepository;
import it.davioooh.datatablepager.tablepager.UserTablePager;

@RestController

public class UserController {
	private DataTablePager tablePager = new UserTablePager(new UserTableRepository<User>());

	@RequestMapping
	public String getUsers() {
		return "list";
	}

	@RequestMapping(path = "/data", method = RequestMethod.POST)
	public @ResponseBody TablePage getUsersData(@RequestBody PaginationCriteria treq) {
		try {
			return tablePager.getPage(treq);
		} catch (TablePagerException e) {
			return null;
		}
	}
}
