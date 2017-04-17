# DataTablePager #

Java server-side pagination utility for DataTables jQuery plug-in.

### Notes ###

* Version 0.5.x is tested for DataTables.js v 1.10.x
* Version 0.3.x and previous are tested for DataTables.js v 1.9.x

### For suggestions and info ###

* e-mail: **castellettid (at) gmail [dot] com**

### Support project ###

* If you find this project useful, please make a donation: https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=265EAR835B5WG

### Usage Example ###

Here I provide a simple usage example using Spring MVC.

#### 1. Repository ####

First of all you need to define logic to retrieve + sort + filter data from your data source. You do this implementing ```TablePagerRepository```:

```
#!java

public class UserTableRepository implements TablePagerRepository<User> {

	private static final List<User> TEST_DATA = Arrays.asList(new User(1, "Lisa", 20), new User(2, "Tom", 31),
			new User(3, "David", 38), new User(4, "Marco", 23), new User(5, "Jenny", 15));

	@Override
	public long countTotalEntries() throws TablePagerRepositoryException {
		return TEST_DATA.size();
	}

	@Override
	public List<User> findPageEntries(PaginationCriteria pCriteria)
			throws TablePagerRepositoryException {
		// ... order and filter your data here
		return TEST_DATA;
	}

}
```

I'm using this simple entity class:

```
#!java

public class User {

	private int id;
	private String name;
	private int age;
	
	// ... constructor + accessors
	
}
```

Probably you store your data in a DB, so you could use JPA or plain JDBC to query your data source and implement ordering.

**NOTE** I recently added an abstract implementation of ```TablePagerRepository``` based on JPA. This is a generic class implementing basic filtering and ordering logic via JPQL.

#### 2. Table pager ####

You also need to provide an implementation of ```AbstractDataTablePager ```, but this is quite simple. For example:

```
#!java

public class UserTablePager extends AbstractDataTablePager<User> {

	public UserTablePager(TablePagerRepository<User> repo) {
		super(repo);
	}

}
```

This way you specify which entity type to retrieve and to process.

#### 3. Controller ####

Finally you can use the pager in a controller. This is very simple, because you just need to instantiate a ```DataTablePager``` and use directly in the action method. You'll get ```PaginationCriteria``` as request payload and the method will return a ```TablePage``` as response body:


```
#!java

@Controller
@RequestMapping("/users")
public class UserController {

	private DataTablePager tablePager = new UserTablePager(new UserTableRepository());

	@RequestMapping
	public String getUsers() {
		return "list";
	}

	@RequestMapping(value = "/data", method = RequestMethod.POST)
	public @ResponseBody TablePage getUsersData(@RequestBody PaginationCriteria treq) {
		try {
			return tablePager.getPage(treq);
		} catch (TablePagerException e) {
			return null;
		}
	}
}
```

#### 4. On Client side ####

In your page you just need to include fiew lines:


```
#!html

<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/v/dt/jq-2.2.3/dt-1.10.12/datatables.css" />
<script type="text/javascript" src="https://cdn.datatables.net/v/dt/jq-2.2.3/dt-1.10.12/datatables.js" />
<script type="text/javascript">
	$(document).ready(function() {
		$('#example').DataTable({
			"processing" : true,
			"serverSide" : true,
			"columns": [ // NOTE: you need to specify names of fields as ids for the columns
                { "data": "id" },
                { "data": "name" },
                { "data": "age" }
            ],
			"ajax": {
			    "url": "users/data",
			    "type": "POST",
			    "contentType" : "application/json; charset=utf-8",			    
			    "data": function ( d ) {
	                return JSON.stringify(d); // NOTE: you also need to stringify POST payload
	            }
			}
		});
	});
</script>
</head>
<body>
	<table id="example" class="display" cellspacing="0" width="100%">
		<thead>
			<tr>
				<th>ID</th>
				<th>Name</th>
				<th>Age</th>
			</tr>
		</thead>
	</table>
</body>
</html>
```

Notice that you need to set ids for table columns matching fields in you server-side entities.